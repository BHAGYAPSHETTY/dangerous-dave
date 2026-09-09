package com.dave.ui;

import com.dave.gfx.Palette;
import com.dave.model.Dave;
import com.dave.model.Level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Authentic retro bottom HUD status bar (y: 160 to 200 on the 320x200 canvas).
 * Displays Score, Lives, Level number, Jetpack fuel meter, Gun status, and the iconic "GO THRU THE DOOR!" banner.
 */
public class HUD {
    public static final int HUD_Y = 160;
    public static final int HUD_HEIGHT = 40;

    private static final Font RETRO_FONT = new Font("Monospaced", Font.BOLD, 10);
    private static final Font BANNER_FONT = new Font("Monospaced", Font.BOLD, 12);

    private int flashTick = 0;

    public void render(Graphics2D g, Dave dave, Level level, int score) {
        flashTick++;

        // Status bar background
        g.setColor(Palette.BLACK);
        g.fillRect(0, HUD_Y, 320, HUD_HEIGHT);

        // Top separator border line
        g.setColor(Palette.BROWN);
        g.drawLine(0, HUD_Y, 320, HUD_Y);
        g.drawLine(0, HUD_Y + 1, 320, HUD_Y + 1);

        g.setFont(RETRO_FONT);

        // 1. SCORE
        g.setColor(Palette.WHITE);
        g.drawString("SCORE:", 10, HUD_Y + 14);
        g.setColor(Palette.YELLOW);
        g.drawString(String.format("%06d", score), 52, HUD_Y + 14);

        // 2. DAVES (Lives)
        g.setColor(Palette.WHITE);
        g.drawString("DAVES:", 110, HUD_Y + 14);
        g.setColor(Palette.LIGHT_CYAN);
        int lives = Math.max(0, dave.getDavesRemaining());
        g.drawString(String.valueOf(lives), 154, HUD_Y + 14);

        // Draw miniature Dave heads for each spare life
        for (int i = 0; i < Math.min(lives, 5); i++) {
            int hx = 168 + i * 9;
            g.setColor(Palette.RED);
            g.fillRect(hx, HUD_Y + 7, 6, 3);
            g.setColor(Palette.SKIN_TONE);
            g.fillRect(hx + 1, HUD_Y + 10, 4, 3);
        }

        // 3. LEVEL
        g.setColor(Palette.WHITE);
        g.drawString("LEVEL:", 230, HUD_Y + 14);
        g.setColor(Palette.LIGHT_GREEN);
        g.drawString(String.valueOf(level.getLevelNumber()), 275, HUD_Y + 14);

        // --- SECOND ROW: EQUIPMENT AND NOTIFICATION BANNER ---

        // A. GUN INDICATOR
        if (dave.hasGun()) {
            g.setColor(Palette.LIGHT_RED);
            g.drawString("GUN", 10, HUD_Y + 30);
        }

        // B. JETPACK FUEL BAR
        if (dave.hasJetpack()) {
            g.setColor(Palette.LIGHT_MAGENTA);
            g.drawString("JETPACK", 45, HUD_Y + 30);

            // Fuel Bar Background
            g.setColor(Palette.DARK_GRAY);
            g.fillRect(95, HUD_Y + 22, 60, 9);
            g.setColor(Palette.LIGHT_GRAY);
            g.drawRect(95, HUD_Y + 22, 60, 9);

            // Fuel Fill
            int fuel = dave.getJetpackFuel();
            int fuelWidth = (int) ((fuel / (float) Dave.MAX_JETPACK_FUEL) * 58);
            if (fuelWidth > 0) {
                g.setColor(fuel > 15 ? Palette.LIGHT_GREEN : Palette.RED);
                g.fillRect(96, HUD_Y + 23, fuelWidth, 8);
            }
        }

        // C. "GO THRU THE DOOR!" FLASHING BANNER
        if (level.isTrophyCollected() && !level.isCompleted()) {
            // Flash between Yellow and White
            if (flashTick % 30 < 18) {
                g.setFont(BANNER_FONT);
                g.setColor(Palette.YELLOW);
                g.drawString("GO THRU THE DOOR!", 170, HUD_Y + 32);
            }
        }
    }
}
