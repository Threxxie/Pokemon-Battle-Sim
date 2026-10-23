package model;

/**
 * A stronger, named attack that is limited by a cooldown.
 *
 * Cooldown rule: after the move is used, its owner must wait COOLDOWN_TURNS of
 * its own turns. The countdown ticks down at the start of each of the owner's turns,
 * so with a cooldown of 3 the move can be used every third turn.
 */
public class SpecialAttack implements Move {

    public static final int COOLDOWN_TURNS = 3;

    private final String name;
    private final int power;
    private int cooldownRemaining = 0; // 0 means ready

    public SpecialAttack(String name, int power) {
        this.name = name;
        this.power = power;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public boolean isAvailable() {
        return cooldownRemaining == 0;
    }

    @Override
    public void onUse() {
        cooldownRemaining = COOLDOWN_TURNS;
    }

    /** Called once at the start of the owner's turn. */
    public void tickCooldown() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    public int getCooldownRemaining() {
        return cooldownRemaining;
    }
}
