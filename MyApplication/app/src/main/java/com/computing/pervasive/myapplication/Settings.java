package com.computing.pervasive.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Switch;

/**
 * Created by Thomas on 12.05.2015.
 */
public class Settings extends ActionBarActivity {

    private static final String ONLINE_PREF = "ONLINE_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        boolean online = settings.getBoolean("ONLINE", false);
        setContentView(R.layout.activity_settings);
        Switch sw = (Switch) findViewById(R.id.switch_online);
        sw.setChecked(online);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Switch sw = (Switch) findViewById(R.id.switch_online);
        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("ONLINE", sw.isChecked());
        editor.commit();
    }
}
