package com.davidread.theguardiannewsclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
     * {@link String} log tag name for {@link MainActivity}.
     */
    public static final String LOG_TAG_NAME = SettingsActivity.class.getSimpleName();


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
         * preferences layout, binds each preference's summary to its current value, and sets up
         * the about preference.
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

            // Attach listener to open about alert dialog to the about preference.
            Preference aboutPreference = findPreference(getString(R.string.about_key));
            if (aboutPreference != null && getContext() != null) {
                setAboutPreferenceOnClickListener(aboutPreference, getContext());
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

        /**
         * Attaches a {@link Preference.OnPreferenceClickListener} to the passed {@link Preference}
         * object. The listener opens an alert dialog that contains information about the app.
         *
         * @param aboutPreference {@link Preference} object to attach the listener to.
         * @param context         {@link Context} object to access package manager and other helper
         *                        objects.
         */
        private void setAboutPreferenceOnClickListener(Preference aboutPreference, Context context) {

            /* DialogInterface.OnClickListener defines how the visit website button handles its
             * click event. */
            DialogInterface.OnClickListener visitWebsiteOnClickListener = new DialogInterface.OnClickListener() {

                /**
                 * Handles click event. On this event, open The Guardian's contact page.
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.theguardian.com/help/contact-us"));

                    // Do nothing if no browser app is installed on the device.
                    if (intent.resolveActivity(context.getPackageManager()) == null) {
                        Log.e(LOG_TAG_NAME, "No browser app installed to handle view intent");
                        Toast.makeText(context, getString(R.string.no_browser_label), Toast.LENGTH_LONG).show();
                        return;
                    }

                    startActivity(intent);
                }
            };

            /* Preference.OnPreferenceClickListener defines how the about preference handles its
             * preferenceClick event. */
            Preference.OnPreferenceClickListener aboutPreferenceOnPreferenceClickListener = new Preference.OnPreferenceClickListener() {

                /**
                 * Handles preferenceClick event. On this event, show an {@link AlertDialog}
                 * containing information about the app.
                 */
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.about_dialog_title_label));
                    builder.setMessage(getString(R.string.about_dialog_message_label));
                    builder.setPositiveButton(R.string.about_dialog_positive_button_label, visitWebsiteOnClickListener);
                    builder.setNegativeButton(android.R.string.cancel, null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                }
            };

            // Attach Preference.OnPreferenceClickListener object to the Preference object.
            aboutPreference.setOnPreferenceClickListener(aboutPreferenceOnPreferenceClickListener);
        }
    }
}
