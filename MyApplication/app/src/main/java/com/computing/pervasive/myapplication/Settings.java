package com.computing.pervasive.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

/**
 * Created by Thomas on 12.05.2015.
 */
public class Settings extends Activity {

    private static final String ONLINE_PREF = "ONLINE_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        boolean online = settings.getBoolean("ONLINE", false);
        setContentView(R.layout.activity_settings);
        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnline);
        cb.setChecked(online);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnline);
        SharedPreferences settings = getSharedPreferences(ONLINE_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("ONLINE", cb.isChecked());
        editor.commit();
    }
}
