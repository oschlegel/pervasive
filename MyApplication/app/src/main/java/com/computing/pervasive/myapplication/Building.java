package com.computing.pervasive.myapplication;

import java.io.Serializable;

/**
 * Created by Thomas on 08.04.2015.
 */
public class Building implements Serializable {

    private int id;
    private String name;

    public Building(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getBuildingID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
