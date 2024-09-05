package com.sgssavingstracker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
//        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
//        }
    }

    @Provides
    SGSSavingsTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SGSSavingsTrackerConfig.class);
    }
}
