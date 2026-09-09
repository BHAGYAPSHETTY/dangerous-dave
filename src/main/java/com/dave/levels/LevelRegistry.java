package com.dave.levels;

import com.dave.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry and builder for all 10 authentic levels of Dangerous Dave.
 * Includes platforms, hazards, collectible loot, gun/jetpack equipment, and monster encounters.
 */
public class LevelRegistry {

    public static int getTotalLevelCount() {
        return 10;
    }

    public static Level buildLevel(int levelNumber) {
        switch (levelNumber) {
            case 1:  return buildLevel1();
            case 2:  return buildLevel2();
            case 3:  return buildLevel3();
            case 4:  return buildLevel4();
            case 5:  return buildLevel5();
            case 6:  return buildLevel6();
            case 7:  return buildLevel7();
            case 8:  return buildLevel8();
            case 9:  return buildLevel9();
            case 10: return buildLevel10();
            default: return buildLevel1();
        }
    }

    /**
     * Level 1: The Training Grounds
     * Single screen (20x10). Brick platforms, central fire pit, trophy at top, door at bottom right.
     */
    private static Level buildLevel1() {
        Level lvl = new Level(1, "LEVEL 1: THE HIDEOUT ENTRANCE", 20, 10, 2, 8);

        // Surrounding border
        fillHorizontal(lvl, 0, 19, 0, TileType.BRICK); // Top ceiling
        fillHorizontal(lvl, 0, 7, 9, TileType.BRICK);  // Left floor
        fillHorizontal(lvl, 8, 12, 9, TileType.FIRE);  // Central Fire Pit
        fillHorizontal(lvl, 13, 19, 9, TileType.BRICK); // Right floor
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);    // Left wall
        fillVertical(lvl, 19, 0, 9, TileType.BRICK);   // Right wall

        // Platforms
        fillHorizontal(lvl, 2, 6, 7, TileType.BRICK);
        fillHorizontal(lvl, 7, 12, 5, TileType.BRICK);
        fillHorizontal(lvl, 2, 7, 3, TileType.BRICK);
        fillHorizontal(lvl, 13, 17, 4, TileType.BRICK);
        fillHorizontal(lvl, 14, 18, 7, TileType.BRICK);

        // Door at bottom right
        lvl.setTile(18, 8, TileType.DOOR_CLOSED);

        // Collectibles
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 3, 2));       // Main Trophy
        lvl.addCollectible(new Collectible(CollectibleType.BLUE_DIAMOND, 4, 6)); // Diamond
        lvl.addCollectible(new Collectible(CollectibleType.BLUE_DIAMOND, 5, 6));
        lvl.addCollectible(new Collectible(CollectibleType.RED_GEM, 9, 4));      // Ruby
        lvl.addCollectible(new Collectible(CollectibleType.RED_GEM, 10, 4));
        lvl.addCollectible(new Collectible(CollectibleType.RING, 15, 3));         // Ring
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 16, 6));        // Crown

        return lvl;
    }

    /**
     * Level 2: The Inverted Forest
     * 30x10. Upside down trees, starry night sky, water pools.
     */
    private static Level buildLevel2() {
        Level lvl = new Level(2, "LEVEL 2: INVERTED FOREST", 30, 10, 1, 8);

        // Border
        fillHorizontal(lvl, 0, 29, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 29, 0, 9, TileType.BRICK);

        // Floor with water hazards
        fillHorizontal(lvl, 0, 8, 9, TileType.BRICK);
        fillHorizontal(lvl, 9, 13, 9, TileType.WATER);  // Water pit 1
        fillHorizontal(lvl, 14, 19, 9, TileType.BRICK);
        fillHorizontal(lvl, 20, 24, 9, TileType.WATER); // Water pit 2
        fillHorizontal(lvl, 25, 29, 9, TileType.BRICK);

        // Hanging trees and floating platforms
        for (int x = 3; x < 28; x += 4) {
            lvl.setTile(x, 1, TileType.TREE);
            lvl.setTile(x, 2, TileType.TREE);
        }

        fillHorizontal(lvl, 4, 8, 7, TileType.GIRDER);
        fillHorizontal(lvl, 10, 14, 5, TileType.GIRDER);
        fillHorizontal(lvl, 16, 21, 6, TileType.GIRDER);
        fillHorizontal(lvl, 22, 27, 4, TileType.GIRDER);
        fillHorizontal(lvl, 11, 16, 3, TileType.GIRDER);

        // Stars in background
        lvl.setTile(6, 4, TileType.STAR);
        lvl.setTile(15, 2, TileType.STAR);
        lvl.setTile(24, 2, TileType.STAR);

        // Door & Trophy
        lvl.setTile(28, 8, TileType.DOOR_CLOSED);
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 13, 2));

        // Loot
        lvl.addCollectible(new Collectible(CollectibleType.RED_GEM, 6, 6));
        lvl.addCollectible(new Collectible(CollectibleType.RING, 12, 4));
        lvl.addCollectible(new Collectible(CollectibleType.SCEPTRE, 18, 5));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 24, 3));
        lvl.addCollectible(new Collectible(CollectibleType.BLUE_DIAMOND, 27, 8));

        return lvl;
    }

    /**
     * Level 3: Spider Cavern
     * 35x10. Introduces the GUN pickup and Spider monsters!
     */
    private static Level buildLevel3() {
        Level lvl = new Level(3, "LEVEL 3: SPIDER CAVERN", 35, 10, 2, 5);

        // Border & floor
        fillHorizontal(lvl, 0, 34, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 34, 0, 9, TileType.BRICK);

        fillHorizontal(lvl, 0, 10, 9, TileType.BRICK);
        fillHorizontal(lvl, 11, 15, 9, TileType.WATER);
        fillHorizontal(lvl, 16, 25, 9, TileType.BRICK);
        fillHorizontal(lvl, 26, 30, 9, TileType.WATER);
        fillHorizontal(lvl, 31, 34, 9, TileType.BRICK);

        // Platforms
        fillHorizontal(lvl, 1, 5, 6, TileType.BRICK);
        fillHorizontal(lvl, 7, 12, 5, TileType.BRICK);
        fillHorizontal(lvl, 14, 20, 4, TileType.BRICK);
        fillHorizontal(lvl, 22, 28, 6, TileType.BRICK);
        fillHorizontal(lvl, 29, 33, 4, TileType.BRICK);

        // Gun pickup right at the beginning!
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 4, 5));

        // Enemies: Spiders patrolling vertical / horizontal paths
        lvl.addEnemy(new Enemy("spider_1", EnemyType.SPIDER, 16 * 16f, 3 * 16f, new int[]{1, -1}, new int[]{0, 0}, 90));
        lvl.addEnemy(new Enemy("spider_2", EnemyType.SPIDER, 25 * 16f, 4 * 16f, new int[]{0, 0}, new int[]{1, -1}, 75));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 31, 3));
        lvl.setTile(33, 8, TileType.DOOR_CLOSED);

        // Treasures
        lvl.addCollectible(new Collectible(CollectibleType.BLUE_DIAMOND, 9, 4));
        lvl.addCollectible(new Collectible(CollectibleType.RED_GEM, 17, 3));
        lvl.addCollectible(new Collectible(CollectibleType.RING, 24, 5));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 18, 8));

        return lvl;
    }

    /**
     * Level 4: The Pipeworks
     * 36x10. Blue industrial pipes, spiky spinners, tight platforming.
     */
    private static Level buildLevel4() {
        Level lvl = new Level(4, "LEVEL 4: THE PIPEWORKS", 36, 10, 1, 5);

        fillHorizontal(lvl, 0, 35, 0, TileType.PIPE);
        fillVertical(lvl, 0, 0, 9, TileType.PIPE);
        fillVertical(lvl, 35, 0, 9, TileType.PIPE);

        fillHorizontal(lvl, 0, 6, 9, TileType.PIPE);
        fillHorizontal(lvl, 7, 12, 9, TileType.WEED);
        fillHorizontal(lvl, 13, 20, 9, TileType.PIPE);
        fillHorizontal(lvl, 21, 27, 9, TileType.WEED);
        fillHorizontal(lvl, 28, 35, 9, TileType.PIPE);

        // Pipe obstacles
        fillHorizontal(lvl, 1, 4, 6, TileType.PIPE);
        fillHorizontal(lvl, 6, 10, 4, TileType.PIPE);
        fillHorizontal(lvl, 12, 17, 6, TileType.PIPE);
        fillHorizontal(lvl, 19, 23, 4, TileType.PIPE);
        fillHorizontal(lvl, 25, 30, 6, TileType.PIPE);
        fillHorizontal(lvl, 28, 33, 3, TileType.PIPE);

        // Gun pickup
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 8, 3));

        // Spiky Spinner monsters
        lvl.addEnemy(new Enemy("spinner_1", EnemyType.SPINNER, 14 * 16f, 4 * 16f, new int[]{1, -1}, new int[]{1, -1}, 60));
        lvl.addEnemy(new Enemy("spinner_2", EnemyType.SPINNER, 26 * 16f, 4 * 16f, new int[]{-1, 1}, new int[]{1, -1}, 60));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 31, 2));
        lvl.setTile(34, 8, TileType.DOOR_CLOSED);

        // Treasures
        lvl.addCollectible(new Collectible(CollectibleType.RED_GEM, 3, 5));
        lvl.addCollectible(new Collectible(CollectibleType.SCEPTRE, 15, 5));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 21, 3));

        return lvl;
    }

    /**
     * Level 5: The Molten Core
     * 40x10. Introduces the JETPACK! Fiery suns and massive lava pits.
     */
    private static Level buildLevel5() {
        Level lvl = new Level(5, "LEVEL 5: THE MOLTEN CORE", 40, 10, 2, 8);

        fillHorizontal(lvl, 0, 39, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 39, 0, 9, TileType.BRICK);

        fillHorizontal(lvl, 0, 5, 9, TileType.BRICK);
        fillHorizontal(lvl, 6, 32, 9, TileType.FIRE); // Giant lava pit! Must fly!
        fillHorizontal(lvl, 33, 39, 9, TileType.BRICK);

        // Starting ledge
        fillHorizontal(lvl, 1, 4, 7, TileType.BRICK);

        // Jetpack pickup on starting platform!
        lvl.addCollectible(new Collectible(CollectibleType.JETPACK, 3, 6));

        // Floating island stepping stones
        fillHorizontal(lvl, 10, 13, 5, TileType.BRICK);
        fillHorizontal(lvl, 20, 23, 4, TileType.BRICK);
        fillHorizontal(lvl, 28, 31, 6, TileType.BRICK);

        // Fiery Sun enemies hovering over lava
        lvl.addEnemy(new Enemy("sun_1", EnemyType.SUN, 11 * 16f, 3 * 16f, new int[]{1, -1}, new int[]{0, 0}, 80));
        lvl.addEnemy(new Enemy("sun_2", EnemyType.SUN, 21 * 16f, 2 * 16f, new int[]{0, 0}, new int[]{1, -1}, 70));
        lvl.addEnemy(new Enemy("sun_3", EnemyType.SUN, 29 * 16f, 4 * 16f, new int[]{-1, 1}, new int[]{1, -1}, 65));

        // High trophy perched near ceiling
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 21, 1));

        // Door at exit
        lvl.setTile(37, 8, TileType.DOOR_CLOSED);

        // High value gems
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 12, 4));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 29, 5));
        lvl.addCollectible(new Collectible(CollectibleType.RING, 35, 7));

        return lvl;
    }

    /**
     * Level 6: The Crypt of Skulls
     * 38x10. Bouncing skulls, poisonous weed floor, vertical tiers.
     */
    private static Level buildLevel6() {
        Level lvl = new Level(6, "LEVEL 6: CRYPT OF SKULLS", 38, 10, 2, 8);

        fillHorizontal(lvl, 0, 37, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 37, 0, 9, TileType.BRICK);

        fillHorizontal(lvl, 0, 4, 9, TileType.BRICK);
        fillHorizontal(lvl, 5, 32, 9, TileType.WEED);
        fillHorizontal(lvl, 33, 37, 9, TileType.BRICK);

        // Multi-tier stone ledges
        fillHorizontal(lvl, 2, 7, 7, TileType.BRICK);
        fillHorizontal(lvl, 8, 13, 5, TileType.BRICK);
        fillHorizontal(lvl, 15, 20, 3, TileType.BRICK);
        fillHorizontal(lvl, 22, 27, 5, TileType.BRICK);
        fillHorizontal(lvl, 28, 33, 7, TileType.BRICK);

        // Gun & Jetpack equipment
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 9, 4));
        lvl.addCollectible(new Collectible(CollectibleType.JETPACK, 24, 4));

        // Bouncing Skull creatures
        lvl.addEnemy(new Enemy("skull_1", EnemyType.SKULL, 10 * 16f, 3 * 16f, new int[]{1, -1}, new int[]{1, -1}, 60));
        lvl.addEnemy(new Enemy("skull_2", EnemyType.SKULL, 17 * 16f, 1 * 16f, new int[]{-1, 1}, new int[]{0, 0}, 50));
        lvl.addEnemy(new Enemy("skull_3", EnemyType.SKULL, 30 * 16f, 5 * 16f, new int[]{0, 0}, new int[]{1, -1}, 55));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 17, 2));
        lvl.setTile(35, 8, TileType.DOOR_CLOSED);

        // Loot
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 5, 6));
        lvl.addCollectible(new Collectible(CollectibleType.SCEPTRE, 19, 2));
        lvl.addCollectible(new Collectible(CollectibleType.RING, 31, 6));

        return lvl;
    }

    /**
     * Level 7: Alien Incursion
     * 42x10. Floating steel girders, UFO enemies firing energy bolts.
     */
    private static Level buildLevel7() {
        Level lvl = new Level(7, "LEVEL 7: ALIEN INCURSION", 42, 10, 1, 2);

        fillHorizontal(lvl, 0, 41, 0, TileType.PIPE);
        fillVertical(lvl, 0, 0, 9, TileType.PIPE);
        fillVertical(lvl, 41, 0, 9, TileType.PIPE);

        fillHorizontal(lvl, 0, 41, 9, TileType.WATER); // Entire bottom floor is acid water!

        // Suspended girder platforms
        fillHorizontal(lvl, 1, 5, 3, TileType.GIRDER);
        fillHorizontal(lvl, 7, 12, 5, TileType.GIRDER);
        fillHorizontal(lvl, 14, 19, 3, TileType.GIRDER);
        fillHorizontal(lvl, 21, 26, 6, TileType.GIRDER);
        fillHorizontal(lvl, 28, 33, 4, TileType.GIRDER);
        fillHorizontal(lvl, 35, 39, 2, TileType.GIRDER);
        fillHorizontal(lvl, 36, 40, 8, TileType.GIRDER);

        // Gun pickup
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 3, 2));

        // UFO enemies
        lvl.addEnemy(new Enemy("ufo_1", EnemyType.UFO, 9 * 16f, 3 * 16f, new int[]{1, -1}, new int[]{0, 0}, 50));
        lvl.addEnemy(new Enemy("ufo_2", EnemyType.UFO, 23 * 16f, 4 * 16f, new int[]{1, -1}, new int[]{1, -1}, 45));
        lvl.addEnemy(new Enemy("ufo_3", EnemyType.UFO, 30 * 16f, 2 * 16f, new int[]{-1, 1}, new int[]{0, 0}, 40));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 37, 1));
        lvl.setTile(39, 7, TileType.DOOR_CLOSED);

        // Loot
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 9, 4));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 16, 2));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 31, 3));

        return lvl;
    }

    /**
     * Level 8: The Eye of Clyde
     * 45x10. Floating Eye demons, chasm of spikes, high crown caches.
     */
    private static Level buildLevel8() {
        Level lvl = new Level(8, "LEVEL 8: THE EYE OF CLYDE", 45, 10, 2, 8);

        fillHorizontal(lvl, 0, 44, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 44, 0, 9, TileType.BRICK);

        fillHorizontal(lvl, 0, 5, 9, TileType.BRICK);
        fillHorizontal(lvl, 6, 38, 9, TileType.WEED);
        fillHorizontal(lvl, 39, 44, 9, TileType.BRICK);

        // Platforms
        fillHorizontal(lvl, 1, 4, 7, TileType.BRICK);
        fillHorizontal(lvl, 8, 12, 5, TileType.BRICK);
        fillHorizontal(lvl, 16, 20, 3, TileType.BRICK);
        fillHorizontal(lvl, 24, 28, 5, TileType.BRICK);
        fillHorizontal(lvl, 32, 36, 4, TileType.BRICK);

        // Jetpack & Gun
        lvl.addCollectible(new Collectible(CollectibleType.JETPACK, 2, 6));
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 10, 4));

        // Eye Demons
        lvl.addEnemy(new Enemy("eye_1", EnemyType.EYE_DEMON, 10 * 16f, 3 * 16f, new int[]{1, -1}, new int[]{0, 0}, 55));
        lvl.addEnemy(new Enemy("eye_2", EnemyType.EYE_DEMON, 26 * 16f, 3 * 16f, new int[]{0, 0}, new int[]{1, -1}, 50));
        lvl.addEnemy(new Enemy("eye_3", EnemyType.EYE_DEMON, 34 * 16f, 2 * 16f, new int[]{-1, 1}, new int[]{1, -1}, 45));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 18, 2));
        lvl.setTile(42, 8, TileType.DOOR_CLOSED);

        // Crown Cache
        for (int x = 25; x <= 27; x++) {
            lvl.addCollectible(new Collectible(CollectibleType.CROWN, x, 4));
        }

        return lvl;
    }

    /**
     * Level 9: Demon's Gauntlet
     * 48x10. Intense combat against Red Demons, alternating fire and water traps.
     */
    private static Level buildLevel9() {
        Level lvl = new Level(9, "LEVEL 9: DEMON'S GAUNTLET", 48, 10, 2, 2);

        fillHorizontal(lvl, 0, 47, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 47, 0, 9, TileType.BRICK);

        // Dangerous floor
        fillHorizontal(lvl, 0, 4, 9, TileType.BRICK);
        fillHorizontal(lvl, 5, 12, 9, TileType.FIRE);
        fillHorizontal(lvl, 13, 17, 9, TileType.BRICK);
        fillHorizontal(lvl, 18, 26, 9, TileType.WATER);
        fillHorizontal(lvl, 27, 32, 9, TileType.BRICK);
        fillHorizontal(lvl, 33, 41, 9, TileType.FIRE);
        fillHorizontal(lvl, 42, 47, 9, TileType.BRICK);

        // High platforms
        fillHorizontal(lvl, 1, 5, 3, TileType.BRICK);
        fillHorizontal(lvl, 8, 13, 5, TileType.BRICK);
        fillHorizontal(lvl, 15, 20, 3, TileType.BRICK);
        fillHorizontal(lvl, 22, 27, 6, TileType.BRICK);
        fillHorizontal(lvl, 29, 34, 4, TileType.BRICK);
        fillHorizontal(lvl, 36, 41, 3, TileType.BRICK);

        // Gun & Jetpack
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 3, 2));
        lvl.addCollectible(new Collectible(CollectibleType.JETPACK, 17, 2));

        // Red Demons
        lvl.addEnemy(new Enemy("demon_1", EnemyType.RED_DEMON, 10 * 16f, 4 * 16f, new int[]{1, -1}, new int[]{0, 0}, 45));
        lvl.addEnemy(new Enemy("demon_2", EnemyType.RED_DEMON, 24 * 16f, 4 * 16f, new int[]{1, -1}, new int[]{1, -1}, 40));
        lvl.addEnemy(new Enemy("demon_3", EnemyType.RED_DEMON, 38 * 16f, 2 * 16f, new int[]{-1, 1}, new int[]{0, 0}, 35));

        // Trophy & Door
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 39, 2));
        lvl.setTile(45, 8, TileType.DOOR_CLOSED);

        // Loot
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 10, 4));
        lvl.addCollectible(new Collectible(CollectibleType.CROWN, 24, 5));
        lvl.addCollectible(new Collectible(CollectibleType.SCEPTRE, 31, 3));

        return lvl;
    }

    /**
     * Level 10: Clyde Cooper's Sanctum (The Final Challenge)
     * 50x10. The climax! Clyde's mechanical guardians, all hazard types, final golden chalice.
     */
    private static Level buildLevel10() {
        Level lvl = new Level(10, "LEVEL 10: CLYDE'S SANCTUM", 50, 10, 2, 8);

        fillHorizontal(lvl, 0, 49, 0, TileType.BRICK);
        fillVertical(lvl, 0, 0, 9, TileType.BRICK);
        fillVertical(lvl, 49, 0, 9, TileType.BRICK);

        // Complex deadly floor
        fillHorizontal(lvl, 0, 5, 9, TileType.BRICK);
        fillHorizontal(lvl, 6, 14, 9, TileType.FIRE);
        fillHorizontal(lvl, 15, 20, 9, TileType.BRICK);
        fillHorizontal(lvl, 21, 29, 9, TileType.WATER);
        fillHorizontal(lvl, 30, 36, 9, TileType.WEED);
        fillHorizontal(lvl, 37, 43, 9, TileType.FIRE);
        fillHorizontal(lvl, 44, 49, 9, TileType.BRICK);

        // Platform architecture
        fillHorizontal(lvl, 1, 4, 7, TileType.GIRDER);
        fillHorizontal(lvl, 7, 12, 5, TileType.GIRDER);
        fillHorizontal(lvl, 15, 20, 3, TileType.GIRDER);
        fillHorizontal(lvl, 23, 28, 5, TileType.GIRDER);
        fillHorizontal(lvl, 31, 37, 4, TileType.GIRDER);
        fillHorizontal(lvl, 40, 45, 6, TileType.GIRDER);
        fillHorizontal(lvl, 20, 26, 1, TileType.GIRDER); // Top secret ridge

        // Both weapons provided
        lvl.addCollectible(new Collectible(CollectibleType.GUN, 2, 6));
        lvl.addCollectible(new Collectible(CollectibleType.JETPACK, 9, 4));

        // Clyde Guardians
        lvl.addEnemy(new Enemy("guardian_1", EnemyType.CLYDE_GUARDIAN, 17 * 16f, 2 * 16f, new int[]{1, -1}, new int[]{0, 0}, 40));
        lvl.addEnemy(new Enemy("guardian_2", EnemyType.CLYDE_GUARDIAN, 25 * 16f, 3 * 16f, new int[]{0, 0}, new int[]{1, -1}, 35));
        lvl.addEnemy(new Enemy("guardian_3", EnemyType.CLYDE_GUARDIAN, 34 * 16f, 2 * 16f, new int[]{-1, 1}, new int[]{1, -1}, 30));
        lvl.addEnemy(new Enemy("guardian_4", EnemyType.CLYDE_GUARDIAN, 42 * 16f, 4 * 16f, new int[]{1, -1}, new int[]{0, 0}, 30));

        // The Master Trophy!
        lvl.addCollectible(new Collectible(CollectibleType.TROPHY, 23, 0));

        // Final Victory Door!
        lvl.setTile(47, 8, TileType.DOOR_CLOSED);

        // Royal spoils of victory
        for (int x = 41; x <= 44; x++) {
            lvl.addCollectible(new Collectible(CollectibleType.CROWN, x, 5));
        }

        return lvl;
    }

    // Helper methods for clean geometry generation
    private static void fillHorizontal(Level lvl, int startX, int endX, int y, TileType type) {
        for (int x = startX; x <= endX; x++) {
            lvl.setTile(x, y, type);
        }
    }

    private static void fillVertical(Level lvl, int x, int startY, int endY, TileType type) {
        for (int y = startY; y <= endY; y++) {
            lvl.setTile(x, y, type);
        }
    }
}
