package com.cbcds.app.unitable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onAppThemeButtonClick(View view) {
        int id = view.getId();
        String prefValue;
        switch (id) {
            case R.id.forest_pref:
                prefValue = getString(R.string.string_misty_forest_pref);
                break;
            case R.id.sunset_pref:
                prefValue = getString(R.string.string_crimson_sunset_pref);
                break;
            case R.id.ocean_pref:
                prefValue = getString(R.string.string_mysterious_ocean_pref);
                break;
            default:
                prefValue = getString(R.string.string_misty_forest_pref);
        }
        String key = getString(R.string.string_pref_theme);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(key, prefValue).apply();
    }

    public void restartFragment() {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}