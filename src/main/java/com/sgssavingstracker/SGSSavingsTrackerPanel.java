package com.sgssavingstracker;

import com.sgssavingstracker.views.RestorePanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

public class SGSSavingsTrackerPanel extends PluginPanel
{
	RestorePanel restorePanel = new RestorePanel();

	SGSSavingsTrackerPanel()
	{
		getParent().setLayout(new BorderLayout());
		getParent().add(this, BorderLayout.CENTER);

		setLayout(new GridBagLayout());

		PluginErrorPanel titlePanel = new PluginErrorPanel();
		titlePanel.setContent("SGS Savings Tracker", "Track Hitpoints and Prayer saved by using the SGS Special Attack.");
		GridBagConstraints c0 = new GridBagConstraints();
		c0.gridy = 0;
		c0.fill = GridBagConstraints.BOTH;
		c0.weighty = 0.1;
		c0.weightx = 1;
		add(titlePanel, c0);

		restorePanel.setMinimumSize(restorePanel.getPreferredSize());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 1;
		c1.weightx = 1;
		c0.weighty = 0.25;
		c1.fill = GridBagConstraints.BOTH;
		c1.insets = new Insets(40, 0, 0, 0);
		add(restorePanel, c1);

		JPanel spacer = new JPanel();
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridy = 2;
		c2.weighty = 1;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.BOTH;
		add(spacer, c2);

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(event -> {
			final int result = JOptionPane.showOptionDialog(this,
				"<html>This will reset Hitpoints and Prayer Points to 0.<br>This action cannot be undone. Are you sure?</html>",
				"Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, new String[]{"Yes", "No"}, "No");
			if (result == JOptionPane.YES_OPTION)
			{
				// TODO: Implement Reset
			}
		});
		add(resetButton);

		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridy = 3;
		c3.weightx = 1;
		c0.weighty = 0.25;
		c3.fill = GridBagConstraints.BOTH;
		add(resetButton, c3);
	}

	public void setHitpoints(int hitpoints)
	{
		restorePanel.setHitpoints(hitpoints);
	}

	public void setPrayer(int prayer)
	{
		restorePanel.setPrayer(prayer);
	}
}
