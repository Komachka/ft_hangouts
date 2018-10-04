package com.example.kstorozh.ft_hangouts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by kateryna on 29.09.18.
 */

public class PrefActivity extends PreferenceActivity {

    static final String SHAR_KEY = "toolbarCol";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference preference = findPreference(SHAR_KEY);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(PrefActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });







    }
}
