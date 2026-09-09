package com.dave;

import com.dave.core.GameEngine;
import com.dave.core.InputHandler;
import com.dave.ui.DaveCanvas;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Application entry point for Dangerous Dave.
 */
public class Main {
    public static final String GAME_TITLE = "Dangerous Dave (1990) — Remastered";
    public static final int DEFAULT_SCALE = 3; // 320x200 * 3 = 960x600

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(GAME_TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

            InputHandler input = new InputHandler();
            DaveCanvas canvas = new DaveCanvas(DEFAULT_SCALE);
            canvas.addKeyListener(input);

            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);

            canvas.requestFocusInWindow();

            GameEngine engine = new GameEngine(canvas, input);
            engine.start();

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    engine.stop();
                }
            });
        });
    }
}
