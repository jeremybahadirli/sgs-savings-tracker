package com.sgssavingstracker;

import lombok.Getter;
import lombok.Setter;

public class RestoreOccurrence
{
	@Getter
	private final int specTick;
	@Getter
	private final int previousHitpoints;
	@Getter
	private final int previousPrayer;

	@Setter
	private int actualHitpoints;
	@Setter
	private int actualPrayer;
	private int expectedHitpoints;
	private int expectedPrayer;
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
		// TODO: Delete logs
		System.out.println("Hitpoints\t--- Actual: " + actualHitpoints + ", Expected: " + expectedHitpoints + ", Saved: " + savedHitpoints);
		System.out.println("Prayer\t--- Actual: " + actualPrayer + ", Expected: " + expectedPrayer + ", Saved: " + savedPrayer);
	}
}
