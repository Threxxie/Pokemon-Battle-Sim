package ui;

import model.ElementType;
import model.Pokemon;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Draws a Pokemon picture. If a file named sprites/<lowercase name>.png exists next to
 * the program it is used; otherwise a simple colored placeholder creature is drawn.
 * This means the game works completely offline with no image files at all.
 */
public class SpritePanel extends JComponent {

    private static final Map<String, Optional<Image>> IMAGE_CACHE = new HashMap<>();

    private String name = "";
    private ElementType type = ElementType.NORMAL;
    private boolean facingRight;
    private boolean fainted;
    private boolean shielded;
    private int shakeOffset;
    private int shakeStep;
    private Timer shakeTimer;

    public SpritePanel(boolean facingRight) {
        this.facingRight = facingRight;
        setPreferredSize(new Dimension(190, 190));
        setMinimumSize(new Dimension(100, 100));
    }

    public void setPokemon(Pokemon pokemon) {
        this.name = pokemon.getName();
        this.type = pokemon.getType();
        this.fainted = pokemon.isFainted();
        this.shielded = pokemon.isBlocking();
        repaint();
    }

    /** A quick left-right wobble that plays when this Pokemon is hit. */
    public void shake() {
        if (shakeTimer != null) {
            shakeTimer.stop();
        }
        shakeStep = 0;
        shakeTimer = new Timer(30, e -> {
            shakeStep++;
            shakeOffset = (shakeStep % 2 == 0 ? 1 : -1) * Math.max(0, 16 - shakeStep * 2);
            if (shakeStep >= 8) {
                shakeOffset = 0;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        shakeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2 + shakeOffset;
        int y = (getHeight() - size) / 2;

        if (shielded && !fainted) {
            paintShield(g2, x, y, size);
        }
        if (fainted) {
            // Draw into a buffer first so the whole sprite fades as ONE layer
            // (fading each shape separately would let the parts show through each other).
            BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bg = Theme.aa(buffer.createGraphics());
            paintSprite(bg, name, type, x, y, size, facingRight);
            bg.dispose();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.drawImage(buffer, 0, 0, null);
        } else {
            paintSprite(g2, name, type, x, y, size, facingRight);
        }
        g2.dispose();
    }

    /** Translucent blue bubble drawn around a Pokemon that is blocking. */
    private static void paintShield(Graphics2D g2, int x, int y, int size) {
        int pad = size / 14;
        g2.setColor(new Color(Theme.SHIELD.getRed(), Theme.SHIELD.getGreen(), Theme.SHIELD.getBlue(), 50));
        g2.fillOval(x + pad, y + pad, size - 2 * pad, size - 2 * pad);
        g2.setColor(new Color(Theme.SHIELD.getRed(), Theme.SHIELD.getGreen(), Theme.SHIELD.getBlue(), 200));
        g2.setStroke(new BasicStroke(Math.max(3f, size / 40f)));
        g2.drawOval(x + pad, y + pad, size - 2 * pad, size - 2 * pad);
    }

    // ------------------------------------------------------------------
    // Sprite drawing (also used by the selection and title screens)
    // ------------------------------------------------------------------

    private static Optional<Image> loadImage(String name) {
        return IMAGE_CACHE.computeIfAbsent(name, n -> {
            File file = new File("sprites/" + n.toLowerCase() + ".png");
            if (!file.isFile()) {
                return Optional.empty();
            }
            try {
                return Optional.ofNullable(ImageIO.read(file));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    public static void paintSprite(Graphics2D g2, String name, ElementType type,
                                   int x, int y, int size, boolean facingRight) {
        Optional<Image> image = loadImage(name);
        if (image.isPresent()) {
            g2.drawImage(image.get(), x, y, size, size, null);
            return;
        }
        Color base = Theme.typeColor(type);

        // ground shadow
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillOval(x + pct(size, 15), y + pct(size, 84), pct(size, 70), pct(size, 12));

        paintAccessory(g2, type, base, x, y, size);

        // body
        int bx = x + pct(size, 15);
        int by = y + pct(size, 22);
        int bw = pct(size, 70);
        int bh = pct(size, 66);
        g2.setPaint(new GradientPaint(bx, by, base.brighter(), bx, by + bh, base.darker()));
        g2.fillOval(bx, by, bw, bh);
        g2.setColor(base.darker().darker());
        g2.setStroke(new BasicStroke(Math.max(2f, size / 55f)));
        g2.drawOval(bx, by, bw, bh);

        // belly highlight
        g2.setColor(new Color(255, 255, 255, 55));
        g2.fillOval(bx + bw * 25 / 100, by + bh * 52 / 100, bw * 50 / 100, bh * 38 / 100);

        // eyes
        int eyeW = pct(size, 13);
        int eyeH = pct(size, 17);
        int eyeY = by + bh * 28 / 100;
        int leftX = bx + bw * 24 / 100;
        int rightX = bx + bw * 60 / 100;
        int shift = facingRight ? eyeW / 5 : -eyeW / 5;
        for (int ex : new int[] {leftX, rightX}) {
            g2.setColor(Color.WHITE);
            g2.fillOval(ex, eyeY, eyeW, eyeH);
            g2.setColor(new Color(0x15182b));
            g2.fillOval(ex + eyeW / 4 + shift, eyeY + eyeH / 4, eyeW / 2, eyeH / 2);
        }
        if (type == ElementType.ELECTRIC) { // red cheeks
            g2.setColor(new Color(0xe53935));
            g2.fillOval(bx + bw * 8 / 100, eyeY + eyeH + pct(size, 3), pct(size, 9), pct(size, 9));
            g2.fillOval(bx + bw * 83 / 100, eyeY + eyeH + pct(size, 3), pct(size, 9), pct(size, 9));
        }

        // mouth
        g2.setColor(new Color(0x15182b));
        g2.setStroke(new BasicStroke(Math.max(2f, size / 60f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(bx + bw * 38 / 100, eyeY + eyeH + pct(size, 3), bw * 24 / 100, pct(size, 12), 200, 140);
    }

    private static int pct(int size, int percent) {
        return size * percent / 100;
    }

    /** A small decoration on top of the head that hints at the type. */
    private static void paintAccessory(Graphics2D g2, ElementType type, Color base, int x, int y, int s) {
        int cx = x + s / 2;
        switch (type) {
            case FIRE: {
                g2.setColor(new Color(0xff8a1f));
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 11), cx, cx + pct(s, 11)},
                        new int[] {y + pct(s, 32), y + pct(s, 2), y + pct(s, 32)}, 3));
                g2.setColor(new Color(0xffd54a));
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 6), cx, cx + pct(s, 6)},
                        new int[] {y + pct(s, 32), y + pct(s, 13), y + pct(s, 32)}, 3));
                break;
            }
            case WATER: {
                g2.setColor(new Color(0x8fd0ff));
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 8), cx, cx + pct(s, 8)},
                        new int[] {y + pct(s, 28), y + pct(s, 4), y + pct(s, 28)}, 3));
                g2.fillOval(cx - pct(s, 8), y + pct(s, 20), pct(s, 16), pct(s, 14));
                break;
            }
            case GRASS: {
                g2.setColor(new Color(0x2e7d32));
                g2.fillOval(cx - pct(s, 22), y + pct(s, 8), pct(s, 22), pct(s, 22));
                g2.fillOval(cx, y + pct(s, 8), pct(s, 22), pct(s, 22));
                g2.setColor(new Color(0x66bb6a));
                g2.fillOval(cx - pct(s, 9), y + pct(s, 14), pct(s, 18), pct(s, 20));
                break;
            }
            case ELECTRIC: {
                g2.setColor(base);
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 26), cx - pct(s, 16), cx - pct(s, 6)},
                        new int[] {y + pct(s, 34), y + pct(s, 0), y + pct(s, 30)}, 3));
                g2.fillPolygon(new Polygon(new int[] {cx + pct(s, 26), cx + pct(s, 16), cx + pct(s, 6)},
                        new int[] {y + pct(s, 34), y + pct(s, 0), y + pct(s, 30)}, 3));
                g2.setColor(new Color(0x2b2b2b));
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 20), cx - pct(s, 16), cx - pct(s, 12)},
                        new int[] {y + pct(s, 10), y + pct(s, 0), y + pct(s, 10)}, 3));
                g2.fillPolygon(new Polygon(new int[] {cx + pct(s, 20), cx + pct(s, 16), cx + pct(s, 12)},
                        new int[] {y + pct(s, 10), y + pct(s, 0), y + pct(s, 10)}, 3));
                break;
            }
            case ROCK: {
                g2.setColor(base.darker());
                g2.fillOval(cx - pct(s, 24), y + pct(s, 14), pct(s, 20), pct(s, 20));
                g2.fillOval(cx - pct(s, 8), y + pct(s, 8), pct(s, 22), pct(s, 24));
                g2.fillOval(cx + pct(s, 10), y + pct(s, 16), pct(s, 18), pct(s, 18));
                break;
            }
            case GHOST: {
                g2.setColor(base.darker());
                g2.fillPolygon(new Polygon(new int[] {cx - pct(s, 28), cx - pct(s, 20), cx - pct(s, 8)},
                        new int[] {y + pct(s, 34), y + pct(s, 6), y + pct(s, 30)}, 3));
                g2.fillPolygon(new Polygon(new int[] {cx + pct(s, 28), cx + pct(s, 20), cx + pct(s, 8)},
                        new int[] {y + pct(s, 34), y + pct(s, 6), y + pct(s, 30)}, 3));
                break;
            }
            default: { // NORMAL: round ears
                g2.setColor(base.darker());
                g2.fillOval(cx - pct(s, 30), y + pct(s, 14), pct(s, 20), pct(s, 20));
                g2.fillOval(cx + pct(s, 10), y + pct(s, 14), pct(s, 20), pct(s, 20));
                break;
            }
        }
    }
}
