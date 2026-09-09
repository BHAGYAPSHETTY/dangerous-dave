package com.dave.physics;

/**
 * Axis-Aligned Bounding Box (AABB) for high-precision 2D collision detection.
 */
public class BoundingBox {
    private float x;
    private float y;
    private float width;
    private float height;

    public BoundingBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getLeft() { return x; }
    public float getRight() { return x + width; }
    public float getTop() { return y; }
    public float getBottom() { return y + height; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Standard AABB intersection test with open boundaries.
     */
    public boolean intersects(BoundingBox other) {
        return this.getLeft() < other.getRight() &&
               this.getRight() > other.getLeft() &&
               this.getTop() < other.getBottom() &&
               this.getBottom() > other.getTop();
    }

    /**
     * Checks if this bounding box intersects a specific rectangle.
     */
    public boolean intersects(float rx, float ry, float rw, float rh) {
        return this.getLeft() < rx + rw &&
               this.getRight() > rx &&
               this.getTop() < ry + rh &&
               this.getBottom() > ry;
    }

    /**
     * Checks if a point is contained inside this bounding box.
     */
    public boolean contains(float px, float py) {
        return px >= x && px <= x + width &&
               py >= y && py <= y + height;
    }

    @Override
    public String toString() {
        return String.format("BoundingBox[x=%.1f, y=%.1f, w=%.1f, h=%.1f]", x, y, width, height);
    }
}
