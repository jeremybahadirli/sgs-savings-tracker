package com.sgssavingstracker;

import lombok.Getter;
import lombok.Setter;

public class Restore {
    @Getter
    private int specTick;
    @Getter
    private int previousHitpoints;
    @Getter
    private int previousPrayer;

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

    public Restore(int specTick, int previousHitpoints, int previousPrayer) {
        this.specTick = specTick;
        this.previousHitpoints = previousHitpoints;
        this.previousPrayer = previousPrayer;
    }

    public void computeExpected(int specDamage) {
        this.expectedHitpoints = Math.max(10, specDamage / 2);
        this.expectedPrayer = Math.max(5, specDamage / 4);
    }

    public void computeSaved() {
        savedHitpoints = Math.min(actualHitpoints, expectedHitpoints);
        savedPrayer = Math.min(actualPrayer, expectedPrayer);

        System.out.println("Hitpoints\t--- Actual: " + actualHitpoints + ", Expected: " + expectedHitpoints + ", Saved: " + savedHitpoints);
        System.out.println("Prayer\t--- Actual: " + actualPrayer + ", Expected: " + expectedPrayer + ", Saved: " + savedPrayer);
    }
}
