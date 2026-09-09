package com.dave.ui.screens;

import com.dave.gfx.Palette;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Game Over screen shown when Dave runs out of lives.
 */
public class GameOverScreen {
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 26);
    private static final Font TEXT_FONT = new Font("Monospaced", Font.BOLD, 12);
    private int animTick = 0;

    public void render(Graphics2D g, int finalScore, int reachedLevel) {
        animTick++;

        g.setColor(Palette.BLACK);
        g.fillRect(0, 0, 320, 200);

        g.setColor(Palette.RED);
        g.drawRect(8, 8, 303, 183);

        g.setFont(TITLE_FONT);
        g.setColor(Palette.RED);
        g.drawString("GAME OVER", 82, 65);
        g.setColor(Palette.YELLOW);
        g.drawString("GAME OVER", 80, 63);

        g.setFont(TEXT_FONT);
        g.setColor(Palette.WHITE);
        g.drawString("YOU WERE DEFEATED BY CLYDE'S LAIR!", 32, 100);

        g.setColor(Palette.LIGHT_CYAN);
        g.drawString("REACHED: LEVEL " + reachedLevel, 100, 120);

        g.setColor(Palette.YELLOW);
        g.drawString(String.format("FINAL SCORE: %06d", finalScore), 85, 140);

        if (animTick % 40 < 25) {
            g.setColor(Palette.WHITE);
            g.drawString("PRESS R TO RESTART OR ENTER FOR MENU", 25, 175);
        }
    }
}
