package com.sgssavingstracker;

import java.util.function.Function;
import lombok.Getter;

@Getter
public enum HPItem
{
	KARAMBWAN("Karambwan", 3144, hitpointsLevel -> 18),
	SHARK("Shark", 385, hitpointsLevel -> 20),
	MANTA_RAY("Manta ray", 391, hitpointsLevel -> 22),
	SARADOMIN_BREW("Saradomin brew(4)", 6685, hitpointsLevel -> (hitpointsLevel * 15 / 100) + 2, 4);

	private final String name;
	private final int id;
	private final Function<Integer, Integer> restorationFunction;
	private final int dosesPerItem;

	HPItem(String name, int id, Function<Integer, Integer> restorationFunction)
	{
		this.name = name;
		this.id = id;
		this.restorationFunction = restorationFunction;
		this.dosesPerItem = 1;
	}

	HPItem(String name, int id, Function<Integer, Integer> restorationFunction, int dosesPerItem)
	{
		this.name = name;
		this.id = id;
		this.restorationFunction = restorationFunction;
		this.dosesPerItem = dosesPerItem;
	}
}
