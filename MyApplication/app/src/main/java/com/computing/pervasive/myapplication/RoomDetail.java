package com.computing.pervasive.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Thomas on 06.04.2015.
 *
 */
public class RoomDetail extends ActionBarActivity {

    private static final String ONLINE_PREF = "ONLINE_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details);
        Intent intent = getIntent();

        if (intent.hasExtra("ROOM")) {
            Room room = (Room) intent.getSerializableExtra("ROOM");

            setView(room);
        }

        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        boolean online = settings.getBoolean("ONLINE", false);
        if (online) {
            if (intent.hasExtra("MYBEACON")) {
                MyBeacon mybeacon = (MyBeacon) intent.getSerializableExtra("MYBEACON");
                Download task = new Download();
                task.execute(mybeacon);
            }
        }
    }

    private void setView(Room room)
    {
        if (room != null) {
            TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
            lblRoomNumber.setText(room.toString());
            TextView beaconID = (TextView) findViewById(R.id.beaconID);
            beaconID.setText(room.getMyBeacon().getMacAddress());
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

    private class Download extends AsyncTask<MyBeacon, Void, JSONObject[]> {

        @Override
        protected JSONObject[] doInBackground(MyBeacon... params) {
            JSONObject room = null;
            JSONObject lecture = null;

            try {
                room = getRoom(params[0]);
                lecture = getLecture(params[0]);
            }
            catch (Exception e) {
                Log.d("DOWNLOAD", e.getMessage());
                e.printStackTrace();
            }
            JSONObject[] objects = new JSONObject[2];
            objects[0] = room;
            objects[1] = lecture;
            return objects;
        }

        @Override
        protected void onPostExecute(JSONObject[] objects) {
            super.onPostExecute(objects);

            if (objects != null && objects.length == 2) {

                JSONObject room = objects[0];
                JSONObject lecture = objects[1];

                if (room != null) {
                    try {
                        int seatCount = room.getInt("seatcount");
                        String setup = room.getString("setup");
                        int roomID = room.getInt("id");
                        String name = room.getString("name");
                        JSONObject building = room.getJSONObject("building");
                        JSONObject myBeacon = room.getJSONObject("mybeacon");
                        int buildingID = -1;
                        if (building != null) {
                            buildingID = building.getInt("id");
                        }
                        String macAddress = "";
                        if (myBeacon != null) {
                            macAddress = myBeacon.getString("macaddress");
                        }
                        TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
                        lblRoomNumber.setText(name);
                        TextView beaconID = (TextView) findViewById(R.id.beaconID);
                        beaconID.setText(macAddress);
                        TextView lblSeatCount = (TextView) findViewById(R.id.seat_count);
                        lblSeatCount.setText("" + seatCount);
                        TextView lblSetup = (TextView) findViewById(R.id.room_setup);
                        lblSetup.setText(setup);
                        TextView lblBuilding = (TextView) findViewById(R.id.building);
                        lblBuilding.setText("" + buildingID);
                        TextView lblRoomID = (TextView) findViewById(R.id.roomID);
                        lblRoomID.setText("" + roomID);
                        TextView lblRoomName = (TextView) findViewById(R.id.roomName);
                        lblRoomName.setText(name);

                        TextView txt = (TextView) findViewById(R.id.building);
                        txt.setText(""+buildingID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (lecture != null) {
                    try {
                        String lectureName = lecture.getString("name");
                        TextView lblLectureName = (TextView) findViewById(R.id.lecture);
                        lblLectureName.setText(lectureName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    TextView lblLectureName = (TextView) findViewById(R.id.lecture);
                    lblLectureName.setText("keine Vorlesung");
                }
            }
        }

        private JSONObject getRoom(MyBeacon mybeacon) throws Exception {
            URL url = new URL("http://hftroomer.appspot.com/rooms?macAddress="+mybeacon.getMacAddress());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/string");
            if (connection.getResponseCode() == 200) {
                InputStream stream = connection.getInputStream();
                return new JSONObject(readInput(stream));
            }
            return null;
        }

        private JSONObject getLecture(MyBeacon mybeacon) throws Exception {
            URL url = new URL("http://hftroomer.appspot.com/lectures?macAddress="+mybeacon.getMacAddress());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/string");
            if (connection.getResponseCode() == 200) {
                InputStream stream = connection.getInputStream();
                return new JSONObject(readInput(stream));
            }
            return null;
        }

        private String readInput(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String result = "", line;
            while ((line = br.readLine()) != null) {
                result += line+"\n";
            }
            if (!result.isEmpty()) {
                result = result.substring(0,result.lastIndexOf('\n'));
            }
            return result;
        }
    }
}
