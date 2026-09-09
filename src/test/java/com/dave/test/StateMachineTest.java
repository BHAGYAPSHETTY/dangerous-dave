package com.dave.test;

import com.dave.core.GameState;
import com.dave.core.StateMachine;

public class StateMachineTest {
    public static void runAll() {
        testValidTransitions();
        testInvalidTransitionsRejected();
        System.out.println("  [PASS] StateMachineTest - All state safety tests passed.");
    }

    private static void testValidTransitions() {
        StateMachine sm = new StateMachine(GameState.MAIN_MENU);
        if (!sm.transitionTo(GameState.PLAYING)) {
            throw new AssertionError("MAIN_MENU -> PLAYING should be valid!");
        }
        if (!sm.transitionTo(GameState.PAUSED)) {
            throw new AssertionError("PLAYING -> PAUSED should be valid!");
        }
        if (!sm.transitionTo(GameState.PLAYING)) {
            throw new AssertionError("PAUSED -> PLAYING should be valid!");
        }
        if (!sm.transitionTo(GameState.LEVEL_COMPLETE)) {
            throw new AssertionError("PLAYING -> LEVEL_COMPLETE should be valid!");
        }
        if (!sm.transitionTo(GameState.PLAYING)) {
            throw new AssertionError("LEVEL_COMPLETE -> PLAYING should be valid!");
        }
        if (!sm.transitionTo(GameState.GAME_OVER)) {
            throw new AssertionError("PLAYING -> GAME_OVER should be valid!");
        }
    }

    private static void testInvalidTransitionsRejected() {
        StateMachine sm = new StateMachine(GameState.MAIN_MENU);

        // Cannot skip directly to VICTORY or LEVEL_COMPLETE from MAIN_MENU
        if (sm.transitionTo(GameState.VICTORY)) {
            throw new AssertionError("EXPLOIT DETECTED: MAIN_MENU -> VICTORY should be prohibited!");
        }
        if (sm.transitionTo(GameState.LEVEL_COMPLETE)) {
            throw new AssertionError("EXPLOIT DETECTED: MAIN_MENU -> LEVEL_COMPLETE should be prohibited!");
        }

        sm.transitionTo(GameState.PLAYING);
        // Cannot jump directly to VICTORY from PLAYING without LEVEL_COMPLETE
        if (sm.transitionTo(GameState.VICTORY)) {
            throw new AssertionError("EXPLOIT DETECTED: PLAYING -> VICTORY without LEVEL_COMPLETE should be prohibited!");
        }
    }
}
