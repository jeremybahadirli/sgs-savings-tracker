package com.sgssavingstracker.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import net.runelite.client.util.ImageUtil;

public class RestorePanel extends JPanel
{
	StatPanel hitpointsPanel;
	StatPanel prayerPanel;

	public RestorePanel()
	{
		setLayout(new GridBagLayout());

		JPanel vSpacer1 = new JPanel();
		vSpacer1.setPreferredSize(new Dimension(0, 32));
		GridBagConstraints c0 = new GridBagConstraints();
		c0.gridx = 0;
		c0.gridy = 0;
		c0.weighty = 1;
		add(vSpacer1, c0);

		BufferedImage hitpointsIcon = ImageUtil.loadImageResource(getClass(), "/hitpoints_icon.png");
		hitpointsPanel = new StatPanel(hitpointsIcon);
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		c1.weightx = 0.5;
		add(hitpointsPanel, c1);

		JPanel vSpacer2 = new JPanel();
		vSpacer2.setPreferredSize(new Dimension(0, 32));
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 3;
		c3.weighty = 1;
		add(vSpacer2, c3);

		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 1;
		c4.gridy = 0;
		c4.gridheight = 4;
		c4.fill = GridBagConstraints.BOTH;
		add(separator, c4);

		BufferedImage prayerIcon = ImageUtil.loadImageResource(getClass(), "/prayer_icon.png");
		prayerPanel = new StatPanel(prayerIcon);
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx = 2;
		c5.gridy = 1;
		c5.weightx = 0.5;
		add(prayerPanel, c5);
	}

	public void setHitpoints(int hitpoints)
	{
		hitpointsPanel.setValue(hitpoints);
	}

	public void setPrayer(int prayer)
	{
		prayerPanel.setValue(prayer);
	}
}
