package com.computing.pervasive.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
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

import java.util.Collection;
import java.util.List;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class MainActivity extends ActionBarActivity implements BeaconConsumer {

    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private MyDBHandler handlerDB = new MyDBHandler(this);
    private Intent intent = null;
    private Region REGION = new Region("MyUnifiedID", null, null, null);
    private Room lastroom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        setContentView(R.layout.activity_main);

        ListView mainListView = (ListView) findViewById( R.id.mainListView );

        List<Room> rooms = handlerDB.getAllRoomsSorted();
        rooms.add(new Room("1/U37", 100, "Tiefenhörsaal", null, handlerDB.getBuilding(1)));

        if (!rooms.isEmpty()) {
            ArrayAdapter<Room> mainListAdapter = new ArrayAdapter<>(this, R.layout.simple_row, rooms);
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
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        /*beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "I just saw an beacon for the first time!");

                Context context = getApplicationContext();
                CharSequence text = region.getUniqueId();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void didExitRegion(Region region) {

                Log.d(TAG, "I no longer see an beacon");

                Context context = getApplicationContext();
                CharSequence text = region.getUniqueId();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("MyUnifiedID", null, null, null));
            Log.d(TAG, "Start Monitoring");
        }
        catch (RemoteException e) {
            Log.d(TAG, "Error");
        }*/
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon beacon = null;
                for (Beacon b : beacons) {
                    if (beacon == null)
                    {
                        beacon = b;
                        continue;
                    }
                    if (b.getDistance() < beacon.getDistance()) {
                        beacon = b;
                    }
                }
                if (beacon != null) {
                    String beaconID = beacon.getId1().toString();
                    if (beacon.getDistance() < 1) {
                        lookupRoom(beaconID);
                    }
                    else {
                        if (intent != null) {
                            intent.putExtra("keep", false);
                            startActivity(intent);
                            intent = null;
                        }
                    }
                }
                else {
                    if (intent != null) {
                        intent.putExtra("keep", false);
                        startActivity(intent);
                        intent = null;
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(REGION);
            Log.d(TAG, "Start Ranging");
        }
        catch (RemoteException e) {
            Log.d(TAG, "Error");
        }
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void lookupRoom(String beaconID)
    {
        Room room = handlerDB.findRoom(beaconID);

        if (room != null) {
            if (lastroom == null || lastroom != room) {
                lastroom = room;
                intent = new Intent(this, RoomDetail.class);
                intent.putExtra("keep", true);
                intent.putExtra("ROOM", room);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
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

    private void lookupRoom(int roomID)
    {
        Room room = handlerDB.findRoom(roomID);

        if (room != null)
        {
            intent = new Intent(this, RoomDetail.class);
            intent.putExtra("keep", true);
            intent.putExtra("ROOM", room);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
}
