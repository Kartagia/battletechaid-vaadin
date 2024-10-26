package com.kautiainen.antti.btechgame.systemlog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * The system information of the game.
 */
public class SystemInfo {
    
    /**
     * The system name.
     */
    public final String name;

    /**
     * The faction owning hte system.
     * - An undefined owner indicates an independent system without affiliation.
     */
    public final String faction;

    public final Shop[] shops; 

    public final Mission[] missions; 

    public SystemInfo(String system, String faction, Shop[] publicShop, Mission[] missionList) {
        this.name = system;
        this.faction = faction;
        this.shops = publicShop == null ? new Shop[0] : Arrays.asList(publicShop).stream().toArray((int size) -> (new Shop[size]));
        ArrayList<Mission> actualMissions =  new ArrayList<>( missionList == null ? 1 : missionList.length); 
        if (missionList != null && missionList.length > 0) {
            actualMissions.addAll(Arrays.asList(missionList));
        }
        this.missions = actualMissions.toArray( (int size) -> (new Mission[size]));
    }

    public SystemInfo(String system, String faction, Shop publicShop, Mission[] missionList) {
        this(system, faction, new Shop[] {}, missionList);
    }

    public SystemInfo(String system, String faction) {
        this(system, faction, new Shop[0], new Mission[0]);
    }

    public SystemInfo removeMission(Mission mission) {
        if (mission != null && ( (mission.system == null || Objects.equals(mission.system, this.name) ) || (mission.travelTime > 0))) {
            return new SystemInfo(this.name, this.faction, this.shops, Arrays.stream(this.missions).filter( (Mission cursor) -> (
                cursor != mission 
            )).toArray( (int size) -> (new Mission[size])));
        } else {
            throw new IllegalArgumentException("Invalid mission");
        }
    }

    public SystemInfo addMission(Mission mission) {
        if (mission != null && ( (mission.system == null || Objects.equals(mission.system, this.name) ) || (mission.travelTime > 0))) {
            java.util.List<Mission> missionList = new ArrayList<>(Arrays.asList(this.missions));
            missionList.add(mission);
            return new SystemInfo(this.name, this.faction, this.shops, 
            missionList.toArray( (int size) -> (new Mission[size])));
        } else {
            throw new IllegalArgumentException("Invalid mission");
        }
    }

}
