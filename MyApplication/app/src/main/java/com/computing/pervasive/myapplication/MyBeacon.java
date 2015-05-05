package com.computing.pervasive.myapplication;

import org.altbeacon.beacon.Identifier;

import java.io.Serializable;

/**
 * Created by Thomas on 05.05.2015.
 */
public class MyBeacon implements Serializable {

    private int id;
    private String id1;
    private String id2;
    private String id3;
    private String macAdress;

    public MyBeacon(int id, String id1, String id2, String id3, String macAdress)
    {
        this.id = id;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.macAdress = macAdress;
    }

    public int getID() {
        return id;
    }

    public String getID1() {
        return id1;
    }

    public String getID2() {
        return id2;
    }

    public String getID3() {
        return id3;
    }

    public String getMacAdress() {
        return macAdress;
    }
}
