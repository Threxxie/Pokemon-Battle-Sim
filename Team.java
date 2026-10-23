package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One side's group of Pokemon, plus which one is currently fighting.
 * Keeping this in its own class means BattleManager does not have to juggle
 * lists and indexes for both sides.
 */
public class Team {

    private final List<Pokemon> members;
    private int activeIndex = 0;

    public Team(List<Pokemon> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("A team needs at least one Pokemon.");
        }
        this.members = new ArrayList<>(members);
    }

    public Pokemon getActive() {
        return members.get(activeIndex);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    /** Read-only view so outside code cannot add or remove team members. */
    public List<Pokemon> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public int size() {
        return members.size();
    }

    public boolean hasRemaining() {
        return aliveCount() > 0;
    }

    public int aliveCount() {
        int count = 0;
        for (Pokemon p : members) {
            if (!p.isFainted()) {
                count++;
            }
        }
        return count;
    }

    /** Number of healthy Pokemon waiting on the bench. */
    public int benchAliveCount() {
        int count = aliveCount();
        return getActive().isFainted() ? count : count - 1;
    }

    public boolean canSwitchTo(int index) {
        return index >= 0 && index < members.size()
                && index != activeIndex
                && !members.get(index).isFainted();
    }

    public void switchTo(int index) {
        if (!canSwitchTo(index)) {
            throw new IllegalArgumentException("Cannot switch to index " + index);
        }
        getActive().resetTurnState();
        activeIndex = index;
    }
}
