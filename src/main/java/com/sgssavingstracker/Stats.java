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
	private int prayerLevel = 0;

	public void setHpSaved(int value)
	{
		int previous = this.hpSaved;
		this.hpSaved = value;
		support.firePropertyChange("hp", previous, this.hpSaved);
	}

	public void incrementHpSaved(int value)
	{
		int previous = this.hpSaved;
		this.hpSaved += value;
		support.firePropertyChange("hp", previous, this.hpSaved);
	}

	public void setPpSaved(int value)
	{
		int previous = this.ppSaved;
		this.ppSaved = value;
		support.firePropertyChange("pp", previous, this.ppSaved);
	}

	public void incrementPpSaved(int value)
	{
		int previous = this.ppSaved;
		this.ppSaved += value;
		support.firePropertyChange("pp", previous, this.ppSaved);
	}

	public void setPrayerLevel(int value)
	{
		int previous = this.prayerLevel;
		this.prayerLevel = value;
		support.firePropertyChange("prayerLevel", previous, this.prayerLevel);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}
}