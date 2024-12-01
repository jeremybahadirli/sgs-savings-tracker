package com.sgssavingstracker.views;

import com.sgssavingstracker.HPItem;
import com.sgssavingstracker.PPItem;
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
	JLabel hpLabel;
	JLabel ppLabel;
	JLabel hpValue;
	JLabel ppValue;
	ItemManager itemManager;

	public SavingsPanel(ItemManager itemManager)
	{
		this.itemManager = itemManager;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 0, 8, 0));

		JLabel label = new JLabel("You've saved the equivalent of:");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);

		add(Box.createRigidArea(new Dimension(0, 8)));

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(2, 2, 16, 8));
		gridPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		hpLabel = new JLabel();
		hpLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel.add(hpLabel);

		hpValue = new JLabel();
		hpValue.setFont(FontManager.getRunescapeSmallFont());
		gridPanel.add(hpValue);

		ppLabel = new JLabel();
		ppLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel.add(ppLabel);

		ppValue = new JLabel();
		ppValue.setFont(FontManager.getRunescapeSmallFont());
		gridPanel.add(ppValue);

		add(gridPanel);
	}

	public void setHitpoints(int hitpoints, HPItem item)
	{
		String itemName;
		int itemId;
		int hpPerItem;
		switch (item)
		{
			case KARAMBWAN:
				itemName = "Karambwan";
				itemId = 3144;
				hpPerItem = 18;
				break;
			case SHARK:
				itemName = "Shark";
				itemId = 385;
				hpPerItem = 20;
				break;
			case MANTA_RAY:
			default:
				itemName = "Manta Ray";
				itemId = 391;
				hpPerItem = 22;
				break;
		}

		int itemsRequired = Math.round((float) hitpoints / hpPerItem);

		int pricePerItem = itemManager.getItemPrice(itemId);
		int totalPrice = pricePerItem * itemsRequired;

		AsyncBufferedImage itemImage = itemManager.getImage(itemId, itemsRequired, true);
		itemImage.addTo(hpLabel);
		hpLabel.setToolTipText(itemName + ": " + QuantityFormatter.quantityToStackSize(pricePerItem) + " gp each");
		hpValue.setText("<html>- <font color='white'>" + QuantityFormatter.quantityToStackSize(totalPrice) + "</font> gp</html>");
	}

	public void setPrayer(int prayer, int prayerLevel, PPItem item)
	{
		String itemName;
		int itemId;
		int restorePerDose;
		switch (item)
		{
			case PRAYER_POTION:
				itemName = "Prayer potion(4)";
				itemId = 2434;
				restorePerDose = (prayerLevel / 4) + 7;
				break;
			case SUPER_RESTORE:
				itemName = "Super restore(4)";
				itemId = 3024;
				restorePerDose = (prayerLevel / 4) + 8;
				break;
			case SANFEW_SERUM:
			default:
				itemName = "Sanfew serum(4)";
				itemId = 10925;
				restorePerDose = (prayerLevel * 3 / 10) + 4;
				break;
		}

		// On login, restore values are loaded from config before prayer level is determined
		// Prevent incorrect potionsRequired from briefly displaying prior to determining prayer level
		float dosesRequired = (float) prayer / restorePerDose;
		int potionsRequired = (prayerLevel > 0) ? Math.round(dosesRequired / 4) : 0;

		int pricePerPotion = itemManager.getItemPrice(itemId);
		int totalPrice = pricePerPotion * potionsRequired;

		AsyncBufferedImage prayerImage = itemManager.getImage(itemId, potionsRequired, true);
		prayerImage.addTo(ppLabel);
		ppLabel.setToolTipText(itemName + ": " + QuantityFormatter.quantityToStackSize(pricePerPotion) + " gp each");
		ppValue.setText("<html>- <font color='white'>" + QuantityFormatter.quantityToStackSize(totalPrice) + "</font> gp</html>");
	}
}
