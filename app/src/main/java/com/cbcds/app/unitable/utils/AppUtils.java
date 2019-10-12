package com.cbcds.app.unitable.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.cbcds.app.unitable.R;

public class AppUtils {

    public static void setupActionBar(AppCompatActivity activity, int layoutId) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(layoutId);
        actionBar.setElevation(2);
    }

    public static void setThemeFromPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.string_pref_theme);
        String defaultValue = context.getString(R.string.string_misty_forest);
        int styleId = getStyleId(context,
                sharedPreferences.getString(key, defaultValue));
        context.setTheme(styleId);
    }

    private static int getStyleId(Context context, String themeSting) {
        if (themeSting.equals(context.getString(R.string.string_misty_forest_pref))) {
            return R.style.AppTheme_FoggyForest;
        } else if (themeSting.equals(context.getString(R.string.string_crimson_sunset_pref))) {
            return R.style.AppTheme_CrimsonSunset;
        } else if (themeSting.equals(context.getString(R.string.string_mysterious_ocean_pref))) {
            return R.style.AppTheme_MysteriousOcean;
        } else {
            return R.style.AppTheme_FoggyForest;
        }
    }
}
