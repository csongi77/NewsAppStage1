package com.example.csongor.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

            // set up Sections preference
            Preference sectionsPreference = getPreferenceManager().findPreference(getString(R.string.sections_key));
            setupPreference(sectionsPreference);
        }

        // set up preference and listener for specified preference
        private void setupPreference(Preference preference) {

            // first we set up the listener
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof EditTextPreference || preference instanceof ListPreference) {

                // we read the value for the preference we got as parameter, and fire onPreferenceChange event
                String valueString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, valueString);
            } else {

                // for multiSelectListPreference we have to get a set of values and fire onPreferenceChange event
                MultiSelectListPreference multiSelectListPreference=(MultiSelectListPreference)preference;
                Set<String> values = sharedPreferences.getStringSet(multiSelectListPreference.getKey(),null);
                onPreferenceChange(preference, values);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            // depending on preference class we set the summary in PreferenceFragment
            if (preference instanceof EditTextPreference) {
                preference.setSummary(newValue.toString());
            } else if (preference instanceof MultiSelectListPreference) {

                // for multiSelectListPreferences it's a bit more difficult. First ve get all entries
                // in a CharSequence array. Then we assign the newValue parameter as Set of Strings
                CharSequence[] labels = ((MultiSelectListPreference)preference).getEntries();
                Set<String> values = (Set<String>)newValue;

                // Then we build up the summary for PreferenceFragment's preference
                if (values != null && !values.isEmpty()) {

                    // If the values set is not empty we get an Iterator to check which preference has been set
                    Iterator<String> iterator = values.iterator();
                    StringBuilder builder = new StringBuilder("");

                    // get the index of related String value and append it to StringBuilder object
                    int index=((MultiSelectListPreference)preference).findIndexOfValue(iterator.next());
                    builder.append(labels[index]);
                    while (iterator.hasNext()) {
                        int innerIndex=((MultiSelectListPreference)preference).findIndexOfValue(iterator.next());
                        builder.append(" | "+labels[innerIndex]);
                    }
                    preference.setSummary(builder.toString());
                } else {
                    // if no options has been selected, we inform the user by an appropriate summary
                    preference.setSummary(getString(R.string.all_other_sections));
                }
            }
            return true;
        }
    }
}
