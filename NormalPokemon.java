package model;

/** Normal-type Pokemon. Inherits everything from Pokemon and adds its own special attack flavor text. */
public class NormalPokemon extends Pokemon {

    public NormalPokemon(String name, int maxHp, int attack, int defense, int speed,
                    String specialName, int specialPower) {
        super(name, ElementType.NORMAL, maxHp, attack, defense, speed, specialName, specialPower);
    }

    @Override
    public String describeSpecialAttack() {
        return getName() + " puts its full weight behind " + getSpecialAttack().getName() + "!";
    }
}
