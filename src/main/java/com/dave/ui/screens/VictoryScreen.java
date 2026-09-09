package com.dave.ui.screens;

import com.dave.gfx.Palette;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Victory screen displayed when Dave beats Level 10 and retrieves all trophies.
 */
public class VictoryScreen {
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 24);
    private static final Font TEXT_FONT = new Font("Monospaced", Font.BOLD, 12);
    private int animTick = 0;

    public void render(Graphics2D g, int finalScore) {
        animTick++;

        g.setColor(Palette.BLACK);
        g.fillRect(0, 0, 320, 200);

        g.setColor(Palette.YELLOW);
        g.drawRect(8, 8, 303, 183);

        g.setFont(TITLE_FONT);
        g.setColor(Palette.RED);
        g.drawString("VICTORY!", 102, 50);
        g.setColor(Palette.YELLOW);
        g.drawString("VICTORY!", 100, 48);

        g.setFont(TEXT_FONT);
        g.setColor(Palette.WHITE);
        g.drawString("CONGRATULATIONS DAVE!", 75, 80);
        g.setColor(Palette.LIGHT_GREEN);
        g.drawString("YOU RECOVERED ALL 10 TROPHIES AND", 35, 100);
        g.drawString("CONQUERED CLYDE COOPER'S HIDEOUT!", 35, 116);

        g.setColor(Palette.LIGHT_CYAN);
        g.drawString(String.format("FINAL SCORE: %06d", finalScore), 85, 145);

        if (animTick % 40 < 25) {
            g.setColor(Palette.WHITE);
            g.drawString("PRESS ENTER OR R TO PLAY AGAIN", 45, 175);
        }
    }
}
