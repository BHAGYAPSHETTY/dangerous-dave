package com.dave.core;

/**
 * Validated finite state machine for game lifecycle management.
 * Strictly prevents illegal state transitions (e.g. continuing normal gameplay while dead or in game over).
 */
public class StateMachine {
    private GameState currentState;
    private GameState previousState;
    private int ticksInCurrentState;

    public StateMachine(GameState initialState) {
        this.currentState = initialState;
        this.previousState = null;
        this.ticksInCurrentState = 0;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public int getTicksInCurrentState() {
        return ticksInCurrentState;
    }

    public void tick() {
        ticksInCurrentState++;
    }

    /**
     * Attempts to transition to a new game state.
     * @param targetState desired state
     * @return true if valid and applied, false if rejected as an invalid state transition.
     */
    public synchronized boolean transitionTo(GameState targetState) {
        if (targetState == null || targetState == currentState) {
            return false;
        }

        if (!isValidTransition(currentState, targetState)) {
            System.err.println("Illegal State Transition Rejected: " + currentState + " -> " + targetState);
            return false;
        }

        this.previousState = this.currentState;
        this.currentState = targetState;
        this.ticksInCurrentState = 0;
        return true;
    }

    public static boolean isValidTransition(GameState from, GameState to) {
        switch (from) {
            case MAIN_MENU:
                return to == GameState.PLAYING;

            case PLAYING:
                return to == GameState.PAUSED ||
                       to == GameState.LEVEL_COMPLETE ||
                       to == GameState.GAME_OVER;

            case PAUSED:
                return to == GameState.PLAYING ||
                       to == GameState.MAIN_MENU;

            case LEVEL_COMPLETE:
                return to == GameState.PLAYING ||
                       to == GameState.VICTORY;

            case GAME_OVER:
                return to == GameState.MAIN_MENU ||
                       to == GameState.PLAYING;

            case VICTORY:
                return to == GameState.MAIN_MENU ||
                       to == GameState.PLAYING;

            default:
                return false;
        }
    }
}
