package model;

/**
 * Base class for every Pokemon.
 *
 * ENCAPSULATION: every field is private. Other classes can read state through
 * getters, but health only changes through takeDamage() and heal(), which keep
 * it between 0 and maxHp. Blocking can only be switched on by block().
 *
 * INHERITANCE: the type-specific subclasses (FirePokemon, WaterPokemon, ...)
 * extend this class and reuse all of this behaviour.
 *
 * POLYMORPHISM: describeSpecialAttack() is abstract. Each subclass writes its
 * own version, but callers just use a Pokemon reference.
 */
public abstract class Pokemon {

    private final String name;
    private final ElementType type;
    private final int maxHp;
    private final int attackStat;
    private final int defenseStat;
    private final int speedStat;

    private int currentHp;

    // Block state (see block(), startTurn()).
    private boolean blocking = false;
    private boolean blockedThisTurn = false;
    private boolean blockedLastTurn = false;

    private final Move normalAttack;
    private final SpecialAttack specialAttack;

    protected Pokemon(String name, ElementType type, int maxHp, int attackStat,
                      int defenseStat, int speedStat, String specialName, int specialPower) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attackStat = attackStat;
        this.defenseStat = defenseStat;
        this.speedStat = speedStat;
        this.normalAttack = new NormalAttack("Tackle");
        this.specialAttack = new SpecialAttack(specialName, specialPower);
    }

    // ----- read-only access -----

    public String getName() { return name; }
    public ElementType getType() { return type; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAttackStat() { return attackStat; }
    public int getDefenseStat() { return defenseStat; }
    public int getSpeedStat() { return speedStat; }
    public Move getNormalAttack() { return normalAttack; }
    public SpecialAttack getSpecialAttack() { return specialAttack; }

    public boolean isFainted() {
        return currentHp <= 0;
    }

    // ----- health -----

    /**
     * Subtracts health. Negative amounts are ignored and health never drops below 0.
     * @return the damage that was actually taken
     */
    public int takeDamage(int amount) {
        if (amount < 0) {
            amount = 0;
        }
        int actual = Math.min(amount, currentHp);
        currentHp -= actual;
        return actual;
    }

    /** Restores health up to maxHp. A fainted Pokemon cannot be healed. */
    public void heal(int amount) {
        if (isFainted() || amount < 0) {
            return;
        }
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    // ----- blocking -----

    /** Raise a guard that halves the next incoming attack. */
    public void block() {
        blocking = true;
        blockedThisTurn = true;
    }

    public boolean isBlocking() {
        return blocking;
    }

    /** Called after the block has absorbed an attack. */
    public void endBlock() {
        blocking = false;
    }

    /** To stop block-spamming, a Pokemon cannot block two turns in a row. */
    public boolean canBlock() {
        return !blockedLastTurn;
    }

    /**
     * Called by the BattleManager at the start of this Pokemon's turn.
     * An unused block expires here, and the special attack cooldown ticks down.
     */
    public void startTurn() {
        blockedLastTurn = blockedThisTurn;
        blockedThisTurn = false;
        blocking = false;
        specialAttack.tickCooldown();
    }

    /** Clears block state when this Pokemon leaves the battle field. */
    public void resetTurnState() {
        blocking = false;
        blockedThisTurn = false;
        blockedLastTurn = false;
    }

    // ----- polymorphism -----

    /** Flavor text shown when this Pokemon uses its special attack. Written by each subclass. */
    public abstract String describeSpecialAttack();
}
