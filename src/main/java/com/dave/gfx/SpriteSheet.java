package com.dave.gfx;

import com.dave.model.CollectibleType;
import com.dave.model.Dave;
import com.dave.model.Enemy;
import com.dave.model.EnemyType;
import com.dave.model.TileType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/**
 * Procedural pixel-art sprite generator for Dangerous Dave.
 * Accurately creates original, unencumbered 16x16 retro EGA-styled sprites in memory.
 */
public class SpriteSheet {
    // Tile sprites
    private final Map<TileType, BufferedImage> tileSprites = new EnumMap<>(TileType.class);
    private BufferedImage fireFrame2;
    private BufferedImage waterFrame2;

    // Collectible sprites
    private final Map<CollectibleType, BufferedImage> itemSprites = new EnumMap<>(CollectibleType.class);

    // Dave sprites
    private BufferedImage daveIdleRight;
    private BufferedImage daveIdleLeft;
    private BufferedImage[] daveWalkRight;
    private BufferedImage[] daveWalkLeft;
    private BufferedImage daveJumpRight;
    private BufferedImage daveJumpLeft;
    private BufferedImage daveJetpackRight;
    private BufferedImage daveJetpackLeft;
    private BufferedImage[] daveExplosion;

    // Enemy sprites
    private final Map<EnemyType, BufferedImage[]> enemySprites = new EnumMap<>(EnemyType.class);

    // Projectile sprites
    private BufferedImage bulletSprite;
    private BufferedImage enemyShotSprite;

    public SpriteSheet() {
        generateTiles();
        generateCollectibles();
        generateDaveSprites();
        generateEnemies();
        generateProjectiles();
    }

    private void generateTiles() {
        // Red Brick
        BufferedImage brick = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = brick.createGraphics();
        g.setColor(Palette.RED);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Palette.BROWN);
        g.drawLine(0, 7, 15, 7);
        g.drawLine(0, 15, 15, 15);
        g.drawLine(7, 0, 7, 7);
        g.drawLine(15, 8, 15, 15);
        g.drawLine(0, 8, 0, 15);
        g.setColor(Palette.LIGHT_RED);
        g.drawLine(0, 0, 15, 0);
        g.drawLine(0, 8, 15, 8);
        g.dispose();
        tileSprites.put(TileType.BRICK, brick);

        // Blue Pipe
        BufferedImage pipe = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = pipe.createGraphics();
        g.setColor(Palette.BLUE);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Palette.LIGHT_BLUE);
        g.fillRect(2, 2, 12, 4);
        g.setColor(Palette.CYAN);
        g.drawLine(3, 3, 12, 3);
        g.setColor(Palette.DARK_GRAY);
        g.drawRect(0, 0, 15, 15);
        g.dispose();
        tileSprites.put(TileType.PIPE, pipe);

        // Girder / Platform
        BufferedImage girder = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = girder.createGraphics();
        g.setColor(Palette.DARK_GRAY);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Palette.LIGHT_GRAY);
        g.drawRect(0, 0, 15, 15);
        g.drawLine(0, 0, 15, 15);
        g.drawLine(15, 0, 0, 15);
        g.dispose();
        tileSprites.put(TileType.GIRDER, girder);

        // Inverted Tree (Level 2 foliage)
        BufferedImage tree = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = tree.createGraphics();
        g.setColor(Palette.GREEN);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Palette.LIGHT_GREEN);
        g.fillRect(2, 2, 12, 6);
        g.setColor(Palette.BROWN);
        g.fillRect(6, 8, 4, 8);
        g.dispose();
        tileSprites.put(TileType.TREE, tree);

        // Star tile (Night sky background)
        BufferedImage star = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = star.createGraphics();
        g.setColor(Palette.BLACK);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Palette.WHITE);
        g.fillRect(4, 4, 2, 2);
        g.fillRect(12, 10, 2, 2);
        g.setColor(Palette.YELLOW);
        g.fillRect(8, 7, 2, 2);
        g.dispose();
        tileSprites.put(TileType.STAR, star);

        // Door Closed (Locked exit)
        BufferedImage doorClosed = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = doorClosed.createGraphics();
        g.setColor(Palette.BROWN);
        g.fillRect(1, 0, 14, 16);
        g.setColor(Palette.DARK_GRAY);
        g.drawRect(1, 0, 13, 15);
        g.setColor(Palette.RED); // red locked light
        g.fillRect(6, 3, 4, 3);
        g.setColor(Palette.YELLOW); // brass knob
        g.fillRect(11, 8, 2, 2);
        g.dispose();
        tileSprites.put(TileType.DOOR_CLOSED, doorClosed);

        // Door Open (Unlocked exit)
        BufferedImage doorOpen = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = doorOpen.createGraphics();
        g.setColor(Palette.DARK_GRAY);
        g.fillRect(1, 0, 14, 16);
        g.setColor(Palette.BLACK);
        g.fillRect(3, 2, 10, 14);
        g.setColor(Palette.LIGHT_GREEN); // green exit sign
        g.fillRect(5, 0, 6, 2);
        g.dispose();
        tileSprites.put(TileType.DOOR_OPEN, doorOpen);

        // Fire Hazard (Frame 1 & 2)
        BufferedImage fire1 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = fire1.createGraphics();
        g.setColor(Palette.RED);
        g.fillPolygon(new int[]{0, 4, 8, 12, 16, 12, 8, 4}, new int[]{16, 4, 16, 2, 16, 12, 16, 12}, 8);
        g.setColor(Palette.YELLOW);
        g.fillPolygon(new int[]{2, 5, 8, 11, 14}, new int[]{16, 8, 16, 6, 16}, 5);
        g.dispose();
        tileSprites.put(TileType.FIRE, fire1);

        fireFrame2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = fireFrame2.createGraphics();
        g.setColor(Palette.LIGHT_RED);
        g.fillPolygon(new int[]{0, 3, 7, 11, 16, 13, 9, 5}, new int[]{16, 2, 16, 4, 16, 10, 16, 10}, 8);
        g.setColor(Palette.WHITE);
        g.fillPolygon(new int[]{3, 6, 9, 12}, new int[]{16, 7, 16, 9}, 4);
        g.dispose();

        // Water Hazard (Frame 1 & 2)
        BufferedImage water1 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = water1.createGraphics();
        g.setColor(Palette.BLUE);
        g.fillRect(0, 4, 16, 12);
        g.setColor(Palette.LIGHT_BLUE);
        g.drawLine(0, 4, 15, 4);
        g.drawLine(2, 8, 6, 8);
        g.drawLine(10, 8, 14, 8);
        g.dispose();
        tileSprites.put(TileType.WATER, water1);

        waterFrame2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = waterFrame2.createGraphics();
        g.setColor(Palette.BLUE);
        g.fillRect(0, 4, 16, 12);
        g.setColor(Palette.CYAN);
        g.drawLine(0, 5, 15, 5);
        g.drawLine(4, 9, 8, 9);
        g.dispose();

        // Purple Weed Hazard
        BufferedImage weed = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = weed.createGraphics();
        g.setColor(Palette.MAGENTA);
        for (int i = 1; i < 16; i += 4) {
            g.fillPolygon(new int[]{i, i + 2, i + 4}, new int[]{16, 3, 16}, 3);
        }
        g.setColor(Palette.LIGHT_MAGENTA);
        for (int i = 2; i < 15; i += 4) {
            g.drawLine(i, 6, i, 16);
        }
        g.dispose();
        tileSprites.put(TileType.WEED, weed);
    }

    private void generateCollectibles() {
        // Gold Trophy / Chalice
        BufferedImage trophy = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = trophy.createGraphics();
        g.setColor(Palette.YELLOW);
        g.fillPolygon(new int[]{3, 13, 11, 8, 5}, new int[]{3, 3, 9, 11, 9}, 5);
        g.fillRect(7, 11, 2, 3);
        g.fillRect(4, 14, 8, 2);
        g.setColor(Palette.WHITE); // shine
        g.drawLine(5, 4, 5, 8);
        g.dispose();
        itemSprites.put(CollectibleType.TROPHY, trophy);

        // Blue Diamond
        BufferedImage diamond = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = diamond.createGraphics();
        g.setColor(Palette.CYAN);
        g.fillPolygon(new int[]{8, 14, 8, 2}, new int[]{2, 8, 14, 8}, 4);
        g.setColor(Palette.WHITE);
        g.fillPolygon(new int[]{8, 11, 8, 5}, new int[]{4, 8, 12, 8}, 4);
        g.dispose();
        itemSprites.put(CollectibleType.BLUE_DIAMOND, diamond);

        // Red Gem / Ruby
        BufferedImage ruby = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = ruby.createGraphics();
        g.setColor(Palette.RED);
        g.fillPolygon(new int[]{4, 12, 15, 8, 1}, new int[]{3, 3, 7, 14, 7}, 5);
        g.setColor(Palette.LIGHT_RED);
        g.drawLine(5, 4, 11, 4);
        g.setColor(Palette.WHITE);
        g.fillRect(7, 6, 2, 2);
        g.dispose();
        itemSprites.put(CollectibleType.RED_GEM, ruby);

        // Golden Ring
        BufferedImage ring = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = ring.createGraphics();
        g.setColor(Palette.YELLOW);
        g.drawOval(3, 3, 10, 10);
        g.drawOval(4, 4, 8, 8);
        g.setColor(Palette.WHITE);
        g.fillRect(7, 2, 2, 3); // diamond setting
        g.dispose();
        itemSprites.put(CollectibleType.RING, ring);

        // Royal Sceptre / Wand
        BufferedImage sceptre = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = sceptre.createGraphics();
        g.setColor(Palette.YELLOW);
        g.drawLine(4, 14, 12, 6);
        g.drawLine(5, 14, 13, 6);
        g.setColor(Palette.LIGHT_MAGENTA);
        g.fillOval(10, 3, 5, 5);
        g.dispose();
        itemSprites.put(CollectibleType.SCEPTRE, sceptre);

        // Crown
        BufferedImage crown = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = crown.createGraphics();
        g.setColor(Palette.YELLOW);
        g.fillPolygon(new int[]{2, 2, 5, 8, 11, 14, 14}, new int[]{13, 6, 9, 5, 9, 6, 13}, 7);
        g.setColor(Palette.RED);
        g.fillRect(4, 11, 2, 2);
        g.fillRect(10, 11, 2, 2);
        g.setColor(Palette.CYAN);
        g.fillRect(7, 11, 2, 2);
        g.dispose();
        itemSprites.put(CollectibleType.CROWN, crown);

        // Gun Pickup
        BufferedImage gun = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = gun.createGraphics();
        g.setColor(Palette.DARK_GRAY);
        g.fillRect(2, 6, 12, 4);
        g.setColor(Palette.BROWN);
        g.fillRect(2, 8, 4, 6);
        g.setColor(Palette.LIGHT_GRAY);
        g.drawLine(8, 5, 14, 5);
        g.dispose();
        itemSprites.put(CollectibleType.GUN, gun);

        // Jetpack Pickup
        BufferedImage jetpack = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g = jetpack.createGraphics();
        g.setColor(Palette.LIGHT_GRAY);
        g.fillRect(3, 3, 10, 10);
        g.setColor(Palette.RED);
        g.fillRect(4, 4, 3, 6);
        g.fillRect(9, 4, 3, 6);
        g.setColor(Palette.YELLOW);
        g.fillRect(6, 12, 4, 3);
        g.dispose();
        itemSprites.put(CollectibleType.JETPACK, jetpack);
    }

    private void generateDaveSprites() {
        // Dave: Red cap, skin face, blue shirt, blue trousers, brown shoes
        daveIdleRight = renderDaveFrame(0, false, false, false);
        daveIdleLeft  = renderDaveFrame(0, true,  false, false);

        daveWalkRight = new BufferedImage[4];
        daveWalkLeft  = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            daveWalkRight[i] = renderDaveFrame(i, false, false, false);
            daveWalkLeft[i]  = renderDaveFrame(i, true,  false, false);
        }

        daveJumpRight = renderDaveFrame(1, false, true, false);
        daveJumpLeft  = renderDaveFrame(1, true,  true, false);

        daveJetpackRight = renderDaveFrame(0, false, false, true);
        daveJetpackLeft  = renderDaveFrame(0, true,  false, true);

        // Dave Death Explosion (4 animated frames)
        daveExplosion = new BufferedImage[4];
        for (int frame = 0; frame < 4; frame++) {
            BufferedImage exp = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = exp.createGraphics();
            int r = (frame + 1) * 3;
            g.setColor(frame % 2 == 0 ? Palette.YELLOW : Palette.LIGHT_RED);
            g.fillOval(8 - r, 8 - r, r * 2, r * 2);
            g.setColor(Palette.WHITE);
            g.fillOval(8 - r / 2, 8 - r / 2, r, r);
            g.dispose();
            daveExplosion[frame] = exp;
        }
    }

    private BufferedImage renderDaveFrame(int walkPhase, boolean left, boolean jumping, boolean jetpack) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Direction offset
        int faceDir = left ? -1 : 1;
        int headX = left ? 4 : 5;

        // Red baseball cap
        g.setColor(Palette.RED);
        g.fillRect(headX - (left ? 1 : 0), 1, 6, 3);
        g.fillRect(left ? headX - 2 : headX + 4, 3, 3, 1); // cap brim

        // Face skin
        g.setColor(Palette.SKIN_TONE);
        g.fillRect(headX, 4, 5, 3);
        // Eye
        g.setColor(Palette.BLACK);
        g.fillRect(left ? headX : headX + 3, 4, 1, 1);

        // Jetpack tank on back
        if (jetpack) {
            g.setColor(Palette.LIGHT_GRAY);
            g.fillRect(left ? 10 : 2, 6, 3, 7);
            g.setColor(Palette.YELLOW);
            g.fillRect(left ? 11 : 3, 13, 2, 2); // thruster flame
        }

        // Blue Shirt
        g.setColor(Palette.BLUE);
        g.fillRect(4, 7, 7, 4);

        // Legs / Walking animation
        g.setColor(Palette.LIGHT_BLUE);
        if (jumping) {
            g.fillRect(4, 11, 3, 3);
            g.fillRect(8, 11, 3, 2);
            g.setColor(Palette.BROWN);
            g.fillRect(3, 13, 3, 2);
            g.fillRect(8, 12, 3, 2);
        } else if (walkPhase == 0) {
            // Standing
            g.fillRect(4, 11, 3, 3);
            g.fillRect(8, 11, 3, 3);
            g.setColor(Palette.BROWN);
            g.fillRect(4, 14, 3, 2);
            g.fillRect(8, 14, 3, 2);
        } else if (walkPhase == 1 || walkPhase == 3) {
            // Walk stride 1
            g.fillRect(3, 11, 3, 3);
            g.fillRect(9, 11, 3, 3);
            g.setColor(Palette.BROWN);
            g.fillRect(2, 14, 3, 2);
            g.fillRect(10, 14, 3, 2);
        } else {
            // Walk stride 2
            g.fillRect(5, 11, 3, 3);
            g.fillRect(7, 11, 3, 3);
            g.setColor(Palette.BROWN);
            g.fillRect(4, 14, 3, 2);
            g.fillRect(8, 14, 3, 2);
        }

        g.dispose();
        return img;
    }

    private void generateEnemies() {
        for (EnemyType type : EnemyType.values()) {
            BufferedImage[] frames = new BufferedImage[2];
            for (int f = 0; f < 2; f++) {
                int w = (int) type.getWidth();
                int h = (int) type.getHeight();
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();

                switch (type) {
                    case SPIDER:
                        g.setColor(Palette.RED);
                        g.fillOval(w / 4, h / 4, w / 2, h / 2);
                        g.setColor(Palette.LIGHT_GRAY);
                        int legY = (f == 0) ? 2 : 4;
                        g.drawLine(2, legY, w - 2, h - legY);
                        g.drawLine(2, h - legY, w - 2, legY);
                        break;
                    case SPINNER:
                        g.setColor(Palette.LIGHT_GREEN);
                        g.fillOval(2, 2, w - 4, h - 4);
                        g.setColor(Palette.WHITE);
                        int ang = f * 45;
                        g.drawArc(3, 3, w - 6, h - 6, ang, 90);
                        break;
                    case SUN:
                        g.setColor(f == 0 ? Palette.YELLOW : Palette.LIGHT_RED);
                        g.fillOval(3, 3, w - 6, h - 6);
                        g.setColor(Palette.WHITE);
                        g.fillOval(w / 3, h / 3, w / 3, h / 3);
                        break;
                    case SKULL:
                        g.setColor(Palette.LIGHT_GRAY);
                        g.fillOval(2, 2, w - 4, h - 6);
                        g.fillRect(4, h - 6, w - 8, 4);
                        g.setColor(Palette.BLACK);
                        g.fillRect(4, 5, 2, 3);
                        g.fillRect(w - 6, 5, 2, 3);
                        break;
                    case UFO:
                        g.setColor(Palette.CYAN);
                        g.fillOval(2, 4, w - 4, 6);
                        g.setColor(Palette.LIGHT_MAGENTA);
                        g.fillOval(w / 3, 2, w / 3, 5);
                        g.setColor(f == 0 ? Palette.YELLOW : Palette.LIGHT_CYAN);
                        g.fillRect(4, 9, 2, 2);
                        g.fillRect(w - 6, 9, 2, 2);
                        break;
                    case EYE_DEMON:
                        g.setColor(Palette.WHITE);
                        g.fillOval(2, 2, w - 4, h - 4);
                        g.setColor(Palette.GREEN);
                        g.fillOval(w / 3 + (f == 0 ? 1 : -1), h / 3, w / 3, h / 3);
                        g.setColor(Palette.BLACK);
                        g.fillOval(w / 3 + 1, h / 3 + 1, 2, 2);
                        break;
                    case RED_DEMON:
                        g.setColor(Palette.RED);
                        g.fillOval(2, 2, w - 4, h - 4);
                        g.setColor(Palette.YELLOW);
                        g.fillRect(4, 5, 2, 2);
                        g.fillRect(w - 6, 5, 2, 2);
                        g.setColor(Palette.WHITE);
                        g.drawLine(3, 2, 5, 4);
                        g.drawLine(w - 3, 2, w - 5, 4);
                        break;
                    case CLYDE_GUARDIAN:
                        g.setColor(Palette.DARK_GRAY);
                        g.fillRect(2, 2, w - 4, h - 4);
                        g.setColor(f == 0 ? Palette.LIGHT_RED : Palette.YELLOW);
                        g.fillRect(6, 6, w - 12, 4);
                        g.setColor(Palette.LIGHT_CYAN);
                        g.drawRect(2, 2, w - 5, h - 5);
                        break;
                }
                g.dispose();
                frames[f] = img;
            }
            enemySprites.put(type, frames);
        }
    }

    private void generateProjectiles() {
        // Dave's bullet
        bulletSprite = new BufferedImage(6, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bulletSprite.createGraphics();
        g.setColor(Palette.YELLOW);
        g.fillRect(0, 1, 5, 2);
        g.setColor(Palette.WHITE);
        g.fillRect(4, 1, 2, 2);
        g.dispose();

        // Enemy shot
        enemyShotSprite = new BufferedImage(6, 6, BufferedImage.TYPE_INT_ARGB);
        g = enemyShotSprite.createGraphics();
        g.setColor(Palette.LIGHT_RED);
        g.fillOval(0, 0, 6, 6);
        g.setColor(Palette.YELLOW);
        g.fillOval(1, 1, 4, 4);
        g.dispose();
    }

    // Accessors
    public BufferedImage getTileSprite(TileType type, int animTick) {
        if (type == TileType.FIRE) {
            return (animTick % 30 < 15) ? tileSprites.get(TileType.FIRE) : fireFrame2;
        }
        if (type == TileType.WATER) {
            return (animTick % 40 < 20) ? tileSprites.get(TileType.WATER) : waterFrame2;
        }
        return tileSprites.get(type);
    }

    public BufferedImage getCollectibleSprite(CollectibleType type) {
        return itemSprites.get(type);
    }

    public BufferedImage getDaveSprite(Dave dave) {
        if (dave.isDead()) {
            int frame = Math.min(3, (40 - dave.getExplosionTimer()) / 10);
            return daveExplosion[frame];
        }
        if (dave.isJetpackActive()) {
            return dave.isFacingLeft() ? daveJetpackLeft : daveJetpackRight;
        }
        if (!dave.isGrounded()) {
            return dave.isFacingLeft() ? daveJumpLeft : daveJumpRight;
        }
        if (Math.abs(dave.getVx()) > 0.1f) {
            int f = dave.getWalkFrame();
            return dave.isFacingLeft() ? daveWalkLeft[f] : daveWalkRight[f];
        }
        return dave.isFacingLeft() ? daveIdleLeft : daveIdleRight;
    }

    public BufferedImage getEnemySprite(Enemy enemy) {
        if (enemy.isExploding()) {
            int frame = Math.min(3, (24 - enemy.getExplosionTimer()) / 6);
            return daveExplosion[frame];
        }
        BufferedImage[] frames = enemySprites.get(enemy.getType());
        if (frames == null || frames.length == 0) return null;
        int frameIdx = (enemy.getAnimFrame() % 30 < 15) ? 0 : 1;
        return frames[frameIdx];
    }

    public BufferedImage getBulletSprite() { return bulletSprite; }
    public BufferedImage getEnemyShotSprite() { return enemyShotSprite; }
}
