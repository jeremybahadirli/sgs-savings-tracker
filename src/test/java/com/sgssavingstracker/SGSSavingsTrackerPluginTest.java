package com.sgssavingstracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SGSSavingsTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SGSSavingsTrackerPlugin.class);
		RuneLite.main(args);
	}
}