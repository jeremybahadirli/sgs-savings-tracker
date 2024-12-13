package com.sgssavingstracker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Stats
{
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	@Setter
	private int specPercent = 0;
	private int hpSaved = 0;
	private int ppSaved = 0;
	private int hitpointsLevel = 0;
	private int prayerLevel = 0;

	public void setHpSaved(int value)
	{
		int previous = hpSaved;
		hpSaved = value;
		support.firePropertyChange("hp", previous, hpSaved);
	}

	public void incrementHpSaved(int value)
	{
		int previous = hpSaved;
		hpSaved += value;
		support.firePropertyChange("hp", previous, hpSaved);
	}

	public void setPpSaved(int value)
	{
		int previous = ppSaved;
		ppSaved = value;
		support.firePropertyChange("pp", previous, ppSaved);
	}

	public void incrementPpSaved(int value)
	{
		int previous = ppSaved;
		ppSaved += value;
		support.firePropertyChange("pp", previous, ppSaved);
	}

	public void setHitpointsLevel(int value)
	{
		if (value == hitpointsLevel)
		{
			return;
		}

		int previous = hitpointsLevel;
		hitpointsLevel = value;
		support.firePropertyChange("hitpointsLevel", previous, hitpointsLevel);
	}

	public void setPrayerLevel(int value)
	{
		if (value == prayerLevel)
		{
			return;
		}

		int previous = prayerLevel;
		prayerLevel = value;
		support.firePropertyChange("prayerLevel", previous, prayerLevel);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}
}