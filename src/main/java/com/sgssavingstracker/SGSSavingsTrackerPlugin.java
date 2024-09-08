package com.sgssavingstracker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private Restore currentRestore;

    int totalPrayerSaved = 0;
    int totalHpSaved = 0;

    int specPercent;
    int previousPrayer;
    int previousHp;

    @Override
    protected void startUp() throws Exception {
        specPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
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

//        if (!wieldingSGS()) {
//            return;
//        }

        currentRestore = new Restore(
                client.getTickCount(),
                client.getBoostedSkillLevel(Skill.HITPOINTS),
                client.getBoostedSkillLevel(Skill.PRAYER));

    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (client.getTickCount() != currentRestore.getSpecTick()) {
            return;
        }

        int newLevel = event.getBoostedLevel();

        switch (event.getSkill()) {
            case HITPOINTS:
                currentRestore.setActualHitpoints(newLevel - currentRestore.getPreviousHitpoints());
                break;
            case PRAYER:
                currentRestore.setActualPrayer(newLevel - currentRestore.getPreviousPrayer());
                break;
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (!event.getHitsplat().isMine() || event.getActor() == client.getLocalPlayer()) {
            return;
        }

        if (client.getTickCount() != currentRestore.getSpecTick() + 1) {
            return;
        }

        currentRestore.computeExpected(event.getHitsplat().getAmount());
        currentRestore.computeSaved();
    }

    private boolean wieldingSGS() {
        final ItemContainer equipmentItemContainer = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipmentItemContainer == null) {
            return false;
        }

        Item weaponSlotItem = equipmentItemContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weaponSlotItem == null) {
            return false;
        }

        return weaponSlotItem.getId() == SPEC_ITEM_ID;
    }

    @Provides
    SGSSavingsTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SGSSavingsTrackerConfig.class);
    }
}
