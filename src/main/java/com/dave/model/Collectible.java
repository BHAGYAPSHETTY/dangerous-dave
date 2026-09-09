package com.dave.model;

import com.dave.physics.BoundingBox;

/**
 * Represents a collectible item or pickup on the map.
 */
public class Collectible {
    private final String id;
    private final CollectibleType type;
    private final int tileX;
    private final int tileY;
    private final float x;
    private final float y;
    private final BoundingBox bounds;
    private boolean collected;

    public Collectible(CollectibleType type, int tileX, int tileY) {
        this.id = type.name() + "_" + tileX + "_" + tileY;
        this.type = type;
        this.tileX = tileX;
        this.tileY = tileY;
        this.x = tileX * 16f;
        this.y = tileY * 16f;
        // Collectible bounding box slightly inset for natural pickup feel
        this.bounds = new BoundingBox(x + 2f, y + 2f, 12f, 12f);
        this.collected = false;
    }

    public String getId() { return id; }
    public CollectibleType getType() { return type; }
    public int getTileX() { return tileX; }
    public int getTileY() { return tileY; }
    public float getX() { return x; }
    public float getY() { return y; }
    public BoundingBox getBounds() { return bounds; }
    public boolean isCollected() { return collected; }

    /**
     * Atomically marks the collectible as collected.
     * @return true if it was collected just now, false if it was ALREADY collected (prevents duplicate rewards).
     */
    public boolean collect() {
        if (collected) {
            return false;
        }
        this.collected = true;
        return true;
    }

    public void reset() {
        this.collected = false;
    }
}
