package com.computing.pervasive.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String TABLE_MYBEACONS = "mybeacons";

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
    public static final String COLUMN_MYBEACON_ID = "mybeaconid";
    public static final String COLUMN_BEACON_ID1 = "beaconid1";
    public static final String COLUMN_BEACON_ID2 = "beaconid2";
    public static final String COLUMN_BEACON_ID3 = "beaconid3";
    public static final String COLUMN_MAC_ADRESS = "macadress";
    public static final String COLUMN_MYBEACON = "mybeacon";

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
    private final String CREATE_MYBEACON_TABLE = "CREATE TABLE " + TABLE_MYBEACONS + "("
            + COLUMN_MYBEACON_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COLUMN_BEACON_ID1 + " TEXT NOT NULL, "
            + COLUMN_BEACON_ID2 + " TEXT NOT NULL, "
            + COLUMN_BEACON_ID3 + " TEXT NOT NULL, "
            + COLUMN_MAC_ADRESS + " TEXT NOT NULL"
            + ");";
    private final String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
            + COLUMN_ROOM_ID + " INTEGER NOT NULL PRIMARY KEY, "
            + COLUMN_ROOM_NAME + " TEXT NOT NULL, "
            + COLUMN_ROOM_SEAT_COUNT + " INTEGER, "
            + COLUMN_ROOM_SETUP + " TEXT, "
            + COLUMN_MYBEACON + " INTEGER NOT NULL, "
            + COLUMN_BUILDING + " INTEGER NOT NULL, "
            + "FOREIGN KEY("+ COLUMN_MYBEACON +") REFERENCES "+TABLE_MYBEACONS+"("+COLUMN_MYBEACON_ID+"), "
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
        try {
            db.execSQL(ENABLE_FOREIGN_KEYS);
            db.execSQL(CREATE_BLOCK_TABLE);
            db.execSQL(CREATE_BUILDING_TABLE);
            db.execSQL(CREATE_MYBEACON_TABLE);
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

            MyBeacon b1 = new MyBeacon(1, "73676723-7400-0000-ffff-0000ffff0007", "31539", "18343", "78:A5:04:4A:19:D4");
            MyBeacon b2 = new MyBeacon(2, "73676723-7400-0000-ffff-0000ffff0005", "50325", "16373", "78:A5:04:4A:29:89");
            addMyBeaconSQL(db, b1);
            addMyBeaconSQL(db, b2);
            Room room = new Room(1, "1/101", 40, "Beamer", b1, bau1);
            addRoomSQL(db, room);
            addRoomSQL(db, new Room(2, "1/102", 30, "PC-Pool", b2, bau1));

            addLectureSQL(db, new Lecture(1, new Date(1000), new Date(2000), "Teacher", getBlockSQL(db, 1), room));

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYBEACONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKS);
        onCreate(db);
    }

    public void addMyBeacon(MyBeacon myBeacon)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MYBEACON_ID, myBeacon.getID());
        values.put(COLUMN_BEACON_ID1, myBeacon.getID1());
        values.put(COLUMN_BEACON_ID2, myBeacon.getID2());
        values.put(COLUMN_BEACON_ID3, myBeacon.getID3());
        values.put(COLUMN_MAC_ADRESS, myBeacon.getMacAdress());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MYBEACONS, null, values);
        db.close();
    }

    public void addRoom(Room room)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_ID, room.getRoomID());
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_ROOM_SEAT_COUNT, room.getSeatCount());
        values.put(COLUMN_ROOM_SETUP, room.getSetup());
        values.put(COLUMN_MYBEACON, room.getMyBeacon().getID());
        values.put(COLUMN_BUILDING, room.getBuilding().getBuildingID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ROOMS, null, values);
        db.close();
    }

    private void addLecture(Lecture lecture){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LECTURE_ID, lecture.getLectureID());
        values.put(COLUMN_LECTURE_BEGIN, dateFormat.format(lecture.getBegin()));
        values.put(COLUMN_LECTURE_END, dateFormat.format(lecture.getEnd()));
        values.put(COLUMN_LECTURER, lecture.getLecturer());
        values.put(COLUMN_BLOCK, lecture.getBlock().getBlockID());
        values.put(COLUMN_ROOM, lecture.getRoom().getRoomID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_LECTURE, null, values);
        db.close();
    }

    public Room findRoom(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOMS, null, COLUMN_ROOM_ID + " = '" + id + "'", null, null, null, null);

        Room room = null;
        if (cursor.moveToFirst()) {
            Building b = getBuilding(cursor.getInt(5));
            MyBeacon myb = getMyBeacon(cursor.getInt(4));
            room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), myb, b);
        }
        db.close();
        return room;
    }

    public Room findRoom(String macAdress) {
        Room room = null;

        MyBeacon myBeacon = getMyBeacon(macAdress);

        if (myBeacon != null) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_ROOMS, null, COLUMN_MYBEACON_ID + "= '" + myBeacon.getID() + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                Building b = getBuilding(cursor.getInt(5));
                room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), myBeacon, b);
            }
            db.close();
        }
        return room;
    }

    public Room findRoom(String beaconID1, String beaconID2, String beaconID3) {
        Room room = null;

        MyBeacon myBeacon = getMyBeacon(beaconID1, beaconID2, beaconID3);

        if (myBeacon != null) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_ROOMS, null, COLUMN_MYBEACON + " = '" + myBeacon.getID() + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                Building b = getBuilding(cursor.getInt(5));
                room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), myBeacon, b);
            }
            db.close();
        }
        return room;
    }

    public List<Room> getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROOMS, null, null, null, null, null, null);

        List<Room> rooms = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Building b = getBuilding(cursor.getInt(5));
                MyBeacon myb = getMyBeacon(4);
                Room room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), myb, b);
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
                Building b = getBuilding(cursor.getInt(5));
                MyBeacon myb = getMyBeacon(4);
                Room room = new Room(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), myb, b);
                rooms.add(room);
            } while (cursor.moveToNext());
        }
        db.close();
        return rooms;
    }

    public void addMyBeaconSQL(SQLiteDatabase db, MyBeacon myBeacon)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MYBEACON_ID, myBeacon.getID());
        values.put(COLUMN_BEACON_ID1, myBeacon.getID1());
        values.put(COLUMN_BEACON_ID2, myBeacon.getID2());
        values.put(COLUMN_BEACON_ID3, myBeacon.getID3());
        values.put(COLUMN_MAC_ADRESS, myBeacon.getMacAdress());
        db.insert(TABLE_MYBEACONS, null, values);
    }

    private void addRoomSQL(SQLiteDatabase db, Room room)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_ID, room.getRoomID());
        values.put(COLUMN_ROOM_NAME, room.getRoomName());
        values.put(COLUMN_ROOM_SEAT_COUNT, room.getSeatCount());
        values.put(COLUMN_ROOM_SETUP, room.getSetup());
        values.put(COLUMN_MYBEACON, room.getMyBeacon().getID());
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

    private void addLectureSQL(SQLiteDatabase db, Lecture lecture){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LECTURE_ID, lecture.getLectureID());
        values.put(COLUMN_LECTURE_BEGIN, dateFormat.format(lecture.getBegin()));
        values.put(COLUMN_LECTURE_END, dateFormat.format(lecture.getEnd()));
        values.put(COLUMN_LECTURER, lecture.getLecturer());
        values.put(COLUMN_BLOCK, lecture.getBlock().getBlockID());
        values.put(COLUMN_ROOM, lecture.getRoom().getRoomID());
        db.insert(TABLE_LECTURE, null, values);
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

    public MyBeacon getMyBeacon(int id)
    {
        MyBeacon myBeacon = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MYBEACONS, null, COLUMN_MYBEACON_ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst())
        {
            myBeacon = new MyBeacon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        db.close();
        return myBeacon;
    }

    public MyBeacon getMyBeacon(String macAdress)
    {
        MyBeacon myBeacon = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MYBEACONS, null, COLUMN_MAC_ADRESS + " = '" + macAdress + "'", null, null, null, null);
        if (cursor.moveToFirst())
        {
            myBeacon = new MyBeacon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        db.close();
        return myBeacon;
    }

    public MyBeacon getMyBeacon(String beaconID1, String beaconID2, String beaconID3)
    {
        MyBeacon myBeacon = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MYBEACONS, null, COLUMN_BEACON_ID1 + " = '" + beaconID1 + "' AND " + COLUMN_BEACON_ID2 + " = '" + beaconID2 + "' AND " + COLUMN_BEACON_ID3 + " = '" + beaconID3 + "'", null, null, null, null);
        if (cursor.moveToFirst())
        {
            myBeacon = new MyBeacon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        db.close();
        return myBeacon;
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

    public Block getBlockSQL(SQLiteDatabase db, int id)
    {
        Block block = null;
        Cursor cursor = db.query(TABLE_BLOCKS, null, COLUMN_BLOCK_ID + " = " + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            block = new Block(cursor.getInt(0), cursor.getString(1), parseTime(cursor.getString(2)), parseTime(cursor.getString(3)), Day.valueOf(cursor.getString(4)));
        }
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
