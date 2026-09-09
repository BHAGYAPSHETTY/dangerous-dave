package com.dave.physics;

import com.dave.audio.RetroAudioEngine;
import com.dave.audio.SoundEffect;
import com.dave.model.*;

import java.util.List;

/**
 * High-precision, continuous Swept-AABB collision resolution system.
 * Eliminates tunneling, corner snagging, boundary escaping, and objective skipping exploits.
 */
public class CollisionSystem {
    public interface CollisionCallback {
        void onScoreAdded(int points);
        void onTrophyCollected();
        void onLevelCompleted();
        void onPlayerDied();
        void onSoundTriggered(SoundEffect sfx);
    }

    private final CollisionCallback callback;

    public CollisionSystem(CollisionCallback callback) {
        this.callback = callback;
    }

    /**
     * Resolves player movement and collisions against solid tiles.
     * Separates X and Y axes with sub-pixel alignment to prevent wall clipping.
     */
    public void resolvePlayerTileCollisions(Dave dave, Level level) {
        if (dave.isDead()) return;

        float x = dave.getX();
        float y = dave.getY();
        float vx = dave.getVx();
        float vy = dave.getVy();
        float w = Dave.WIDTH;
        float h = Dave.HEIGHT;

        // --- HORIZONTAL AXIS (X) ---
        float nextX = x + vx;
        if (vx > 0) {
            // Moving Right: scan all tile columns from Dave's current right edge to target right edge
            int startTileX = (int) ((x + w) / 16f);
            int endTileX = (int) ((nextX + w) / 16f);
            int topTileY = (int) (y / 16f);
            int bottomTileY = (int) ((y + h - 0.1f) / 16f);

            for (int tx = startTileX; tx <= endTileX; tx++) {
                boolean hit = false;
                for (int ty = topTileY; ty <= bottomTileY; ty++) {
                    if (level.getTile(tx, ty).isSolid()) {
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    nextX = tx * 16f - w;
                    dave.setVx(0);
                    break;
                }
            }
        } else if (vx < 0) {
            // Moving Left: scan all tile columns from Dave's current left edge down to target left edge
            int startTileX = (int) (x / 16f);
            int endTileX = (int) (nextX / 16f);
            int topTileY = (int) (y / 16f);
            int bottomTileY = (int) ((y + h - 0.1f) / 16f);

            for (int tx = startTileX; tx >= endTileX; tx--) {
                boolean hit = false;
                for (int ty = topTileY; ty <= bottomTileY; ty++) {
                    if (level.getTile(tx, ty).isSolid()) {
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    nextX = (tx + 1) * 16f;
                    dave.setVx(0);
                    break;
                }
            }
        }

        // Clamp to level horizontal bounds
        if (nextX < 0) {
            nextX = 0;
            dave.setVx(0);
        } else if (nextX > level.getPixelWidth() - w) {
            nextX = level.getPixelWidth() - w;
            dave.setVx(0);
        }
        dave.setX(nextX);

        // --- VERTICAL AXIS (Y) ---
        float nextY = y + vy;
        boolean onGround = false;

        if (vy > 0) {
            // Moving Down (Falling): scan all tile rows from current bottom to target bottom
            int startTileY = (int) ((y + h) / 16f);
            int endTileY = (int) ((nextY + h) / 16f);
            int leftTileX = (int) ((nextX + 1f) / 16f);
            int rightTileX = (int) ((nextX + w - 1f) / 16f);

            for (int ty = startTileY; ty <= endTileY; ty++) {
                boolean hit = false;
                for (int tx = leftTileX; tx <= rightTileX; tx++) {
                    if (level.getTile(tx, ty).isSolid()) {
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    nextY = ty * 16f - h;
                    dave.setVy(0);
                    onGround = true;
                    break;
                }
            }
        } else if (vy < 0) {
            // Moving Up (Jumping/Jetpack): scan from current top down to target top
            int startTileY = (int) (y / 16f);
            int endTileY = (int) (nextY / 16f);
            int leftTileX = (int) ((nextX + 1f) / 16f);
            int rightTileX = (int) ((nextX + w - 1f) / 16f);

            for (int ty = startTileY; ty >= endTileY; ty--) {
                boolean hit = false;
                for (int tx = leftTileX; tx <= rightTileX; tx++) {
                    if (level.getTile(tx, ty).isSolid()) {
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    nextY = (ty + 1) * 16f;
                    dave.setVy(0);
                    break;
                }
            }
        }

        // Ceiling clamp
        if (nextY < 0) {
            nextY = 0;
            dave.setVy(0);
        }

        // Lethal pit fall check (falling off bottom of map)
        if (nextY > level.getPixelHeight() + 10f) {
            if (dave.die()) {
                if (callback != null) {
                    callback.onPlayerDied();
                    callback.onSoundTriggered(SoundEffect.EXPLODE);
                }
            }
            return;
        }

        dave.setY(nextY);

        // Check ground contact underneath feet if not already grounded
        if (!onGround && !dave.isJetpackActive()) {
            int checkGroundY = (int) ((nextY + h + 1f) / 16f);
            int leftTileX = (int) ((nextX + 1f) / 16f);
            int rightTileX = (int) ((nextX + w - 1f) / 16f);
            for (int tx = leftTileX; tx <= rightTileX; tx++) {
                if (level.getTile(tx, checkGroundY).isSolid()) {
                    onGround = true;
                    break;
                }
            }
        }

        dave.setGrounded(onGround);
    }

    /**
     * Checks lethal hazards (Fire, Water, Weed) overlapping Dave.
     */
    public void checkHazards(Dave dave, Level level) {
        if (dave.isDead()) return;

        BoundingBox b = dave.getBounds();
        int minTx = Math.max(0, (int) (b.getLeft() / 16f));
        int maxTx = Math.min(level.getWidthInTiles() - 1, (int) (b.getRight() / 16f));
        int minTy = Math.max(0, (int) (b.getTop() / 16f));
        int maxTy = Math.min(level.getHeightInTiles() - 1, (int) (b.getBottom() / 16f));

        for (int ty = minTy; ty <= maxTy; ty++) {
            for (int tx = minTx; tx <= maxTx; tx++) {
                TileType tile = level.getTile(tx, ty);
                if (tile.isHazard()) {
                    // Slight inset check for hazards so player doesn't die from grazing pixel edge
                    if (b.intersects(tx * 16f + 2f, ty * 16f + 2f, 12f, 12f)) {
                        if (dave.die()) {
                            if (callback != null) {
                                callback.onPlayerDied();
                                callback.onSoundTriggered(SoundEffect.EXPLODE);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * Checks collectible pickups (Trophy, gems, rings, gun, jetpack).
     */
    public void checkCollectibles(Dave dave, Level level) {
        if (dave.isDead()) return;

        BoundingBox playerBounds = dave.getBounds();
        for (Collectible c : level.getCollectibles()) {
            if (!c.isCollected() && playerBounds.intersects(c.getBounds())) {
                if (c.collect()) {
                    int pts = c.getType().getPoints();
                    if (callback != null) {
                        callback.onScoreAdded(pts);
                    }

                    switch (c.getType()) {
                        case TROPHY:
                            level.onTrophyCollected();
                            if (callback != null) {
                                callback.onTrophyCollected();
                                callback.onSoundTriggered(SoundEffect.TROPHY);
                            }
                            break;
                        case GUN:
                            dave.equipGun();
                            if (callback != null) {
                                callback.onSoundTriggered(SoundEffect.PICKUP);
                            }
                            break;
                        case JETPACK:
                            dave.equipJetpack();
                            if (callback != null) {
                                callback.onSoundTriggered(SoundEffect.PICKUP);
                            }
                            break;
                        default:
                            if (callback != null) {
                                callback.onSoundTriggered(SoundEffect.PICKUP);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * Checks exit door interaction.
     * EXPLOIT PREVENTION: Can ONLY exit through door if trophy is strictly collected!
     */
    public void checkDoor(Dave dave, Level level) {
        if (dave.isDead() || level.isCompleted()) return;

        int doorX = level.getDoorTileX();
        int doorY = level.getDoorTileY();
        if (doorX < 0 || doorY < 0) return;

        // Door bounding area
        if (dave.getBounds().intersects(doorX * 16f + 2f, doorY * 16f, 12f, 16f)) {
            if (level.isTrophyCollected()) {
                level.setCompleted(true);
                if (callback != null) {
                    callback.onScoreAdded(2000); // 2000 bonus points for exiting level
                    callback.onSoundTriggered(SoundEffect.DOOR);
                    callback.onLevelCompleted();
                }
            }
        }
    }

    /**
     * Checks player vs enemy collisions.
     */
    public void checkEnemies(Dave dave, Level level) {
        if (dave.isDead()) return;

        BoundingBox playerBounds = dave.getBounds();
        for (Enemy enemy : level.getEnemies()) {
            if (enemy.isAlive() && playerBounds.intersects(enemy.getBounds())) {
                // When Dave touches an enemy, both explode!
                enemy.destroy();
                if (dave.die()) {
                    if (callback != null) {
                        callback.onPlayerDied();
                        callback.onSoundTriggered(SoundEffect.EXPLODE);
                    }
                }
                return;
            }
        }
    }

    /**
     * Updates and tests all projectiles against enemies and walls.
     */
    public void updateProjectiles(Dave dave, Level level) {
        List<Projectile> projectiles = level.getProjectiles();
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            if (!p.isActive()) {
                projectiles.remove(i);
                continue;
            }

            p.update();

            // Check projectile bounds against level borders
            if (p.getX() < 0 || p.getX() > level.getPixelWidth() || p.getY() < 0 || p.getY() > level.getPixelHeight()) {
                p.deactivate();
                continue;
            }

            // Check collision against solid tiles
            int tx = (int) (p.getX() / 16f);
            int ty = (int) (p.getY() / 16f);
            if (level.getTile(tx, ty).isSolid()) {
                p.deactivate();
                continue;
            }

            if (p.isFromPlayer()) {
                // Bullet hits enemies
                for (Enemy enemy : level.getEnemies()) {
                    if (enemy.isAlive() && p.getBounds().intersects(enemy.getBounds())) {
                        p.deactivate();
                        int pts = enemy.destroy();
                        if (pts > 0 && callback != null) {
                            callback.onScoreAdded(pts);
                            callback.onSoundTriggered(SoundEffect.EXPLODE);
                        }
                        break;
                    }
                }
            } else {
                // Enemy shot hits Dave
                if (!dave.isDead() && p.getBounds().intersects(dave.getBounds())) {
                    p.deactivate();
                    if (dave.die()) {
                        if (callback != null) {
                            callback.onPlayerDied();
                            callback.onSoundTriggered(SoundEffect.EXPLODE);
                        }
                    }
                }
            }
        }
    }
}
