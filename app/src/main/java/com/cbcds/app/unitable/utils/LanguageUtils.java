package com.cbcds.app.unitable.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.cbcds.app.unitable.R;

public class LanguageUtils {

    public static String getSavedLanguage(Context context) {
        String prefLanguage = context.getString(R.string.string_pref_language);
        String langCode = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(prefLanguage, "en");
        return langCode;
    }
}
