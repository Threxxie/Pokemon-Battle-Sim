package model;

/** Electric-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class ElectricPokemon extends Pokemon {

    public ElectricPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.ELECTRIC, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " crackles with electricity and uses " + getSpecialAttack().getName() + "!";
    }
}
