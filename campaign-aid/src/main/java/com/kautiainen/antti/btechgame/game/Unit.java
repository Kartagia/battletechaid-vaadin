
package com.kautiainen.antti.btechgame.game;

import java.util.Arrays;

import com.kautiainen.antti.btechgame.game.LoadoutController.EquipmentLoadout;

/**
 * A POJO representing an unit.
 */
public class Unit {
    
    /**
     * The name of the unit.
     */
    final String name;

    /**
     * The model name of the unit.
     */
    final String model;

    /**
     * The total tonnage of the unit.
     */
    final double tonnage;

    /**
     * The available tonnage.
     */
    final double availableTonnage;

    /**
     * The loadout of the unit.
     */
    final EquipmentLoadout[] loadout;

    /**
     * The unit type of the unit.
     */
    final GameRules.UnitType type;

    /**
     * The armor loadout.
     */
    final ArmorLoadout[] armor; 

    /**
     * The internal sturcture loadout.
     */
    final StructureLoadout[] structure; 

    /**
     * Create a new unit.
     * @param type The unit type.
     * @param name THe name of the unit.
     * @param tonnage The maximum tonnage of the unit.
     * @param availableTonange The tonnage available for equipment.
     * @param loadout The initial loadout of the unit.
     */
    public Unit(GameRules.UnitType type, String name, String model, double tonnage,
    double availableTonnage, EquipmentLoadout[] loadout, ArmorLoadout[] armor, StructureLoadout[] structure) {
        if (type == null) throw new IllegalArgumentException("Unit without unit type");
        if (armor == null) throw new IllegalArgumentException("Unit without armor");
        if (structure == null) throw new IllegalArgumentException("Unit without structure");
        this.type = type;
        this.name = name;
        this.model = model;
        this.tonnage = tonnage;
        this.availableTonnage = availableTonnage;
        this.loadout = java.util.Arrays.asList(loadout).stream().toArray( (int size) -> (new EquipmentLoadout[size]) );
        this.armor = java.util.Arrays.asList(armor).toArray( (int size) -> (new ArmorLoadout[size]));
        this.structure = java.util.Arrays.asList(structure).toArray( (int size) -> (new StructureLoadout[size]));
    }

    /**
     * Create a loadout controller for this unit.
     * @return The loadout controller for the unit preserving its current loadout.
     */
    public LoadoutController getLoadoutController() {
        return getLoadoutController(false, true);
    }

    public LoadoutController getLoadoutController(boolean stripArmor, boolean stripEquipment) {
        return new LoadoutController(this.tonnage, this.availableTonnage
        + (stripEquipment ? (Arrays.stream(this.loadout).collect(
            java.util.stream.Collectors.summingDouble( (EquipmentLoadout item) -> (item == null ? 0.0 : -item.getValue().mass) )
        )) : 0.0)
        + (stripArmor ? Math.floor(Arrays.stream(this.armor).filter( item -> (item.max != null)).collect(
            java.util.stream.Collectors.summingInt( (ArmorLoadout location) -> (-location.max))
        ) / 16.0) : 0.0)); 

    }
}
