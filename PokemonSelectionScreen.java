package ui;

import model.Pokemon;
import model.PokemonFactory;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Lets the player pick 1 to 3 Pokemon from the roster by clicking cards.
 * The first pick leads the battle. Picking 1 gives a 1v1; picking more gives a team battle,
 * and the opponent brings the same number of Pokemon chosen from the ones left over.
 */
public class PokemonSelectionScreen extends JPanel {

    private static final int MAX_PICKS = 3;

    private final List<String> picks = new ArrayList<>();
    private final List<RosterCard> cards = new ArrayList<>();
    private final JLabel hint = new JLabel("", SwingConstants.CENTER);
    private final RoundedButton startButton = new RoundedButton("Start Battle", null, Theme.GOOD);

    public PokemonSelectionScreen(Consumer<List<String>> onStart, Runnable onBack) {
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(22, 32, 22, 32));

        JLabel title = new JLabel("Choose your " + Theme.POKEMON, SwingConstants.CENTER);
        title.setFont(Theme.TITLE.deriveFont(36f));
        title.setForeground(Theme.TEXT);
        hint.setFont(Theme.BODY);
        hint.setForeground(Theme.TEXT_DIM);

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(hint, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 4, 16, 16));
        grid.setOpaque(false);
        for (Pokemon pokemon : PokemonFactory.createRoster()) {
            RosterCard card = new RosterCard(pokemon);
            cards.add(card);
            grid.add(card);
        }
        add(grid, BorderLayout.CENTER);

        RoundedButton backButton = new RoundedButton("Back", null, Theme.NEUTRAL_BUTTON);
        backButton.setPreferredSize(new Dimension(150, 56));
        backButton.addActionListener(e -> onBack.run());
        startButton.setPreferredSize(new Dimension(300, 56));
        startButton.addActionListener(e -> {
            if (!picks.isEmpty()) {
                onStart.accept(new ArrayList<>(picks));
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(backButton);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(startButton);
        bottom.add(left, BorderLayout.WEST);
        bottom.add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        updateState();
    }

    private void toggle(String name) {
        if (picks.contains(name)) {
            picks.remove(name);
        } else if (picks.size() < MAX_PICKS) {
            picks.add(name);
        } else {
            hint.setText("You can pick at most " + MAX_PICKS + ". Click a picked " + Theme.POKEMON + " to remove it.");
            hint.setForeground(Theme.WARN);
            return;
        }
        updateState();
    }

    private void updateState() {
        hint.setForeground(Theme.TEXT_DIM);
        if (picks.isEmpty()) {
            hint.setText("Pick 1 to " + MAX_PICKS + " " + Theme.POKEMON + ". Your first pick leads the battle. "
                    + "One pick = 1v1, more = team battle with switching.");
            startButton.setLabels("Pick a " + Theme.POKEMON + " first", null);
        } else {
            hint.setText("Leading with " + picks.get(0) + ". The opponent will bring " + picks.size()
                    + " " + Theme.POKEMON + " from the ones you did not pick.");
            startButton.setLabels(picks.size() == 1 ? "Start Battle (1v1)"
                    : "Start Team Battle (" + picks.size() + "v" + picks.size() + ")", null);
        }
        startButton.setEnabled(!picks.isEmpty());
        for (RosterCard card : cards) {
            card.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.paintBackground(g2, getWidth(), getHeight());
        g2.dispose();
    }

    /** One clickable card in the roster grid. */
    private class RosterCard extends JComponent {
        private final Pokemon pokemon;
        private boolean hover = false;

        RosterCard(Pokemon pokemon) {
            this.pokemon = pokemon;
            setPreferredSize(new Dimension(220, 250));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle(pokemon.getName());
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.aa(g);
            int w = getWidth();
            int h = getHeight();
            int order = picks.indexOf(pokemon.getName());
            boolean selected = order >= 0;
            Color typeColor = Theme.typeColor(pokemon.getType());

            g2.setColor(hover ? new Color(0x262e58) : Theme.PANEL);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 28, 28);
            g2.setColor(selected ? Theme.ACCENT : hover ? typeColor : Theme.PANEL_BORDER);
            g2.setStroke(new BasicStroke(selected ? 4f : 2f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, 26, 26);

            int spriteSize = Math.min(w - 60, h * 44 / 100);
            SpritePanel.paintSprite(g2, pokemon.getName(), pokemon.getType(),
                    (w - spriteSize) / 2, 12, spriteSize, true);

            // name
            g2.setFont(Theme.NAME);
            FontMetrics fm = g2.getFontMetrics();
            int y = 12 + spriteSize + 26;
            g2.setColor(Theme.TEXT);
            g2.drawString(pokemon.getName(), (w - fm.stringWidth(pokemon.getName())) / 2, y);

            // type pill (centered)
            g2.setFont(Theme.SMALL_BOLD);
            String typeText = pokemon.getType().getDisplayName();
            int pillW = Theme.pillWidth(g2.getFontMetrics(), typeText);
            Theme.paintPill(g2, typeText, Theme.SMALL_BOLD, (w - pillW) / 2, y + 8, typeColor);

            // stats
            g2.setFont(Theme.BODY);
            g2.setColor(Theme.TEXT_DIM);
            String line1 = "HP " + pokemon.getMaxHp() + "   ATK " + pokemon.getAttackStat();
            String line2 = "DEF " + pokemon.getDefenseStat() + "   SPD " + pokemon.getSpeedStat();
            FontMetrics sm = g2.getFontMetrics();
            g2.drawString(line1, (w - sm.stringWidth(line1)) / 2, y + 52);
            g2.drawString(line2, (w - sm.stringWidth(line2)) / 2, y + 72);
            g2.setFont(Theme.SMALL_BOLD);
            String special = "Special: " + pokemon.getSpecialAttack().getName();
            g2.setColor(typeColor);
            g2.drawString(special, (w - g2.getFontMetrics().stringWidth(special)) / 2, y + 94);

            // pick order badge
            if (selected) {
                String label = order == 0 ? "LEAD" : String.valueOf(order + 1);
                Theme.paintPill(g2, label, Theme.SMALL_BOLD, 12, 12, Theme.ACCENT);
            }
            g2.dispose();
        }
    }
}
