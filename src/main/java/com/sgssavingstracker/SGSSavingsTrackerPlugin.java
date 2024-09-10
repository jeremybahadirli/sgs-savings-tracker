package com.sgssavingstracker;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.events.ScreenshotTaken;
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

	private RestoreOccurrence currentRestoreOccurrence;

	private int hitpointsSaved;
	private int prayerSaved;
	private int specPercent;

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp()
	{
		SGSSavingsTrackerPanel panel = new SGSSavingsTrackerPanel();

		NavigationButton navButton = NavigationButton.builder()
			.panel(panel)
			.tooltip("SGS Savings Tracker")
			.icon(ImageUtil.loadImageResource(getClass(), "/sgs_icon.png"))
			.priority(5)
			.build();
		clientToolbar.addNavigation(navButton);

		Integer configHitpoints = configManager.getConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, Integer.class);
		Integer configPrayer = configManager.getConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, Integer.class);
		System.out.println(configHitpoints);
		hitpointsSaved = (configHitpoints != null) ? configHitpoints : 0;
		System.out.println(hitpointsSaved);
		prayerSaved = (configPrayer != null) ? configPrayer : 0;

		// TODO: Make sure spec loading is robust
		specPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
	}

	@Override
	protected void shutDown()
	{
		saveData();
	}

	@Subscribe
	public void onClientShutdown(ClientShutdown event)
	{
		saveData();
	}

	private void saveData()
	{
		configManager.setConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, hitpointsSaved);
		configManager.setConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, prayerSaved);
		System.out.println("saved");
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

		hitpointsSaved += currentRestoreOccurrence.getSavedHitpoints();
		prayerSaved += currentRestoreOccurrence.getSavedPrayer();
	}

	@Subscribe
	public void onScreenshotTaken(ScreenshotTaken event)
	{
		hitpointsSaved++;
		prayerSaved++;
		// TODO: Delete logs
		System.out.println(hitpointsSaved + " " + prayerSaved);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
		}
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		System.out.println("CHAGE: " + event.toString());
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
}