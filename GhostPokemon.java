package model;

/** Ghost-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class GhostPokemon extends Pokemon {

    public GhostPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.GHOST, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " cackles from the shadows and uses " + getSpecialAttack().getName() + "!";
    }
}
