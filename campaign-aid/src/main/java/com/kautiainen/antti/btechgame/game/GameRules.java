
package com.kautiainen.antti.btechgame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game rules. 
 */
public class GameRules {

    /**
     * The unit types of the BattleTech.
     */
    public static enum UnitType {
        Mech, Vehicle, Fighter, AeroSpace, DropShip, JumpShip, Infantry, BattleArmor;
    }
    /**
     * The record of unit type specific hit locations.
     * @param unitType The type of the unit.
     * @param location The hit location.
     */
    public static record UnitHitLocation(String unitType, HitLocation location, Short criticalSlots) {

    }

    /**
     * Create a new slotted critical location for a unit type.
     * @param unitType The unit type.
     * @param name The hit location name.
     * @param criticalSlots The critical slot capacity of the location.
     * @return The hit location for the unit type.
     * @throws IllegalArgumentException Any parameter was invalid.
     */
    public UnitHitLocation createSlottedHitLocation(UnitType unitType, String name, String abbrev, short criticalSlots) {
        if (unitType == null) throw new IllegalArgumentException("Missing unit type");
        if (name == null) throw new IllegalArgumentException("Missing hit location name");
        if (criticalSlots < 0) throw new IllegalArgumentException("Invalid critical slot capacity", new IllegalArgumentException("Negative critical slot count"));
        return new UnitHitLocation(unitType.toString(), new HitLocation.SlottedHitLocation(name, abbrev, criticalSlots), criticalSlots);
    }

    /**
     * Create a slotted unit hit location.
     * @param unitType The unit type.
     * @param location The location.
     * @return The unit hit location for the given unit type and slotted mech location.
     * @throws IllegalArgumentException The location or unit type was invalid.
     */
    public static UnitHitLocation createSlottedHitLocation(UnitType unitType, HitLocation.SlottedHitLocation location) {
        if (unitType == null) throw new IllegalArgumentException("Missing unit type");
        if (location == null) throw new IllegalArgumentException("Missing hit location");
        return new UnitHitLocation(unitType.toString(), location, (short)Math.max(location.critSlotCapacity, Short.MAX_VALUE));
    }

    /**
     * Create an unslotted unit hit location.
     * @param unitType The unit type.
     * @param location The location.
     * @return The unit hit location for the given unit type and generic hit location without critical slot capacity.
     * @throws IllegalArgumentException The location or unit type was invalid.
     */
    public static UnitHitLocation createUnslottedHitLocation(UnitType unitType, HitLocation location) {
        return new UnitHitLocation(unitType.toString(), location, (short)0);
    }

    /**
     * Create a new unslotted critical location for a unit type.
     * @param unitType The unit type.
     * @param name The hit location name.
     * @return The hit location for the unit type.
     * @throws IllegalArgumentException Any parameter was invalid.
     */
    public UnitHitLocation craeteUnslottedHitLocation(UnitType unitType, String name, String abbrev) {
        return new UnitHitLocation(unitType.toString(), new HitLocation(name, abbrev), (short)0);
    }

    /**
     * Create a mech unit hit location with a name, an abbreviation, and critical slot capacity.
     * @param name The name of the location.
     * @param abbrev The abbreviation of the location.
     * @param criticalSlots The critical slot capacity
     * @return The unit hit location for a mech hit location with given name, abbreviation, and equipment slots.
     */
    public static UnitHitLocation createMechHitLocation(String name, String abbrev, short criticalSlots) {
        return new UnitHitLocation(UnitType.Mech.toString(), new HitLocation.SlottedHitLocation(name, abbrev, criticalSlots), criticalSlots);
    }

    /**
     * Create a mech hit location.
     * @param criticalSlots The number of critical slots.
     * @param nameSegments The name segemnts of the location.
     * @return The unit hit location for the mech hit location with given name.
     */
    public static UnitHitLocation createMechHitLocation(short criticalSlots, String... nameSegments) {
        return createMechHitLocation(String.join(" ", nameSegments), String.join("", 
        Arrays.stream(nameSegments)
        .map( (String segment) -> (segment.substring(0,1))).toArray( (size) -> (new String[size]))), criticalSlots);
    }

    /**
     * The base game rules.
     */
    public static class BaseGameRules extends GameRules {

        public BaseGameRules(String mode) {
            super("Harebrained Base Game", mode);
        }

    }

    /**
     * The game ruels name.
     */
    public final String mode;

    /**
     * The campaign name.
     */
    public final String name;

    /**
     * Get default hit locations.
     * @return The default hit locations for the game rules.
     */
    public static UnitHitLocation[] defaultHitLocations() {
        final List<UnitHitLocation> result = new ArrayList<>(8);
        result.add(createMechHitLocation((short)1, "Head"));
        result.add(createMechHitLocation((short)4, "Center", "Torso"));
        Arrays.asList("Left", "Right").forEach( (String prefix) -> {
            result.add(createMechHitLocation((short)10, prefix, "Torso"));
            result.add(createMechHitLocation((short)8, prefix, "Arm"));
            result.add(createMechHitLocation((short)4, prefix, "Leg"));
        });

        return result.stream().toArray( (int size) -> (new UnitHitLocation[size]));
    }
    
    /**
     * Create new game rules.
     * @param name The name of the game rules.
     * @param mode The game mode.
     */
    public GameRules(String name, String mode) {
        this(name, mode, java.util.Arrays.asList(UnitType.Mech, UnitType.Vehicle).toArray( (size) -> (new UnitType[size])),
        GameRules.defaultHitLocations());
    }

    /**
     * Create new game rules.
     * @param name The name of the game rules.
     * @param mode The game mode.
     * @param unitTypes The allowed unit types.
     * @param hitLocations The hit locations of the game.
     */
    public GameRules(String name, String mode, UnitType[] unitTypes, UnitHitLocation[] hitLocations) {
        this.mode = mode;
        this.name = name;
        this.unitTypes = unitTypes;
        this.hitLocations = hitLocations;
    }

    /**
     * The unit types of the rules.
     */
    public final UnitType[] unitTypes;

    /**
     * The hit locations of the unit type.
     * @return The hit locations of the unit type.
     */
    public final UnitHitLocation[] hitLocations; 
}


