package com.example.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private Button getRequestButton;
    private ImageButton refreshButton;
    private TextView weather_info;
    private ImageView weatherIcon;

    //Weather data
    private String weather_text_info="";
    private String city = "Barcelona";
    private final String apiId = "6e36078b02f257df49f83be2314b0b32";
    private String currentTemp = "";
    private String skyState = "";
    private String tempMin = "";
    private String tempMax = "";
    private String weatherIconName = ""; //name of the weather icon associate to the weather description

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather_info = findViewById(R.id.textView);
        refreshButton = findViewById(R.id.imageButton);
        weatherIcon = findViewById(R.id.imageView);

        new weatherBackgroundTask().execute();
    }

    class weatherBackgroundTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
           super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);*/
        }

        protected String doInBackground(String... args) {
            String response;
            try {
                response = getRequest.getWeather("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiId);
            } catch (IOException e) {
                response = e.toString();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONtoVARS(result);
                weather_info.setText(currentTemp);
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute("https://openweathermap.org/img/wn/"+weatherIconName+"@2x.png"); //get the weather description icon from the api

                //Refresh the weather data with a button
                //using thread because internet request
                //have to be done in background
                refreshButton.setOnClickListener(view -> {
                    //We have to do a thread for a network petition to work in background(mandatory)
                    Thread thread = new Thread(() -> {
                        try {
                            weather_text_info=getRequest.getWeather("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiId);
                            JSONtoVARS(weather_text_info);
                        } catch (Exception e) {
                            weather_text_info = e.toString();
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                        weather_info.setText(currentTemp);
                    } catch (InterruptedException e) {
                        weather_info.setText(e.toString());
                    }
                });
            } catch (JSONException e) {
                weather_info.setText(e.toString());
                /*
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);*/
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

        if(Character.compare(main.getString("temp").charAt(1), '.') != 0) //0 is equals
            currentTemp = main.getString("temp").charAt(0) +""+ main.getString("temp").charAt(1) + "ºC";
        else
            currentTemp = main.getString("temp").charAt(0) + "°C"; //round degrees
        tempMin = main.getString("temp_min").charAt(0) + "°C";
        tempMax = main.getString("temp_max").charAt(0) + "°C";

        skyState = weather.getString("description");
        weatherIconName = weather.getString("icon");

        city = jsonObj.getString("name");
    }
}