package com.dave.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a level layout, tilemap, entities, and level progression state.
 */
public class Level {
    private final int levelNumber;
    private final String name;
    private final int widthInTiles;
    private final int heightInTiles;
    private final TileType[][] tiles; // [y][x]
    private final List<Collectible> collectibles;
    private final List<Enemy> enemies;
    private final List<Projectile> projectiles;

    // Spawn and target coordinates
    private final int spawnTileX;
    private final int spawnTileY;
    private int doorTileX = -1;
    private int doorTileY = -1;
    private int trophyTileX = -1;
    private int trophyTileY = -1;

    // Level session state
    private boolean trophyCollected;
    private boolean completed;
    private int scoreAtLevelStart;

    public Level(int levelNumber, String name, int widthInTiles, int heightInTiles, int spawnTileX, int spawnTileY) {
        this.levelNumber = levelNumber;
        this.name = name;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.spawnTileX = spawnTileX;
        this.spawnTileY = spawnTileY;
        this.tiles = new TileType[heightInTiles][widthInTiles];
        this.collectibles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.trophyCollected = false;
        this.completed = false;
        this.scoreAtLevelStart = 0;

        // Initialize empty tiles
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                tiles[y][x] = TileType.EMPTY;
            }
        }
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < widthInTiles && y >= 0 && y < heightInTiles) {
            tiles[y][x] = type;
            if (type == TileType.DOOR_CLOSED || type == TileType.DOOR_OPEN) {
                doorTileX = x;
                doorTileY = y;
            }
        }
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || x >= widthInTiles || y < 0 || y >= heightInTiles) {
            return TileType.EMPTY;
        }
        return tiles[y][x];
    }

    public void addCollectible(Collectible c) {
        collectibles.add(c);
        if (c.getType() == CollectibleType.TROPHY) {
            trophyTileX = c.getTileX();
            trophyTileY = c.getTileY();
        }
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Unlocks the door when trophy is retrieved.
     */
    public void onTrophyCollected() {
        this.trophyCollected = true;
        if (doorTileX >= 0 && doorTileY >= 0) {
            tiles[doorTileY][doorTileX] = TileType.DOOR_OPEN;
        }
    }

    /**
     * Resets level entities and collectibles for an anti-exploit respawn.
     * When Dave dies, collectibles are reset to prevent infinite score farming!
     */
    public void resetForRespawn() {
        this.trophyCollected = false;
        this.completed = false;
        if (doorTileX >= 0 && doorTileY >= 0) {
            tiles[doorTileY][doorTileX] = TileType.DOOR_CLOSED;
        }
        for (Collectible c : collectibles) {
            c.reset();
        }
        for (Enemy e : enemies) {
            e.reset();
        }
        projectiles.clear();
    }

    public int getLevelNumber() { return levelNumber; }
    public String getName() { return name; }
    public int getWidthInTiles() { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }
    public float getPixelWidth() { return widthInTiles * 16f; }
    public float getPixelHeight() { return heightInTiles * 16f; }
    public int getSpawnTileX() { return spawnTileX; }
    public int getSpawnTileY() { return spawnTileY; }
    public int getDoorTileX() { return doorTileX; }
    public int getDoorTileY() { return doorTileY; }
    public int getTrophyTileX() { return trophyTileX; }
    public int getTrophyTileY() { return trophyTileY; }
    public boolean isTrophyCollected() { return trophyCollected; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getScoreAtLevelStart() { return scoreAtLevelStart; }
    public void setScoreAtLevelStart(int score) { this.scoreAtLevelStart = score; }
    public List<Collectible> getCollectibles() { return collectibles; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Projectile> getProjectiles() { return projectiles; }
}
