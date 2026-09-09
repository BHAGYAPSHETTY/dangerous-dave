package com.dave.model;

import com.dave.physics.BoundingBox;

/**
 * An enemy creature in Dangerous Dave.
 */
public class Enemy {
    private final String id;
    private final EnemyType type;
    private final float startX;
    private final float startY;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private final BoundingBox bounds;

    // Movement path parameters (relative movement array or oscillation)
    private final int[] pathDeltasX;
    private final int[] pathDeltasY;
    private int pathIndex;
    private int stepCount;
    private final int maxStepsPerNode;

    // Shooting parameters
    private final int shootInterval;
    private int shootCooldown;

    // State flags
    private boolean alive;
    private boolean pointsAwarded;
    private int explosionTimer;
    private int animFrame;

    public Enemy(String id, EnemyType type, float startX, float startY, int[] pathDeltasX, int[] pathDeltasY, int shootInterval) {
        this.id = id;
        this.type = type;
        this.startX = startX;
        this.startY = startY;
        this.x = startX;
        this.y = startY;
        this.pathDeltasX = (pathDeltasX != null && pathDeltasX.length > 0) ? pathDeltasX : new int[]{ 1, -1 };
        this.pathDeltasY = (pathDeltasY != null && pathDeltasY.length > 0) ? pathDeltasY : new int[]{ 0, 0 };
        this.pathIndex = 0;
        this.stepCount = 0;
        this.maxStepsPerNode = 40; // 40 ticks per path segment
        this.shootInterval = shootInterval;
        this.shootCooldown = (int)(Math.random() * shootInterval); // staggered start

        this.bounds = new BoundingBox(x, y, type.getWidth(), type.getHeight());
        this.alive = true;
        this.pointsAwarded = false;
        this.explosionTimer = 0;
        this.animFrame = 0;
    }

    public void update(float playerX, float playerY, java.util.List<Projectile> levelProjectiles) {
        if (!alive) {
            if (explosionTimer > 0) {
                explosionTimer--;
            }
            return;
        }

        // Animation frame toggle
        animFrame = (animFrame + 1) % 60;

        // Path movement
        int dx = pathDeltasX[pathIndex];
        int dy = pathDeltasY[pathIndex];
        x += dx * 0.75f;
        y += dy * 0.75f;
        bounds.setPosition(x, y);

        stepCount++;
        if (stepCount >= maxStepsPerNode) {
            stepCount = 0;
            pathIndex = (pathIndex + 1) % pathDeltasX.length;
        }

        // Projectile firing logic
        if (shootInterval > 0 && levelProjectiles != null) {
            shootCooldown++;
            if (shootCooldown >= shootInterval) {
                shootCooldown = 0;
                // Only shoot if within 180 pixels of player horizontally
                float distX = playerX - x;
                float distY = playerY - y;
                if (Math.abs(distX) < 180f && Math.abs(distY) < 120f) {
                    float speed = 2.0f;
                    float angle = (float) Math.atan2(distY, distX);
                    float pvx = (float) Math.cos(angle) * speed;
                    float pvy = (float) Math.sin(angle) * speed;
                    levelProjectiles.add(new Projectile(x + type.getWidth() / 2f, y + type.getHeight() / 2f, pvx, pvy, false));
                }
            }
        }
    }

    /**
     * Kills the enemy and starts the explosion sequence.
     * @return points to award if not already awarded, 0 otherwise.
     */
    public int destroy() {
        if (!alive) return 0;
        alive = false;
        explosionTimer = 24; // 24 ticks of explosion
        if (!pointsAwarded) {
            pointsAwarded = true;
            return type.getPoints();
        }
        return 0;
    }

    public void reset() {
        this.x = startX;
        this.y = startY;
        this.bounds.setPosition(x, y);
        this.pathIndex = 0;
        this.stepCount = 0;
        this.shootCooldown = 0;
        this.alive = true;
        this.pointsAwarded = false;
        this.explosionTimer = 0;
    }

    public String getId() { return id; }
    public EnemyType getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public BoundingBox getBounds() { return bounds; }
    public boolean isAlive() { return alive; }
    public boolean isExploding() { return !alive && explosionTimer > 0; }
    public int getExplosionTimer() { return explosionTimer; }
    public int getAnimFrame() { return animFrame; }
}
