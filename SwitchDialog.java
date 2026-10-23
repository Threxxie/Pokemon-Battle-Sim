package ui;

import model.Pokemon;
import model.Team;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * A small pop-up that lists the player's team so one Pokemon can be chosen.
 * Fainted Pokemon (and the one already fighting) are shown but cannot be clicked.
 */
public final class SwitchDialog {

    private SwitchDialog() { }

    /**
     * @param forced true when the active Pokemon fainted: the dialog cannot be cancelled
     * @return the chosen team index, or -1 if the player cancelled (only possible when not forced)
     */
    public static int show(Window owner, Team team, boolean forced) {
        final int[] choice = {-1};
        JDialog dialog = new JDialog(owner, forced ? "Choose your next " + Theme.POKEMON : "Switch " + Theme.POKEMON,
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(forced ? WindowConstants.DO_NOTHING_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(Theme.BG_TOP);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel(forced ? "Your " + Theme.POKEMON + " fainted! Who's next?" : "Who do you want to send out?");
        title.setFont(Theme.HEADING.deriveFont(20f));
        title.setForeground(Theme.TEXT);
        content.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel(new GridLayout(0, 1, 0, 10));
        list.setOpaque(false);
        for (int i = 0; i < team.size(); i++) {
            final int index = i;
            Pokemon p = team.getMembers().get(i);
            String sub;
            if (p.isFainted()) {
                sub = "Fainted";
            } else if (i == team.getActiveIndex()) {
                sub = "Currently in battle";
            } else {
                sub = "HP " + p.getCurrentHp() + " / " + p.getMaxHp();
            }
            RoundedButton button = new RoundedButton(p.getName() + "  (" + p.getType().getDisplayName() + ")", sub,
                    Theme.typeColor(p.getType()));
            button.setEnabled(team.canSwitchTo(i));
            button.setPreferredSize(new java.awt.Dimension(360, 62));
            button.addActionListener(e -> {
                choice[0] = index;
                dialog.dispose();
            });
            list.add(button);
        }
        content.add(list, BorderLayout.CENTER);

        if (!forced) {
            RoundedButton cancel = new RoundedButton("Cancel", null, Theme.NEUTRAL_BUTTON);
            cancel.setPreferredSize(new java.awt.Dimension(360, 44));
            cancel.addActionListener(e -> dialog.dispose());
            content.add(cancel, BorderLayout.SOUTH);
        }

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true); // blocks until the dialog is closed
        return choice[0];
    }
}
