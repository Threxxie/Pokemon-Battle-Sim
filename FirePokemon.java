package model;

/** Fire-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class FirePokemon extends Pokemon {

    public FirePokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.FIRE, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " erupts in flames and uses " + getSpecialAttack().getName() + "!";
    }
}
