package com.kautiainen.antti.btechgame.game;

/**
 * Structure loadout.
 */
public class StructureLoadout {

    /**
     * The location of the internal structure.
     */
    public final HitLocation location;

    /**
     * The maximum structure.
     * If the maximum is undefined, the loadout does not alter the maximum structure.
     */
    public final Short max;

    /**
     * The current structure.
     */
    public final short current;

    /**
     * Create a new structure loadout.
     * @param location The location of the loadout.
     * @param max The maximum structure of the loadout. If undefined, the entry does not alter the maximum.
     * @param current The amount the loadout alters the current loadout.
     * @throws IllegalArgumentException The loadout location, maximum, or current value is invalid.
     */
    public StructureLoadout(HitLocation location, Short max, short current) throws IllegalArgumentException {
        if (max <= 0) throw new IllegalArgumentException("Negative maximum structure");
        if (current > max) throw new IllegalArgumentException("Current structure larger than maximum");
        this.location = location;
        this.max = max;
        this.current = current;
    }

    public static StructureLoadout ofDamage(HitLocation location, short amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount of structural damage");
        return new StructureLoadout(location, null, (short)-amount);
    }

    public static StructureLoadout ofRepair(HitLocation location, short amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount of structure repaired");
        return new StructureLoadout(location, null, amount);
    }
}
