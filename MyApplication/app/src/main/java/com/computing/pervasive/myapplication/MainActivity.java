package com.computing.pervasive.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.MonitorNotifier;


public class MainActivity extends ActionBarActivity implements BeaconConsumer{

    private ListView mainListView;
    private ArrayAdapter<String> mainListAdapter;
    private BeaconManager beaconManager;
    private Map<String,String> roomIdetifiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListView = (ListView) findViewById( R.id.mainListView );
        mainListAdapter = new ArrayAdapter<String>(this, R.layout.simple_row,new String[]{});
        mainListView.setAdapter(mainListAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                // Get roomname for beaconid from database
                String rangeID = region.getUniqueId();
                String roomname;

                roomname = "Roomname";// <<< Get roomname from database <<<

                if (roomname!="") {
                    roomIdetifiers.put(rangeID, roomname);

                    // Add roomname for listView
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) mainListView.getAdapter();
                    adapter.add(roomIdetifiers.get(rangeID));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void didExitRegion(Region region) {

                String rangeID = region.getUniqueId();

                if (roomIdetifiers.containsKey(rangeID)) {

                    //Remove roomname from listView
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) mainListView.getAdapter();
                    adapter.remove(roomIdetifiers.get(rangeID));
                    adapter.notifyDataSetChanged();

                    roomIdetifiers.remove(rangeID);
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
