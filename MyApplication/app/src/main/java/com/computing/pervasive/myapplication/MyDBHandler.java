package com.computing.pervasive.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thomas on 04.04.2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String TAG ="DBHelper";
    private static final int DATABASE_Version = 1;
    private static final String DATABASE_NAME = "roomDB.db";
    private static final String TABLE_BUILDING = "buildings";
    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_BLOCKS = "blocks";
    private static final String TABLE_LECTURE = "lecture";

    public static final String COLUMN_ROOM_ID = "roomid";
    public static final String COLUMN_ROOM_NAME = "roomname";
    public static final String COLUMN_BUILDING_ID = "buildingid";
    public static final String COLUMN_BUILDING_NAME = "buildingname";
    public static final String COLUMN_ROOM_SEAT_COUNT = "seatcount";
    public static final String COLUMN_ROOM_SETUP = "roomSetup";
    public static final String COLUMN_BLOCK_DAY = "day";
    public static final String COLUMN_BUILDING = "building";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_BLOCK_ID = "blockid";
    public static final String COLUMN_BLOCK_NAME = "name";
    public static final String COLUMN_BLOCK_START = "start";
    public static final String COLUMN_BLOCK_END = "end";
    public static final String COLUMN_LECTURE_ID = "lectureid";
    public static final String COLUMN_LECTURE_BEGIN = "begin";
    public static final String COLUMN_LECTURE_END = "end";
    public static final String COLUMN_LECTURER = "lecturer";
    public static final String COLUMN_BLOCK = "block";
    public static final String COLUMN_BEACON_ID = "beaconid";

    private final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON;";

    private final String CREATE_BLOCK_TABLE = "CREATE TABLE " + TABLE_BLOCKS + "("
            + COLUMN_BLOCK_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COLUMN_BLOCK_NAME + " TEXT, "
            + COLUMN_BLOCK_START + " TEXT NOT NULL, "
            + COLUMN_BLOCK_END + " TEXT NOT NULL, "
            + COLUMN_BLOCK_DAY + " TEXT NOT NULL"
            + ");";
    private final String CREATE_BUILDING_TABLE = "CREATE TABLE " + TABLE_BUILDING + "("
            + COLUMN_BUILDING_ID + " INTEGER NOT NULL PRIMARY KEY , "
            + COLUMN_BUILDING_NAME + " TEXT"
            + ");";
    private final String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
            + COLUMN_ROOM_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COLUMN_ROOM_NAME + " TEXT NOT NULL, "
            + COLUMN_ROOM_SEAT_COUNT + " INTEGER, "
            + COLUMN_ROOM_SETUP + " TEXT, "
            + COLUMN_BEACON_ID + " TEXT, "
            + COLUMN_BUILDING + " INTEGER NOT NULL, "
            + "FOREIGN KEY("+ COLUMN_BUILDING +") REFERENCES "+TABLE_BUILDING+"("+COLUMN_BUILDING_ID+")"
            + ");";
    private final String CREATE_LECTURE_TABLE = "CREATE TABLE " + TABLE_LECTURE + "("
            + COLUMN_LECTURE_ID + " INT NOT NULL PRIMARY KEY, "
            + COLUMN_LECTURE_BEGIN + " TEXT NOT NULL, "
            + COLUMN_LECTURE_END + " TEXT NOT NULL, "
            + COLUMN_LECTURER + " TEXT, "
            + COLUMN_BLOCK + " INTEGER, "
            + COLUMN_ROOM + " INTEGER, "
            + "FOREIGN KEY("+ COLUMN_BLOCK +") REFERENCES "+TABLE_BLOCKS+"("+COLUMN_BLOCK_ID+"), "
            + "FOREIGN KEY("+ COLUMN_ROOM +") REFERENCES "+TABLE_ROOMS+"("+COLUMN_ROOM_ID+")"
            + ");";

    public MyDBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Oncreate " + CREATE_ROOM_TABLE);
        try {
            db.execSQL(ENABLE_FOREIGN_KEYS);
            db.execSQL(CREATE_BLOCK_TABLE);
            db.execSQL(CREATE_BUILDING_TABLE);
            db.execSQL(CREATE_ROOM_TABLE);
            db.execSQL(CREATE_LECTURE_TABLE);

            int i = 1;
            for (Day d : Day.values()) {
                addBlockSQL(db, new Block(i, null, new Time(28800000l), new Time(34200000l), d));
                addBlockSQL(db, new Block(i+1, null, new Time(35100000l), new Time(40500000l), d));
                addBlockSQL(db, new Block(i+2, null, new Time(41400000l), new Time(46800000l), d));
                addBlockSQL(db, new Block(i+3, null, new Time(50400000l), new Time(55800000l), d));
                addBlockSQL(db, new Block(i+4, null, new Time(56700000l), new Time(62100000l), d));
                addBlockSQL(db, new Block(i+5, null, new Time(63000000l), new Time(68400000l), d));
                i+=6;
            }

            Building bau1 = new Building(1, "Bau 1");

            addBuildingSQL(db, bau1);

            addRoomSQL(db, new Room(1, "1/101", 40, "Beamer", null, bau1));
            addRoomSQL(db, new Room(2, "1/102", 30, "PC-Pool", null, bau1));


        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKS);
        onCreate(db);
    }

    public void addRoom(Room room)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_ID, room.getRoomID());
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_ROOM_SEAT_COUNT, room.getSeatCount());
        values.put(COLUMN_ROOM_SETUP, room.getSetup());
        values.put(COLUMN_BUILDING_ID, room.getBuilding().getBuildingID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ROOMS, null, values);
        db.close();
    }

    public Room findRoom(int id) {
        //String query = "SELECT * FROM " + TABLE_ROOMS + " WHERE " + COLUMN_ID + " = \"" + roomNumber + "\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOMS, null, COLUMN_ROOM_ID + " = '" + id + "'", null, null, null, null);

        Room room = null;
        if (cursor.moveToFirst()) {
            Building b = getBuilding(cursor.getInt(4));
            room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(5), b);
        }
        db.close();
        return room;
    }

    public List<Room> getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOMS, null, null, null, null, null, null);

        List<Room> rooms = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Building b = getBuilding(cursor.getInt(4));
                Room room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(5), b);
                rooms.add(room);
            } while (cursor.moveToNext());
        }
        db.close();
        return rooms;
    }

    public List<Room> getAllRoomsSorted() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOMS, null, null, null, null, null, COLUMN_ROOM_ID + " ASC");

        List<Room> rooms = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Building b = getBuilding(cursor.getInt(4));
                Room room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(5), b);
                rooms.add(room);
            } while (cursor.moveToNext());
        }
        db.close();
        return rooms;
    }

    private void addRoomSQL(SQLiteDatabase db, Room room)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_ID, room.getRoomID());
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_ROOM_SEAT_COUNT, room.getSeatCount());
        values.put(COLUMN_ROOM_SETUP, room.getSetup());
        values.put(COLUMN_BUILDING, room.getBuilding().getBuildingID());
        db.insert(TABLE_ROOMS, null, values);
    }

    private void addBlockSQL(SQLiteDatabase db, Block block)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BLOCK_ID, block.getBlockID());
        values.put(COLUMN_BLOCK_NAME, block.getName());
        values.put(COLUMN_BLOCK_START, timeFormat.format(block.getStart()));
        values.put(COLUMN_BLOCK_END, timeFormat.format(block.getEnd()));
        values.put(COLUMN_BLOCK_DAY, block.getDay().toString());
        db.insert(TABLE_BLOCKS, null, values);
    }

    private void addBuildingSQL(SQLiteDatabase db, Building building)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUILDING_ID, building.getBuildingID());
        values.put(COLUMN_BUILDING_NAME, building.getName());
        db.insert(TABLE_BUILDING, null, values);
    }

    public Building getBuilding(int id)
    {
        Building building = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BUILDING, null, COLUMN_BUILDING_ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            building = new Building(cursor.getInt(0), cursor.getString(1));
        }
        db.close();
        return building;
    }

    public Block getBlock(int id)
    {
        Block block = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BLOCKS, null, COLUMN_BLOCK_ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            block = new Block(cursor.getInt(0), cursor.getString(1), parseTime(cursor.getString(2)), parseTime(cursor.getString(3)), Day.valueOf(cursor.getString(4)));
        }
        db.close();
        return block;
    }

    private Time parseTime(String str)
    {
        try {
            Date d = timeFormat.parse(str);
            return new Time(d.getTime());
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }
        return null;
    }

    private Date parseDate(String str)
    {
        try {
            Date d = dateFormat.parse(str);
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }
        return null;
    }
}
