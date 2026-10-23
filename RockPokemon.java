package model;

/** Rock-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class RockPokemon extends Pokemon {

    public RockPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.ROCK, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " hurls solid stone with " + getSpecialAttack().getName() + "!";
    }
}
