package fragments;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lasalle.crowdcloud.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Fragmented Assets
    private AutoCompleteTextView edLocation;
    private Button btnFinish;
    private ScrollView svResults;
    private TextView tvCityResult, tvTemperatureResult, tvHumidityResult, tvDescriptionResult, tvWindSpeedResult, tvCloudinessResult, tvPressureResult;
    private ImageView ivWeather;

    // Adapter for AutoCompleteTextView
    private ArrayAdapter<String> autoCompleteAdapter;

    // List to store autocomplete suggestions
    private List<String> autoCompleteSuggestions;

    //API

    private static final String api = "bd5e378503939ddaee76f12ad7a97608";
    private static final String url = "https://api.openweathermap.org/data/2.5/weather";
    private static final String imageUrl = "https://openweathermap.org/img/wn/";
    private static final String forecastUrl = "https://openweathermap.org/data/2.5/forecast?lat=";
    private static final String geolocateUrl = "https://api.openweathermap.org/geo/1.0/direct";

    //Formatting
    DecimalFormat df = new DecimalFormat("#.##");


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1, param1 );
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        edLocation = view.findViewById(R.id.edLocation);
        btnFinish = view.findViewById(R.id.btnFinish);
        svResults = view.findViewById(R.id.svResults);
        tvCityResult = view.findViewById(R.id.tvCityResult);
        tvTemperatureResult = view.findViewById(R.id.tvTemperatureResult);
        tvHumidityResult = view.findViewById(R.id.tvHumidityResult);
        tvDescriptionResult = view.findViewById(R.id.tvDescriptionResult);
        tvWindSpeedResult = view.findViewById(R.id.tvWindSpeedResult);
        tvCloudinessResult = view.findViewById(R.id.tvCloudinessResult);
        tvPressureResult = view.findViewById(R.id.tvPressureResult);
        ivWeather = view.findViewById(R.id.ivWeather);



        //Remove visibility to Scroll View
        svResults.setVisibility(View.GONE);




        // Set onClickListener for the Finish button
        btnFinish.setOnClickListener(v -> {
            // Get the entered location from the AutoCompleteTextView
            String location = edLocation.getText().toString().toLowerCase().trim();

            // Check if the location is empty
            if (location.isEmpty()) {
                // Display a toast message
                Toast.makeText(getContext(), "Please insert a city", Toast.LENGTH_SHORT).show();
            } else {
                // Apply button pressed animation
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.button_scale);
                v.startAnimation(animation);

                // Make API request using Volley
                fetchWeatherData(location);

                // Animate the Scroll View
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(svResults, "alpha", 0f, 1f);
                alphaAnimator.setDuration(2000);
                alphaAnimator.start();

                ObjectAnimator alphaAnimatorImage = ObjectAnimator.ofFloat(ivWeather, "alpha", 0f, 1f);
                alphaAnimatorImage.setDuration(1000);
                alphaAnimator.start();

                // Show the Scroll View
                svResults.setVisibility(View.VISIBLE);
            }
        });



        return view;
    }

    private void fetchWeatherData(String location) {
        // Construct the API URLs
        String apiUrl = url + "?q=" + location + "&appid=" + api;
        String apiGeoUrl = geolocateUrl + "?q=" + location + "&limit=5&appid=" + api;

        // Create a StringRequest to make the API request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the JSON response
                        handleWeatherResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getContext(), "Error fetching weather data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Create a StringRequest to make the geolocation API request
        StringRequest geoLocateRequest = new StringRequest(Request.Method.GET, apiGeoUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the JSON response from geolocation API
                        handleGeolocationResponse(response, location);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getContext(), "Error fetching geolocation data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the requests to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
        requestQueue.add(geoLocateRequest);
    }


    private void handleGeolocationResponse(String response, String location) {
        try {
            // Parsing the JSON response for geolocation
            JSONArray jsonArray = new JSONArray(response);

            if (jsonArray.length() > 0) {
                JSONObject locationObject = jsonArray.getJSONObject(0);

                // Extract latitude and longitude
                double latitude = locationObject.getDouble("lat");
                double longitude = locationObject.getDouble("lon");

                // Now, use the latitude and longitude to make the forecast API call
                fetchForecastData(location, latitude, longitude);
            } else {
                Toast.makeText(getContext(), "Geolocation data not found", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchForecastData(String location, double latitude, double longitude) {
        // Construct the API URL for forecast
        String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&appid=" + api;

        // Create a StringRequest to make the forecast API request
        StringRequest forecastRequest = new StringRequest(Request.Method.GET, forecastUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the JSON response from forecast API
                        handleForecastResponse(response, location);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getContext(), "Error fetching forecast data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the forecast request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(forecastRequest);
    }

    private void handleForecastResponse(String response, String location) {
        try {
            // Parsing the JSON response for forecast
            JSONObject forecastObject = new JSONObject(response);

            // Extracting the list of forecasts
            JSONArray forecastList = forecastObject.getJSONArray("list");

            // Update UI components for each forecast item (assuming 5 days for this example)
            for (int i = 0; i < 5; i++) {
                // Extract forecast for a specific day
                JSONObject dayForecast = forecastList.getJSONObject(i);

                // Extract the date
                long timestamp = dayForecast.getLong("dt");
                String dayOfWeek = getDayOfWeek(timestamp);

                // Extract other relevant information for the day (e.g., temperature, weather icon)
                double temperature = dayForecast.getJSONObject("temp").getDouble("day");
                String weatherIcon = dayForecast.getJSONArray("weather").getJSONObject(0).getString("icon");

                double tempCelsius = Math.round(temperature - 273.15);

                // Update UI components based on the extracted information
                updateDayUI(dayOfWeek, tempCelsius, weatherIcon, i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDayOfWeek(long timestamp) {
        // Convert timestamp to day of the week
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(date);
    }

    private void updateDayUI(String dayOfWeek, double temperature, String weatherIcon, int dayIndex) {
        // Implement the logic to update the UI components for each day
        // For example, update the labels, image views, and temperature values based on the forecast data
        // You can access the corresponding views using their IDs and set their text or image resources

        // Example (you need to replace these with your actual view IDs):
        TextView dayLabel = getView().findViewById(getDayLabelId(dayIndex));
        ImageView weatherImageView = getView().findViewById(getWeatherImageViewId(dayIndex));
        TextView temperatureLabel = getView().findViewById(getTemperatureLabelId(dayIndex));

        // Update the views
        dayLabel.setText(dayOfWeek);
        temperatureLabel.setText(String.valueOf(temperature) + " °C");

        // Load weather icon using Picasso (assuming you have Picasso library added)
        String imageUrl = "https://openweathermap.org/img/wn/" + weatherIcon + ".png";
        Picasso.get().load(imageUrl).into(weatherImageView, new Callback() {
            @Override
            public void onSuccess() {
                // Icon loaded successfully
                Picasso.get().load(imageUrl).into(weatherImageView);
            }

            @Override
            public void onError(Exception e) {
                // Log or handle the error
                Log.e("Picasso Error", e.toString());
                e.printStackTrace();
            }
        });

    }

    private int getDayLabelId(int dayIndex) {
        // Implement the logic to get the ID of the day label based on the index
        // For example, if you have labels with IDs tvDay1, tvDay2, ..., you can return R.id.tvDay1 + dayIndex
        // Replace R.id.tvDay1 with the actual ID of your day labels
        return R.id.tvDay1_1 + dayIndex;
    }

    private int getWeatherImageViewId(int dayIndex) {
        // Implement the logic to get the ID of the weather image view based on the index
        // For example, if you have image views with IDs ivWeather1, ivWeather2, ..., you can return R.id.ivWeather1 + dayIndex
        // Replace R.id.ivWeather1 with the actual ID of your weather image views
        return R.id.ivIcon1_1 + dayIndex;
    }

    private int getTemperatureLabelId(int dayIndex) {
        // Implement the logic to get the ID of the temperature label based on the index
        // For example, if you have labels with IDs tvTemperature1, tvTemperature2, ..., you can return R.id.tvTemperature1 + dayIndex
        // Replace R.id.tvTemperature1 with the actual ID of your temperature labels
        return R.id.tvTempDay1_1 + dayIndex;
    }



    private void handleWeatherResponse(String response) {
        try {
            // Parsing the JSON response
            JSONObject jsonObject = new JSONObject(response);

            // Extracting relevant information
            String cityName = jsonObject.getString("name");
            double temperature = jsonObject.getJSONObject("main").getDouble("temp");
            int humidity = jsonObject.getJSONObject("main").getInt("humidity");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            int cloudiness = jsonObject.getJSONObject("clouds").getInt("all");
            int pressure = jsonObject.getJSONObject("main").getInt("pressure");


            double tempCelsius = Math.round(temperature - 273.15);


            // Updating UI with the extracted information
            tvCityResult.setText(cityName);
            tvTemperatureResult.setText(String.valueOf((int)tempCelsius + " °C"));
            tvHumidityResult.setText(String.valueOf(humidity) + " %");
            tvWindSpeedResult.setText(String.valueOf(windSpeed) + " kmph");
            tvCloudinessResult.setText(String.valueOf(cloudiness) + " %");
            tvPressureResult.setText(String.valueOf(pressure) + " mb");

            try {
                //extracting the description
                if (weatherArray.length() > 0) {
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String description = weatherObject.getString("description");
                    String main = weatherObject.getString("main");

                    tvDescriptionResult.setText(main + ", " + description);

                    if(weatherObject.has("icon")){
                        String iconCode = weatherObject.getString("icon");
                        String iconUrl = imageUrl + iconCode + "@2x.png";

                        Picasso.get().load(iconUrl).resize(350,350).centerInside().into(ivWeather);

                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}