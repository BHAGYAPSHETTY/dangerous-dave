package com.dave.model;

/**
 * Types of collectibles and special equipment pickups in Dangerous Dave.
 */
public enum CollectibleType {
    TROPHY(1000, "Gold Trophy"),
    BLUE_DIAMOND(100, "Blue Diamond"),
    RED_GEM(150, "Red Gem"),
    RING(200, "Golden Ring"),
    SCEPTRE(300, "Royal Sceptre"),
    CROWN(500, "Crown of Clyde"),
    GUN(100, "Hunting Gun"),
    JETPACK(100, "Jetpack");

    private final int points;
    private final String displayName;

    CollectibleType(int points, String displayName) {
        this.points = points;
        this.displayName = displayName;
    }

    public int getPoints() {
        return points;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEquipment() {
        return this == GUN || this == JETPACK;
    }
}
