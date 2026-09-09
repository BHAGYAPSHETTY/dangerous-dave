package com.dave.model;

/**
 * The authentic enemy creature types across the 10 levels of Dangerous Dave.
 */
public enum EnemyType {
    SPIDER(1, 100, 20, 16, "Spider"),                 // Level 3
    SPINNER(2, 150, 16, 16, "Spiky Spinner"),         // Level 4
    SUN(3, 200, 18, 18, "Fiery Sun"),                // Level 5
    SKULL(4, 200, 16, 16, "Bouncing Skull"),          // Level 6
    UFO(5, 300, 20, 16, "Alien UFO"),                 // Level 7
    EYE_DEMON(6, 300, 18, 18, "Floating Eye"),        // Level 8
    RED_DEMON(7, 400, 20, 18, "Red Demon"),           // Level 9
    CLYDE_GUARDIAN(8, 500, 24, 20, "Clyde Guardian"); // Level 10

    private final int id;
    private final int points;
    private final float width;
    private final float height;
    private final String displayName;

    EnemyType(int id, int points, float width, float height, String displayName) {
        this.id = id;
        this.points = points;
        this.width = width;
        this.height = height;
        this.displayName = displayName;
    }

    public int getId() { return id; }
    public int getPoints() { return points; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public String getDisplayName() { return displayName; }
}
