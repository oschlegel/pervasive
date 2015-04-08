package com.computing.pervasive.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
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
        if (intent.hasExtra("ROOM")) {
            Room room = (Room) intent.getSerializableExtra("ROOM");

            if (room != null) {
                TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
                lblRoomNumber.setText(room.toString());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
