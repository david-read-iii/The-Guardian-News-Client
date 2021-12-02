package com.davidread.theguardiannewsclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * {@link SettingsActivity} is a simple activity class that contains a {@link SettingsFragment}.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Callback method invoked exactly once when this activity is created. It just inflates a
     * layout in the activity.
     *
     * @param savedInstanceState {@link Bundle} object where instance state from a previous
     *                           configuration change is stored.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /**
     * Callback method invoked when an options menu item is clicked. On this event, have the up
     * button mimic the behavior of the back button.
     *
     * @param item {@link MenuItem} that was clicked.
     * @return False to allow normal menu processing to proceed. True to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    /**
     * {@link SettingsFragment} is a fragment class whose UI is a list of settings. Settings to
     * specify the order articles are arranged and to specify an optional search term are present.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        /**
         * Callback method invoked exactly once when this fragment is created. It inflates a
         * preferences layout and binds each preference's summary to its current value.
         *
         * @param savedInstanceState {@link Bundle} object where instance state from a previous
         *                           configuration change is stored.
         * @param rootKey            {@link String} that if non-null, this preference fragment
         *                           should be rooted at the
         *                           {@link androidx.preference.PreferenceScreen} with this key.
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            // Inflate preferences layout.
            setPreferencesFromResource(R.xml.settings, rootKey);

            // Bind each preference's summary to its current value.
            Preference orderByPreference = findPreference(getString(R.string.order_by_key));
            if (orderByPreference != null) {
                bindSummaryToValue(orderByPreference);
            }
            Preference searchTermPreference = findPreference(getString(R.string.search_term_key));
            if (searchTermPreference != null) {
                bindSummaryToValue(searchTermPreference);
            }
        }

        /**
         * Binds the summary of a {@link Preference} object to its value.
         *
         * @param preference {@link Preference} object where the bind is committed.
         */
        private void bindSummaryToValue(Preference preference) {

            /* Attach a OnPreferenceChangeListener to the passed Preference object that binds its
             * summary to its value each time the value is changed. */
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newValueString = newValue.toString();
                    if (preference instanceof ListPreference) {
                        ListPreference listPreference = (ListPreference) preference;
                        int listPreferenceIndex = listPreference.findIndexOfValue(newValueString);
                        CharSequence[] listPreferenceLabels = listPreference.getEntries();
                        preference.setSummary(listPreferenceLabels[listPreferenceIndex]);
                        return true;
                    } else if (preference instanceof EditTextPreference) {
                        if (newValueString.isEmpty()) {
                            preference.setSummary(getString(R.string.search_term_empty_label));
                        } else {
                            preference.setSummary(newValueString);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            // Set the current summary of the passed Preference object.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String summary = sharedPreferences.getString(preference.getKey(), "");
            preference.getOnPreferenceChangeListener().onPreferenceChange(preference, summary);
        }
    }
}
