package com.sgssavingstracker;

import lombok.Getter;
import lombok.Setter;

public class RestoreOccurrence
{
	@Getter
	private final int specTick;
	@Getter
	private final int previousHp;
	@Getter
	private final int previousPp;

	private int expectedHp;
	private int expectedPp;
	@Setter
	private int actualHp;
	@Setter
	private int actualPp;
	@Getter
	private int savedHp;
	@Getter
	private int savedPp;

	public RestoreOccurrence(int specTick, int previousHp, int previousPp)
	{
		this.specTick = specTick;
		this.previousHp = previousHp;
		this.previousPp = previousPp;
	}

	public void computeExpected(int specDamage)
	{
		this.expectedHp = Math.max(10, specDamage / 2);
		this.expectedPp = Math.max(5, specDamage / 4);
	}

	public void computeSaved()
	{
		savedHp = Math.min(actualHp, expectedHp);
		savedPp = Math.min(actualPp, expectedPp);
	}
}
