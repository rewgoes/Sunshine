package com.wolfgoes.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wolfgoes.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mUseTodayLayout ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = viewType == VIEW_TYPE_TODAY ?
                R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View listItem = LayoutInflater.from(context).inflate(layoutId, parent, false);
        listItem.setTag(new ViewHolder(listItem));

        return listItem;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(WeatherContract.COL_WEATHER_CONDITION_ID);
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = viewType == VIEW_TYPE_TODAY ? Utility.getArtResourceForWeatherCondition(weatherId) : Utility.getIconResourceForWeatherCondition(weatherId);
        // Use placeholder image for now
        holder.iconView.setImageResource(layoutId);

        // TODO Read date from cursor
        long date = cursor.getLong(WeatherContract.COL_WEATHER_DATE);
        holder.dateView.setText(Utility.getFriendlyDayString(mContext, date, mUseTodayLayout));

        // TODO Read weather forecast from cursor
        String description = cursor.getString(WeatherContract.COL_WEATHER_DESC);
        holder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(WeatherContract.COL_WEATHER_MAX_TEMP);
        holder.highTempView.setText(Utility.formatTemperature(mContext, high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(WeatherContract.COL_WEATHER_MIN_TEMP);
        holder.lowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));

    }
}