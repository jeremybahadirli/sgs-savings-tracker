package com.sgssavingstracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SGSSavingsTrackerPlugin.CONFIG_GROUP_NAME)
public interface SGSSavingsTrackerConfig extends Config
{
	@ConfigItem(
		keyName = "hp_item",
		name = "HP Restore Item",
		description = "The item to use when displaying equivalent HP savings."
	)
	default HPItem hpItem()
	{
		return HPItem.SHARK;
	}

	@ConfigItem(
		keyName = "pp_item",
		name = "PP Restore Item",
		description = "The item to use when displaying equivalent PP savings."
	)
	default PPItem ppItem()
	{
		return PPItem.PRAYER_POTION;
	}
}