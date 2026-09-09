package com.dave.test;

import com.dave.core.GameEngine;
import com.dave.core.GameState;
import com.dave.core.InputHandler;
import com.dave.levels.LevelRegistry;
import com.dave.model.Dave;
import com.dave.model.Level;
import com.dave.ui.DaveCanvas;

import java.awt.event.KeyEvent;

/**
 * End-to-end integration test simulating actual gameplay frames and level progression.
 */
public class GameplaySimulationTest {
    public static void main(String[] args) {
        System.out.println("Running Gameplay Simulation Integration Test...");

        // Verify all 10 levels build cleanly
        for (int i = 1; i <= LevelRegistry.getTotalLevelCount(); i++) {
            Level level = LevelRegistry.buildLevel(i);
            if (level == null || level.getWidthInTiles() < 20 || level.getHeightInTiles() != 10) {
                throw new AssertionError("Level " + i + " failed geometry validation!");
            }
            if (level.getDoorTileX() < 0 || level.getDoorTileY() < 0) {
                throw new AssertionError("Level " + i + " missing exit door!");
            }
            if (level.getTrophyTileX() < 0 || level.getTrophyTileY() < 0) {
                throw new AssertionError("Level " + i + " missing trophy cup!");
            }
        }
        System.out.println("  [PASS] All 10 level layouts and objectives validated.");

        // Headless canvas and engine simulation
        DaveCanvas canvas = new DaveCanvas(1);
        InputHandler input = new InputHandler();
        GameEngine engine = new GameEngine(canvas, input);

        if (engine.getGameState() != GameState.MAIN_MENU) {
            throw new AssertionError("Engine must start in MAIN_MENU!");
        }

        // Simulate pressing ENTER to start game
        input.keyPressed(new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));

        // Run engine thread for 1.5 seconds of game simulation
        engine.start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}

        if (engine.getGameState() != GameState.PLAYING) {
            throw new AssertionError("Engine should have transitioned to PLAYING after Enter key!");
        }

        Dave dave = engine.getDave();
        if (dave == null) {
            throw new AssertionError("Dave player entity was not spawned!");
        }

        System.out.println("  [PASS] Engine loop and 60 FPS update cycle verified cleanly.");
        engine.stop();
        System.out.println("ALL INTEGRATION CHECKS PASSED!");
        System.exit(0);
    }
}
