package com.sgssavingstracker;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "SGS Savings Tracker",
	description = "Track Hitpoints and Prayer saved by using the SGS Special Attack.",
	tags = {"saradomin", "godsword", "hitpoints", "hp", "prayer", "pp"}
)
public class SGSSavingsTrackerPlugin extends Plugin
{
	// SGS: 11806
	public static final int SGS_ITEM_ID = 11806;

	public static final String CONFIG_GROUP_NAME = "sgssavingstracker";
	public static final String CONFIG_HITPOINTS_KEY = "hitpointsSaved";
	public static final String CONFIG_PRAYER_KEY = "prayerSaved";

	private int specPercent;
	private Stats stats;
	private RestoreOccurrence currentRestoreOccurrence;

	private NavigationButton navigationButton;

	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ClientThread clientThread;
	@Inject
	private ConfigManager configManager;
	@Inject
	private ItemManager itemManager;

	// THINGS TO MANAGE:
	// SPEC, PRAYER LEVEL, STATS, CURRENTRESTOREOCCURENCE

	@Override
	protected void startUp()
	{
		currentRestoreOccurrence = null;
		stats = new Stats();

		SGSSavingsTrackerPanel panel = new SGSSavingsTrackerPanel(stats, itemManager);
		stats.addPropertyChangeListener(event ->
			clientThread.invokeLater(() -> {
				panel.update(event);
				saveData();
			}));

		loadData();
		setPrayerLevel(client.getRealSkillLevel(Skill.PRAYER));

		navigationButton = NavigationButton.builder()
			.panel(panel)
			.tooltip("SGS Savings Tracker")
			.icon(ImageUtil.loadImageResource(getClass(), "/sgs_icon.png"))
			.priority(5)
			.build();
		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		loadData();
	}

	private void loadData()
	{
		Integer configHitpoints = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, Integer.class);
		Integer configPrayer = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, Integer.class);
		int hitpointsValue = (configHitpoints != null) ? configHitpoints : 0;
		int prayerValue = (configPrayer != null) ? configPrayer : 0;
		stats.setHitpoints(hitpointsValue);
		stats.setPrayer(prayerValue);

		specPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
	}

	private void saveData()
	{
		if (stats.getHitpoints() > 0)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, stats.getHitpoints());
		}
		else
		{
			configManager.unsetRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY);
		}
		if (stats.getPrayer() > 0)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, stats.getPrayer());
		}
		else
		{
			configManager.unsetRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayer.SPECIAL_ATTACK_PERCENT)
		{
			return;
		}

		int previousSpecPercent = this.specPercent;
		this.specPercent = event.getValue();

		if (this.specPercent >= previousSpecPercent || !playerIsWieldingSGS())
		{
			return;
		}

		currentRestoreOccurrence = new RestoreOccurrence(
			client.getTickCount(),
			client.getBoostedSkillLevel(Skill.HITPOINTS),
			client.getBoostedSkillLevel(Skill.PRAYER));
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.PRAYER)
		{
			setPrayerLevel(event.getLevel());
		}
		setRestore(event);
	}

	private void setPrayerLevel(int level)
	{
		stats.setPrayerLevel(level);
	}

	private void setRestore(StatChanged event)
	{
		if (currentRestoreOccurrence == null || client.getTickCount() != currentRestoreOccurrence.getSpecTick())
		{
			return;
		}

		int newLevel = event.getBoostedLevel();
		switch (event.getSkill())
		{
			case HITPOINTS:
				currentRestoreOccurrence.setActualHitpoints(newLevel - currentRestoreOccurrence.getPreviousHitpoints());
				break;
			case PRAYER:
				currentRestoreOccurrence.setActualPrayer(newLevel - currentRestoreOccurrence.getPreviousPrayer());
				break;
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (!event.getHitsplat().isMine() || event.getActor() == client.getLocalPlayer())
		{
			return;
		}

		if (currentRestoreOccurrence == null || client.getTickCount() != currentRestoreOccurrence.getSpecTick() + 1)
		{
			return;
		}

		currentRestoreOccurrence.computeExpected(event.getHitsplat().getAmount());
		currentRestoreOccurrence.computeSaved();

		stats.incrementHitpoints(currentRestoreOccurrence.getSavedHitpoints());
		stats.incrementPrayer(currentRestoreOccurrence.getSavedPrayer());
	}

	private boolean playerIsWieldingSGS()
	{
		final ItemContainer equipmentItemContainer = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipmentItemContainer == null)
		{
			return false;
		}

		Item weaponSlotItem = equipmentItemContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weaponSlotItem == null)
		{
			return false;
		}

		return weaponSlotItem.getId() == SGS_ITEM_ID;
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		String[] args = commandExecuted.getArguments();
		switch (commandExecuted.getCommand())
		{
			case "sgsprint":
				System.out.println("stats: " + stats.toString());
				System.out.print("currentRestoreOccurrence: ");
				if (currentRestoreOccurrence != null)
				{
					System.out.println(currentRestoreOccurrence);
				}
				else
				{
					System.out.println("null");
				}
				System.out.println("Spec percent: " + specPercent);
				break;
			case "sgshp":
				stats.incrementHitpoints(Integer.parseInt(args[0]));
				break;
			case "sgspp":
				stats.incrementPrayer(Integer.parseInt(args[0]));
				break;
		}
	}
}