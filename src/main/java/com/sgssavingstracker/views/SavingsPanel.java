package com.sgssavingstracker.views;

import com.sgssavingstracker.HPItem;
import com.sgssavingstracker.PPItem;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

public class SavingsPanel extends JPanel
{
	HPItem hpItem;
	PPItem ppItem;
	int hpSaved = 0;
	int ppSaved = 0;
	int prayerLevel = 0;

	int hpSavedValue;
	int ppSavedValue;

	JLabel hpIconLabel;
	JLabel ppIconLabel;
	JLabel hpValueLabel;
	JLabel ppValueLabel;
	JLabel totalValueLabel;
	ItemManager itemManager;

	public SavingsPanel(ItemManager itemManager, HPItem hpItem, PPItem ppItem)
	{
		this.itemManager = itemManager;
		this.hpItem = hpItem;
		this.ppItem = ppItem;

		initView();
	}

	private void initView()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 0, 8, 0));

		JLabel label = new JLabel("You've saved the equivalent of:");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);

		add(Box.createRigidArea(new Dimension(0, 8)));

		JPanel gp1 = new JPanel();
		gp1.setLayout(new GridLayout(2, 2, 16, 8));
		gp1.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		hpIconLabel = new JLabel();
		hpIconLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gp1.add(hpIconLabel);

		hpValueLabel = new JLabel();
		hpValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gp1.add(hpValueLabel);

		ppIconLabel = new JLabel();
		ppIconLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gp1.add(ppIconLabel);

		ppValueLabel = new JLabel();
		ppValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gp1.add(ppValueLabel);

		add(gp1);

		add(Box.createRigidArea(new Dimension(0, 7)));

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setMinimumSize(new Dimension(160, 2));
		separator.setMaximumSize(new Dimension(160, 2));
		add(separator);

		add(Box.createRigidArea(new Dimension(0, 9)));

		JPanel gp2 = new JPanel();
		gp2.setLayout(new GridLayout(1, 2, 16, 8));
		gp2.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel totalLabel = new JLabel("Total: ");
		totalLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gp2.add(totalLabel);

		totalValueLabel = new JLabel();
		totalValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gp2.add(totalValueLabel);

		add(gp2);
	}

	public void setHPSaved(int hpSaved)
	{
		this.hpSaved = hpSaved;
		calculateHitpoints();
	}

	public void setHPItem(HPItem item)
	{
		this.hpItem = item;
		calculateHitpoints();
	}

	public void setPPSaved(int ppSaved)
	{
		this.ppSaved = ppSaved;
		calculatePrayer();
	}

	public void setPPItem(PPItem item)
	{
		this.ppItem = item;
		calculatePrayer();
	}

	public void setPrayerLevel(int level)
	{
		this.prayerLevel = level;
		calculatePrayer();
	}

	private void calculateHitpoints()
	{
		int itemsRequired = Math.round((float) hpSaved / hpItem.getHpPerItem());

		int pricePerItem = itemManager.getItemPrice(hpItem.getId());
		hpSavedValue = pricePerItem * itemsRequired;

		AsyncBufferedImage itemImage = itemManager.getImage(hpItem.getId(), itemsRequired, true);
		itemImage.addTo(hpIconLabel);
		hpIconLabel.setToolTipText("<html>"
			+ hpItem.getName()
			+ ": <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(pricePerItem)
			+ "</font> gp each</html>");
		hpValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(hpSavedValue)
			+ "</font> gp</html>");

		calculateTotal();
	}

	private void calculatePrayer()
	{
		int restorePerDose = ppItem.getRestorationFunction().apply(prayerLevel);

		// On login, restore values are loaded from config before prayer level is determined
		// Prevent incorrect potionsRequired from briefly displaying prior to determining prayer level
		float dosesRequired = (float) ppSaved / restorePerDose;
		int potionsRequired = (prayerLevel > 0) ? Math.round(dosesRequired / 4) : 0;

		int pricePerPotion = itemManager.getItemPrice(ppItem.getId());
		ppSavedValue = pricePerPotion * potionsRequired;

		AsyncBufferedImage prayerImage = itemManager.getImage(ppItem.getId(), potionsRequired, true);
		prayerImage.addTo(ppIconLabel);
		ppIconLabel.setToolTipText("<html>"
			+ ppItem.getName()
			+ ": <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(pricePerPotion)
			+ "</font> gp each</html>");
		ppValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(ppSavedValue)
			+ "</font> gp</html>");

		calculateTotal();
	}

	private void calculateTotal()
	{
		totalValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(hpSavedValue + ppSavedValue)
			+ "</font> gp</html>");
	}
}
