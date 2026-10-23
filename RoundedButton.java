package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

/** A flat, rounded button with a main label and an optional smaller second line. */
public class RoundedButton extends JButton {

    private static final Color DISABLED_FILL = new Color(0x343a63);
    private static final Color DISABLED_TEXT = new Color(0x7a82ad);

    private String mainLabel;
    private String subLabel;
    private Color baseColor;

    public RoundedButton(String mainLabel, String subLabel, Color baseColor) {
        this.mainLabel = mainLabel;
        this.subLabel = subLabel;
        this.baseColor = baseColor;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(170, 72));
    }

    public void setLabels(String mainLabel, String subLabel) {
        this.mainLabel = mainLabel;
        this.subLabel = subLabel;
        repaint();
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
        repaint();
    }

    public String getMainLabel() {
        return mainLabel;
    }

    public String getSubLabel() {
        return subLabel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        int w = getWidth();
        int h = getHeight();

        Color fill;
        Color textColor;
        if (!isEnabled()) {
            fill = DISABLED_FILL;
            textColor = DISABLED_TEXT;
        } else {
            fill = baseColor;
            if (getModel().isPressed()) {
                fill = baseColor.darker();
            } else if (getModel().isRollover()) {
                fill = baseColor.brighter();
            }
            textColor = Theme.readableTextOn(fill);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);

        g2.setColor(textColor);
        g2.setFont(Theme.BODY_BOLD.deriveFont(17f));
        FontMetrics big = g2.getFontMetrics();
        boolean hasSub = subLabel != null && !subLabel.isEmpty();
        int mainY = hasSub ? h / 2 - 4 : (h + big.getAscent()) / 2 - 2;
        g2.drawString(mainLabel, (w - big.stringWidth(mainLabel)) / 2, mainY);
        if (hasSub) {
            g2.setFont(Theme.SMALL);
            FontMetrics small = g2.getFontMetrics();
            g2.drawString(subLabel, (w - small.stringWidth(subLabel)) / 2, h / 2 + 15);
        }
        g2.dispose();
    }
}
