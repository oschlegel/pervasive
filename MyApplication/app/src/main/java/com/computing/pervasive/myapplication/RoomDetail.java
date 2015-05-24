package com.computing.pervasive.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    private final MyDBHandler handlerDB = new MyDBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details);
        Intent intent = getIntent();

        if (intent.hasExtra("ROOM")) {
            Room room = (Room) intent.getSerializableExtra("ROOM");
            Lecture lecture = handlerDB.getLecture(room);

            setView(room, lecture);
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

    private void setView(Room room, Lecture lecture)
    {
        if (room != null) {
            TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
            lblRoomNumber.setText(room.toString());
            TextView seatCount = (TextView) findViewById(R.id.seat_count);
            seatCount.setText("" + room.getSeatCount());
            TextView setup = (TextView) findViewById(R.id.room_setup);
            setup.setText(room.getSetup());

            TextView lblLectureName = (TextView) findViewById(R.id.lecture);

            if (lecture != null) {
                String lectureName = lecture.getName();
                lblLectureName.setText(lectureName);
            }
            else {
                lblLectureName.setText("keine Vorlesung");
            }
        }
    }

    private class Download extends AsyncTask<MyBeacon, Void, JSONObject[]> {

        private ProgressDialog dialog = new ProgressDialog(RoomDetail.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage(RoomDetail.this.getString(R.string.waiting_Dialog));
            this.dialog.show();
        }

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
                        boolean found = room.getBoolean("found");
                        TextView lblRoomNumber = (TextView) findViewById(R.id.lblRoomNumber);
                        TextView lblSeatCount = (TextView) findViewById(R.id.seat_count);
                        TextView lblSetup = (TextView) findViewById(R.id.room_setup);
                        if (found) {
                            String name = room.getString("name");
                            int seatCount = room.getInt("seatcount");
                            String setup = room.getString("setup");
                            lblRoomNumber.setText(name);
                            lblSeatCount.setText("" + seatCount);
                            lblSetup.setText(setup);
                        }
                        else {
                            Toast toast = Toast.makeText(RoomDetail.this, R.string.no_room_not_found_for_beacon, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (lecture != null) {
                    try {
                        boolean found = lecture.getBoolean("found");
                        TextView lblLectureName = (TextView) findViewById(R.id.lecture);
                        if (found) {
                            String lectureName = lecture.getString("name");
                            lblLectureName.setText(lectureName);
                        }
                        else {
                            lblLectureName.setText("keine Vorlesung");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (room == null || lecture == null) {
                    Toast toast = Toast.makeText(RoomDetail.this, R.string.no_internet, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private JSONObject getRoom(MyBeacon mybeacon) throws Exception {
            URL url = new URL("http://hftroomer.appspot.com/rooms?macAddress="+mybeacon.getMacAddress());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/string");
            if (connection.getResponseCode() == 200) {
                InputStream stream = connection.getInputStream();
                return new JSONObject(readInput(stream)).put("found", true);
            }
            if (connection.getResponseCode() == 404) {
                return new JSONObject().put("found", false);
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
                return new JSONObject(readInput(stream)).put("found", true);
            }
            if (connection.getResponseCode() == 404) {
                return new JSONObject().put("found", false);
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
