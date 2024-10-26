package com.kautiainen.antti.btechgame.game;

/**
 * A hit locataion track tracks a current, maximum, and minimum value attached to a hit location.
 * @author kautsu
 */
public abstract class HitLocationTrack {

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


    public HitLocationTrack(HitLocation location, Short maximum, short current) {
            if (maximum != null && current > maximum) {
                throw new IllegalArgumentException("The current armor exceeds the maximum");
            }
            this.location = location;
            this.max = maximum;
            this.current = current;
        }

    /**
     * Create a new hit location track.
     * @param location The location of the track.
     * @param maximum The maximum of the track. If undefined, the track does not alter the maximum value.
     * @param current THe curretn value. 
     */
    public HitLocationTrack(HitLocation location, Integer maximum, int current) {
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
     * Get a hit location track with maximum altered.
     * 
     * @param amount The maximum change.
     * @return The hit location track with amount altered. 
     * @throws IllegalArgumentException The amount was invalid. 
     * @throws IllegalStateException The maximum amount could not be set due state of the object.
     * @implNote The implementor of subclasses should replace the implementation with one returning the subclass instance.
     */
    public HitLocationTrack addMaximum(int amount) throws IllegalArgumentException, IllegalStateException {
        
        return new HitLocationTrack(this.location, this.max == null ? amount : this.max.intValue() + amount, this.current) {
        };
    }

    public HitLocationTrack addCurrent(int amount) throws IllegalArgumentException, IllegalStateException {
        if (amount < 0) {
            if (this.current + amount < 0) {
                throw new IllegalArgumentException("Cannot reduce current amount below zero");
            }
        } else if (amount == 0) {
            return this;
        } else if (this.max != null && this.current < this.max - amount) {
            throw new IllegalArgumentException("Cannot increase current amount above maximum");
        }
        return new HitLocationTrack(this.location, this.max == null ? amount : this.max.intValue(), this.current + amount) {
        };        
    }

}
