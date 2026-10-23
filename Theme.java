package ui;

import model.ElementType;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Shared colors, fonts and small drawing helpers so every screen looks consistent. */
public final class Theme {

    public static final String POKEMON = "Pok\u00e9mon";

    public static final Color BG_TOP = new Color(0x12172e);
    public static final Color BG_BOTTOM = new Color(0x2a3262);
    public static final Color PANEL = new Color(0x1d2446);
    public static final Color PANEL_BORDER = new Color(0x3a4478);
    public static final Color TEXT = new Color(0xf2f4ff);
    public static final Color TEXT_DIM = new Color(0xa6aed6);
    public static final Color ACCENT = new Color(0xffcb3d);
    public static final Color GOOD = new Color(0x4cd97b);
    public static final Color BAD = new Color(0xff5c6c);
    public static final Color WARN = new Color(0xffa63d);
    public static final Color SHIELD = new Color(0x58b5ff);
    public static final Color NEUTRAL_BUTTON = new Color(0x4a5599);

    public static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 46);
    public static final Font HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font NAME = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    public static final Font BODY_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font SMALL_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 12);

    private Theme() { }

    public static Color typeColor(ElementType type) {
        switch (type) {
            case FIRE:     return new Color(0xf4672c);
            case WATER:    return new Color(0x3b8de8);
            case GRASS:    return new Color(0x45b649);
            case ELECTRIC: return new Color(0xf5c518);
            case ROCK:     return new Color(0xa8925a);
            case GHOST:    return new Color(0x7e57c2);
            default:       return new Color(0x9aa0a6);
        }
    }

    /** Dark or light text, whichever is easier to read on the given background. */
    public static Color readableTextOn(Color background) {
        double luminance = 0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue();
        return luminance > 150 ? new Color(0x1a1d33) : Color.WHITE;
    }

    /** Creates a copy of the Graphics object with anti-aliasing turned on. Caller must dispose it. */
    public static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    public static void paintBackground(Graphics2D g2, int width, int height) {
        g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, height, BG_BOTTOM));
        g2.fillRect(0, 0, width, height);
    }

    /** Width a pill with this text would have. */
    public static int pillWidth(FontMetrics fm, String text) {
        return fm.stringWidth(text) + 20;
    }

    /** Draws a small rounded label. (x, y) is the top-left corner; returns the pill width. */
    public static int paintPill(Graphics2D g2, String text, Font font, int x, int y, Color color) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int w = pillWidth(fm, text);
        int h = fm.getHeight() + 4;
        g2.setColor(color);
        g2.fillRoundRect(x, y, w, h, h, h);
        g2.setColor(readableTextOn(color));
        g2.drawString(text, x + 10, y + 2 + fm.getAscent());
        return w;
    }
}
