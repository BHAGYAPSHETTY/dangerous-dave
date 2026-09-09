package com.dave.gfx;

import java.awt.Color;

/**
 * Authentic 16-color EGA / VGA palette used in the original Dangerous Dave (1990).
 */
public final class Palette {
    private Palette() {}

    public static final Color BLACK        = new Color(0x00, 0x00, 0x00);
    public static final Color BLUE         = new Color(0x00, 0x00, 0xAA);
    public static final Color GREEN        = new Color(0x00, 0xAA, 0x00);
    public static final Color CYAN         = new Color(0x00, 0xAA, 0xAA);
    public static final Color RED          = new Color(0xAA, 0x00, 0x00);
    public static final Color MAGENTA      = new Color(0xAA, 0x00, 0xAA);
    public static final Color BROWN        = new Color(0xAA, 0x55, 0x00);
    public static final Color LIGHT_GRAY   = new Color(0xAA, 0xAA, 0xAA);
    public static final Color DARK_GRAY    = new Color(0x55, 0x55, 0x55);
    public static final Color LIGHT_BLUE   = new Color(0x55, 0x55, 0xFF);
    public static final Color LIGHT_GREEN  = new Color(0x55, 0xFF, 0x55);
    public static final Color LIGHT_CYAN   = new Color(0x55, 0xFF, 0xFF);
    public static final Color LIGHT_RED    = new Color(0xFF, 0x55, 0x55);
    public static final Color LIGHT_MAGENTA= new Color(0xFF, 0x55, 0xFF);
    public static final Color YELLOW       = new Color(0xFF, 0xFF, 0x55);
    public static final Color WHITE        = new Color(0xFF, 0xFF, 0xFF);

    // Common skin tone for Dave
    public static final Color SKIN_TONE    = new Color(0xFF, 0xBB, 0x88);
    public static final Color TRANSPARENT  = new Color(0, 0, 0, 0);
}
