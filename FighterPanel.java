package ui;

import model.Pokemon;
import model.Team;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * One side of the battle arena: name, type badge, shield indicator, sprite,
 * health bar and (in team battles) a row of dots showing who is still standing.
 * It only DISPLAYS a Pokemon; it never changes one.
 */
public class FighterPanel extends RoundedPanel {

    private final JLabel roleLabel = new JLabel();
    private final JLabel nameLabel = new JLabel(" ");
    private final Badge typeBadge = new Badge();
    private final Badge shieldBadge = new Badge();
    private final SpritePanel sprite;
    private final HealthBar healthBar = new HealthBar();
    private final TeamDots teamDots = new TeamDots();
    private Pokemon lastShown;

    public FighterPanel(String role, boolean isPlayer) {
        super(30, new Color(0x1d2446), Theme.PANEL_BORDER);
        this.sprite = new SpritePanel(isPlayer);
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(14, 20, 14, 20));

        roleLabel.setText(role);
        roleLabel.setFont(Theme.SMALL_BOLD);
        roleLabel.setForeground(Theme.TEXT_DIM);
        nameLabel.setFont(Theme.NAME);
        nameLabel.setForeground(Theme.TEXT);

        JPanel nameRow = new JPanel();
        nameRow.setOpaque(false);
        nameRow.setLayout(new BoxLayout(nameRow, BoxLayout.X_AXIS));
        nameRow.add(nameLabel);
        nameRow.add(Box.createHorizontalStrut(10));
        nameRow.add(typeBadge);
        nameRow.add(Box.createHorizontalStrut(8));
        nameRow.add(shieldBadge);
        nameRow.add(Box.createHorizontalGlue());
        shieldBadge.setVisible(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        roleLabel.setAlignmentX(LEFT_ALIGNMENT);
        nameRow.setAlignmentX(LEFT_ALIGNMENT);
        header.add(roleLabel);
        header.add(nameRow);

        JPanel footer = new JPanel(new BorderLayout(0, 6));
        footer.setOpaque(false);
        footer.add(healthBar, BorderLayout.CENTER);
        footer.add(teamDots, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(sprite, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    /** Refreshes everything from the Pokemon's current state. */
    public void update(Pokemon pokemon, Team team) {
        boolean newPokemon = pokemon != lastShown;
        lastShown = pokemon;

        nameLabel.setText(pokemon.getName());
        typeBadge.setBadge(pokemon.getType().getDisplayName(), Theme.typeColor(pokemon.getType()));
        if (pokemon.isBlocking()) {
            shieldBadge.setBadge("SHIELD UP", Theme.SHIELD);
            shieldBadge.setVisible(true);
        } else {
            shieldBadge.setVisible(false);
        }
        sprite.setPokemon(pokemon);
        healthBar.setHealth(pokemon.getCurrentHp(), pokemon.getMaxHp(), !newPokemon);
        teamDots.setTeam(team);
    }

    public void shake() {
        sprite.shake();
    }

    /** Highlights this panel with a golden border while it is this side's turn. */
    public void setTurnHighlight(boolean active) {
        setBorderColor(active ? Theme.ACCENT : Theme.PANEL_BORDER);
    }

    /** Row of small circles, one per team member (hidden for 1v1). */
    private static class TeamDots extends JComponent {
        private Team team;

        void setTeam(Team team) {
            this.team = team;
            setPreferredSize(new Dimension(100, team.size() > 1 ? 18 : 0));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (team == null || team.size() <= 1) {
                return;
            }
            Graphics2D g2 = Theme.aa(g);
            List<Pokemon> members = team.getMembers();
            for (int i = 0; i < members.size(); i++) {
                int x = i * 22;
                g2.setColor(members.get(i).isFainted() ? new Color(0x4a4f73) : Theme.GOOD);
                g2.fillOval(x, 2, 14, 14);
                if (i == team.getActiveIndex()) {
                    g2.setColor(Color.WHITE);
                    g2.drawOval(x - 2, 0, 17, 17);
                }
            }
            g2.dispose();
        }
    }
}
