package com.computing.pervasive.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Thomas on 06.04.2015.
 */
public class RoomDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details);

        Intent intent = getIntent();

        setView(intent);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        boolean keep = intent.getExtras().getBoolean("keep");
        if(!keep)
        {
            finish();
        }
        setView(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setView(Intent intent)
    {
        if (intent.hasExtra("ROOM")) {
            Room room = (Room) intent.getSerializableExtra("ROOM");
            if (room != null) {
                TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
                lblRoomNumber.setText(room.toString());
                TextView beaconID = (TextView) findViewById(R.id.beaconID);
                beaconID.setText(room.getBeaconID());
                TextView seatcount = (TextView) findViewById(R.id.seat_count);
                seatcount.setText("" + room.getSeatCount());
                TextView setup = (TextView) findViewById(R.id.room_setup);
                setup.setText(room.getSetup());
                TextView building = (TextView) findViewById(R.id.building);
                building.setText("" + room.getBuilding().getBuildingID());
                TextView roomID = (TextView) findViewById(R.id.roomID);
                roomID.setText("" + room.getRoomID());
                TextView roomName = (TextView) findViewById(R.id.roomName);
                roomName.setText(room.getRoomName());
            }
        }
    }
}
