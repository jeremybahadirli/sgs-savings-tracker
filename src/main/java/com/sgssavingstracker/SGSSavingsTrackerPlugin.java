package com.sgssavingstracker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "SGS Savings Tracker"
)
public class SGSSavingsTrackerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private SGSSavingsTrackerConfig config;

    @Inject
    private ItemManager itemManager;

    // SGS: 11806
    public static final int SPEC_ITEM_ID = 11061;

    int specPercent;
    int specTick;
    int previousPrayer;
    int previousHP;

    @Override
    protected void startUp() throws Exception {
        specPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        System.out.println(specPercent);
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (client.getTickCount() != specTick) {
            return;
        }

        switch (event.getSkill()) {
            case PRAYER:
                System.out.println("Prayer gain: " + (event.getBoostedLevel() - previousPrayer));
                System.out.println("tick: " + client.getTickCount());
                break;
            case HITPOINTS:
                System.out.println("HP gain: " + (event.getBoostedLevel() - previousHP));
                System.out.println("tick: " + client.getTickCount());
                break;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() != VarPlayer.SPECIAL_ATTACK_PERCENT) {
            return;
        }

        if (event.getValue() >= this.specPercent) {
            this.specPercent = event.getValue();
            return;
        }
        this.specPercent = event.getValue();

        if (!wieldingSGS()) {
            return;
        }

        specTick = client.getTickCount();
        previousPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        previousHP = client.getBoostedSkillLevel(Skill.HITPOINTS);

        System.out.println("SGS Spec Used. Tick: " + specTick);
    }

    private boolean wieldingSGS() {
        final ItemContainer equipmentItemContainer = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipmentItemContainer == null) {
            return false;
        }

        Item weaponSlotItem = equipmentItemContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        return (weaponSlotItem != null ? weaponSlotItem.getId() : 0) == SPEC_ITEM_ID;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGING_IN) {
        }
    }

    @Provides
    SGSSavingsTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SGSSavingsTrackerConfig.class);
    }
}
