package com.ascent.WeatherApp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class weatherForecast extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIv, searchIV;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_weather_forecast);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.temperature);
        conditionTV = findViewById(R.id.condition);
        weatherRV = findViewById(R.id.weather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idBlack);
        iconIv = findViewById(R.id.icon);
        searchIV = findViewById(R.id.search);
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        weatherRV.setAdapter(weatherAdapter);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(weatherForecast.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

          }

        Location location =  locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>LATITUDE<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(+ latitude);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>LONGITUDE<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(+ longitude);
        cityName = getCityName(longitude, latitude);

        getWeatherInfo(cityName);
//        getWeatherInfoPerDay(cityName);


        searchIV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString().toUpperCase();
                if(city.isEmpty()) {
                    Toast.makeText(weatherForecast.this, "Please enter city", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
//                    getWeatherInfoPerDay(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for(Address adr : addresses){
                if(adr != null) {
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"User City not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo (String cityName){
//        String url = "http://api.weatherapi.com/v1/current.json?key=b53cf8f0101a4f32a4f191402231303&q="+ cityName +"&aqi=no";
//        String url = "http://api.weatherapi.com/v1/forecast.json?key=b53cf8f0101a4f32a4f191402231303&q="+ cityName +"&aqi=yes&alerts=yes";
        String url = "http://api.weatherapi.com/v1/forecast.json?key=3c86c89760b04d5ba2d153617231403&q="+ cityName +"&days=7&aqi=no&alerts=no";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(weatherForecast.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherModalArrayList.clear();

                try{

                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°c");
//                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIv);
                    conditionTV.setText(condition);

//                    if (isDay == 1){
//                        Picasso.get().load("@drawable/morning").into(backIV);
//                    }else{
//                        Picasso.get().load("@drawable/night2").into(backIV);
//                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherModalArrayList.add(new WeatherModal(time, temper, img, wind));
                    }
                    weatherAdapter.notifyDataSetChanged();

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(weatherForecast.this,"Please enter valid city name..", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }


    private String getDayOfWeek(String date) {
        // Parse the input date string to a LocalDate object
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        // Get the day of week as a TextStyle.FULL string
        String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

        return dayOfWeek;
    }


//    private void getWeatherInfoPerDay (String cityName){
////        String url = "http://api.weatherapi.com/v1/current.json?key=b53cf8f0101a4f32a4f191402231303&q="+ cityName +"&aqi=no";
////        String url = "http://api.weatherapi.com/v1/forecast.json?key=b53cf8f0101a4f32a4f191402231303&q="+ cityName +"&aqi=yes&alerts=yes";
//        String url = "http://api.weatherapi.com/v1/forecast.json?key=3c86c89760b04d5ba2d153617231403&q="+ cityName +"&days=7&aqi=no&alerts=no";
//        cityNameTV.setText(cityName);
//        RequestQueue requestQueue = Volley.newRequestQueue(weatherForecast.this);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                loadingPB.setVisibility(View.GONE);
//                homeRL.setVisibility(View.VISIBLE);
//                weatherModalArrayList.clear();
//
//                try{
//
//                    String temperature = response.getJSONObject("forecast").getJSONObject("forecastday").getString("maxtemp_c");
//                    temperatureTV.setText(temperature + "°c");
////                    int isDay = response.getJSONObject("current").getInt("is_day");
//                    String condition = response.getJSONObject("forecast").getJSONObject("forecastday").getJSONObject("date").getJSONObject("condition").getString("text");
//                    String conditionIcon = response.getJSONObject("forecast").getJSONObject("forecastday").getJSONObject("date").getJSONObject("condition").getString("icon");
//                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIv);
//                    conditionTV.setText(condition);
//
////                    if (isDay == 1){
////                        Picasso.get().load("https://www.google.com/imgres?imgurl=https%3A%2F%2Fwallpaper.dog%2Flarge%2F17016714.jpg&imgrefurl=https%3A%2F%2Fwallpaper.dog%2Fblue-sky-phone-wallpapers&tbnid=dcptiOxrPFDJeM&vet=12ahUKEwjhhefP-tr9AhV40HMBHequD1cQMygCegUIARDBAQ..i&docid=ReDlEktEET37iM&w=1080&h=1920&q=sky%204k%20wallpaper%20for%20android&ved=2ahUKEwjhhefP-tr9AhV40HMBHequD1cQMygCegUIARDBAQ").into(backIV);
////                    }else{
////                        Picasso.get().load("https://www.google.com/imgres?imgurl=https%3A%2F%2Fi.pinimg.com%2F736x%2F1c%2F23%2F2b%2F1c232b9015ecea9f7fea80acf94348af.jpg&imgrefurl=https%3A%2F%2Fwww.pinterest.com%2Fpin%2Ff--602004675188227749%2F&tbnid=EOMcvIzvjX8MzM&vet=10CE4QMyiMAWoXChMI2JKI0Pra_QIVAAAAAB0AAAAAEAg..i&docid=6nWvFwY5PWB_OM&w=736&h=1308&q=sky%204k%20wallpaper%20for%20android&ved=0CE4QMyiMAWoXChMI2JKI0Pra_QIVAAAAAB0AAAAAEAg").into(backIV);
////                    }
//
//                    JSONObject forecastObj = response.getJSONObject("forecast");
//                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
//                    JSONArray dayArray = forecastO.getJSONArray("date");
//
//                    for (int i = 0; i < dayArray.length(); i++) {
//                        JSONObject hourObj = dayArray.getJSONObject(i);
//                        String time = hourObj.getString("date");
//                        String temper = hourObj.getString("maxtemp_c");
//                        String img = hourObj.getJSONObject("condition").getString("icon");
//                        String wind = hourObj.getString("maxwind_kph");
//                        weatherModalArrayList.add(new WeatherModal(time, temper, img, wind));
//                    }
//                    weatherAdapter.notifyDataSetChanged();
//
//                } catch(JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(weatherForecast.this,"Please enter valid city name..", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        requestQueue.add(jsonObjectRequest);
//    }


}