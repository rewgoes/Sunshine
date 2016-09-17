package com.wolfgoes.sunshine.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wolfgoes.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    ForecastAdapter mForecastAdapter;
    String mUnit;

    public ForecastFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter = new ForecastAdapter(
                getContext(),
                cur,
                0
        );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String forecast = mForecastAdapter.getItem(position);
//                Intent detailIntent = new Intent(getContext(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(detailIntent);
//            }
//        });

        return rootView;
    }

    public void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getContext());

            /* getActivity().getPreferences(int mode) uses getActivity().getLocalClassName() as filename,
             * which is not the name of the default shared preference */

            /* Name of default shared preference file: context.getPackageName() + "_preferences" */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        mUnit = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));

        weatherTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

}
