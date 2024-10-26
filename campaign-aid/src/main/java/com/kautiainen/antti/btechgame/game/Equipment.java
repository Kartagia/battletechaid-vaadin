
package com.kautiainen.antti.btechgame.game;


/**
 * An item representing equipment.
 */
public class Equipment {
    
    /**
     * The name of the equipment.
     */
    public final String name;

    /**
     * The abbreviation of the equpment.
     * - If undefined, the equipment has no abbreviation.
     */
    public final String abbrev;

    /**
     * The mass of the item.
     */
    public final double mass;

    /**
     * The equipment size in critical slots.
     */
    public final int size;

    /**
     * Create a new equipment.
     * @param name The name of the equipment.
     * @param abbrev The abbreviation of the equipment.
     * @param mass The mass of the equipment in tons.
     * @param size The size of the equipment in critical slots.
     */
    public Equipment(String name, String abbrev, double mass, int size) {
        this.name = name;
        this.abbrev = abbrev;
        this.mass = mass;
        this.size = size;
        this.modifiers = new RulesModifier[0];
    }

    /**
     * Create a new equipment.
     * @param name The name of the equipment.
     * @param abbrev The abbreviation of the equipment.
     * @param mass The mass of the equipment in tons.
     */
    public Equipment(String name, String abbreviation, double mass) {
        this(name, abbreviation, mass, 1);
    }

    /**
     * The rules modifiers of the equipment.
     */
    public final RulesModifier[] modifiers;

    @Override
    public String toString() {
        return String.format("%s%s", this.name, "+".repeat(this.modifiers.length));
    }
}
