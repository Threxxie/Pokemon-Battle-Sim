package model;

/** A basic attack: standard power, no cooldown, always available. */
public class NormalAttack implements Move {

    public static final int POWER = 30;

    private final String name;

    public NormalAttack(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPower() {
        return POWER;
    }

    @Override
    public boolean isAvailable() {
        return true; // never limited
    }

    @Override
    public void onUse() {
        // nothing to track for a normal attack
    }
}
