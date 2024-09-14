package com.sgssavingstracker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Stats
{
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	@Getter
	private int hitpoints = 0;
	@Getter
	private int prayer = 0;
	@Getter
	private int prayerLevel = 0;

	public void setHitpoints(int value)
	{
		int previous = this.hitpoints;
		this.hitpoints = value;
		support.firePropertyChange("hitpoints", previous, this.hitpoints);
	}

	public void setPrayer(int value)
	{
		int previous = this.prayer;
		this.prayer = value;
		support.firePropertyChange("prayer", previous, this.prayer);
	}

	public void setPrayerLevel(int value)
	{
		int previous = this.prayerLevel;
		this.prayerLevel = value;
		support.firePropertyChange("prayerLevel", previous, this.prayerLevel);
	}

	public void incrementHitpoints(int value)
	{
		int previous = this.hitpoints;
		this.hitpoints += value;
		support.firePropertyChange("hitpoints", previous, this.hitpoints);
	}

	public void incrementPrayer(int value)
	{
		int previous = this.prayer;
		this.prayer += value;
		support.firePropertyChange("prayer", previous, this.prayer);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}
}