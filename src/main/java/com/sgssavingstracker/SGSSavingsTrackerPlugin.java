package com.sgssavingstracker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
	public static final int SGS_ITEM_ID = 11806;
	public static final String CONFIG_GROUP_NAME = "sgssavingstracker";
	public static final String CONFIG_HP_KEY = "hitpointsSaved";
	public static final String CONFIG_PP_KEY = "prayerSaved";

	private Stats stats;
	private RestoreOccurrence currentRestoreOccurrence;
	private NavigationButton navigationButton;
	private SGSSavingsTrackerPanel panel;

	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ClientThread clientThread;
	@Inject
	private SGSSavingsTrackerConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp()
	{
		stats = new Stats();
		panel = new SGSSavingsTrackerPanel(stats, itemManager, config);
		clientThread.invokeLater(() -> {
			panel.savingsPanel.setHpItem(config.hpItem());
			panel.savingsPanel.setPpItem(config.ppItem());
		});

		stats.addPropertyChangeListener(event ->
			clientThread.invokeLater(() -> {
				panel.update(event);
				saveToConfig();
			}));

		loadFromConfig();
		stats.setSpecPercent(client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT));
		stats.setPrayerLevel(client.getRealSkillLevel(Skill.PRAYER));

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
		loadFromConfig();
	}

	private void loadFromConfig()
	{
		Integer configHp = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HP_KEY, Integer.class);
		Integer configPp = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PP_KEY, Integer.class);
		int formattedConfigHp = (configHp != null) ? configHp : 0;
		int formattedConfigPp = (configPp != null) ? configPp : 0;
		stats.setHpSaved(formattedConfigHp);
		stats.setPpSaved(formattedConfigPp);
	}

	private void saveToConfig()
	{
		if (stats.getHpSaved() > 0)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HP_KEY, stats.getHpSaved());
		}
		else
		{
			configManager.unsetRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HP_KEY);
		}
		if (stats.getPpSaved() > 0)
		{
			configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PP_KEY, stats.getPpSaved());
		}
		else
		{
			configManager.unsetRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PP_KEY);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayer.SPECIAL_ATTACK_PERCENT)
		{
			return;
		}

		int previousSpecPercent = stats.getSpecPercent();
		stats.setSpecPercent(event.getValue());

		if (playerIsWieldingSgs() && stats.getSpecPercent() < previousSpecPercent)
		{
			currentRestoreOccurrence = new RestoreOccurrence(
				client.getTickCount(),
				client.getBoostedSkillLevel(Skill.HITPOINTS),
				client.getBoostedSkillLevel(Skill.PRAYER));
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.PRAYER && event.getLevel() != stats.getPrayerLevel())
		{
			stats.setPrayerLevel(event.getLevel());
		}

		// Player used SGS spec
		if (currentRestoreOccurrence == null || client.getTickCount() != currentRestoreOccurrence.getSpecTick())
		{
			return;
		}

		// Record actually gained HP/PP
		switch (event.getSkill())
		{
			case HITPOINTS:
				currentRestoreOccurrence.setActualHp(event.getBoostedLevel() - currentRestoreOccurrence.getPreviousHp());
				break;
			case PRAYER:
				currentRestoreOccurrence.setActualPp(event.getBoostedLevel() - currentRestoreOccurrence.getPreviousPp());
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

		// Player used SGS spec
		if (currentRestoreOccurrence == null || client.getTickCount() != currentRestoreOccurrence.getSpecTick() + 1)
		{
			return;
		}

		currentRestoreOccurrence.computeExpected(event.getHitsplat().getAmount());
		currentRestoreOccurrence.computeSaved();

		stats.incrementHpSaved(currentRestoreOccurrence.getSavedHp());
		stats.incrementPpSaved(currentRestoreOccurrence.getSavedPp());
	}

	private boolean playerIsWieldingSgs()
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
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP_NAME))
		{
			clientThread.invokeLater(() -> {
				panel.savingsPanel.setHpItem(config.hpItem());
				panel.savingsPanel.setPpItem(config.ppItem());
			});
		}
	}

	@Provides
	SGSSavingsTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SGSSavingsTrackerConfig.class);
	}
}