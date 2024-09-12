package com.sgssavingstracker.views;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;

public class StatPanel extends JPanel
{
	NumberFormat formatter = NumberFormat.getInstance();
	JLabel valueLabel = new JLabel();

	public StatPanel(BufferedImage image)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(8, 8, 8, 8));

		JLabel iconLabel = new JLabel(new ImageIcon(image));
		iconLabel.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(iconLabel, BorderLayout.PAGE_START);

		valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setValue(0);
		add(valueLabel, BorderLayout.PAGE_END);
	}

	public void setValue(int value)
	{
		System.out.println("updateing: " + formatter.format(value));
		valueLabel.setText(formatter.format(value));
	}
}
