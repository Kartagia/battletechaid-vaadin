package com.kautiainen.antti.btechgame.game;

import java.util.Objects;



/**
 * The controller of loadouts.
 */
public class LoadoutController {

    /**
     * An entry.
     * @param <K> The key type of the entry.
     * @param <V> The value type of the entry.
     */
    public static interface Entry<K, V> {

        /**
         * The key of the entry.
         * @return The key of the entry.
         */
        public K getKey();

        /**
         * The value of the entry.
         * @return The value of the entry.
         */
        public V getValue();
    }

    /**
     * An entry of an equipment located in a location.
     */
    public static class EquipmentLoadout implements Entry<HitLocation, Equipment> {

        public final HitLocation location;

        public final Equipment equipment;

        public EquipmentLoadout(HitLocation location, Equipment equipment) {
            this.location = location;
            this.equipment = equipment;
        }

        @Override
        public final HitLocation getKey() {
            return this.location;
        }

        @Override
        public final Equipment getValue() {
            return this.equipment;
        }
    }

    /**
     * The entry representing the loadout of a single location.
     */
    public static class LoadoutEntry implements Entry<HitLocation, Equipment[]> {

        protected final HitLocation key; 

        protected final Equipment[] value; 

        @Override
        public HitLocation getKey() {
            return this.key;
        }

        @Override
        public Equipment[] getValue() {
            return this.value;
        }

        @SuppressWarnings("CollectionsToArray")
        public LoadoutEntry(HitLocation location, java.util.Collection<Equipment> equipment) {
            this.key = location;
            this.value = equipment.toArray(new Equipment[equipment.size()]);
        }

        /**
         * Create a new location loadout from list of eqiupment.
         * @param location The location of the result.
         * @param equipment The list of equipment.
         */
        public LoadoutEntry(HitLocation location, EquipmentLoadout[] equipment) {
            java.util.List<Equipment> content = new java.util.ArrayList<>(equipment.length);
            for (EquipmentLoadout item : equipment) {
                if (item != null && Objects.equals(location, item.getKey())) {
                    content.add(item.getValue());
                }
            }
            this.key = location;
            this.value = content.stream().toArray( (int size) -> (new Equipment[size]));
        }

        /**
         * Convert the loadout entry.
         * @return The hit location loadout as laodout of the individual equipment.
         */
        public EquipmentLoadout[] toEquipmentArray() {
            EquipmentLoadout[] result = new EquipmentLoadout[this.value.length];
            int i = 0;
            for (Equipment equipment: value) {
                result[i++] = new EquipmentLoadout(this.key, equipment);
            }
            return result;
        }
    }

    public class Loadout {

        /**
         * The content of the loadout.
         */
        private final java.util.Map<HitLocation, java.util.List<Equipment>> content = new java.util.HashMap<>();

        /**
         * The tonnage of the loadout.
         */
        private double totalTonnage = 0.0;

        /**
         * Is the loadout strict. A strict loadout throws exception, if it exceeds the tonnage.
         */
        private boolean strict = false;

        /**
         * Total tonnage of the laodout
         */
        public final double getTonnage() {
            return this.totalTonnage;
        }

        /**
         * Create a new loadout from a hit location laodout as a map.
         * @param loadout The hit location loadout as a map.
         * @param strict Is the loadout strict preventing exceeding the available tonnage.
         */
        public Loadout(java.util.Map<HitLocation, Equipment[]> loadout, boolean strict) {
            this.strict = strict;
            for (java.util.Map.Entry<HitLocation, Equipment[]> entry : loadout.entrySet()) {
                final HitLocation location = entry.getKey();
                for (Equipment equipment: entry.getValue()) {
                    addEquipment(location, equipment);
                }
            }
        }

        /**
         * Create a new laodout from entries.
         * @param entries The content entries.
         * @param strict Is the loadout strict preventing exceeding the available tonnage.
         */
        public Loadout(LoadoutController.Entry<HitLocation, Equipment>[] entries, boolean strict) {
            this.strict = strict;
            for (Entry<HitLocation, Equipment> entry: entries) {
                addEquipment(entry.getKey(), entry.getValue());
            }
        }

        /**
         * Create a lenient loadout containing given equipment loadout.
         * @param entries The equipment laodouts of the loadout.
         */
        public Loadout(LoadoutController.Entry<HitLocation, Equipment>[] entries) {
            this(entries, false);
        }

        /**
         * Add an equipment to the loadout.
         * @param location The location of the equipment.
         * @param equipment The added equipment.
         * @throws IllegalArgumentException The equipment was invalid.
         */
        public synchronized final void addEquipment(HitLocation location, Equipment equipment) throws IllegalArgumentException {
            if (equipment == null) throw new IllegalArgumentException("Undefined equipment not accepted");
            if (strict && (totalTonnage + equipment.mass > LoadoutController.this.availableTonnage) ) {
                throw new IllegalArgumentException("Equipment too heavy");
            }

            if (!this.content.containsKey(location)) {
                this.content.put(location, new java.util.ArrayList<>());
            }
            this.content.get(location).add(equipment);
            this.totalTonnage += equipment.mass;
        }

        /**
         * Remove equipment from the loadout.
         * @param location The location of the removed loadout.
         * @param equipment The removed equipment.
         * @returns True, if and only if the loadout was modified.
         */
        public synchronized final boolean removeEquipment(HitLocation location, Equipment equipment) {
            if (equipment != null && this.content.containsKey(location)) {
                int index = this.content.get(location).indexOf(equipment);
                if (index >= 0) {
                    Equipment removed = this.content.get(location).get(index);
                    this.content.get(location).remove(index);
                    this.totalTonnage -= removed.mass;
                }
            }
            return false;
        }

        /**
         * Convern the loadout into an array of equipment loadouts.
         */
        public LoadoutController.Entry<HitLocation, Equipment>[] toArray() {
            return this.content.entrySet().stream().map( 
                (entry) -> (new LoadoutEntry(entry.getKey(), entry.getValue()))
            ).flatMap( (LoadoutEntry entry) -> {
                return java.util.Arrays.asList(entry.getValue()).stream().map( equipment -> (new EquipmentLoadout(entry.getKey(), equipment)));
            }).toArray( (int size) -> (new EquipmentLoadout[size]));
        }
    }

    private double maxTonnage; 

    private double availableTonnage;

    /**
     * The base layout of the controller. 
     * The base layout cannot be modified. 
     */
    private java.util.Map<HitLocation, java.util.ArrayList<Equipment>> baseLoadout; 

    /**
     * Create a new loadout controller.
     * @param maxTonnage The maximum tonnage of the loadout.
     * @param availableTonnage The available tonnage of the loadout.
     * @param baseLoadout The base loadout, which cannot be modified. 
     * @throws IllegalArgumentException Either maximum or available tonnage is invalid.
     */
    public LoadoutController(double maxTonnage, double availableTonnage, java.util.Map<HitLocation, java.util.Collection<Equipment>> baseLoadout) {
        this.maxTonnage = maxTonnage;
        this.availableTonnage = availableTonnage;
        this.baseLoadout = new java.util.HashMap<>();
        if (baseLoadout != null) {
            for (java.util.Map.Entry<HitLocation, java.util.Collection<Equipment>> entry: baseLoadout.entrySet()) {
                if (!baseLoadout.containsKey(entry.getKey())) {
                    baseLoadout.put(entry.getKey(), new java.util.ArrayList<>());
                }
                baseLoadout.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    /**
     * Create a new loadout controller with a mass tonngae, and a base loadout. The available tonnage is counted
     * from the max tonnage and the base loadout.
     * @param maxTonnage The maximum tonnage of the loadout.
     * @param baseLoadout The base loadout of the controller.
     */
    public LoadoutController(double maxTonnage, java.util.Map<HitLocation, java.util.Collection<Equipment>> baseLoadout) {
        this.maxTonnage = maxTonnage;
        this.availableTonnage = this.maxTonnage;
        this.baseLoadout = new java.util.HashMap<>();
        
        if (baseLoadout != null) {
            for (java.util.Map.Entry<HitLocation, java.util.Collection<Equipment>> entry: baseLoadout.entrySet()) {
                if (!baseLoadout.containsKey(entry.getKey())) {
                    baseLoadout.put(entry.getKey(), new java.util.ArrayList<>());
                }
                java.util.ArrayList<Equipment> items = this.baseLoadout.get(entry.getKey());
                for (Equipment item: entry.getValue()) {
                    this.availableTonnage -= item.mass;
                    items.add(item);
                }
            }
        }
    }

    /**
     * Create a new loadout controller.
     * @param maxTonnage The maximum tonnage of the loadout.
     * @param availableTonnage The available tonnage of the loadout.
     * @throws IllegalArgumentException Either maximum or available tonnage is invalid.
     */
    public LoadoutController(double maxTonnage, double availableTonnage) {
        this(maxTonnage, availableTonnage, java.util.Collections.emptyMap());
    }

    /**
     * Create a loadout controller for a unit. 
     * @param unit The unit, whose loadout is controlled.
     */
    public LoadoutController(Unit unit) {
        this(unit.tonnage, unit.availableTonnage);
    }
}
