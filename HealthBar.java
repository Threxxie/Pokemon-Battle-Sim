package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Timer;

/** A health bar that slides smoothly to its new value and changes color as health drops. */
public class HealthBar extends JComponent {

    private int current = 1;
    private int max = 1;
    private double shownFraction = 1.0;
    private final Timer animation;

    public HealthBar() {
        setPreferredSize(new Dimension(300, 28));
        setMinimumSize(new Dimension(120, 28));
        animation = new Timer(16, e -> step());
    }

    /** @param animate false to jump straight to the value (used when a new Pokemon appears) */
    public void setHealth(int current, int max, boolean animate) {
        this.current = current;
        this.max = Math.max(1, max);
        if (animate) {
            if (!animation.isRunning()) {
                animation.start();
            }
        } else {
            animation.stop();
            shownFraction = (double) current / this.max;
        }
        repaint();
    }

    private void step() {
        double target = (double) current / max;
        double diff = target - shownFraction;
        if (Math.abs(diff) < 0.004) {
            shownFraction = target;
            animation.stop();
        } else {
            shownFraction += diff * 0.15;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(0x0c1022));
        g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);

        int inner = h - 6;
        int fillWidth = (int) Math.round((w - 6) * Math.max(0, Math.min(1, shownFraction)));
        if (fillWidth > 0) {
            Color color = shownFraction > 0.5 ? Theme.GOOD : shownFraction > 0.2 ? Theme.ACCENT : Theme.BAD;
            g2.setColor(color);
            g2.fillRoundRect(3, 3, Math.max(inner, fillWidth), inner, inner, inner);
        }

        g2.setFont(Theme.SMALL_BOLD);
        FontMetrics fm = g2.getFontMetrics();
        String text = "HP " + current + " / " + max;
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h + fm.getAscent()) / 2 - 2;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.drawString(text, tx + 1, ty + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx, ty);
        g2.dispose();
    }
}
