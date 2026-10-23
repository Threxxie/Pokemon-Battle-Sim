package model;

/** The elemental types that a Pokemon (and therefore its attacks) can have. */
public enum ElementType {
    NORMAL("Normal"),
    FIRE("Fire"),
    WATER("Water"),
    GRASS("Grass"),
    ELECTRIC("Electric"),
    ROCK("Rock"),
    GHOST("Ghost");

    private final String displayName;

    ElementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
