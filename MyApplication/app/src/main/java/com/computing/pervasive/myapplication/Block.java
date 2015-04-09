package com.computing.pervasive.myapplication;

import java.io.Serializable;
import java.sql.Time;

/**
 * Created by Thomas on 08.04.2015.
 */
public class Block implements Serializable {

    private int id;
    private String name;
    private Time start;
    private Time end;
    private Day day;

    public Block(int id, String name, Time start, Time end, Day day)
    {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.day = day;
    }

    public int getBlockID()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Time getStart()
    {
        return start;
    }

    public Time getEnd()
    {
        return end;
    }

    public Day getDay()
    {
        return day;
    }
}
