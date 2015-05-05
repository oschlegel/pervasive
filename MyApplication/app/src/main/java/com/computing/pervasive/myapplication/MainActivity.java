package com.computing.pervasive.myapplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    private Region REGION = new Region("MyUnifiedID", null, null, null);
    MainActivity instance;

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
                    .setTitle("No Bluetooth detected!")
                    .setMessage("You can't use this app on your device.")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                new AlertDialog.Builder(this)
                        .setTitle("Bluetooth disabled")
                        .setMessage("Do you want to enable bluetooth?\nOtherwise you close this app.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BluetoothAdapter.getDefaultAdapter().enable();
                            }
                        })
                        .setNegativeButton("Close app", new DialogInterface.OnClickListener() {
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
                    BeaconParser bp = new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24");
                    if (beaconManager.getBeaconParsers().size() < 2) {
                        beaconManager.getBeaconParsers().add(bp);
                    }
                    beaconManager.bind(this);
                }
                else {
                    throw new UnsupportedOperationException("No instance for beaconManager.");
                }
            }

            setContentView(R.layout.activity_main);

            ListView mainListView = (ListView) findViewById(R.id.mainListView);

            ArrayAdapter<Room> mainListAdapter = new ArrayAdapter<>(this, R.layout.simple_row, new ArrayList<Room>());
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

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                // list all detected rooms
                List<Room> rooms = new ArrayList<>();

                Collections.sort((List) beacons, new Comparator<Beacon>() {
                    @Override
                    public int compare(Beacon lhs, Beacon rhs) {
                        double dis1 = lhs.getDistance();
                        double dis2 = rhs.getDistance();
                        return dis1 <= dis2 ? -1 : 1;
                    }
                });

                for (Beacon beac : beacons) {
                    if (beac != null) {
                        double dis = beac.getDistance();

                        Room room = handlerDB.findRoom(beac.getId1().toString(), beac.getId2().toString(), beac.getId3().toString());

                        if (room != null) {
                            room.setDistance(dis);

                            rooms.add(room);
                        }
                    }
                }

                // update listview
                if (isActivityRunning(instance.getClass())) {
                    final ListView mainListView = (ListView) findViewById(R.id.mainListView);
                    final ArrayAdapter<Room> mainListAdapter = new ArrayAdapter<>(instance, R.layout.simple_row, R.id.rowTextView, rooms);
                    mainListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mainListView.setAdapter(mainListAdapter);
                        }
                    });
                }

                // logcat message
                //String logtext = "Ranged following beacons:\n";
                //for (Beacon beac : beacons) {
                //    logtext += beac.getId1().toString() + "\n";
                //}
                //Log.i(TAG, logtext);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void lookupRoom(String beaconID1, String beaconID2, String beaconID3)
    {
        Room room = handlerDB.findRoom(beaconID1, beaconID2, beaconID3);

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
    }*/

    private void lookupRoom(int roomID)
    {
        Room room = handlerDB.findRoom(roomID);

        if (room != null)
        {
            Intent intent = new Intent(this, RoomDetail.class);
            intent.putExtra("ROOM", room);
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

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo task : tasks) {
            if (task.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return true;
        }

        return false;
    }

}
