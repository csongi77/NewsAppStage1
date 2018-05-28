package com.example.csongor.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public static class SearchPreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.search_setting_preference);

            // set up Date preference - the number of past days to fetch news
            Preference datePreference = getPreferenceManager().findPreference(getString(R.string.date_key));
            setupPreference(datePreference);

            Preference sectionsPreference = getPreferenceManager().findPreference(getString(R.string.sections_key));
            setupPreference(sectionsPreference);


        }

        private void setupPreference(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof EditTextPreference || preference instanceof ListPreference) {
                String valueString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, valueString);
            } else {
                // Create default Set in order to avoid nullPointerException
                MultiSelectListPreference msl=(MultiSelectListPreference)preference;
                Set<String> values = sharedPreferences.getStringSet(msl.getKey(),null);
                onPreferenceChange(preference, values);
            }
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof EditTextPreference) {
                preference.setSummary(newValue.toString());
            } else if (preference instanceof MultiSelectListPreference) {
                CharSequence[] labels = ((MultiSelectListPreference)preference).getEntries();
                Set<String> values = (Set<String>)newValue;
                if (values != null && !values.isEmpty()) {
                    Iterator<String> iterator = values.iterator();
                    StringBuilder builder = new StringBuilder("");
                    int index=((MultiSelectListPreference)preference).findIndexOfValue(iterator.next());
                    builder.append(labels[index]);
                    while (iterator.hasNext()) {
                        int innerIndex=((MultiSelectListPreference)preference).findIndexOfValue(iterator.next());
                        builder.append(" | "+labels[innerIndex]);
                    }
                    preference.setSummary(builder.toString());
                } else {
                    preference.setSummary(getString(R.string.all_other_sections));
                }
            }
            return true;
        }


    }
}
