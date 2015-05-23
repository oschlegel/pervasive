package com.computing.pervasive.myapplication;

import org.altbeacon.beacon.Identifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Thomas on 05.05.2015.
 */
public class MyBeacon implements Serializable {

    private int id;
    private String id1;
    private String id2;
    private String id3;
    private String macAddress;

    public MyBeacon(int id, String id1, String id2, String id3, String macAddress)
    {
        this.id = id;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.macAddress = macAddress;
    }

    public MyBeacon(JSONObject object) throws JSONException {
        id = object.getInt("id");
        id1 = object.getString("id1");
        id2 = object.getString("id2");
        id3 = object.getString("id3");
        macAddress = object.getString("macaddress");
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

    public String getMacAddress() {
        return macAddress;
    }
}
