
package com.kautiainen.antti.btechgame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.kautiainen.antti.btechgame.game.GameRules.UnitType;
import com.kautiainen.antti.btechgame.game.LoadoutController.EquipmentLoadout;

/**
 * A POJO representing an unit.
 */
public class Unit {

    /**
     * The name of the unit.
     */
    public final String name;

    /**
     * The model name of the unit.
     */
    public final String model;

    /**
     * The total tonnage of the unit.
     */
    public final double tonnage;

    /**
     * The available tonnage.
     */
    public final double availableTonnage;

    /**
     * The loadout of the unit.
     */
    public final EquipmentLoadout[] loadout;

    /**
     * The unit type of the unit.
     */
    public final GameRules.UnitType type;

    /**
     * The armor loadout.
     */
    public final ArmorLoadout[] armor;

    /**
     * The internal sturcture loadout.
     */
    public final StructureLoadout[] structure;

    /**
     * Create a new unit.
     * 
     * @param type             The unit type.
     * @param name             THe name of the unit.
     * @param tonnage          The maximum tonnage of the unit.
     * @param availableTonange The tonnage available for equipment.
     * @param loadout          The initial loadout of the unit.
     */
    public Unit(GameRules.UnitType type, String name, String model, double tonnage,
            double availableTonnage, EquipmentLoadout[] loadout, ArmorLoadout[] armor, StructureLoadout[] structure) {
        if (type == null)
            throw new IllegalArgumentException("Unit without unit type");
        if (armor == null)
            throw new IllegalArgumentException("Unit without armor");
        if (structure == null)
            throw new IllegalArgumentException("Unit without structure");
        this.type = type;
        this.name = name;
        this.model = model;
        this.tonnage = tonnage;
        this.availableTonnage = availableTonnage;
        this.loadout = java.util.Arrays.asList(loadout == null ? new EquipmentLoadout[0] : loadout).stream()
                .toArray((int size) -> (new EquipmentLoadout[size]));
        this.armor = java.util.Arrays.asList(armor).toArray((int size) -> (new ArmorLoadout[size]));
        this.structure = java.util.Arrays.asList(structure).toArray((int size) -> (new StructureLoadout[size]));
    }

    /**
     * Create a loadout controller for this unit.
     * 
     * @return The loadout controller for the unit preserving its current loadout.
     */
    public LoadoutController getLoadoutController() {
        return getLoadoutController(false, true);
    }

    public LoadoutController getLoadoutController(boolean stripArmor, boolean stripEquipment) {
        if (this.loadout == null)
            return new LoadoutController(this.tonnage, this.availableTonnage);
        return new LoadoutController(
                this.tonnage,
                this.availableTonnage
                        + (stripEquipment
                                ? Optional.ofNullable(Arrays.stream(this.loadout).collect(
                                        java.util.stream.Collectors.summingDouble(
                                                (EquipmentLoadout item) -> (item == null ? 0.0
                                                        : -(item.getValue().mass)))))
                                        .orElse(0.0)
                                : 0.0)
                        + (stripArmor
                                ? Math.floor(Arrays.stream(this.armor).filter(item -> (item.max != null)).collect(
                                        java.util.stream.Collectors
                                                .summingInt((ArmorLoadout location) -> (-location.max)))
                                        / 16.0)
                                : 0.0));
    }

    /**
     * Test validity of the model name.
     * 
     * @param model The tested model name.
     * @returns True, if and only if the model name is a valid model name.
     */
    public static boolean isValidModel(String model) {
        return Pattern.compile("^(?:\\p{Lu})(?:[\\p{Lu}\\p{N}])*(?:-(?:[\\\\p{Lu}\\\\p{N}])+)*$", Pattern.UNICODE_CASE)
                .matcher(model).matches();
    }

    /**
     * Test validity of the model name.
     * 
     * @param model The tested model name.
     * @returns True, if and only if the model name is a valid model name.
     */
    public boolean validModel(String model) {
        return Unit.isValidModel(model);
    }

    /**
     * Create amor loadout of hit locations from collection of armor loadouts.
     * 
     * @param entries The armor loadout entries.
     * @return A map from hit locations to armor loadouts of hit locations for every
     *         location found in the entries.
     */
    public static java.util.Map<HitLocation, ArmorLoadout> getFullArmorLoadout(
            java.util.Collection<? extends ArmorLoadout> entries) {
        java.util.Map<HitLocation, ArmorLoadout> result = new java.util.HashMap<>();

        for (ArmorLoadout entry : entries) {
            if (result.containsKey(entry.location)) {
                ArmorLoadout source = result.get(entry.location);
                result.put(source.location, new ArmorLoadout(source.location, source.max.intValue() +
                        (entry.max == null ? 0 : entry.max.intValue()), source.current + entry.current));
            } else if (entry.max == null) {
                throw new IllegalStateException(String.format("Setting current value before maximum on location %s",
                        entry.location == null ? "undefined" : entry.location.name));
            } else {
                ArmorLoadout added = new ArmorLoadout(entry.location, entry.max.intValue(), entry.current);
                result.put(added.location, added);
            }
        }
        return result;
    };

    /**
     * Get hit location track summary.
     * 
     * @param <TRACK>  The type of the entries.
     * @param entries  The current entries of the type.
     * @param location The location, whose current hit location track is acquired.
     * @return The hit lcoation track created by combining all teh entries of the
     *         location.
     * @throws IllegalStateException The state of the entries prevents the
     *                               generation of the summary.
     */
    public static <TRACK extends HitLocationTrack> HitLocationTrack getHitLocationTrackSummary(
            java.util.List<? extends TRACK> entries, HitLocation location) {
        HitLocationTrack result = entries.stream().filter(
                (HitLocationTrack cursor) -> (Objects.equals(cursor.location, location))).collect(
                        () -> (new AtomicReference<HitLocationTrack>()),
                        (AtomicReference<HitLocationTrack> accumulator, TRACK entry) -> {
                            HitLocationTrack current = accumulator.get();
                            if (current == null) {
                                accumulator.set(entry);
                            } else if (entry.max == null) {
                                accumulator.set(current.addCurrent(entry.current));
                            } else {
                                accumulator.set(current.addMaximum(entry.max).addCurrent(entry.current));
                            }
                        },
                        (AtomicReference<HitLocationTrack> head, AtomicReference<HitLocationTrack> tail) -> {
                            HitLocationTrack accumulator = head.get();
                            if (accumulator == null) {
                                head.set(tail.get());
                            } else {
                                HitLocationTrack tailTrack = tail.get();
                                if (tailTrack != null) {
                                    if (tailTrack.max == null) {
                                        head.set(accumulator.addCurrent(tailTrack.current));
                                    } else {
                                        head.set(accumulator.addMaximum(tailTrack.max).addCurrent(tailTrack.current));
                                    }
                                }
                            }
                        })
                .get();
        return result;
    }

    /**
     * Create an armor loadout summary for the location.
     * 
     * @param entries  The armor loadout entries.
     * @param location The hit location of the created loadout.
     * @return The armor loadout generated by combining the given entries.
     * @throws IllegalStateExceptioon The given entries contained an invalid armor
     *                                loadout.
     */
    public static ArmorLoadout getArmorLoadout(java.util.List<ArmorLoadout> entries, HitLocation location)
            throws IllegalStateException {
        ArmorLoadout currentArmor = entries.stream().filter(
                (ArmorLoadout cursor) -> (java.util.Objects.equals(cursor.location, location))).collect(
                        () -> (new AtomicReference<ArmorLoadout>(null)),
                        (AtomicReference<ArmorLoadout> result, ArmorLoadout cursor) -> {
                            if (cursor.max != null) {
                                ArmorLoadout source = result.get();
                                result.set(
                                        new ArmorLoadout(source.location,
                                                (source.max == null ? 0 : source.max.intValue())
                                                        + cursor.max.intValue(),
                                                source.current + cursor.current));
                            } else if (result.get() == null) {
                                throw new IllegalStateException("The current armor is set before maximum");
                            } else {
                                ArmorLoadout source = result.get();
                                result.set(new ArmorLoadout(source.location,
                                        source.max == null ? null : source.max.intValue(),
                                        source.current + cursor.current));
                            }
                        },
                        (AtomicReference<ArmorLoadout> head, AtomicReference<ArmorLoadout> tail) -> {
                            ArmorLoadout headLoadout = head.get();
                            if (headLoadout == null) {
                                head.set(tail.get());
                            } else if (headLoadout.max == null) {
                                // The first half of the seeking is invalid.
                                throw new IllegalStateException("Current armor set before maximum");
                            } else {
                                ArmorLoadout tailLoadout = tail.get();
                                head.set(new ArmorLoadout(headLoadout.location, headLoadout.max.intValue() +
                                        java.util.Optional.ofNullable(tailLoadout.max).orElse((short) 0),
                                        headLoadout.current + tailLoadout.current));
                            }
                        })
                .get();
        return currentArmor;
    }

    /**
     * Create a structure loadout summary for the location.
     * 
     * @param entries  The structure loadout entries.
     * @param location The hit location of the created loadout.
     * @return The structure loadout generated by combining the given entries.
     * @throws IllegalStateExceptioon The given entries contained an invalid
     *                                structure
     *                                loadout.
     */
    public static StructureLoadout getStructureLoadout(java.util.List<StructureLoadout> entries, HitLocation location)
            throws IllegalStateException {
        HitLocationTrack current = getHitLocationTrackSummary(entries, location);
        if (current == null)
            throw new IllegalStateException("Could not generate the structure summary");
        if (current instanceof StructureLoadout result) {
            return result;
        } else {
            return new StructureLoadout(location, current.max, current.current);
        }
    }

    /**
     * The unit builder.
     */
    public class UnitBuilder {

        private UnitType type;

        private String name;

        private String model;

        private double tonnage;

        private double availableTonnage;

        private final ArrayList<EquipmentLoadout> equipment = new ArrayList<>();

        private final ArrayList<ArmorLoadout> armor = new ArrayList<>();

        private final ArrayList<StructureLoadout> structure = new ArrayList<>();

        private final boolean isMutable;

        public boolean isMutable() {
            return this.isMutable;
        }

        public UnitBuilder() {
            this(false);
        }

        public UnitBuilder(boolean isMutable) {
            this.isMutable = isMutable;
        }

        /**
         * Test validity of a property value.
         * 
         * @param property The builder property.
         * @param value    The tested value.
         * @return True, if and only if the given property value is valid.
         */
        @SuppressWarnings("HidesField")
        protected boolean validPropertyValue(String property, Object value) {
            if (property == null)
                return false;
            return switch (property) {
                case "mutable" -> value != null && value instanceof Boolean;
                case "model" -> value instanceof String modelName && validModel(modelName);
                default -> false;
            };
        }

        protected UnitBuilder(UnitBuilder source, String property, Object value)
                throws IllegalArgumentException, IllegalStateException {
            this("mutable".equals(property) ? ((Boolean) value) == true : source.isMutable());
            switch (property) {
                case "model":
                    this.model = (String) value;
                    break;
                default:

            }
        }

        public UnitBuilder setModel(String model) throws IllegalStateException, IllegalArgumentException {
            if (model == null) {
                throw new IllegalArgumentException("Invalid model", new NullPointerException("Undefined model name"));
            } else if (Unit.this.validModel(model)) {
                if (this.isMutable()) {
                    this.model = model;
                    return this;
                } else {
                    return new UnitBuilder(this, "model", model);
                }
            } else {
                return this;
            }
        }

        /**
         * Add maximum armor of a hit location.
         * 
         * @param location The affected hit location.
         * @param amount   The amount of armor added.
         * @return The unit builder with maximum armor added.
         * @throws IllegalArgumentException The amount was invalid.
         */
        public UnitBuilder addMaxArmor(HitLocation location, int amount) {
            ArmorLoadout currentLoadout = getArmorLoadout(this.armor, location);
            if (amount < 0) {
                // Removing maximum armor.
                if (Optional.ofNullable(currentLoadout.max).orElse((short) 0) > -amount) {
                    throw new IllegalArgumentException("Cannot remove more maximum armor than the unit has");
                }
                if (-amount > currentLoadout.current) {
                    throw new IllegalArgumentException("Cannot reduce the maximum armor below the current armor");
                }
            } else if (amount == 0) {
                return this;
            }

            if (isMutable()) {
                this.armor.add(new ArmorLoadout(currentLoadout.location, amount));
                return this;
            } else {
                return Unit.this.new UnitBuilder(this, "armor", new ArmorLoadout(
                        currentLoadout.location,
                        currentLoadout.max == null ? amount : currentLoadout.max.intValue() + amount,
                        currentLoadout.current));
            }
        }

        /**
         * Add current armor of a hit location.
         * 
         * @param location The affected hit location.
         * @param amount   The amount of armor added.
         * @return The unit builder with current armor added.
         * @throws IllegalArgumentException The amount was invalid.
         */
        public UnitBuilder addArmor(HitLocation location, int amount) {
            if (this.armor.isEmpty())
                throw new IllegalStateException("Cannot alter armor before setting the maximum armor");
            ArmorLoadout currentArmor = getArmorLoadout(this.armor, location);
            if (amount >= 0) {
                if (amount > currentArmor.max)
                    throw new IllegalArgumentException("The armor amount exceeds the maximum");
            } else if (amount + currentArmor.max < 0) {
                throw new IllegalArgumentException("The armor reduction exceeds the armor amount");
            }
            if (this.isMutable()) {
                this.armor.add(amount >= 0 ? ArmorLoadout.ofRepair(location, amount)
                        : ArmorLoadout.ofDamage(location, -amount));
                return this;
            } else {
                return new UnitBuilder(this, "armor", amount);
            }

        }

        /**
         * Add current structure of a hit location.
         * 
         * @param location The affected hit location.
         * @param amount   The amount of structure added.
         * @return The unit builder with current strcture added.
         * @throws IllegalArgumentException The amount was invalid.
         */
        public UnitBuilder addStructure(HitLocation location, int amount) {
            StructureLoadout current = getStructureLoadout(this.structure, location);

            if (amount >= 0) {
                if (amount > current.max)
                    throw new IllegalArgumentException("The structure amount exceeds the maximum");
            } else if (amount + current.max < 0) {
                throw new IllegalArgumentException("The structure reduction exceeds the armor amount");
            }
            if (this.isMutable()) {
                this.structure.add(amount >= 0 ? StructureLoadout.ofRepair(location, (short) amount)
                        : StructureLoadout.ofDamage(location, (short) -amount));
                return this;
            } else {
                return new UnitBuilder(this, "structure", amount);
            }
        }

        /***
         * Build teh unit.
         * 
         * @return The unit created from the builder state.
         * @throws java.lang.IllegalStateException The state of the builder is invalid.
         */
        public Unit build() throws java.lang.IllegalStateException {
            try {
                return new Unit(this.type, this.name, this.model, this.tonnage, this.availableTonnage,
                        this.equipment.toArray(
                                (int size) -> (new EquipmentLoadout[size])),
                        this.armor.toArray((int size) -> (new ArmorLoadout[size])),
                        this.structure.toArray((int size) -> (new StructureLoadout[size])));
            } catch (IllegalArgumentException exception) {
                throw new IllegalStateException(exception.getMessage());
            }
        }

    }
}
