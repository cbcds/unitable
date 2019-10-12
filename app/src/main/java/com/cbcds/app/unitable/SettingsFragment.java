package com.cbcds.app.unitable;

import android.app.Application;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cbcds.app.unitable.database.ClassRepository;
import com.cbcds.app.unitable.database.MarkRepository;
import com.cbcds.app.unitable.database.SubjectRepository;
import com.cbcds.app.unitable.utils.AppUtils;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String PREF_LANGUAGE;
    private String PREF_WIPE_DATA;
    private String PREF_THEME;

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        PREF_LANGUAGE = getString(R.string.string_pref_language);
        PREF_WIPE_DATA = getString(R.string.string_pref_wipe_data);
        PREF_THEME = getString(R.string.string_pref_theme);

        ListPreference langPreference = (ListPreference) findPreference(PREF_LANGUAGE);
        langPreference.setSummary(langPreference.getEntry());
        Preference wipeDataPreference = findPreference(PREF_WIPE_DATA);
        wipeDataPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final View dlgView = getActivity().getLayoutInflater().inflate(R.layout.dialog_delete, null);
                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                TextView tvMessage = dlgView.findViewById(R.id.tv_delete_message);
                String message = getString(R.string.string_wipe_data_message);
                tvMessage.setText(message);

                Button wipeButton = dlgView.findViewById(R.id.dlg_button_delete);
                wipeButton.setText(R.string.string_button_wipe);
                Button cancelButton = dlgView.findViewById(R.id.dlg_button_cancel);
                wipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Application app = getActivity().getApplication();
                        new ClassRepository(app, 0).deleteAllData();
                        new SubjectRepository(app).deleteAllData();
                        new MarkRepository(app, "").deleteAllData();
                        Toast.makeText(getActivity(), R.string.string_toast_wipe_data, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setView(dlgView);
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_LANGUAGE)) {
            TaskStackBuilder.create(getActivity())
                    .addNextIntent(new Intent(getActivity(), MainActivity.class))
                    .addNextIntent(getActivity().getIntent())
                    .startActivities();
        } else if (key.equals(PREF_THEME)) {
            AppUtils.setThemeFromPreferences(getActivity());
            String message = getString(R.string.string_toast_theme_changed);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            TaskStackBuilder.create(getActivity())
                    .addNextIntent(new Intent(getActivity(), MainActivity.class))
                    .addNextIntent(getActivity().getIntent())
                    .startActivities();
        }
    }
}
