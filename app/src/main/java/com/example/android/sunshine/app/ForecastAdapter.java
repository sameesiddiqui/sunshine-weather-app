
package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private final int VIEW_TODAY = 0;
    private final int VIEW_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TODAY:VIEW_FUTURE;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == 0) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_COND_ID);

        if (getItemViewType(cursor.getPosition()) == VIEW_TODAY) {
            viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        } else {
            viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        }
        // Read date from cursor
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        viewHolder.lowView.setText(Utility.formatTemperature(context, low, isMetric));
    }
}