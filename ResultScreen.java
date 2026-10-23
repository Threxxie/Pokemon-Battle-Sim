package ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/** The victory / defeat screen with options to play again. */
public class ResultScreen extends JPanel {

    private final boolean won;

    public ResultScreen(boolean won, String summary, Runnable rematch, Runnable newTeam, Runnable mainMenu) {
        this.won = won;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel(won ? "VICTORY!" : "DEFEAT");
        title.setFont(Theme.TITLE.deriveFont(72f));
        title.setForeground(won ? Theme.GOOD : Theme.BAD);
        c.gridy = 0;
        add(title, c);

        JLabel sub = new JLabel(won ? "You won the battle!" : "Your " + Theme.POKEMON + " were defeated.");
        sub.setFont(Theme.HEADING);
        sub.setForeground(Theme.TEXT);
        c.gridy = 1;
        add(sub, c);

        JLabel detail = new JLabel(summary);
        detail.setFont(Theme.BODY);
        detail.setForeground(Theme.TEXT_DIM);
        c.gridy = 2;
        c.insets = new Insets(2, 0, 36, 0);
        add(detail, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttons.setOpaque(false);
        RoundedButton again = new RoundedButton("Rematch", "Same picks, new opponent", Theme.GOOD);
        RoundedButton choose = new RoundedButton("New Team", "Pick different " + Theme.POKEMON, Theme.NEUTRAL_BUTTON);
        RoundedButton menu = new RoundedButton("Main Menu", null, Theme.NEUTRAL_BUTTON);
        again.setPreferredSize(new java.awt.Dimension(230, 72));
        choose.setPreferredSize(new java.awt.Dimension(230, 72));
        menu.setPreferredSize(new java.awt.Dimension(160, 72));
        again.addActionListener(e -> rematch.run());
        choose.addActionListener(e -> newTeam.run());
        menu.addActionListener(e -> mainMenu.run());
        buttons.add(again);
        buttons.add(choose);
        buttons.add(menu);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        add(buttons, c);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Theme.aa(g);
        Theme.paintBackground(g2, getWidth(), getHeight());
        // soft colored glow behind the title
        Color glow = won ? Theme.GOOD : Theme.BAD;
        g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 28));
        int d = Math.min(getWidth(), getHeight()) * 7 / 10;
        g2.fillOval(getWidth() / 2 - d / 2, getHeight() / 2 - d / 2, d, d);
        g2.dispose();
    }
}
