package com.kautiainen.antti.btechgame.game;

/**
 * The armor loadout of a location.
 */
public class ArmorLoadout {

    /**
     * The hit location of the armor loadout.
     */
    public final HitLocation location;

    /**
     * The maximum number of armor.
     * If this value is undefined, the loadout does not alter the maximum armor.
     */
    public final Short max; 

    /**
     * The current amount of armor.
     */
    public final short current;

    /**
     * 
     * @param location
     * @param maximum
     * @param current
     * @throws IllegalArgumentException
     */
    public ArmorLoadout(HitLocation location, Integer maximum, int current) throws IllegalArgumentException {
        if (current < Short.MIN_VALUE) throw new IllegalArgumentException("The current value too small");
        if (current > Short.MAX_VALUE) throw new IllegalArgumentException("The current value too large");
        if (maximum != null) {
            if (maximum < Short.MIN_VALUE) throw new IllegalArgumentException("The maximal value too small");
            if (maximum > Short.MAX_VALUE) throw new IllegalArgumentException("The maximal value too large");
            if (current > maximum) {
                throw new IllegalArgumentException("The current armor exceeds the maximum");
            }
        }
        this.location = location;
        this.max = maximum == null ? null : maximum.shortValue();
        this.current = (short)current;
    }

    /**
     * Create initial armor loadout.
     * @param location the location of the armor.
     * @param maximum The maximum amount of armor for the location.
     */
    public ArmorLoadout(HitLocation location, int maximum) throws IllegalArgumentException {
        this(location, maximum, 0);
    }

    public static ArmorLoadout ofDamage(HitLocation location, int amount) throws IllegalArgumentException  {
        if (amount < 0) throw new IllegalArgumentException("Negative armor damage"); 
        return new ArmorLoadout(location, null, -amount);
    }

    public static ArmorLoadout ofRepair(HitLocation location, int amount) throws IllegalArgumentException  {
        if (amount < 0) throw new IllegalArgumentException("Negative armor repair"); 
        return new ArmorLoadout(location, null, amount);
    }

}
