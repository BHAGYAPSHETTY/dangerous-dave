package com.dave.test;

import com.dave.model.*;
import com.dave.physics.BoundingBox;
import com.dave.physics.CollisionSystem;

public class CollisionTest {
    public static void runAll() {
        testBoundingBoxIntersections();
        testWallCollisionNoClipping();
        testHazardCollision();
        testMapBoundaryClamping();
        System.out.println("  [PASS] CollisionTest - All tests passed.");
    }

    private static void testBoundingBoxIntersections() {
        BoundingBox b1 = new BoundingBox(10, 10, 16, 16);
        BoundingBox b2 = new BoundingBox(20, 20, 16, 16); // Overlaps b1
        BoundingBox b3 = new BoundingBox(50, 50, 16, 16); // Far away

        if (!b1.intersects(b2)) {
            throw new AssertionError("b1 and b2 should intersect!");
        }
        if (b1.intersects(b3)) {
            throw new AssertionError("b1 and b3 should NOT intersect!");
        }
        if (!b1.contains(15, 15)) {
            throw new AssertionError("b1 should contain point (15, 15)");
        }
    }

    private static void testWallCollisionNoClipping() {
        Level level = new Level(1, "Test Level", 20, 10, 2, 8);
        level.setTile(5, 8, TileType.BRICK); // Solid wall at x=80..95, y=128..143

        Dave dave = new Dave(70, 8 * 16f);
        dave.setVx(15.0f); // High velocity rightward into wall

        CollisionSystem cs = new CollisionSystem(null);
        cs.resolvePlayerTileCollisions(dave, level);

        // Dave should be stopped flush with the left side of the brick wall (x = 80 - 14 = 66)
        if (dave.getX() > 66.01f) {
            throw new AssertionError("Wall clipping detected! Dave x=" + dave.getX() + " penetrated wall at x=80");
        }
        if (dave.getVx() != 0) {
            throw new AssertionError("Horizontal velocity should be zeroed upon wall collision!");
        }
    }

    private static void testHazardCollision() {
        Level level = new Level(1, "Hazard Test", 20, 10, 2, 8);
        level.setTile(4, 8, TileType.FIRE); // Fire pit at x=64, y=128

        Dave dave = new Dave(64, 8 * 16f);
        boolean[] died = new boolean[1];

        CollisionSystem cs = new CollisionSystem(new CollisionSystem.CollisionCallback() {
            @Override public void onScoreAdded(int points) {}
            @Override public void onTrophyCollected() {}
            @Override public void onLevelCompleted() {}
            @Override public void onPlayerDied() { died[0] = true; }
            @Override public void onSoundTriggered(com.dave.audio.SoundEffect sfx) {}
        });

        cs.checkHazards(dave, level);

        if (!dave.isDead() || !died[0]) {
            throw new AssertionError("Dave should die upon touching fire hazard!");
        }
    }

    private static void testMapBoundaryClamping() {
        Level level = new Level(1, "Bounds Test", 20, 10, 2, 8);
        Dave dave = new Dave(5, 8 * 16f);
        dave.setVx(-50.0f); // Massive move to the left

        CollisionSystem cs = new CollisionSystem(null);
        cs.resolvePlayerTileCollisions(dave, level);

        if (dave.getX() < 0) {
            throw new AssertionError("Dave escaped left boundary: x=" + dave.getX());
        }
    }
}
