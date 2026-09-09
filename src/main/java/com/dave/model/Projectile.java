package com.dave.model;

import com.dave.physics.BoundingBox;

/**
 * Bullet or enemy projectile in flight.
 */
public class Projectile {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private final boolean fromPlayer;
    private boolean active;
    private final BoundingBox bounds;

    public Projectile(float x, float y, float vx, float vy, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.fromPlayer = fromPlayer;
        this.active = true;
        // Projectile size: 6x4 for bullets, 6x6 for enemy orbs
        float w = fromPlayer ? 6f : 6f;
        float h = fromPlayer ? 4f : 6f;
        this.bounds = new BoundingBox(x, y, w, h);
    }

    public void update() {
        if (!active) return;
        x += vx;
        y += vy;
        bounds.setPosition(x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public boolean isFromPlayer() { return fromPlayer; }
    public boolean isActive() { return active; }
    public BoundingBox getBounds() { return bounds; }

    public void deactivate() {
        this.active = false;
    }
}
