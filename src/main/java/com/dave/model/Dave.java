package com.dave.model;
import com.dave.physics.BoundingBox;

/**
Dave - The main protagonist of Dangerous Dave.
Encapsulates movement physics, jumping arc, jetpack flight, shooting, and life states.
*/
public class Dave {
// Physical dimensions (14x16 pixels)
public static final float WIDTH = 14f;
public static final float HEIGHT = 16f;

// Movement constants
public static final float WALK_SPEED = 1.8f;
public static final float JUMP_VELOCITY = -4.8f;  // Increased from -4.2f
public static final float GRAVITY = 0.20f;        // Reduced from 0.22f for floatier jump
public static final float MAX_FALL_SPEED = 4.5f;  // Slightly increased fall speed
public static final float JETPACK_SPEED = 2.0f;
public static final int MAX_JETPACK_FUEL = 60;

// Position and velocity
private float x;
private float y;
private float vx;
private float vy;
private float spawnX;
private float spawnY;

// Bounding Box
private final BoundingBox bounds;

// Physics and State Flags
private boolean grounded;
private boolean facingLeft;
private boolean dead;
private int explosionTimer;

// Coyote Time & Jump Buffering (Game Feel Improvements)
private int coyoteTimeFrames = 0;
private int jumpBufferFrames = 0;

// Equipment
private boolean hasGun;
private boolean hasJetpack;
private boolean jetpackActive;
private int jetpackFuel;
private int jetpackFuelTick;
private int shootCooldown;

// Lives
private int davesRemaining; // Spare lives (starts at 3)

// Animation state
private int walkFrame;
private int walkTick;

public Dave(float startX, float startY) {
    this.spawnX = startX;
    this.spawnY = startY;
    this.x = startX;
    this.y = startY;
    this.vx = 0;
    this.vy = 0;
    this.bounds = new BoundingBox(x, y, WIDTH, HEIGHT);
    this.grounded = false;
    this.facingLeft = false;
    this.dead = false;
    this.explosionTimer = 0;
    this.hasGun = false;
    this.hasJetpack = false;
    this.jetpackActive = false;
    this.jetpackFuel = 0;
    this.jetpackFuelTick = 0;
    this.shootCooldown = 0;
    this.davesRemaining = 3;
    this.walkFrame = 0;
    this.walkTick = 0;
    this.coyoteTimeFrames = 0;
    this.jumpBufferFrames = 0;
}

/**
Resets Dave's position and physics state to the level spawn point upon respawning.
*/
public void respawnAtSpawn() {
    this.x = spawnX;
    this.y = spawnY;
    this.vx = 0;
    this.vy = 0;
    this.bounds.setPosition(x, y);
    this.grounded = false;
    this.dead = false;
    this.explosionTimer = 0;
    this.jetpackActive = false;
    this.shootCooldown = 0;
    this.coyoteTimeFrames = 0;
    this.jumpBufferFrames = 0;
}

/**
Completely resets Dave for a fresh level attempt (e.g. equipment reset if applicable).
*/
public void resetForLevel(float startX, float startY) {
    this.spawnX = startX;
    this.spawnY = startY;
    this.hasGun = false;
    this.hasJetpack = false;
    this.jetpackActive = false;
    this.jetpackFuel = 0;
    this.jetpackFuelTick = 0;
    respawnAtSpawn();
}

public void updatePhysics() {
    if (dead) {
        vx = 0;
        vy = 0;
        if (explosionTimer > 0) {
            explosionTimer--;
        }
        return;
    }
    
    if (shootCooldown > 0) {
        shootCooldown--;
    }
    
    // Update coyote time (grace period for jumping after leaving platform)
    if (grounded) {
        coyoteTimeFrames = 6; // 6 frames (~100ms) of grace period
    } else {
        if (coyoteTimeFrames > 0) coyoteTimeFrames--;
    }
    
    // Update jump buffer
    if (jumpBufferFrames > 0) {
        jumpBufferFrames--;
    }
    
    if (jetpackActive && hasJetpack) {
        // FIX: Auto-deactivate jetpack if Dave lands on ground
        if (grounded) {
            jetpackActive = false;
        } else {
            // Jetpack burns fuel when active
            jetpackFuelTick++;
            if (jetpackFuelTick >= 6) { // 1 fuel every 6 ticks (approx 10 seconds of continuous flight)
                jetpackFuelTick = 0;
                jetpackFuel--;
                if (jetpackFuel <= 0) {
                    jetpackFuel = 0;
                    jetpackActive = false;
                    hasJetpack = false;
                }
            }
        }
        // No gravity while jetpacking
    } else {
        // Apply normal gravity
        vy += GRAVITY;
        if (vy > MAX_FALL_SPEED) {
            vy = MAX_FALL_SPEED;
        }
    }
    
    // Animate walking when moving horizontally on the ground
    if (grounded && Math.abs(vx) > 0.1f) {
        walkTick++;
        if (walkTick >= 6) {
            walkTick = 0;
            walkFrame = (walkFrame + 1) % 4;
        }
    } else {
        walkTick = 0;
        walkFrame = 0;
    }
}

/**
Requests a jump (buffers the input for game feel).
*/
public void requestJump() {
    if (dead || jetpackActive) {
        jumpBufferFrames = 0;
        return;
    }
    this.jumpBufferFrames = 6; // Store the input for 6 frames (~100ms)
}

/**
Processes buffered jump request with coyote time support.
@return true if jump was executed, false otherwise
*/
public boolean processJump() {
    if (dead || jetpackActive) {
        jumpBufferFrames = 0;
        return false;
    }
    
    // If we have a buffered input AND we are grounded or in coyote time
    if (jumpBufferFrames > 0 && (grounded || coyoteTimeFrames > 0)) {
        vy = JUMP_VELOCITY;
        grounded = false;
        coyoteTimeFrames = 0;
        jumpBufferFrames = 0;
        return true;
    }
    return false;
}

/**
Legacy jump method for backward compatibility.
*/
public boolean jump() {
    if (dead || jetpackActive) {
        return false;
    }
    // Check if grounded or in coyote time
    if (!grounded && coyoteTimeFrames <= 0) {
        return false;
    }
    vy = JUMP_VELOCITY;
    grounded = false;
    coyoteTimeFrames = 0;
    return true;
}

/**
Triggers Dave's death sequence.
Prevents multi-death triggering if already dead.
*/
public boolean die() {
    if (dead) return false;
    this.dead = true;
    this.explosionTimer = 40; // 40 ticks (~667ms) of death explosion
    this.vx = 0;
    this.vy = 0;
    this.jetpackActive = false;
    this.coyoteTimeFrames = 0;
    this.jumpBufferFrames = 0;
    return true;
}

/**
Toggles jetpack on/off if Dave has collected one.
*/
public void toggleJetpack() {
    if (!hasJetpack || dead || jetpackFuel <= 0) {
        jetpackActive = false;
        return;
    }
    jetpackActive = !jetpackActive;
    if (jetpackActive) {
        vy = 0;
        grounded = false;
    }
}

/**
Equips gun pickup.
*/
public void equipGun() {
    this.hasGun = true;
}

/**
Equips jetpack pickup with full fuel tank.
*/
public void equipJetpack() {
    this.hasJetpack = true;
    this.jetpackFuel = MAX_JETPACK_FUEL;
    this.jetpackFuelTick = 0;
}

/**
Attempts to shoot a bullet.
@return Projectile if successfully fired, null otherwise.
*/
public Projectile shoot() {
    if (dead || !hasGun || shootCooldown > 0) {
        return null;
    }
    shootCooldown = 18; // Cooldown between shots
    float bulletVx = facingLeft ? -4.5f : 4.5f;
    float bulletX = facingLeft ? (x - 6f) : (x + WIDTH);
    float bulletY = y + 6f;
    return new Projectile(bulletX, bulletY, bulletVx, 0, true);
}

// Getters and Setters
public float getX() { return x; }
public void setX(float x) { this.x = x; bounds.setPosition(x, y); }
public float getY() { return y; }
public void setY(float y) { this.y = y; bounds.setPosition(x, y); }
public float getVx() { return vx; }
public void setVx(float vx) { this.vx = vx; }
public float getVy() { return vy; }
public void setVy(float vy) { this.vy = vy; }
public BoundingBox getBounds() { return bounds; }
public boolean isGrounded() { return grounded; }
public void setGrounded(boolean grounded) { this.grounded = grounded; }
public boolean isFacingLeft() { return facingLeft; }
public void setFacingLeft(boolean facingLeft) { this.facingLeft = facingLeft; }
public boolean isDead() { return dead; }
public int getExplosionTimer() { return explosionTimer; }
public boolean hasGun() { return hasGun; }
public boolean hasJetpack() { return hasJetpack; }
public boolean isJetpackActive() { return jetpackActive; }
public void setJetpackActive(boolean active) { this.jetpackActive = active; }
public int getJetpackFuel() { return jetpackFuel; }
public int getDavesRemaining() { return davesRemaining; }
public void setDavesRemaining(int lives) { this.davesRemaining = lives; }
public void awardExtraLife() { this.davesRemaining++; }
public int getWalkFrame() { return walkFrame; }
public int getCoyoteTimeFrames() { return coyoteTimeFrames; }
public int getJumpBufferFrames() { return jumpBufferFrames; }
}