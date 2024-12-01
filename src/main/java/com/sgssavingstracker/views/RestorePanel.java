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
	StatPanel hpPanel;
	StatPanel ppPanel;

	public RestorePanel()
	{
		setLayout(new GridBagLayout());

		JPanel spacerTop = new JPanel();
		spacerTop.setPreferredSize(new Dimension(0, 32));
		GridBagConstraints c0 = new GridBagConstraints();
		c0.gridx = 0;
		c0.gridy = 0;
		c0.weighty = 1;
		add(spacerTop, c0);

		BufferedImage hpIcon = ImageUtil.loadImageResource(getClass(), "/hitpoints_icon.png");
		hpPanel = new StatPanel(hpIcon);
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 1;
		c1.weightx = 0.5;
		add(hpPanel, c1);

		JPanel spacerBottom = new JPanel();
		spacerBottom.setPreferredSize(new Dimension(0, 32));
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 3;
		c2.weighty = 1;
		add(spacerBottom, c2);

		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 1;
		c3.gridy = 0;
		c3.gridheight = 4;
		c3.fill = GridBagConstraints.BOTH;
		add(separator, c3);

		BufferedImage ppIcon = ImageUtil.loadImageResource(getClass(), "/prayer_icon.png");
		ppPanel = new StatPanel(ppIcon);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 2;
		c4.gridy = 1;
		c4.weightx = 0.5;
		add(ppPanel, c4);
	}

	public void setHpSaved(int hpSaved)
	{
		hpPanel.setValue(hpSaved);
	}

	public void setPpSaved(int ppSaved)
	{
		ppPanel.setValue(ppSaved);
	}
}
