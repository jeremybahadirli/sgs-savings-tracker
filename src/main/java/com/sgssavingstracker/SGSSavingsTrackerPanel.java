package com.sgssavingstracker;

import com.sgssavingstracker.views.RestorePanel;
import com.sgssavingstracker.views.SavingsPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

public class SGSSavingsTrackerPanel extends PluginPanel
{
	private final Stats stats;
	private SavingsPanel savingsPanel;
	private RestorePanel restorePanel;

	SGSSavingsTrackerPanel(Stats stats, ItemManager itemManager, SGSSavingsTrackerConfig config)
	{
		this.stats = stats;
		initView(itemManager, config);
	}

	private void initView(ItemManager itemManager, SGSSavingsTrackerConfig config)
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

		restorePanel = new RestorePanel();
		restorePanel.setMinimumSize(restorePanel.getPreferredSize());
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 1;
		c1.weightx = 1;
		c1.fill = GridBagConstraints.BOTH;
		c1.insets = new Insets(32, 0, 32, 0);
		add(restorePanel, c1);

		savingsPanel = new SavingsPanel(itemManager, config.hpItem(), config.ppItem());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridy = 3;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.BOTH;
		add(savingsPanel, c2);

		JPanel spacer = new JPanel();
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridy = 4;
		c3.weighty = 1;
		add(spacer, c3);

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(event -> {
			int result = JOptionPane.showOptionDialog(this,
				"<html>This will reset Hitpoints and Prayer Points to 0.<br>This action cannot be undone. Are you sure?</html>",
				"Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, new String[]{"Yes", "No"}, "No");
			if (result == JOptionPane.YES_OPTION)
			{
				resetClicked();
			}
		});
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridy = 5;
		c4.weightx = 1;
		c4.fill = GridBagConstraints.BOTH;
		add(resetButton, c4);
	}

	public void update(PropertyChangeEvent event)
	{
		int newValue = (Integer) event.getNewValue();
		switch (event.getPropertyName())
		{
			case "hp":
				restorePanel.setHpSaved(newValue);
				savingsPanel.setHpSaved(newValue);
				break;
			case "pp":
				restorePanel.setPpSaved(newValue);
				savingsPanel.setPpSaved(newValue);
				break;
			case "hitpointsLevel":
				savingsPanel.setHitpointsLevel(newValue);
				break;
			case "prayerLevel":
				savingsPanel.setPrayerLevel(newValue);
				break;
		}
	}

	public void setHpItem(HPItem hpItem)
	{
		savingsPanel.setHpItem(hpItem);
	}

	public void setPpItem(PPItem ppItem)
	{
		savingsPanel.setPpItem(ppItem);
	}

	private void resetClicked()
	{
		if (stats == null)
		{
			return;
		}
		stats.setHpSaved(0);
		stats.setPpSaved(0);
	}
}