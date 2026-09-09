package com.dave.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * Keyboard input handler with edge detection (press vs hold) to prevent input stutter and key bouncing.
 */
public class InputHandler implements KeyListener {
    private static final int KEY_COUNT = 256;
    private final boolean[] keysDown = new boolean[KEY_COUNT];
    private final boolean[] keysPressed = new boolean[KEY_COUNT];
    private final boolean[] keysReleased = new boolean[KEY_COUNT];

    public synchronized void poll() {
        for (int i = 0; i < KEY_COUNT; i++) {
            keysPressed[i] = false;
            keysReleased[i] = false;
        }
    }

    public synchronized boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return keysDown[keyCode];
        }
        return false;
    }

    public synchronized boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return keysPressed[keyCode];
        }
        return false;
    }

    public synchronized boolean isKeyReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            return keysReleased[keyCode];
        }
        return false;
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < KEY_COUNT) {
            if (!keysDown[code]) {
                keysPressed[code] = true;
            }
            keysDown[code] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < KEY_COUNT) {
            keysDown[code] = false;
            keysReleased[code] = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public synchronized void resetAll() {
        Arrays.fill(keysDown, false);
        Arrays.fill(keysPressed, false);
        Arrays.fill(keysReleased, false);
    }
}
