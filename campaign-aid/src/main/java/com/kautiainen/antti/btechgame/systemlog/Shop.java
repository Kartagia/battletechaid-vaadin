/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.kautiainen.antti.btechgame.systemlog;

import java.util.ArrayList;
import java.util.Collections;

import com.kautiainen.antti.btechgame.game.Equipment;
import com.kautiainen.antti.btechgame.game.GameRules.UnitType;
import com.kautiainen.antti.btechgame.game.LoadoutController.Loadout;
import com.kautiainen.antti.btechgame.game.RulesModifier;
import com.kautiainen.antti.btechgame.game.Unit;

/**
 * A shop represents the shop of the system.
 * 
 * @author antti@kautiainen.com
 */
public class Shop {

    protected ArrayList<Unit> units = new ArrayList<>();

    protected ArrayList<UnitPart> parts = new ArrayList<>();

    protected ArrayList<ShopEquipment> equipment = new ArrayList<>();

    /**
     * Equipment entry for shop equipment.
     */
    public static record ShopEquipment(Equipment item, int count) {

    }

    /**
     * A record representing a unit part.
     */
    public static record UnitPart(UnitType type, String model, Loadout defaultLoadout) {
    }

    /**
     * Create a shop with tags.
     * A shop wihtout tags is a public shop.
     */
    public Shop(String... tags) {

    }

    public java.util.List<ShopEquipment> getEquipment() {
        return java.util.Collections.unmodifiableList(this.equipment);
    }

    public java.util.List<Unit> getUnits() {
        return java.util.Collections.unmodifiableList(this.units);
    }

    public java.util.List<UnitPart> getParts() {
        return java.util.Collections.unmodifiableList(this.parts);
    }

    /**
     * Add a new fitted unit.
     * 
     * @return True, if and only if the shop was altered.
     */
    public synchronized boolean addUnit(Unit unit) {
        if (unit != null) {
            return this.units.add(unit);
        } else {
            throw new NullPointerException("Adding an undefined unit not supported");
        }
    }

    /**
     * Remove a fitted unit.
     * 
     * @param unit The removed unit.
     * @return True, if and only if the shop was altered.
     */
    public synchronized boolean removeUnit(Unit unit) {
        if (unit == null) {
            return false;
        } else {
            return this.units.remove(unit);
        }
    }

    public static final java.util.Comparator<ShopEquipment> equipmentCompare = (ShopEquipment compared, ShopEquipment comparee) -> {
        int result = compared.item.name.compareTo(comparee.item.name);
        if (result == 0) {
            result = Integer.compare(compared.item.modifiers.length, comparee.item.modifiers.length);
            if (result == 0) {
                final int end = compared.item.modifiers.length;
                RulesModifier comparedMod, compareeMod;
                for (int i = 0; result == 0 && i < end; i++) {
                    comparedMod = compared.item.modifiers[i];
                    compareeMod = comparee.item.modifiers[i];
                    result = comparedMod.rule().compareTo(compareeMod.rule());
                    if (result == 0) {
                        result = Double.compare(comparedMod.modifier(), compareeMod.modifier());
                    }
                }
            }
        }
        return result;
    };

    /**
     * Add a single copy of an equipment to the ship.
     * @param equipment The added equipment.
     * @returns True, if and only if the collection was modified.
     */
    public synchronized boolean addEquipment(Equipment equipment) {
        return addEquipment(equipment, 1);
    }


    /**
     * Add a given number of copies of an equipment to the shop.
     * @param equipment The added equipment.
     * @param count The number of items added.
     * @returns True, if and only if the collection was modified.
     * @throws IllegalArgumentException The count was invalid.
     */
    public synchronized boolean addEquipment(Equipment equipment, int count) {
        if (count < 0) throw new IllegalArgumentException("Cannot add negative number of items");
        if (equipment != null && count > 0) {
            ShopEquipment added = new ShopEquipment(equipment, count);
            int index = Collections.binarySearch(this.equipment, added, equipmentCompare);
            if (index >= 0) {
                ShopEquipment replaced = this.equipment.get(index);
                this.equipment.set(index, new ShopEquipment(replaced.item, replaced.count + added.count ));
                return true;
            } else {
                // Adding a new loadout.
                this.equipment.add(-1 - index, added);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a single item from shipo.
     * @param item The removed item.
     * @return True, if and only if the item was removed.
     */
    public synchronized boolean removeEquipment(Equipment item) {
        return removeEquipment(item, 1);
    }

    /**
     * Remove a certain amount of items from equipment.
     * @param item The removed item.
     * @param count The number of items removed.
     * @return True, if and only if the collection was altered.
     */
    public synchronized boolean removeEquipment(Equipment item, int count) {
        if (count < 0) throw new IllegalArgumentException("Cannot remove negative number of items");
        if (count == 0) {
            // Removing zero always fails as the collection is not altered.
            return true;
        }
        ShopEquipment removed = new ShopEquipment(item, count);
        int index = Collections.binarySearch(this.equipment, removed, equipmentCompare);
        if (index >= 0) {
            ShopEquipment altered = this.equipment.get(index);
            if (altered.count < count) {
                throw new IllegalArgumentException("Cannot remove more items we have");
            } else {
                this.equipment.set(index, new ShopEquipment(altered.item, altered.count - count));
                return true;
            }
        }
        // The 
        return false;
    }
}
