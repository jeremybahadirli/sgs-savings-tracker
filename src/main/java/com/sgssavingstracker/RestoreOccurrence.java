package com.sgssavingstracker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class RestoreOccurrence
{
	@Getter
	private final int specTick;
	@Getter
	private final int previousHitpoints;
	@Getter
	private final int previousPrayer;

	private int expectedHitpoints;
	private int expectedPrayer;
	@Setter
	private int actualHitpoints;
	@Setter
	private int actualPrayer;
	@Getter
	private int savedHitpoints;
	@Getter
	private int savedPrayer;

	public RestoreOccurrence(int specTick, int previousHitpoints, int previousPrayer)
	{
		this.specTick = specTick;
		this.previousHitpoints = previousHitpoints;
		this.previousPrayer = previousPrayer;
	}

	public void computeExpected(int specDamage)
	{
		this.expectedHitpoints = Math.max(10, specDamage / 2);
		this.expectedPrayer = Math.max(5, specDamage / 4);
	}

	public void computeSaved()
	{
		savedHitpoints = Math.min(actualHitpoints, expectedHitpoints);
		savedPrayer = Math.min(actualPrayer, expectedPrayer);
	}
}
