package com.udacity.iak.deshantt.sunshinelesson1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    /* step 4_ListviewWithFakeData
    *  declare array adapter for step 4*/
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    /*step 4_ConnectSunshineToTheCloud
    * inflate the refresh item on forecastfragment menu
    * add method : onCreate, onCreateOptionsMenu, onOptionsItemSelected*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // to inflate the menu forecastfragment into this fragment
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            /*step 5_ConnectSunshineToTheCloud
            * execute the asynctask when refresh item selected*/
            FetchWeatherTask weatherTask = new FetchWeatherTask();

            /*step 7_ConnectSunshineToTheCloud
            * add input param postalcode*/
            weatherTask.execute("94043,us");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*step 3_ListviewWithFakeData
        * Create fake data with array of String
        * Then turn it to be ArrayList*/

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] arrayData = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        // Create Array List that reference the string array arrayData
        // List<String> weekForecastList = new ArrayList<String>(Arrays.asList(arrayData));
        List<String> weekForecastList = new ArrayList<>(Arrays.asList(arrayData));

        /*step 4_ListviewWithFakeData
        * initialize the adapter, in this case, ArrayAdapter
        * The ArrayAdapter will take data from a source (like our dummy forecast) and
        * use it to populate the ListView it's attached to.*/
        mForecastAdapter =
                // new ArrayAdapter<String>(
                new ArrayAdapter<>(
                        getActivity(),                      // The current context (this activity)
                        R.layout.list_item_forecast,        // The name of the layout ID.
                        R.id.list_item_forecast_textview,   // The ID of the textview to populate.
                        weekForecastList);


        /*step 5_ListviewWithFakeData_finalStep
        * find reference to ListView
        * then set ArrayAdapter on ListView
        * Get a reference to the ListView, and attach ArrayAdapter to it.*/
        ListView myListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        myListView.setAdapter(mForecastAdapter);

        return rootView;
    }

    /*step 2_ConnectSunshineToTheCloud
    * Create Inner Class that extends Asynctask*/
    /*step 7_ConnectSunshineToTheCloud
    * change Asynctask param to be String, Void, Void & change Param in doInBackground with String*/
    /*step 8_ConnectSunshineToTheCloud
    * FetchWeatherTask use helper function by return an array of String forecast*/
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /*step 8_ConnectSunshineToTheCloud
        * add parsing code to parse JSON data & handling date
        * there are 3 helper method :
        * * *getReadableDateString() for formatting date
        * * *formatHighLows() for rounding temperature, high & low
        * * *getWeatherDataFromJson() for returning a forecast JSON String into an array of Forecast */

        /* The date/time conversion code is going to be moved outside the asynctask later,
        * * so for convenience we're breaking it out into its own method now.*/
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                /*step 9_ConnectSunshineToTheCloud_finalStep
                * remove verbose log when its done*/
                // Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {
            /*step 8_ConnectSunshineToTheCloud
            * return a string array to show the listview on the screen*/
            /*step 7_ConnectSunshineToTheCloud*/
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            /*step 1_ConnectSunshineToTheCloud
            * add networking code using HTTPURLCONNECTION CLASS*/
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            /*step 7_ConnectSunshineToTheCloud
            * add Variable for the input in URIBuilder*/
            String format = "json";
            String units = "metric";
            int numDays = 7;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                /*step 7_ConnectSunshineToTheCloud
                * add variable for the params in URIBuilder*/
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                /*step 7_ConnectSunshineToTheCloud
                * create UriBuilder from the variable inputs & params already defined*/
                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                /*step 7_ConnectSunshineToTheCloud
                * Create URL from the URIBuilder above
                * to check it, Log verbose the url in the logcat*/
                URL url = new URL(buildUri.toString());
                // Log.v(LOG_TAG, "Build URI " + url.toString());
                // Log.v(LOG_TAG, "Build URI " + buildUri.toString());
                /*step 9_ConnectSunshineToTheCloud_finalStep
                * remove verbose log when its done*/
                // Log.v(LOG_TAG, "Build URI : " + String.valueOf(url));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                /*step 6_ConnectSunshineToTheCloud
                * add verbose log to see the json output*/
                // Log.v(LOG_TAG, "Forecast JSON String : " + forecastJsonStr);
                /*step 9_ConnectSunshineToTheCloud_finalStep
                * remove verbose log when its done*/


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            /*step 8_ConnectSunshineToTheCloud
            * parse the response from the server*/
            // This will only happen if there was an error getting or parsing the forecast.
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        /*step 9_ConnectSunshineToTheCloud_finalStep
        * new data from openweathermap is back!*/
        @Override
        protected void onPostExecute(String[] theResult) {
            if (theResult != null){
                mForecastAdapter.clear();
                for (String dayForecastStr : theResult){
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }

}
