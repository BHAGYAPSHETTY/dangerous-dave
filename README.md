# Dangerous Dave (1990) — Remastered in Java

An authentic, independently coded, modernized, and loophole-resistant recreation of John Romero's classic DOS platformer **Dangerous Dave in the Deserted Pirate's Hideout** (1990 Softdisk release).

Built in **Pure Java 17+** with zero external native dependencies (no OpenGL/LWJGL DLL crashes), featuring procedural 16-color EGA pixel-art graphics and a real-time 8-bit software audio synthesizer via the standard Java Sound API.

---

## 🎮 Gameplay & Objectives

Dave must infiltrate Clyde Cooper's hideout across **10 dangerous levels** to recover the stolen gold trophies!

1. **The Primary Objective**: In each level, locate and collect the **Golden Trophy / Chalice**.
2. **Door Unlock**: Collecting the trophy unlocks the exit door and triggers the iconic flashing notification: `"GO THRU THE DOOR!"`.
3. **Level Advance**: Entering the unlocked door completes the level and awards a **+2,000 point bonus**!
4. **Hazards**: Fire pits, acid/water pools, and poisonous purple weeds cause instant death.
5. **Monsters**: From Level 3 onward, creatures patrol designated paths and fire deadly projectiles. If Dave collides with a monster, both Dave and the monster explode!
6. **Special Equipment**:
   - **Hunting Gun**: Allows Dave to fire bullets horizontally, eliminating creatures and awarding score points.
   - **Jetpack**: Features a 60-unit fuel gauge. Pressing the Jetpack key enables 8-way directional flight without gravity until fuel is exhausted.
7. **Lives & Scoring**: Dave starts with 3 spare lives (4 total). Every 20,000 points earned awards an **Extra Life**!

---

## 🕹️ Controls

| Action | Primary Key | Alternate Key | Notes |
| :--- | :--- | :--- | :--- |
| **Walk Left / Right** | `Left Arrow` / `Right Arrow` | `A` / `D` | Smooth authentic platformer velocity |
| **Jump** | `Up Arrow` | `W` or `Space` | Authentic parabolic arc; only when grounded |
| **Fly (Jetpack)** | `Arrow Keys` (8-way) | `W` `A` `S` `D` | Active when Jetpack is toggled ON |
| **Toggle Jetpack** | `Alt` | `J` | Requires Jetpack pickup and remaining fuel |
| **Shoot Gun** | `Ctrl` | `F` or `Space` | Requires Gun pickup; has firing cooldown |
| **Pause / Resume** | `P` | — | Freezes game clock and physics |
| **Restart Level/Game**| `R` | — | Resets level or restarts from game over |
| **Toggle Mute** | `M` | — | Silences synthesized 8-bit sound effects |
| **Start / Advance** | `Enter` | `Space` | Advances menus and title screens |

---

## 🛡️ Loophole & Exploit Prevention Report

As part of Phase 5 & 6 security and QA requirements, the following exploits common to classic platformers were systematically analyzed and resolved:

| Category | Exploit / Vulnerability | Root Cause in Classic Engine | Our Hardened Solution |
| :--- | :--- | :--- | :--- |
| **Movement** | Wall clipping / tunneling | Discrete addition of large velocity deltas through thin 16px walls | Swept-AABB sub-step collision resolution. X and Y axes are solved independently with boundary clamping. |
| **Movement** | Infinite air jumping | Checking jump input without strict grounding | Dave can only jump if strictly grounded (`isGrounded == true`) on top of a solid tile AND the jump input was a fresh press event. |
| **Movement** | Escaping map boundaries | Missing coordinate boundaries | Hard clamping `0 <= x <= mapWidth - 14`. Any fall below `y > mapHeight + 10` immediately triggers lethal pit death. |
| **Score** | Infinite score farming via death | Player collects items, dies, respawns with score intact while items reappear | **Anti-Farming Checkpoint Rollback**: Upon death, score is restored to the checkpoint score at the START of the current level attempt, and items are reset. |
| **Score** | Score arithmetic overflow | Unchecked integer addition | Clamped between `0` and `999,999` with validation (`addScore(points)` rejects negative or zero points). |
| **Gameplay** | Exit door skipping | Missing trophy possession check | The exit door tile remains solid and locked until `level.isTrophyCollected() == true`. Reaching the door without the trophy does not advance. |
| **Gameplay** | Multi-trigger level completion | Consecutive collision frames firing level advance multiple times | State transition to `LEVEL_COMPLETE` is atomic and idempotent. Dave's input and collision are frozen during transition. |
| **Death** | Actions while dying (Ghost Dave) | Input handling not disabled during death animation | Once `die()` is called, `isDead` flag disables all input, zeroes velocity, and detaches collectible pickups. |
| **Equipment**| Infinite Jetpack flight | Fuel check not synchronized with flight ticks | Jetpack burns 1 fuel unit every 6 flight ticks. When fuel <= 0, jetpack immediately cuts thrust and Dave drops under gravity. |

---

## 🏗️ Architecture & Class Structure

```
com.dave/
├── Main.java                     # Application entry point & Swing JFrame setup
├── core/
│   ├── GameEngine.java           # Fixed 60 FPS loop, update & render orchestration
│   ├── GameState.java            # State enum (MAIN_MENU, PLAYING, PAUSED, LEVEL_COMPLETE, GAME_OVER, VICTORY)
│   ├── StateMachine.java         # Validated state transitions preventing illegal jumps
│   └── InputHandler.java         # Key listener with edge detection (press vs hold)
├── model/
│   ├── Dave.java                 # Player entity: physics, jump arc, jetpack, gun, lives
│   ├── Level.java                # Tilemap grid, entities, spawn coordinates, anti-farming reset
│   ├── Enemy.java                # Monster base: patrol paths, shooting cooldown, explosion
│   ├── EnemyType.java            # 8 monster types across levels (Spider, Sun, Skull, UFO, etc.)
│   ├── Collectible.java          # Pickups: Trophy, Diamonds, Rubies, Rings, Sceptres, Crowns
│   ├── CollectibleType.java      # Score values and equipment tags
│   ├── TileType.java             # Brick, Pipe, Girder, Tree, Star, Door, Fire, Water, Weed
│   ├── HazardType.java           # Fire, Water, Weed hazard definitions
│   └── Projectile.java           # Dave bullets and enemy projectiles
├── physics/
│   ├── BoundingBox.java          # Axis-Aligned Bounding Box (AABB) intersection tests
│   └── CollisionSystem.java      # Swept-AABB continuous collision resolution against tiles and entities
├── levels/
│   └── LevelRegistry.java        # Authentic construction of all 10 levels and challenges
├── audio/
│   ├── SoundEffect.java          # Sound event enum (JUMP, PICKUP, TROPHY, SHOOT, EXPLODE, JETPACK, DOOR)
│   └── RetroAudioEngine.java     # Procedural 8-bit square/noise wave synthesizer via Java Sound API
├── gfx/
│   ├── Palette.java              # Authentic 16-color EGA/VGA palette
│   ├── SpriteSheet.java          # Procedural 16x16 pixel-art rasterizer for all entities
│   └── Camera.java               # Smooth horizontal tracking viewport (320x160)
└── ui/
    ├── DaveCanvas.java           # Double-buffered 320x200 buffer with integer scaling
    ├── HUD.java                  # Retro bottom status bar (Score, Lives, Level, Jetpack, Banner)
    └── screens/
        ├── MenuScreen.java       # Title screen & controls guide
        ├── GameOverScreen.java   # Game over prompt & final score
        └── VictoryScreen.java    # Grand finale victory screen
```

---

## 🧪 Testing & Verification

The project includes an automated test harness that runs headlessly without external dependencies:

```bash
java -cp out com.dave.test.TestRunner
```

### Test Suites Included:
1. `CollisionTest`: Verifies AABB intersection math, solid wall stopping (no clipping), lethal hazard triggers, and map boundary clamping.
2. `PlayerPhysicsTest`: Validates authentic parabolic jump arcs, air-jump prevention, terminal fall velocity clamping, and jetpack fuel depletion.
3. `ExploitTest`: Verifies locked door protection, prevention of duplicate item collection, death score rollback (anti-farming), weapon gating, and score bounds clamping.
4. `StateMachineTest`: Tests safety and validation of all finite state machine transitions.

---

## 🚀 How to Build and Run

### Requirements
- **Java Development Kit (JDK) 17 or higher** installed on your system.

### Option 1: 1-Click Batch Scripts (Windows)
- To build and test:
  ```cmd
  build.bat
  ```
- To launch the game:
  ```cmd
  run.bat
  ```

### Option 2: Manual Terminal Compilation
```cmd
# 1. Create output directory
mkdir out

# 2. Compile all source and test files
javac -d out -sourcepath src/main/java;src/test/java src/main/java/com/dave/Main.java src/main/java/com/dave/core/*.java src/main/java/com/dave/model/*.java src/main/java/com/dave/physics/*.java src/main/java/com/dave/levels/*.java src/main/java/com/dave/gfx/*.java src/main/java/com/dave/audio/*.java src/main/java/com/dave/ui/*.java src/main/java/com/dave/ui/screens/*.java src/test/java/com/dave/test/*.java

# 3. Run automated tests
java -cp out com.dave.test.TestRunner

# 4. Package executable JAR
jar cfe DangerousDave.jar com.dave.Main -C out com

# 5. Play!
java -jar DangerousDave.jar
```

### Option 3: Standard Maven (if Maven is installed)
```cmd
mvn clean package
java -jar target/dangerous-dave-1.0.0.jar
```
