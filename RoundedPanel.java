package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JPanel;

/** A JPanel with rounded corners (Swing has no built-in rounded panel). */
public class RoundedPanel extends JPanel {

    private final int arc;
    private Color fill;
    private Color border;

    public RoundedPanel(int arc, Color fill, Color border) {
        this.arc = arc;
        this.fill = fill;
        this.border = border;
        setOpaque(false);
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.border = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
