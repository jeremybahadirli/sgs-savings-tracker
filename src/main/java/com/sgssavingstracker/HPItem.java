package com.sgssavingstracker;

import lombok.Getter;

@Getter
public enum HPItem
{
	KARAMBWAN("Karambwan", 3144, 18),
	SHARK("Shark", 385, 20),
	MANTA_RAY("Manta ray", 391, 22);

	private final String name;
	private final int id;
	private final int hpPerItem;

	HPItem(String name, int id, int hpPerItem)
	{
		this.name = name;
		this.id = id;
		this.hpPerItem = hpPerItem;
	}
}
