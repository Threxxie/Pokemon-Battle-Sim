package ui;

import battle.BattleManager;
import model.ElementType;
import model.Pokemon;
import model.PokemonFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * The root panel of the application. It owns the "which screen is showing" logic:
 * title -> selection -> battle -> result -> (rematch | new team | title).
 * Each screen is its own panel class and reports back through callbacks.
 * Main puts this panel inside a JFrame.
 */
public class MainMenu extends JPanel {

    private final Runnable onQuit;
    private List<String> lastPicks = List.of();

    public MainMenu(Runnable onQuit) {
        this.onQuit = onQuit;
        setLayout(new BorderLayout());
        showTitle();
    }

    private void setScreen(JPanel screen) {
        removeAll();
        add(screen, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showTitle() {
        setScreen(new TitlePanel());
    }

    public void showSelection() {
        setScreen(new PokemonSelectionScreen(this::startBattle, this::showTitle));
    }

    /** Builds brand-new Pokemon (full health, fresh cooldowns) and starts a battle. */
    public void startBattle(List<String> picks) {
        lastPicks = List.copyOf(picks);
        Random random = new Random();
        List<Pokemon> mine = PokemonFactory.createTeam(picks);
        List<Pokemon> theirs = PokemonFactory.createOpponentTeam(picks, random);
        BattleManager battle = new BattleManager(mine, theirs);
        setScreen(new BattleScreen(battle, won -> showResult(won, battle)));
    }

    private void showResult(boolean won, BattleManager battle) {
        int mine = battle.getPlayerTeam().aliveCount();
        int total = battle.getPlayerTeam().size();
        String summary = "Your team: " + mine + " of " + total + " " + Theme.POKEMON + " still standing. "
                + "Turns played: " + battle.getTurnNumber() + ".";
        setScreen(new ResultScreen(won, summary,
                () -> startBattle(lastPicks), this::showSelection, this::showTitle));
    }

    /** The first screen: title, a few sprites and Start / Quit buttons. */
    private class TitlePanel extends JPanel {

        TitlePanel() {
            setLayout(new BorderLayout());
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            buttons.setOpaque(false);
            buttons.setBorder(new EmptyBorder(0, 0, 90, 0));

            RoundedButton start = new RoundedButton("Start Battle", null, Theme.GOOD);
            RoundedButton quit = new RoundedButton("Quit", null, Theme.NEUTRAL_BUTTON);
            start.setPreferredSize(new Dimension(260, 64));
            quit.setPreferredSize(new Dimension(160, 64));
            start.addActionListener(e -> showSelection());
            quit.addActionListener(e -> onQuit.run());
            buttons.add(start);
            buttons.add(quit);
            add(buttons, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Theme.aa(g);
            int w = getWidth();
            int h = getHeight();
            Theme.paintBackground(g2, w, h);

            g2.setFont(Theme.TITLE.deriveFont(58f));
            FontMetrics fm = g2.getFontMetrics();
            String title = Theme.POKEMON.toUpperCase() + " BATTLE";
            g2.setColor(new java.awt.Color(0, 0, 0, 90));
            g2.drawString(title, (w - fm.stringWidth(title)) / 2 + 3, h * 22 / 100 + 3);
            g2.setColor(Theme.ACCENT);
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, h * 22 / 100);

            g2.setFont(Theme.HEADING.deriveFont(22f));
            fm = g2.getFontMetrics();
            String subtitle = "A turn-based battle simulator written in Java";
            g2.setColor(Theme.TEXT_DIM);
            g2.drawString(subtitle, (w - fm.stringWidth(subtitle)) / 2, h * 22 / 100 + 44);

            int size = Math.min(230, h / 4);
            int gap = 40;
            int total = 3 * size + 2 * gap;
            int x = (w - total) / 2;
            int y = h * 38 / 100;
            SpritePanel.paintSprite(g2, "Squirtle", ElementType.WATER, x, y, size, true);
            SpritePanel.paintSprite(g2, "Pikachu", ElementType.ELECTRIC, x + size + gap - 10, y - 20, size + 20, true);
            SpritePanel.paintSprite(g2, "Charizard", ElementType.FIRE, x + 2 * (size + gap), y, size, false);
            g2.dispose();
        }
    }
}
