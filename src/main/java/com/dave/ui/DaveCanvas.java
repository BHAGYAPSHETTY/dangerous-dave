package com.dave.ui;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Double-buffered retro canvas.
 * Renders an internal 320x200 pixel buffer and upscales it with sharp nearest-neighbor interpolation.
 */
public class DaveCanvas extends JPanel {
    public static final int NATIVE_WIDTH = 320;
    public static final int NATIVE_HEIGHT = 200;

    private final BufferedImage backBuffer;
    private final Graphics2D bufferGraphics;

    public DaveCanvas(int defaultScale) {
        int width = NATIVE_WIDTH * defaultScale;
        int height = NATIVE_HEIGHT * defaultScale;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);

        this.backBuffer = new BufferedImage(NATIVE_WIDTH, NATIVE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.bufferGraphics = backBuffer.createGraphics();
        this.bufferGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    public BufferedImage getBackBuffer() {
        return backBuffer;
    }

    public Graphics2D getBufferGraphics() {
        return bufferGraphics;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Calculate aspect-ratio preserved scaling
        float scale = Math.min((float) panelWidth / NATIVE_WIDTH, (float) panelHeight / NATIVE_HEIGHT);
        int drawW = (int) (NATIVE_WIDTH * scale);
        int drawH = (int) (NATIVE_HEIGHT * scale);
        int drawX = (panelWidth - drawW) / 2;
        int drawY = (panelHeight - drawH) / 2;

        // Draw scaled buffer centered
        g2d.drawImage(backBuffer, drawX, drawY, drawW, drawH, null);
    }
}
