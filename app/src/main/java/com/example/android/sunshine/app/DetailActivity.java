package com.example.android.sunshine.app;
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final int DETAIL_LOADER = 0;
        public static final String DETAIL_URI = "URI";
        private ShareActionProvider provider;

        private ImageView mIconView;
        private TextView mDateView;
        private TextView mDescriptionView;
        private TextView mFriendlyDateView;
        private TextView mHighTempView;
        private TextView mLowTempView;
        private TextView mHumidityView;
        private TextView mWindView;
        private TextView mPressureView;
        private Uri mUri;


        private String mForecast;
        private static final String HASHTAG = " #SunshineApp";


        private static final String[] FORECAST_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;
        static final int COL_HUMIDITY = 5;
        static final int COL_WIND= 6;
        static final int COL_DEGREES= 7;
        static final int COL_PRESSURE= 8;
        static final int WEATHER_COND_ID= 9;


        public DetailFragment() {
            setHasOptionsMenu(true);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            if (args != null) {
                mUri = args.getParcelable(DETAIL_URI);
            }

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            mIconView = (ImageView) rootView.findViewById(R.id.image_icon);
            mDateView = (TextView) rootView.findViewById(R.id.date);
            mDescriptionView = (TextView) rootView.findViewById(R.id.description);
            mFriendlyDateView = (TextView) rootView.findViewById(R.id.day_of_the_week);
            mHighTempView = (TextView) rootView.findViewById(R.id.high_temp);
            mLowTempView = (TextView) rootView.findViewById(R.id.low_temp);
            mWindView = (TextView) rootView.findViewById(R.id.wind);
            mHumidityView = (TextView) rootView.findViewById(R.id.humidity);
            mPressureView = (TextView) rootView.findViewById(R.id.pressure);

            return rootView;
        }

        private Intent createShareIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + HASHTAG);
            return shareIntent;

        }
        public void onLocationChanged( String newLocation ) {
            // replace the uri, since the location has changed
            Uri uri = mUri;
            if (null != uri) {
                long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
                Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
                mUri = updatedUri;
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.fragmentdetail, menu);

            MenuItem share = menu.findItem(R.id.action_share);
            provider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

            if (mForecast !=  null) {
                provider.setShareIntent(createShareIntent());
            }
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                return new CursorLoader(getActivity(), mUri, FORECAST_COLUMNS, null, null, null);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {return;}

            int weatherId = data.getInt(WEATHER_COND_ID);
            //TEMPORARY PIC
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            long date = data.getLong(COL_WEATHER_DATE);
            String friendly = Utility.getFriendlyDayString(getActivity(), date);
            String normalDate = Utility.getFormattedMonthDay(getActivity(), date);
            mDateView.setText(normalDate);
            mFriendlyDateView.setText(friendly);

            String description = data.getString(COL_WEATHER_DESC);
            mDescriptionView.setText(description);

            boolean isMetric = Utility.isMetric(getActivity());

            int high = data.getInt(COL_WEATHER_MAX_TEMP);
            int low= data.getInt(COL_WEATHER_MIN_TEMP);
            mHighTempView.setText(Utility.formatTemperature(getActivity(), high, isMetric));
            mLowTempView.setText(Utility.formatTemperature(getActivity(), low, isMetric));


            mHumidityView.setText(getActivity().getString(R.string.format_humidity, data.getFloat(COL_HUMIDITY)));
            mPressureView.setText(getActivity().getString(R.string.format_pressure, data.getFloat(COL_PRESSURE)));

            mWindView.setText(Utility.getFormattedWind(getActivity(), data.getFloat(COL_WIND), data.getFloat(COL_DEGREES)));

            mForecast = String.format("%s - %s - %s/%s", normalDate, description, high, low);


            if (provider != null) {
                provider.setShareIntent(createShareIntent());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}

