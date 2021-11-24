package com.davidread.newsfeed;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Callback method invoked exactly once when this activity is created.
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
}
