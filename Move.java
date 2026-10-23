package model;

/**
 * ABSTRACTION: a Move is anything a Pokemon can attack with.
 * The BattleManager only talks to this interface, so it never needs to know
 * whether it is using a normal or a special attack (POLYMORPHISM).
 */
public interface Move {

    String getName();

    /** Base power of the move. Higher power means more damage. */
    int getPower();

    /** Can this move be used right now? (A special attack on cooldown cannot.) */
    boolean isAvailable();

    /** Called by the BattleManager each time the move is used. */
    void onUse();
}
