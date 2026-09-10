package com.dave.core;
import com.dave.audio.RetroAudioEngine;
import com.dave.audio.SoundEffect;
import com.dave.gfx.Camera;
import com.dave.gfx.Palette;
import com.dave.gfx.SpriteSheet;
import com.dave.levels.LevelRegistry;
import com.dave.model.*;
import com.dave.physics.CollisionSystem;
import com.dave.ui.DaveCanvas;
import com.dave.ui.HUD;
import com.dave.ui.screens.GameOverScreen;
import com.dave.ui.screens.MenuScreen;
import com.dave.ui.screens.VictoryScreen;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
Core Game Engine orchestrating the 60 FPS loop, physics, state transitions, audio, and rendering.
*/
public class GameEngine implements Runnable, CollisionSystem.CollisionCallback {
public static final int TARGET_FPS = 60;
public static final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;

// Core subsystems
private final DaveCanvas canvas;
private final InputHandler input;
private final RetroAudioEngine audio;
private final SpriteSheet sprites;
private final Camera camera;
private final HUD hud;
private final StateMachine stateMachine;
private final CollisionSystem collisionSystem;

// Screens
private final MenuScreen menuScreen;
private final GameOverScreen gameOverScreen;
private final VictoryScreen victoryScreen;

// Game state data
private int currentLevelNumber;
private Level currentLevel;
private Dave dave;
private int score;
private int nextExtraLifeScore;
private int levelCompleteTimer;
private int animTick;

// Screen shake effect
private int screenShakeFrames = 0;
private float screenShakeIntensity = 0.0f;

private volatile boolean running;
private Thread gameThread;

public GameEngine(DaveCanvas canvas, InputHandler input) {
    this.canvas = canvas;
    this.input = input;
    this.audio = new RetroAudioEngine();
    this.sprites = new SpriteSheet();
    this.camera = new Camera();
    this.hud = new HUD();
    this.stateMachine = new StateMachine(GameState.MAIN_MENU);
    this.collisionSystem = new CollisionSystem(this);
    this.menuScreen = new MenuScreen();
    this.gameOverScreen = new GameOverScreen();
    this.victoryScreen = new VictoryScreen();
    this.score = 0;
    this.nextExtraLifeScore = 20000;
    this.currentLevelNumber = 1;
    this.levelCompleteTimer = 0;
    this.animTick = 0;
    this.screenShakeFrames = 0;
    this.screenShakeIntensity = 0.0f;
}

public synchronized void start() {
    if (running) return;
    running = true;
    gameThread = new Thread(this, "DangerousDave-MainLoop");
    gameThread.start();
}

public synchronized void stop() {
    running = false;
    if (audio != null) {
        audio.shutdown();
    }
}

@Override
public void run() {
    long lastLoopTime = System.nanoTime();
    
    while (running) {
        long now = System.nanoTime();
        long updateLength = now - lastLoopTime;
        lastLoopTime = now;
        
        // 1. Process Input and Game Updates
        update();
        
        // 2. Render Frame
        render();
        
        // 3. Sleep to maintain smooth 60 FPS
        long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1_000_000;
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {}
        }
    }
}

private void update() {
    stateMachine.tick();
    animTick++;
    
    // Global input checks
    if (input.isKeyPressed(KeyEvent.VK_M)) {
        audio.toggleMute();
    }
    
    GameState state = stateMachine.getCurrentState();
    switch (state) {
        case MAIN_MENU:
            updateMainMenu();
            break;
        case PLAYING:
            updatePlaying();
            break;
        case PAUSED:
            updatePaused();
            break;
        case LEVEL_COMPLETE:
            updateLevelComplete();
            break;
        case GAME_OVER:
            updateGameOver();
            break;
        case VICTORY:
            updateVictory();
            break;
    }
    
    input.poll();
}

private void updateMainMenu() {
    if (input.isKeyPressed(KeyEvent.VK_ENTER) || input.isKeyPressed(KeyEvent.VK_SPACE)) {
        startNewGame();
    }
}

private void startNewGame() {
    score = 0;
    nextExtraLifeScore = 20000;
    currentLevelNumber = 1;
    loadLevel(currentLevelNumber);
    dave = new Dave(currentLevel.getSpawnTileX() * 16f, currentLevel.getSpawnTileY() * 16f);
    stateMachine.transitionTo(GameState.PLAYING);
}

private void loadLevel(int levelNum) {
    currentLevel = LevelRegistry.buildLevel(levelNum);
    currentLevel.setScoreAtLevelStart(score);
    if (dave != null) {
        dave.resetForLevel(currentLevel.getSpawnTileX() * 16f, currentLevel.getSpawnTileY() * 16f);
    }
    camera.reset();
    screenShakeFrames = 0;
    screenShakeIntensity = 0.0f;
}

private void updatePlaying() {
    // Pause toggle
    if (input.isKeyPressed(KeyEvent.VK_P)) {
        stateMachine.transitionTo(GameState.PAUSED);
        return;
    }
    
    // Restart hotkey
    if (input.isKeyPressed(KeyEvent.VK_R)) {
        handlePlayerDeath();
        return;
    }
    
    // Handle Dave Input
    if (!dave.isDead()) {
        boolean left = input.isKeyDown(KeyEvent.VK_LEFT) || input.isKeyDown(KeyEvent.VK_A);
        boolean right = input.isKeyDown(KeyEvent.VK_RIGHT) || input.isKeyDown(KeyEvent.VK_D);
        boolean up = input.isKeyDown(KeyEvent.VK_UP) || input.isKeyDown(KeyEvent.VK_W);
        boolean down = input.isKeyDown(KeyEvent.VK_DOWN) || input.isKeyDown(KeyEvent.VK_S);
        boolean jumpPressed = input.isKeyPressed(KeyEvent.VK_UP) || 
                              input.isKeyPressed(KeyEvent.VK_W) || 
                              input.isKeyPressed(KeyEvent.VK_SPACE);
        boolean shootPressed = input.isKeyPressed(KeyEvent.VK_CONTROL) || 
                               input.isKeyPressed(KeyEvent.VK_F);
        boolean jetpackPressed = input.isKeyPressed(KeyEvent.VK_ALT) || 
                                 input.isKeyPressed(KeyEvent.VK_J);
        
        // Jetpack toggle
        if (jetpackPressed && dave.hasJetpack()) {
            dave.toggleJetpack();
            if (dave.isJetpackActive()) {
                audio.playSound(SoundEffect.JETPACK);
            }
        }
        
        if (dave.isJetpackActive()) {
            // Jetpack 8-way directional flight
            float vx = 0;
            float vy = 0;
            if (left)  { vx -= Dave.JETPACK_SPEED; dave.setFacingLeft(true); }
            if (right) { vx += Dave.JETPACK_SPEED; dave.setFacingLeft(false); }
            if (up)    { vy -= Dave.JETPACK_SPEED; }
            if (down)  { vy += Dave.JETPACK_SPEED; }
            dave.setVx(vx);
            dave.setVy(vy);
        } else {
            // Normal walking movement
            if (left && !right) {
                dave.setVx(-Dave.WALK_SPEED);
                dave.setFacingLeft(true);
            } else if (right && !left) {
                dave.setVx(Dave.WALK_SPEED);
                dave.setFacingLeft(false);
            } else {
                dave.setVx(0);
            }
            
            // FIX: Use jump buffering system
            if (jumpPressed) {
                dave.requestJump();
            }
        }
        
        // Shoot gun
        if (shootPressed && dave.hasGun()) {
            Projectile bullet = dave.shoot();
            if (bullet != null) {
                currentLevel.getProjectiles().add(bullet);
                audio.playSound(SoundEffect.SHOOT);
            }
        }
    }
    
    // Update Dave Physics
    dave.updatePhysics();
    
    // FIX: Process buffered jump after physics update
    if (dave.processJump()) {
        audio.playSound(SoundEffect.JUMP);
    }
    
    // Resolve Collisions
    collisionSystem.resolvePlayerTileCollisions(dave, currentLevel);
    collisionSystem.checkHazards(dave, currentLevel);
    collisionSystem.checkCollectibles(dave, currentLevel);
    collisionSystem.checkDoor(dave, currentLevel);
    collisionSystem.checkEnemies(dave, currentLevel);
    collisionSystem.updateProjectiles(dave, currentLevel);
    
    // Update Level Enemies
    for (Enemy enemy : currentLevel.getEnemies()) {
        enemy.update(dave.getX(), dave.getY(), currentLevel.getProjectiles());
    }
    
    // Camera Tracking
    camera.update(dave, currentLevel);
    
    // Check Dave Death Timer & Respawn
    if (dave.isDead() && dave.getExplosionTimer() <= 0) {
        handlePlayerDeath();
    }
}

/**
Anti-Exploit Death & Respawn Logic:
Restores score to start-of-level checkpoint and resets collectibles, completely eliminating infinite score farming!
*/
private void handlePlayerDeath() {
    if (dave.getDavesRemaining() > 0) {
        dave.setDavesRemaining(dave.getDavesRemaining() - 1);
        // Roll back score to start of current level attempt
        this.score = currentLevel.getScoreAtLevelStart();
        currentLevel.resetForRespawn();
        dave.respawnAtSpawn();
        screenShakeFrames = 0;
        screenShakeIntensity = 0.0f;
    } else {
        // Out of lives -> Game Over
        stateMachine.transitionTo(GameState.GAME_OVER);
    }
}

private void updatePaused() {
    if (input.isKeyPressed(KeyEvent.VK_P)) {
        stateMachine.transitionTo(GameState.PLAYING);
    } else if (input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
        stateMachine.transitionTo(GameState.MAIN_MENU);
    }
}

private void updateLevelComplete() {
    levelCompleteTimer++;
    if (levelCompleteTimer >= 90 || input.isKeyPressed(KeyEvent.VK_ENTER) || 
        input.isKeyPressed(KeyEvent.VK_SPACE)) {
        levelCompleteTimer = 0;
        if (currentLevelNumber < LevelRegistry.getTotalLevelCount()) {
            currentLevelNumber++;
            loadLevel(currentLevelNumber);
            stateMachine.transitionTo(GameState.PLAYING);
        } else {
            stateMachine.transitionTo(GameState.VICTORY);
        }
    }
}

private void updateGameOver() {
    if (input.isKeyPressed(KeyEvent.VK_R)) {
        startNewGame();
    } else if (input.isKeyPressed(KeyEvent.VK_ENTER) || input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
        stateMachine.transitionTo(GameState.MAIN_MENU);
    }
}

private void updateVictory() {
    if (input.isKeyPressed(KeyEvent.VK_ENTER) || input.isKeyPressed(KeyEvent.VK_R) || 
        input.isKeyPressed(KeyEvent.VK_SPACE)) {
        stateMachine.transitionTo(GameState.MAIN_MENU);
    }
}

// --- RENDERING ---
private void render() {
    Graphics2D g = canvas.getBufferGraphics();
    
    GameState state = stateMachine.getCurrentState();
    switch (state) {
        case MAIN_MENU:
            menuScreen.render(g);
            break;
        case PLAYING:
        case PAUSED:
        case LEVEL_COMPLETE:
            renderWorld(g);
            hud.render(g, dave, currentLevel, score);
            if (state == GameState.PAUSED) {
                renderPauseOverlay(g);
            } else if (state == GameState.LEVEL_COMPLETE) {
                renderLevelCompleteOverlay(g);
            }
            break;
        case GAME_OVER:
            gameOverScreen.render(g, score, currentLevelNumber);
            break;
        case VICTORY:
            victoryScreen.render(g, score);
            break;
    }
    
    canvas.repaint();
}

private void renderWorld(Graphics2D g) {
    // Clear background
    g.setColor(Palette.BLACK);
    g.fillRect(0, 0, 320, 160);
    
    // Calculate camera position with screen shake
    float camX = camera.getX();
    float camY = 0;
    
    // Apply screen shake effect
    if (screenShakeFrames > 0) {
        camX += (float) ((Math.random() - 0.5) * screenShakeIntensity);
        camY += (float) ((Math.random() - 0.5) * screenShakeIntensity);
        screenShakeFrames--;
        if (screenShakeFrames <= 0) {
            screenShakeIntensity = 0.0f;
        }
    }
    
    // 1. Draw Visible Map Tiles
    int minTileX = Math.max(0, (int) (camX / 16f));
    int maxTileX = Math.min(currentLevel.getWidthInTiles() - 1, 
                            (int) ((camX + 320) / 16f) + 1);
    
    for (int ty = 0; ty < currentLevel.getHeightInTiles(); ty++) {
        for (int tx = minTileX; tx <= maxTileX; tx++) {
            TileType type = currentLevel.getTile(tx, ty);
            if (type != TileType.EMPTY) {
                BufferedImage sprite = sprites.getTileSprite(type, animTick);
                if (sprite != null) {
                    g.drawImage(sprite, (int) (tx * 16 - camX), (int) (ty * 16 - camY), null);
                }
            }
        }
    }
    
    // 2. Draw Collectibles
    for (Collectible c : currentLevel.getCollectibles()) {
        if (!c.isCollected()) {
            BufferedImage itemImg = sprites.getCollectibleSprite(c.getType());
            if (itemImg != null) {
                g.drawImage(itemImg, (int) (c.getX() - camX), (int) (c.getY() - camY), null);
            }
        }
    }
    
    // 3. Draw Enemies
    for (Enemy enemy : currentLevel.getEnemies()) {
        if (enemy.isAlive() || enemy.isExploding()) {
            BufferedImage enemyImg = sprites.getEnemySprite(enemy);
            if (enemyImg != null) {
                g.drawImage(enemyImg, (int) (enemy.getX() - camX), 
                           (int) (enemy.getY() - camY), null);
            }
        }
    }
    
    // 4. Draw Projectiles
    for (Projectile p : currentLevel.getProjectiles()) {
        if (p.isActive()) {
            BufferedImage pImg = p.isFromPlayer() ? sprites.getBulletSprite() : 
                                                    sprites.getEnemyShotSprite();
            if (pImg != null) {
                g.drawImage(pImg, (int) (p.getX() - camX), (int) (p.getY() - camY), null);
            }
        }
    }
    
    // 5. Draw Dave
    BufferedImage daveImg = sprites.getDaveSprite(dave);
    if (daveImg != null) {
        g.drawImage(daveImg, (int) (dave.getX() - camX), (int) (dave.getY() - camY), null);
    }
}

private void renderPauseOverlay(Graphics2D g) {
    g.setColor(new Color(0, 0, 0, 180));
    g.fillRect(80, 50, 160, 50);
    g.setColor(Palette.WHITE);
    g.drawRect(80, 50, 160, 50);
    g.setFont(new Font("Monospaced", Font.BOLD, 14));
    g.setColor(Palette.YELLOW);
    g.drawString("PAUSED", 132, 72);
    g.setFont(new Font("Monospaced", Font.PLAIN, 9));
    g.setColor(Palette.WHITE);
    g.drawString("PRESS P TO RESUME", 108, 88);
}

private void renderLevelCompleteOverlay(Graphics2D g) {
    g.setColor(new Color(0, 0, 0, 200));
    g.fillRect(60, 45, 200, 60);
    g.setColor(Palette.LIGHT_GREEN);
    g.drawRect(60, 45, 200, 60);
    g.setFont(new Font("Monospaced", Font.BOLD, 14));
    g.drawString("LEVEL COMPLETE!", 92, 70);
    g.setFont(new Font("Monospaced", Font.PLAIN, 10));
    g.setColor(Palette.WHITE);
    g.drawString("BONUS: +2000 PTS", 108, 88);
}

// --- CollisionCallback Implementations ---
@Override
public void onScoreAdded(int points) {
    if (points <= 0) return;
    this.score = Math.min(999_999, this.score + points);
    
    // Check extra life
    if (this.score >= nextExtraLifeScore) {
        nextExtraLifeScore += 20000;
        if (dave != null) {
            dave.awardExtraLife();
            audio.playSound(SoundEffect.TROPHY);
        }
    }
}

@Override
public void onTrophyCollected() {
    // Trophy unlocked! Flashes banner in HUD
}

@Override
public void onLevelCompleted() {
    stateMachine.transitionTo(GameState.LEVEL_COMPLETE);
}

@Override
public void onPlayerDied() {
    // Player death initiated - screen shake handled in specific collision methods
}

@Override
public void onSoundTriggered(SoundEffect sfx) {
    audio.playSound(sfx);
}

@Override
public void onScreenShake(int frames, float intensity) {
    this.screenShakeFrames = frames;
    this.screenShakeIntensity = intensity;
}

// Accessors for testing & inspection
public GameState getGameState() { return stateMachine.getCurrentState(); }
public StateMachine getStateMachine() { return stateMachine; }
public Level getCurrentLevel() { return currentLevel; }
public Dave getDave() { return dave; }
public int getScore() { return score; }
public void setScore(int score) { this.score = Math.max(0, Math.min(999_999, score)); }
}