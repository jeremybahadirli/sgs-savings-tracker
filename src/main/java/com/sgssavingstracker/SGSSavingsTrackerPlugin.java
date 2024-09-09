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
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ScreenshotTaken;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "SGS Savings Tracker")
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
	private SGSSavingsTrackerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp()
	{
		Integer configHitpoints = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, Integer.class);
		hitpointsSaved = (configHitpoints != null) ? configHitpoints : 0;

		Integer configPrayer = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, Integer.class);
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
		configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_HITPOINTS_KEY, hitpointsSaved);
		configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, CONFIG_PRAYER_KEY, prayerSaved);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarpId() != VarPlayer.SPECIAL_ATTACK_PERCENT)
		{
			return;
		}

		if (event.getValue() >= this.specPercent)
		{
			this.specPercent = event.getValue();
			return;
		}
		this.specPercent = event.getValue();

		if (!wieldingSGS())
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
		System.out.println(hitpointsSaved + " " + prayerSaved);
	}

	private boolean wieldingSGS()
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

	@Provides
	SGSSavingsTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SGSSavingsTrackerConfig.class);
	}
}
