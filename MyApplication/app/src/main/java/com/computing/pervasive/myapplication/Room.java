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
    private Building building;
    private MyBeacon myBeacon;

    public Room(int id, String name, int seatcount, String setup, MyBeacon myBeacon, Building building)
    {
        this.id = id;
        this.name = name;
        this.seatcount = seatcount;
        this.setup = setup;
        this.myBeacon = myBeacon;
        this.building = building;
    }

    public Room(String name, int seatcount, String setup, MyBeacon myBeacon, Building building)
    {
        this.name = name;
        this.seatcount = seatcount;
        this.setup = setup;
        this.myBeacon = myBeacon;
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

    public MyBeacon getMyBeacon() {
        return myBeacon;
    }

    @Override
    public String toString()
    {
        return !name.isEmpty() ? name  : "Raum: " + id;
    }
}
