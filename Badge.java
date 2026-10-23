package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/** A small colored pill with text, used for type labels and the "SHIELD UP" indicator. */
public class Badge extends JComponent {

    private String text = " ";
    private Color color = Theme.TEXT_DIM;

    public void setBadge(String text, Color color) {
        this.text = text;
        this.color = color;
        revalidate();
        repaint();
    }

    public String getText() {
        return text;
    }

    @Override
    public Dimension getPreferredSize() {
        java.awt.FontMetrics fm = getFontMetrics(Theme.SMALL_BOLD);
        return new Dimension(Theme.pillWidth(fm, text) + 1, fm.getHeight() + 5);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        Theme.paintPill(g2, text, Theme.SMALL_BOLD, 0, 0, color);
        g2.dispose();
    }
}
