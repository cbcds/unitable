package com.cbcds.app.unitable;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppThemePreference extends Preference {

    private Context mContext;

    public AppThemePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public AppThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public AppThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AppThemePreference(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.set_theme_peference, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Log.d("MYLOG", "here");
        String PREF_THEME = mContext.getString(R.string.string_pref_theme);
        String theme = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_THEME, mContext.getString(R.string.string_misty_forest));
        if (theme.equals(mContext.getString(R.string.string_misty_forest_pref))) {
            view.findViewById(R.id.forest_tick).setVisibility(View.VISIBLE);
        } else if (theme.equals(mContext.getString(R.string.string_mysterious_ocean_pref))) {
            view.findViewById(R.id.ocean_pref).findViewById(R.id.ocean_tick).setVisibility(View.VISIBLE);
        } else if (theme.equals(mContext.getString(R.string.string_crimson_sunset_pref))) {
            view.findViewById(R.id.sunset_tick).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.forest_tick).setVisibility(View.VISIBLE);
        }
    }
}
