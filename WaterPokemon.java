package model;

/** Water-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class WaterPokemon extends Pokemon {

    public WaterPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.WATER, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " gathers a rush of water and uses " + getSpecialAttack().getName() + "!";
    }
}
