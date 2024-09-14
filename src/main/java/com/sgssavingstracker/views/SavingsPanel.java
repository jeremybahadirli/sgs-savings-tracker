package com.sgssavingstracker.views;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

public class SavingsPanel extends JPanel
{
	private static final int SHARK_ITEM_ID = 385;
	private static final int POTION_ITEM_ID = 2434;

	JLabel sharkLabel;
	JLabel potionLabel;
	JLabel sharkValue;
	JLabel potionValue;
	ItemManager itemManager;

	public SavingsPanel(ItemManager itemManager)
	{
		this.itemManager = itemManager;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(8, 0, 8, 0));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel label = new JLabel("You've saved the equivalent of:");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);

		add(Box.createRigidArea(new Dimension(0, 8)));

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(2, 2, 16, 8));
		gridPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		sharkLabel = new JLabel();
		sharkLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel.add(sharkLabel);

		sharkValue = new JLabel();
		sharkValue.setFont(FontManager.getRunescapeSmallFont());
		gridPanel.add(sharkValue);

		potionLabel = new JLabel();
		potionLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel.add(potionLabel);

		potionValue = new JLabel();
		potionValue.setFont(FontManager.getRunescapeSmallFont());
		gridPanel.add(potionValue);

		add(gridPanel);

		setSharks(0);
		setPotions(0, 0);
	}

	public void setSharks(int hitpoints)
	{
		int quantity = Math.round(hitpoints / 20f);
		int value = (quantity > 0) ? itemManager.getItemPrice(SHARK_ITEM_ID) * quantity : 0;

		AsyncBufferedImage sharkImage = itemManager.getImage(SHARK_ITEM_ID, quantity, true);
		sharkImage.addTo(sharkLabel);

		sharkValue.setText("<html>- <font color='white'>" + QuantityFormatter.quantityToStackSize(value) + "</font> gp</html>");
	}

	public void setPotions(int prayer, int prayerLevel)
	{
		int quantity;
		if (prayerLevel > 0)
		{
			int restorePerDose = (prayerLevel / 4) + 7;
			float dosesRequired = (float) prayer / restorePerDose;
			quantity = Math.round(dosesRequired / 4);
		}
		else
		{
			quantity = 0;
		}

		int value = (quantity > 0) ? itemManager.getItemPrice(POTION_ITEM_ID) * quantity : 0;

		AsyncBufferedImage prayerImage = itemManager.getImage(POTION_ITEM_ID, quantity, true);
		prayerImage.addTo(potionLabel);

		potionValue.setText("<html>- <font color='white'>" + QuantityFormatter.quantityToStackSize(value) + "</font> gp</html>");
	}
}
