package com.dave.model;

/**
 * Definition of tile types in Dangerous Dave.
 */
public enum TileType {
    EMPTY(false, false, null),
    BRICK(true, false, null),             // Standard red brick wall / floor
    PIPE(true, false, null),              // Blue industrial pipes
    GIRDER(true, false, null),            // Metal platform / girder
    TREE(true, false, null),              // Inverted tree block (Level 2)
    STAR(false, false, null),             // Background star decorative block
    DOOR_CLOSED(true, false, null),       // Exit door when locked
    DOOR_OPEN(false, false, null),        // Exit door when unlocked ("GO THRU THE DOOR!")
    FIRE(false, true, HazardType.FIRE),   // Animated fire pit hazard
    WATER(false, true, HazardType.WATER), // Water / slime hazard
    WEED(false, true, HazardType.WEED);   // Purple weed / spike hazard

    private final boolean solid;
    private final boolean hazard;
    private final HazardType hazardType;

    TileType(boolean solid, boolean hazard, HazardType hazardType) {
        this.solid = solid;
        this.hazard = hazard;
        this.hazardType = hazardType;
    }

    public boolean isSolid() { return solid; }
    public boolean isHazard() { return hazard; }
    public HazardType getHazardType() { return hazardType; }

    public boolean isDoor() {
        return this == DOOR_CLOSED || this == DOOR_OPEN;
    }
}
