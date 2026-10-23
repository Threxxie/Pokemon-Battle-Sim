package model;

/** Grass-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class GrassPokemon extends Pokemon {

    public GrassPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.GRASS, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " whips up the leaves and uses " + getSpecialAttack().getName() + "!";
    }
}
