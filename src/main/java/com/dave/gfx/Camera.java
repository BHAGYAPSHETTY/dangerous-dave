package com.dave.gfx;

import com.dave.model.Dave;
import com.dave.model.Level;

/**
 * Horizontal tracking camera for Dangerous Dave.
 * Viewport is 320x160 pixels, smoothly following Dave within level boundaries.
 */
public class Camera {
    public static final int VIEWPORT_WIDTH = 320;
    public static final int VIEWPORT_HEIGHT = 160;

    private float x;
    private final float y;

    public Camera() {
        this.x = 0;
        this.y = 0;
    }

    public void update(Dave dave, Level level) {
        if (level == null || dave == null) return;

        float maxScrollX = Math.max(0, level.getPixelWidth() - VIEWPORT_WIDTH);
        if (maxScrollX == 0) {
            this.x = 0;
            return;
        }

        // Center camera horizontally on Dave with smooth lead
        float targetX = dave.getX() - (VIEWPORT_WIDTH / 2f) + (Dave.WIDTH / 2f);

        // Smooth dampening towards target
        this.x += (targetX - this.x) * 0.2f;

        // Clamp camera to map bounds
        if (this.x < 0) {
            this.x = 0;
        } else if (this.x > maxScrollX) {
            this.x = maxScrollX;
        }
    }

    public void reset() {
        this.x = 0;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
