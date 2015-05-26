package com.computing.pervasive.myapplication;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity implements BeaconConsumer {

    private static final String ONLINE_PREF = "ONLINE_PREFERENCE";

    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private final MyDBHandler handlerDB = new MyDBHandler(this);
    private Region REGION = new Region("MyUnifiedID", null, null, null);
    MainActivity instance;
    private Map<String, Room> beaconRoomMap = new HashMap<>();
    private Map<String, Room> beaconRoomMapOnline = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Check if Bluetooth is enabled and ask for it if not
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "No Bluetooth detected!");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_bluetooth_detected)
                    .setMessage(R.string.no_bluetooth_detected_message)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.btn_exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();

        } else {
            if (isOnlineMode() && !isOnline()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_internet)
                        .setMessage(R.string.no_internet_message)
                        .setPositiveButton(R.string.btn_close, null)
                        .show();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.bluetooth_disabled)
                        .setMessage(R.string.bluetooth_disabled_message)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .setPositiveButton(R.string.btn_enabe, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BluetoothAdapter.getDefaultAdapter().enable();
                            }
                        })
                        .setNegativeButton(R.string.btn_exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }

            // Create BeaconManager
            if (beaconManager == null) {
                beaconManager = BeaconManager.getInstanceForApplication(this);
                if (beaconManager != null) {
                    BeaconParser bp = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                    if (beaconManager.getBeaconParsers().size() < 2) {
                        beaconManager.getBeaconParsers().add(bp);
                    }
                    setBeaconService();
                    beaconManager.bind(this);
                }
                else {
                    throw new UnsupportedOperationException("No instance for beaconManager.");
                }
            }

            setContentView(R.layout.activity_main);

            ListView mainListView = (ListView) findViewById(R.id.mainListView);

            ArrayAdapter<Room> mainListAdapter = new ArrayAdapter<>(this, R.layout.simple_row, R.id.rowTextView, new ArrayList<Room>());
            mainListView.setAdapter(mainListAdapter);
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Room room = (Room) parent.getItemAtPosition(position);
                    lookupRoom(room.getRoomID());
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconManager != null)
        {
            try {
                beaconManager.stopRangingBeaconsInRegion(REGION);
            }
            catch (RemoteException e)
            {
                Log.i(TAG, "Error");
            }
            beaconManager.unbind(this);
            beaconManager = null;
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(REGION);
            Log.i(TAG, "Start Monitoring");
        }
        catch (RemoteException e) {
            Log.i(TAG, "Error");
        }
    }

    private void setBeaconService() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                BeaconLooker task = new BeaconLooker();
                task.execute(beacons);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void lookupRoom(int roomID)
    {
        Room room = handlerDB.findRoom(roomID);

        if (room != null)
        {
            Intent intent = new Intent(this, RoomDetail.class);
            intent.putExtra("ROOM", room);
            intent.putExtra("MYBEACON", room.getMyBeacon());
            startActivity(intent);
        }
        else {
            Log.d(TAG, "No match found!");

            Context context = getApplicationContext();
            CharSequence text = "No match found!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isOnlineMode() {
        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        return settings.getBoolean("ONLINE", false);
    }

    private class BeaconLooker extends AsyncTask<Collection<Beacon>, Void, Collection<Beacon>> {

        private final ListView mainListView = (ListView) findViewById(R.id.mainListView);

        @Override
        protected Collection<Beacon> doInBackground(Collection<Beacon>... beacons) {

            Collections.sort((List) beacons[0], new Comparator<Beacon>() {
                @Override
                public int compare(Beacon lhs, Beacon rhs) {
                    double dis1 = lhs.getDistance();
                    double dis2 = rhs.getDistance();
                    return dis1 <= dis2 ? -1 : 1;
                }
            });


            for (Beacon beacon : beacons[0]) {
                if (beacon != null) {
                    if (!beaconRoomMap.containsKey(beacon.getBluetoothAddress())) {
                        Room room = handlerDB.findRoom(beacon.getBluetoothAddress());
                        beaconRoomMap.put(beacon.getBluetoothAddress(), room);
                    }
                }
            }

            if (isOnlineMode()) {
                for (Beacon beacon : beacons[0]) {
                    if (beacon != null) {
                        if (!beaconRoomMapOnline.containsKey(beacon.getBluetoothAddress()))
                        {
                            GetRoomRemote task = new GetRoomRemote();
                            task.execute(beacon.getBluetoothAddress());
                        }
                    }
                }
            }

            return beacons[0];
        }

        @Override
        protected void onPostExecute(Collection<Beacon> beacons) {
            super.onPostExecute(beacons);
            ArrayAdapter<Room> mainListAdapter = (ArrayAdapter<Room>) mainListView.getAdapter();
            mainListAdapter.clear();
            for (Beacon b : beacons) {
                if (isOnlineMode()) {
                    Room room = beaconRoomMapOnline.get(b.getBluetoothAddress());
                    if (room != null) {
                        mainListAdapter.add(room);
                    }
                    else {
                        addRoomToListFromLocal(mainListAdapter, b);
                    }
                }
                else {
                    addRoomToListFromLocal(mainListAdapter, b);
                }
            }
        }

        private void addRoomToListFromLocal(ArrayAdapter<Room> mainListAdapter, Beacon b) {
            Room room = beaconRoomMap.get(b.getBluetoothAddress());
            if (room != null) {
                mainListAdapter.add(room);
            }
        }
    }

    private class GetRoomRemote extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... macAddress) {
            if (macAddress[0] == null)
            {
                throw new NullPointerException();
            }
            try {
                JSONObject temp = getRoom(macAddress[0]);
                Room room = new Room(temp);
                beaconRoomMapOnline.put(macAddress[0], room);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private JSONObject getRoom(String macAddress) throws Exception {
            URL url = new URL("http://hftroomer.appspot.com/rooms?macAddress="+macAddress);
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