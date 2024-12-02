package com.sgssavingstracker;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RestoreOccurrence
{
	private final int specTick;
	private final int previousHp;
	private final int previousPp;

	private int expectedHp;
	private int expectedPp;
	@Setter
	private int actualHp;
	@Setter
	private int actualPp;
	private int savedHp;
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
