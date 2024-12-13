package com.sgssavingstracker;

import java.util.function.Function;
import lombok.Getter;

@Getter
public enum PPItem
{
	PRAYER_POTION("Prayer potion(4)", 2434, prayerLevel -> (prayerLevel / 4) + 7),
	SUPER_RESTORE("Super restore(4)", 3024, prayerLevel -> (prayerLevel / 4) + 8),
	SANFEW_SERUM("Sanfew serum(4)", 10925, prayerLevel -> (prayerLevel * 3 / 10) + 4);

	private final String name;
	private final int id;
	private final Function<Integer, Integer> restorationFunction;
	private final int dosesPerItem;

	PPItem(String name, int id, Function<Integer, Integer> restorationFunction)
	{
		this.name = name;
		this.id = id;
		this.restorationFunction = restorationFunction;
		this.dosesPerItem = 4;
	}

	PPItem(String name, int id, Function<Integer, Integer> restorationFunction, int dosesPerItem)
	{
		this.name = name;
		this.id = id;
		this.restorationFunction = restorationFunction;
		this.dosesPerItem = dosesPerItem;
	}
}
