package com.computing.pervasive.myapplication;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Thomas on 08.04.2015.
 */
public class Lecture implements Serializable {

    private int id;
    private String name;
    private Date begin;
    private Date end;
    private String lecturer;
    private Block block;
    private Room room;

    public Lecture(int id,  String name, Date begin, Date end, String lecturer, Block block, Room room)
    {
        this.id = id;
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.lecturer = lecturer;
        this.block = block;
        this.room = room;
    }

    public int getLectureID()
    {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getBegin()
    {
        return begin;
    }

    public Date getEnd()
    {
        return end;
    }

    public String getLecturer()
    {
        return lecturer;
    }

    public Block getBlock()
    {
        return block;
    }

    public Room getRoom()
    {
        return room;
    }
}
