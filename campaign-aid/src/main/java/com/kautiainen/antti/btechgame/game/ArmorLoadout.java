package com.kautiainen.antti.btechgame.game;

/**
 * The armor loadout of a location.
 */
public class ArmorLoadout extends HitLocationTrack {

    /**
     * Create a new armor loadout.
     * @param location The location.
     * @param maximum The maximum value. If undefined, the loadout does not affect the maximum.
     * @param current The current value.
     * @throws IllegalArgumentException The location, maximum or current was invalid.
     */
    public ArmorLoadout(HitLocation location, Integer maximum, int current) throws IllegalArgumentException {
        super(location, maximum, current);
    }

    /**
     * Create initial armor loadout.
     * @param location the location of the armor.
     * @param maximum The maximum amount of armor for the location.
     */
    public ArmorLoadout(HitLocation location, int maximum) throws IllegalArgumentException {
        this(location, maximum, 0);
    }

    @Override
    public ArmorLoadout addMaximum(int amount) {
        return new ArmorLoadout(this.location, this.max == null ? amount : this.max.intValue() + amount, this.current);
    }

    @Override
    public ArmorLoadout addCurrent(int amount) {
        return new ArmorLoadout(this.location, this.max == null ? null : this.max.intValue(), this.current + amount);
    }

    /**
     * Create armor damage representing loadout.
     * @param location The target location.
     * @param amount THe amount of damage received.
     * @returns The armor loadout representing an armor damage event.
     * @throws IllegalArgumentException The location or armount was invalid. 
     */
    public static ArmorLoadout ofDamage(HitLocation location, int amount) throws IllegalArgumentException  {
        if (amount < 0) throw new IllegalArgumentException("Negative armor damage"); 
        return new ArmorLoadout(location, null, -amount);
    }

    /**
     * Create armor repair representing loadout.
     * @param location The target location.
     * @param amount THe amount of damage repaired.
     * @returns The armor loadout representing an armor repair event.
     * @throws IllegalArgumentException The location or armount was invalid. 
     */
    public static ArmorLoadout ofRepair(HitLocation location, int amount) throws IllegalArgumentException  {
        if (amount < 0) throw new IllegalArgumentException("Negative armor repair"); 
        return new ArmorLoadout(location, null, amount);
    }

}
