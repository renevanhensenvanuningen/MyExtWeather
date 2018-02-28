package com.example.reneu.myextweather;

/**
 * Created by reneu on 27-2-2018.
 */
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class WeatherForecastFragment extends ListFragment {


    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {

        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        CityPreference cp = new CityPreference(getActivity());
        updateWeatherData(cp.getCity());

    }

    private void renderWeather(JSONObject json){
        try
        {
            JSONArray array = json.getJSONArray("list");
            List<String> values = new ArrayList<String>();
            for (int i =0; i < array.length(); i++)
            {
            JSONObject details = json.getJSONArray("list").getJSONObject(i);

            JSONObject main = details.getJSONObject("main");
            String temp_min = main.getString("temp_min");
            String temp_max = main.getString("temp_max");
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(details.getLong("dt")*1000));

            values.add(updatedOn + " " + temp_min + " "+ temp_max );
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            values.notifyAll();
        }
        catch(Exception e)
        {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }


 //   @Override
 //   public View onCreateView(LayoutInflater inflater, ViewGroup container,
 //                            Bundle savedInstanceState) {
 ///       View rootView = inflater.inflate(R.layout.fragment_forecastweather, container, false);
  ///  }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        CityPreference cp = new CityPreference(getActivity());
        updateWeatherData(cp.getCity());
    }

    private void updateWeatherData(final String city) {
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSONForecast(getActivity(), city);

                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
    }


}
