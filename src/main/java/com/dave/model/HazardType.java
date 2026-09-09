package com.dave.model;

/**
 * Types of instant-death hazards in Dangerous Dave.
 */
public enum HazardType {
    FIRE("Fire Pit"),
    WATER("Water Pool"),
    WEED("Purple Weed");

    private final String displayName;

    HazardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
