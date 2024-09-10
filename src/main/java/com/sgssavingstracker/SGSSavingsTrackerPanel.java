package com.sgssavingstracker;

import com.sun.jna.platform.unix.solaris.LibKstat;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;

public class SGSSavingsTrackerPanel extends PluginPanel
{
    JLabel hitpointsLabel;
    JLabel hitpointsValueLabel;
    JLabel prayerLabel;
    JLabel prayerValueLabel;
    SGSSavingsTrackerPlugin plugin;

    SGSSavingsTrackerPanel(SGSSavingsTrackerPlugin plugin) {
        this.plugin = plugin;
        hitpointsLabel = new JLabel("Hitpoints: ");
        prayerLabel = new JLabel("Prayer: ");
        hitpointsValueLabel = new JLabel("0");
        prayerValueLabel = new JLabel("0");

        add(hitpointsLabel);
        add(hitpointsValueLabel);
        add(prayerLabel);
        add(prayerValueLabel);
    }

   public void setValues(int hitpoints, int prayer) {
        hitpointsValueLabel.setText(String.valueOf(hitpoints));
        prayerValueLabel.setText(String.valueOf(prayer));
       System.out.println("updating panel" + hitpoints + " " + prayer);
   }
}
