package com.computing.pervasive.myapplication;

import java.io.Serializable;

/**
 * Created by Thomas on 04.04.2015.
 */
public class Room implements Serializable {

    private int id;
    private String name;
    private int seatcount;
    private String setup;
    private String beaconID;
    private Building building;

    public Room(int id, String name, int seatcount, String setup, String beaconID, Building building)
    {
        this.id = id;
        this.name = name;
        this.seatcount = seatcount;
        this.setup = setup;
        this.beaconID = beaconID;
        this.building = building;
    }

    public Room(String name, int seatcount, String setup, String beaconID, Building building)
    {
        this.name = name;
        this.seatcount = seatcount;
        this.setup = setup;
        this.beaconID = beaconID;
        this.building = building;
    }

    public int getRoomID() {
        return id;
    }

    public String getRoomName() {
        return name;
    }

    public int getSeatCount()
    {
        return seatcount;
    }

    public String getSetup()
    {
        return setup;
    }

    public Building getBuilding()
    {
        return building;
    }

    public String getBeaconID()
    {
        return beaconID;
    }

    @Override
    public String toString()
    {
        return !name.isEmpty() ? name : "Raum: " + id;
    }
}
