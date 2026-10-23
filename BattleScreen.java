package ui;

import battle.ActionResult;
import battle.BattleManager;
import battle.Side;
import model.Pokemon;
import model.SpecialAttack;
import model.Team;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The battle screen. It shows the state of a BattleManager and forwards button clicks
 * to it. No battle rules live here: damage, turns and winners are all decided by the
 * BattleManager, and this class just displays what the returned ActionResult says.
 */
public class BattleScreen extends JPanel {

    private static final int DEFAULT_OPPONENT_DELAY_MS = 1200;
    private static final int END_DELAY_MS = 1500;

    private static final Color PLAYER_LOG = new Color(0x8ec5ff);
    private static final Color OPPONENT_LOG = new Color(0xffb38e);

    private final BattleManager battle;
    private final Consumer<Boolean> onBattleEnd;
    private final int opponentDelayMs;

    private final RoundedPanel turnBanner = new RoundedPanel(26, Theme.GOOD, null);
    private final JLabel turnLabel = new JLabel("", SwingConstants.CENTER);
    private final FighterPanel playerPanel = new FighterPanel("YOUR " + Theme.POKEMON.toUpperCase(), true);
    private final FighterPanel opponentPanel = new FighterPanel("OPPONENT", false);
    private final RoundedPanel effectPanel = new RoundedPanel(22, new Color(0x1d2446), Theme.PANEL_BORDER);
    private final JLabel effectLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JTextPane logPane = new JTextPane();

    private final RoundedButton normalButton = new RoundedButton("Normal Attack", "", Theme.NEUTRAL_BUTTON);
    private final RoundedButton specialButton = new RoundedButton("Special", "", Theme.NEUTRAL_BUTTON);
    private final RoundedButton blockButton = new RoundedButton("Block", "", Theme.SHIELD.darker());
    private final RoundedButton switchButton = new RoundedButton("Switch", "", new Color(0x7a5cc7));

    private boolean opponentTurnScheduled = false;
    private boolean endScheduled = false;

    public BattleScreen(BattleManager battle, Consumer<Boolean> onBattleEnd) {
        this(battle, onBattleEnd, DEFAULT_OPPONENT_DELAY_MS);
    }

    public BattleScreen(BattleManager battle, Consumer<Boolean> onBattleEnd, int opponentDelayMs) {
        this.battle = battle;
        this.onBattleEnd = onBattleEnd;
        this.opponentDelayMs = opponentDelayMs;

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 24, 18, 24));
        add(buildTurnBanner(), BorderLayout.NORTH);
        add(buildArena(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        normalButton.addActionListener(e -> handleResult(battle.normalAttack(Side.PLAYER)));
        specialButton.addActionListener(e -> handleResult(battle.specialAttack(Side.PLAYER)));
        blockButton.addActionListener(e -> handleResult(battle.block(Side.PLAYER)));
        switchButton.addActionListener(e -> {
            int index = chooseSwitchTarget(false);
            if (index >= 0) {
                handleResult(battle.switchPokemon(Side.PLAYER, index));
            }
        });

        openingLog();
        continueBattle();
    }

    // ------------------------------------------------------------------
    // Building the layout
    // ------------------------------------------------------------------

    private JPanel buildTurnBanner() {
        turnBanner.setLayout(new BorderLayout());
        turnBanner.setPreferredSize(new Dimension(100, 50));
        turnLabel.setFont(Theme.HEADING.deriveFont(20f));
        turnBanner.add(turnLabel, BorderLayout.CENTER);
        return turnBanner;
    }

    private JPanel buildArena() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        JPanel arena = new JPanel(new GridBagLayout());
        arena.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;

        c.gridx = 0;
        c.weightx = 1;
        arena.add(playerPanel, c);

        JLabel vs = new JLabel("VS", SwingConstants.CENTER);
        vs.setFont(Theme.HEADING);
        vs.setForeground(Theme.TEXT_DIM);
        vs.setPreferredSize(new Dimension(80, 40));
        c.gridx = 1;
        c.weightx = 0;
        arena.add(vs, c);

        c.gridx = 2;
        c.weightx = 1;
        arena.add(opponentPanel, c);

        effectPanel.setLayout(new BorderLayout());
        effectPanel.setPreferredSize(new Dimension(100, 46));
        effectLabel.setFont(Theme.BODY_BOLD.deriveFont(18f));
        effectLabel.setForeground(Theme.TEXT);
        effectPanel.add(effectLabel, BorderLayout.CENTER);

        center.add(arena, BorderLayout.CENTER);
        center.add(effectPanel, BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout(14, 0));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(100, 190));

        // battle log
        RoundedPanel logCard = new RoundedPanel(26, new Color(0x151a36), Theme.PANEL_BORDER);
        logCard.setLayout(new BorderLayout());
        logCard.setBorder(new EmptyBorder(10, 14, 10, 8));
        logPane.setEditable(false);
        logPane.setOpaque(false);
        logPane.setFont(Theme.BODY);
        logPane.setForeground(Theme.TEXT);
        JScrollPane scroll = new JScrollPane(logPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getVerticalScrollBar().setOpaque(false);
        logCard.add(scroll, BorderLayout.CENTER);

        // action buttons
        RoundedPanel actions = new RoundedPanel(26, new Color(0x1d2446), Theme.PANEL_BORDER);
        actions.setLayout(new GridLayout(2, 2, 10, 10));
        actions.setBorder(new EmptyBorder(12, 12, 12, 12));
        actions.setPreferredSize(new Dimension(400, 190));
        actions.add(normalButton);
        actions.add(specialButton);
        actions.add(blockButton);
        actions.add(switchButton);

        bottom.add(logCard, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.EAST);
        return bottom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.paintBackground(g2, getWidth(), getHeight());
        g2.dispose();
    }

    // ------------------------------------------------------------------
    // Battle flow
    // ------------------------------------------------------------------

    private void openingLog() {
        Pokemon mine = battle.getPlayerTeam().getActive();
        Pokemon theirs = battle.getOpponentTeam().getActive();
        appendLog("The opponent sends out " + theirs.getName() + "!", OPPONENT_LOG);
        appendLog("Go, " + mine.getName() + "!", PLAYER_LOG);
        boolean playerFirst = battle.getCurrentSide() == Side.PLAYER;
        appendLog((playerFirst ? mine.getName() : theirs.getName()) + " is faster and moves first!", Theme.TEXT_DIM);
    }

    /** Called after every action: refresh the screen, then decide what happens next. */
    private void continueBattle() {
        refresh();

        if (battle.isBattleOver()) {
            if (!endScheduled) {
                endScheduled = true;
                Timer timer = new Timer(END_DELAY_MS, e -> onBattleEnd.accept(battle.getWinner() == Side.PLAYER));
                timer.setRepeats(false);
                timer.start();
            }
            return;
        }
        if (battle.isAwaitingPlayerReplacement()) {
            // invokeLater so the screen repaints (showing the fainted Pokemon) before the dialog opens
            SwingUtilities.invokeLater(this::promptForReplacement);
            return;
        }
        if (battle.getCurrentSide() == Side.OPPONENT && !opponentTurnScheduled) {
            opponentTurnScheduled = true;
            Timer timer = new Timer(opponentDelayMs, e -> {
                opponentTurnScheduled = false;
                handleResult(battle.performOpponentTurn());
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void promptForReplacement() {
        int index = chooseSwitchTarget(true);
        handleResult(battle.chooseReplacement(index));
    }

    /** Overridable so tests can run without opening a real dialog. */
    protected int chooseSwitchTarget(boolean forced) {
        return SwitchDialog.show(SwingUtilities.getWindowAncestor(this), battle.getPlayerTeam(), forced);
    }

    private void handleResult(ActionResult result) {
        if (!result.isValid()) {
            appendLog("! " + result.getMessages().get(0), Theme.WARN);
            if (battle.isAwaitingPlayerReplacement()) {
                SwingUtilities.invokeLater(this::promptForReplacement);
            }
            refresh();
            return;
        }

        Color color = result.getActor() == Side.PLAYER ? PLAYER_LOG : OPPONENT_LOG;
        for (String message : result.getMessages()) {
            boolean isEffect = message.equals(result.getEffectiveness());
            appendLog(message, isEffect ? effectivenessColor(result.getMultiplier()) : color);
        }

        if (result.getEffectiveness() != null) {
            showEffect(result.getEffectiveness(), effectivenessColor(result.getMultiplier()));
        } else {
            showEffect(result.getMessages().get(0), Theme.TEXT);
        }
        if (result.getDamage() > 0) {
            (result.getActor() == Side.PLAYER ? opponentPanel : playerPanel).shake();
        }
        continueBattle();
    }

    private static Color effectivenessColor(double multiplier) {
        if (multiplier > 1.0) {
            return Theme.GOOD;
        }
        return multiplier < 1.0 ? Theme.WARN : Theme.TEXT;
    }

    private void showEffect(String text, Color color) {
        effectLabel.setText(text);
        effectLabel.setForeground(color);
    }

    // ------------------------------------------------------------------
    // Keeping the display in sync with the BattleManager
    // ------------------------------------------------------------------

    private void refresh() {
        Team playerTeam = battle.getPlayerTeam();
        Team opponentTeam = battle.getOpponentTeam();
        Pokemon mine = playerTeam.getActive();

        playerPanel.update(mine, playerTeam);
        opponentPanel.update(opponentTeam.getActive(), opponentTeam);

        boolean over = battle.isBattleOver();
        boolean playerCanAct = !over && !battle.isAwaitingPlayerReplacement()
                && battle.getCurrentSide() == Side.PLAYER;

        // Turn indicator
        if (over) {
            turnLabel.setText(battle.getWinner() == Side.PLAYER ? "YOU WIN!" : "YOU LOST...");
            turnBanner.setFill(battle.getWinner() == Side.PLAYER ? Theme.GOOD : Theme.BAD);
        } else if (playerCanAct) {
            turnLabel.setText("YOUR TURN - choose an action");
            turnBanner.setFill(Theme.GOOD);
        } else if (battle.isAwaitingPlayerReplacement()) {
            turnLabel.setText("Choose your next " + Theme.POKEMON);
            turnBanner.setFill(Theme.ACCENT);
        } else {
            turnLabel.setText("OPPONENT'S TURN...");
            turnBanner.setFill(Theme.WARN);
        }
        turnLabel.setForeground(Theme.readableTextOn(currentBannerColor(over)));
        playerPanel.setTurnHighlight(playerCanAct);
        opponentPanel.setTurnHighlight(!over && !playerCanAct && !battle.isAwaitingPlayerReplacement());

        // Buttons
        normalButton.setLabels("Normal Attack", mine.getNormalAttack().getName() + " | Power " + mine.getNormalAttack().getPower());
        normalButton.setEnabled(playerCanAct);

        SpecialAttack special = mine.getSpecialAttack();
        specialButton.setBaseColor(Theme.typeColor(mine.getType()));
        String specialSub = special.isAvailable()
                ? "Special | Power " + special.getPower()
                : "Cooldown: " + special.getCooldownRemaining() + (special.getCooldownRemaining() == 1 ? " turn" : " turns");
        specialButton.setLabels(special.getName(), specialSub);
        specialButton.setEnabled(playerCanAct && special.isAvailable());

        blockButton.setLabels("Block", mine.canBlock() ? "Halves the next hit" : "Not two turns in a row");
        blockButton.setEnabled(playerCanAct && mine.canBlock());

        String switchSub = playerTeam.size() == 1 ? "1v1 battle"
                : playerTeam.benchAliveCount() > 0 ? playerTeam.benchAliveCount() + " available" : "No one available";
        switchButton.setLabels("Switch", switchSub);
        switchButton.setEnabled(playerCanAct && playerTeam.benchAliveCount() > 0);
    }

    private Color currentBannerColor(boolean over) {
        if (over) {
            return battle.getWinner() == Side.PLAYER ? Theme.GOOD : Theme.BAD;
        }
        if (battle.isAwaitingPlayerReplacement()) {
            return Theme.ACCENT;
        }
        return battle.getCurrentSide() == Side.PLAYER ? Theme.GOOD : Theme.WARN;
    }

    private void appendLog(String text, Color color) {
        StyledDocument doc = logPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setFontFamily(style, Theme.BODY.getFamily());
        StyleConstants.setFontSize(style, 15);
        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException ignored) {
            // inserting at the end of the document cannot fail
        }
        logPane.setCaretPosition(doc.getLength()); // auto-scroll to the newest line
    }

    /** A thin, dark scrollbar that matches the rest of the interface. */
    private static class SlimScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0x4a5599);
            trackColor = new Color(0x151a36);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return emptyButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return emptyButton();
        }

        private static JButton emptyButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
            // transparent track
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
            Graphics2D g2 = Theme.aa(g);
            g2.setColor(thumbColor);
            g2.fillRoundRect(bounds.x + 2, bounds.y, bounds.width - 4, bounds.height, 8, 8);
            g2.dispose();
        }
    }
}
