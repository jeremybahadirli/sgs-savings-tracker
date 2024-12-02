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
	private final ItemManager itemManager;

	private JLabel hpItemStackLabel;
	private JLabel ppItemStackLabel;
	private JLabel hpGpValueLabel;
	private JLabel ppGpValueLabel;
	private JLabel totalGpValueLabel;

	private HPItem hpItem;
	private PPItem ppItem;
	private int hpSaved = 0;
	private int ppSaved = 0;
	private int prayerLevel = 0;
	private int hpSavedGpValue;
	private int ppSavedGpValue;

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

		JPanel gridPanel1 = new JPanel();
		gridPanel1.setLayout(new GridLayout(2, 2, 16, 8));
		gridPanel1.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		hpItemStackLabel = new JLabel();
		hpItemStackLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel1.add(hpItemStackLabel);

		hpGpValueLabel = new JLabel();
		hpGpValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gridPanel1.add(hpGpValueLabel);

		ppItemStackLabel = new JLabel();
		ppItemStackLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel1.add(ppItemStackLabel);

		ppGpValueLabel = new JLabel();
		ppGpValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gridPanel1.add(ppGpValueLabel);

		add(gridPanel1);

		add(Box.createRigidArea(new Dimension(0, 7)));

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setMinimumSize(new Dimension(160, 2));
		separator.setMaximumSize(new Dimension(160, 2));
		add(separator);

		add(Box.createRigidArea(new Dimension(0, 9)));

		JPanel gridPanel2 = new JPanel();
		gridPanel2.setLayout(new GridLayout(1, 2, 16, 8));
		gridPanel2.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel totalLabel = new JLabel("Total: ");
		totalLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		gridPanel2.add(totalLabel);

		totalGpValueLabel = new JLabel();
		totalGpValueLabel.setFont(FontManager.getRunescapeSmallFont());
		gridPanel2.add(totalGpValueLabel);

		add(gridPanel2);
	}

	public void setHpSaved(int hpSaved)
	{
		this.hpSaved = hpSaved;
		calculateHp();
	}

	public void setHpItem(HPItem item)
	{
		this.hpItem = item;
		calculateHp();
	}

	public void setPpSaved(int ppSaved)
	{
		this.ppSaved = ppSaved;
		calculatePp();
	}

	public void setPpItem(PPItem item)
	{
		this.ppItem = item;
		calculatePp();
	}

	public void setPrayerLevel(int level)
	{
		this.prayerLevel = level;
		calculatePp();
	}

	private void calculateHp()
	{
		int itemsRequired = Math.round((float) hpSaved / hpItem.getHpPerItem());

		int gpValuePerItem = itemManager.getItemPrice(hpItem.getId());
		hpSavedGpValue = gpValuePerItem * itemsRequired;

		AsyncBufferedImage hpItemStackImage = itemManager.getImage(hpItem.getId(), itemsRequired, true);
		hpItemStackImage.addTo(hpItemStackLabel);
		hpItemStackLabel.setToolTipText("<html>"
			+ hpItem.getName()
			+ ": <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(gpValuePerItem)
			+ "</font> gp each</html>");
		hpGpValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(hpSavedGpValue)
			+ "</font> gp</html>");

		calculateTotal();
	}

	private void calculatePp()
	{
		int restorePerDose = ppItem.getRestorationFunction().apply(prayerLevel);

		// On login, restore values are loaded from config before prayer level is determined
		// Prevent incorrect itemsRequired from briefly displaying prior to determining prayer level
		float dosesRequired = (float) ppSaved / restorePerDose;
		int itemsRequired = (prayerLevel > 0) ? Math.round(dosesRequired / 4) : 0;

		int gpValuePerItem = itemManager.getItemPrice(ppItem.getId());
		ppSavedGpValue = gpValuePerItem * itemsRequired;

		AsyncBufferedImage ppItemStackImage = itemManager.getImage(ppItem.getId(), itemsRequired, true);
		ppItemStackImage.addTo(ppItemStackLabel);
		ppItemStackLabel.setToolTipText("<html>"
			+ ppItem.getName()
			+ ": <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(gpValuePerItem)
			+ "</font> gp each</html>");
		ppGpValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(ppSavedGpValue)
			+ "</font> gp</html>");

		calculateTotal();
	}

	private void calculateTotal()
	{
		totalGpValueLabel.setText("<html>- <font color='white'>"
			+ QuantityFormatter.quantityToStackSize(hpSavedGpValue + ppSavedGpValue)
			+ "</font> gp</html>");
	}
}
