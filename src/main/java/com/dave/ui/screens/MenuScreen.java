package com.dave.ui.screens;

import com.dave.gfx.Palette;

import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Title and main menu screen for Dangerous Dave.
 */
public class MenuScreen {
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 22);
    private static final Font SUBTITLE_FONT = new Font("Monospaced", Font.BOLD, 12);
    private static final Font TEXT_FONT = new Font("Monospaced", Font.PLAIN, 10);
    private int animTick = 0;

    public void render(Graphics2D g) {
        animTick++;

        // Dark retro background
        g.setColor(Palette.BLACK);
        g.fillRect(0, 0, 320, 200);

        // Outer decorative border
        g.setColor(Palette.BLUE);
        g.drawRect(4, 4, 311, 191);
        g.setColor(Palette.LIGHT_BLUE);
        g.drawRect(6, 6, 307, 187);

        // Title: DANGEROUS DAVE
        g.setFont(TITLE_FONT);
        g.setColor(Palette.RED);
        g.drawString("DANGEROUS DAVE", 48, 42);
        g.setColor(Palette.YELLOW);
        g.drawString("DANGEROUS DAVE", 46, 40);

        g.setFont(SUBTITLE_FONT);
        g.setColor(Palette.LIGHT_CYAN);
        g.drawString("IN THE HIDEOUT OF CLYDE COOPER", 44, 62);

        // Controls box
        g.setColor(Palette.DARK_GRAY);
        g.fillRect(20, 75, 280, 80);
        g.setColor(Palette.LIGHT_GRAY);
        g.drawRect(20, 75, 280, 80);

        g.setFont(TEXT_FONT);
        g.setColor(Palette.WHITE);
        g.drawString("CONTROLS:", 30, 90);
        g.setColor(Palette.LIGHT_GREEN);
        g.drawString("Arrow Keys / WASD  : Move / Jump / Fly", 30, 105);
        g.drawString("Ctrl / Space / F   : Shoot Gun (when held)", 30, 118);
        g.drawString("Alt / J            : Toggle Jetpack (when held)", 30, 131);
        g.drawString("P: Pause  |  R: Restart  |  M: Mute Audio", 30, 144);

        // Flashing "PRESS ENTER TO PLAY"
        if (animTick % 40 < 25) {
            g.setFont(SUBTITLE_FONT);
            g.setColor(Palette.WHITE);
            g.drawString("PRESS ENTER OR SPACE TO START", 44, 180);
        }
    }
}
