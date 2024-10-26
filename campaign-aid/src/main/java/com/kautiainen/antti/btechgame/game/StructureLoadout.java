package com.kautiainen.antti.btechgame.game;

/**
 * Structure loadout.
 */
public class StructureLoadout extends HitLocationTrack {

    /**
     * Create a new structure loadout.
     * @param location The location of the loadout.
     * @param max The maximum structure of the loadout. If undefined, the entry does not alter the maximum.
     * @param current The amount the loadout alters the current loadout.
     * @throws IllegalArgumentException The loadout location, maximum, or current value is invalid.
     */
    public StructureLoadout(HitLocation location, Short max, short current) throws IllegalArgumentException {
        super(location, max, current);
    }

    /**
     * Create a new structure loadout.
     * @param location The location of the loadout.
     * @param max The maximum structure of the loadout. If undefined, the entry does not alter the maximum.
     * @param current The amount the loadout alters the current loadout.
     * @throws IllegalArgumentException The loadout location, maximum, or current value is invalid.
     */
    public StructureLoadout(HitLocation location, Integer max, int current) throws IllegalArgumentException {
        super(location, max, current);
    }

    @Override
    public StructureLoadout addMaximum(int amount) {
        return new StructureLoadout(this.location, this.max == null ? amount : this.max.intValue() + amount, this.current);
    }

    @Override
    public StructureLoadout addCurrent(int amount) {
        return new StructureLoadout(this.location, this.max == null ? null : this.max.intValue(), this.current + amount);
    }
   

    public static StructureLoadout ofDamage(HitLocation location, short amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount of structural damage");
        return new StructureLoadout(location, (Short)null, (short)-amount);
    }

    public static StructureLoadout ofRepair(HitLocation location, short amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount of structure repaired");
        return new StructureLoadout(location, (Short)null, amount);
    }
}
