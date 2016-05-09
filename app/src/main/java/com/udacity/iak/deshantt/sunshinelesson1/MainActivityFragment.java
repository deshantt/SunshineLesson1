package com.udacity.iak.deshantt.sunshinelesson1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    /* step 4_ListviewWithFakeData
    *  declare array adapter for step 4*/
    ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
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
}
