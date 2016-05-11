package com.udacity.iak.deshantt.sunshinelesson1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
            weatherTask.execute();

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
    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            /*step 1_ConnectSunshineToTheCloud
            * add networking code using HTTPURLCONNECTION CLASS*/
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,us&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

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
                Log.v(LOG_TAG, "Forecast JSON String : " + forecastJsonStr);

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

            return null;
        }
    }

}
