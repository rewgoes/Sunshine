package com.wolfgoes.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mLocation;

    private final String FORECASTFRAGMENT_TAG = "FORECASTFRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate");
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onResume");
        String currentLocation = Utility.getPreferredLocation(this);

        if (mLocation == null || !currentLocation.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = currentLocation;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_location) {
            openMyLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openMyLocation () {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        String uri = String.format("geo:0,0?q=%s", location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);

    }

}
