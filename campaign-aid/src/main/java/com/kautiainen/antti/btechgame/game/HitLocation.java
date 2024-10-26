
package com.kautiainen.antti.btechgame.game;

/**
 * A hit location represents a hit location.
 */
public class HitLocation {
    
    /**
     * A hit location with critical slots.
     */
    public static class SlottedHitLocation extends HitLocation {

        /**
         * The critical slot capacity.
         */
        public final int critSlotCapacity;

        /**
         * Create a new mech hit location.
         * @param name The name of the location.
         * @param abbrev The abbreviation of the location.
         * @param critSlotCapacity The critical slot capacity of the location.
         */
        public SlottedHitLocation(String name, String abbrev, short critSlotCapacity) {
            super(name, abbrev);
            this.critSlotCapacity = critSlotCapacity;
        }
    }

    /**
     * The name of the location.
     */
    public final String name; 

    /**
     * The abbreviation of the location.
     */
    public final String abbrev; 

        /**
         * Create a new mech hit location.
         * @param name The name of the location.
         * @param abbrev The abbreviation of the location.
         */
        public HitLocation(String name, String abbrev) {
        this.name = name;
        this.abbrev = abbrev;
    }

}
