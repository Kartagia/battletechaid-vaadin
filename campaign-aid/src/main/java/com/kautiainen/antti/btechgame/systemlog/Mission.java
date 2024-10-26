package com.kautiainen.antti.btechgame.systemlog;

import com.kautiainen.antti.btechgame.game.RulesModifier;

/**
 * A mission represents a single mission of the game.
 */
public class Mission {

    /**
     * The terrain of the mission.
     */
    public static class Terrain {

        /**
         * Create a new terrain.
         * @param name the name of the terrain.
         * @param modifiers The rules modifiers of the terrain.
         */
        public Terrain(String name, RulesModifier... modifiers) {

        }
    }

    /**
     * The value representing a reputation change.
     */
    public static record ReputationChange(short positive, short negative) {

    }

    /**
     * Create an uneven reputation change.
     * @param gain The positive reputation gain from mission to the employer.
     * @param loss The negative reputaiton loss from ission to the opfor. 
     * @return The reputation change.
     * @throws IllegalArgumentException The given change was not valid.
     */
    public static ReputationChange repChange(short gain, short loss) throws IllegalArgumentException {
        if (gain < 0) throw new IllegalArgumentException("Negative reputation change for employer is not allowed");
        if (loss > 0) throw new IllegalArgumentException("Positive reputation change for opponent is not allowed");
        return new ReputationChange(gain, loss);
    }

    /**
     * Create mission reputation change.
     * @param amount The reputation change amount.
     * @return The reputaiton change.
     * @throws IllegalArgumentException The reputation change amount was invalid.
     */
    public static ReputationChange evenChange(short amount) throws IllegalArgumentException {
        if (amount < 0) throw new IllegalArgumentException("Negative reputation change for employer is not allowed");
        return new ReputationChange(amount, (short)-amount);
    }

    /**
     * The name of the mission.
     */
    public final String name;

    /**
     * The difficulty of the misison. 
     * This value is from 1 to 10 with step of half skull.
     */
    public final short difficulty; 

    /**
     * The opposing faction name. An undefined value indicates the mission
     * is against local government.
     */
    public final String opfor;

    /**
     * The employeing faction name. An undefined value indicates the mission
     * is against local governmetn.
     */
    public final String employer; 

    /**
     * The mission type.
     */
    public final String missionType;

    /**
     * The terrain of the mission.
     */
    public final Terrain terrain;

    /**
     * The salary array. 
     */
    public final int[] salary; 

    /**
     * The reputation array.
     */
    public final ReputationChange[] reputation;

    /**
     * The system of the mission. If the system is undefined, it is the local system.
     */
    public final String system; 

    /**
     * The travel time in the mission.
     */
    public final int travelTime;

    /**
     * Create mission.
     * @param name The name of the misison.
     * @param type The type of the mission.
     * @param employer The employer of the mission. Undefined means local government.
     * @param opfor The opponent of the mission. Undefined means local government.
     * @param difficulty The difficulty of the mission.
     * @param biome The biome of the mission.
     * @param system The system of the mission.
     * @param travelTime The travel time included in the mission.
     */
    public Mission(String name, String type, String employer, String opfor, int difficulty, Terrain biome, String system, int travelTime) {
        this(name, type, employer, opfor, difficulty, biome, new int[0], new ReputationChange[0], system, travelTime);
    }
    /**
     * Create mission.
     * @param name The name of the misison.
     * @param type The type of the mission.
     * @param employer The employer of the mission. Undefined means local government.
     * @param opfor The opponent of the mission. Undefined means local government.
     * @param difficulty The difficulty of the mission.
     * @param biome The biome of the mission.
     * @param salary The salary options of the mission.
     * @param repChange The reputation change of the mission. 
     * @param system The system of the mission.
     * @param travelTime The travel time included in the mission.
     */
    public Mission(String name, String type, String employer, String opfor, int difficulty, Terrain biome, 
        int[] salary, 
        ReputationChange[] repChange, String system, int travelTime) {
        this.name = name;
        this.missionType = type;
        this.difficulty = (short)difficulty;
        this.terrain = biome;
        this.system = system;
        this.travelTime = travelTime;
        this.opfor = opfor;
        this.employer = employer;
        this.salary = salary;
        this.reputation = repChange;
    }

    public Mission(String name, String type, int difficulty, Terrain biome, String system, int travelTime) {
        this(name, type, null, null, difficulty, biome, system, travelTime);
    }

    public Mission(String name, String type, int difficulty, Terrain biome) {
        this(name, type, difficulty,biome,  null, 0);
    }
}
