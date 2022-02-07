package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private TextView weather_info;
    private SearchView search_bar;
    private TextView tempMinMax;

    //Weather data
    private String city = "Barcelona";
    private String currentTemp = "";
    private String tempMin = "";
    private String tempMax = "";
    private String weatherIconName = ""; //name of the weather icon associate to the weather description

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather_info = findViewById(R.id.textView);
        search_bar = findViewById(R.id.searchView);
        tempMinMax = findViewById(R.id.textView2);

        new weatherBackgroundTask().execute();

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                city = query;
                new weatherBackgroundTask().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    class weatherBackgroundTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
           super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String response = "";
            try {
                String apiId = "6e36078b02f257df49f83be2314b0b32";
                response = getRequest.getWeather("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) //if an IOException occur the response is empty
            {
                search_bar.setQueryHint(city);
                try {
                    JSONtoVARS(result);
                    String currentTempText = currentTemp + "ºC";
                    String tempMinMaxText = tempMax + "/" + tempMin + "ºC";
                    weather_info.setText(currentTempText);
                    tempMinMax.setText(tempMinMaxText);
                    new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                            .execute("https://openweathermap.org/img/wn/"+weatherIconName+"@2x.png"); //get the weather description icon from the api
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //AsyncTask method to download an image extracted from
    //https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    protected void JSONtoVARS(String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        JSONObject main = jsonObj.getJSONObject("main");
        JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

        currentTemp = main.getString("temp").split("\\.", 0)[0];
        tempMin = main.getString("temp_min").split("\\.", 0)[0];
        tempMax = main.getString("temp_max").split("\\.", 0)[0];
        weatherIconName = weather.getString("icon");
        city = jsonObj.getString("name");
    }
}