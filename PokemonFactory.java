package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * The one place that knows the roster and every Pokemon's stats.
 * It always builds NEW objects, so each battle starts with fully healed Pokemon
 * and fresh cooldowns (this is what makes restarting work).
 */
public final class PokemonFactory {

    private static final Map<String, Supplier<Pokemon>> ROSTER = new LinkedHashMap<>();

    static {
        //                                          name        HP  ATK DEF SPD  special       power
        ROSTER.put("Pikachu",    () -> new ElectricPokemon("Pikachu",    90, 55,  40,  90, "Thunderbolt", 65));
        ROSTER.put("Charizard",  () -> new FirePokemon("Charizard",     110, 84,  78, 100, "Flamethrower", 65));
        ROSTER.put("Charmander", () -> new FirePokemon("Charmander",     95, 52,  43,  65, "Ember",        60));
        ROSTER.put("Squirtle",   () -> new WaterPokemon("Squirtle",     100, 48,  65,  43, "Water Pulse",  60));
        ROSTER.put("Bulbasaur",  () -> new GrassPokemon("Bulbasaur",    105, 49,  49,  45, "Vine Whip",    60));
        ROSTER.put("Gengar",     () -> new GhostPokemon("Gengar",       100, 65,  60, 110, "Shadow Ball",  65));
        ROSTER.put("Geodude",    () -> new RockPokemon("Geodude",       100, 80, 100,  20, "Rock Throw",   60));
        ROSTER.put("Snorlax",    () -> new NormalPokemon("Snorlax",     160, 65,  65,  30, "Body Slam",    70));
    }

    private PokemonFactory() { }

    public static List<String> getRosterNames() {
        return new ArrayList<>(ROSTER.keySet());
    }

    public static Pokemon create(String name) {
        Supplier<Pokemon> supplier = ROSTER.get(name);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown Pokemon: " + name);
        }
        return supplier.get();
    }

    /** One fresh copy of every Pokemon (used by the selection screen). */
    public static List<Pokemon> createRoster() {
        return createTeam(getRosterNames());
    }

    public static List<Pokemon> createTeam(List<String> names) {
        List<Pokemon> team = new ArrayList<>();
        for (String name : names) {
            team.add(create(name));
        }
        return team;
    }

    /** Picks as many opponents as the player chose, from the Pokemon the player did NOT choose. */
    public static List<Pokemon> createOpponentTeam(List<String> playerNames, Random random) {
        List<String> remaining = getRosterNames();
        remaining.removeAll(playerNames);
        Collections.shuffle(remaining, random);
        return createTeam(remaining.subList(0, Math.min(playerNames.size(), remaining.size())));
    }
}
