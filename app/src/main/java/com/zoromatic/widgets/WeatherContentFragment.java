/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zoromatic.widgets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.SunriseSunsetLocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_APP_WIDGET_ID = "app_widget_id";
    private static final String KEY_LOCATION_ID = "location_id";

    Context mContext = null;
    private static String LOG_TAG = "WeatherContentFragment";
    private String mTitle = "";
    SwipeRefreshLayout swipeLayout;

    public static WeatherContentFragment newInstance(CharSequence title, int indicatorColor,
                                                     int appWidgetId, long locId) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_APP_WIDGET_ID, appWidgetId);
        bundle.putLong(KEY_LOCATION_ID, locId);

        WeatherContentFragment fragment = new WeatherContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("InlinedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weatherforecast_page, container, false);

        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((WeatherForecastActivity) getActivity()).refreshData();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            swipeLayout.setColorSchemeColors(Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.RED);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            setTitle((String) args.getCharSequence(KEY_TITLE));
            readCachedData(mContext, args.getInt(KEY_APP_WIDGET_ID));
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public SwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    void readCachedData(Context context, int appWidgetId) {
        readCachedWeatherData(context, appWidgetId);
        readCachedForecastData(context, appWidgetId);
    }

    void readCachedWeatherData(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "WeatherContentFragment readCachedWeatherData appWidgetId: " + appWidgetId);

        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        if (!showWeather) {
            return;
        }

        try {
            File parentDirectory = new File(context.getFilesDir().getAbsolutePath());

            if (!parentDirectory.exists()) {
                Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                if (!parentDirectory.mkdirs()) {
                    Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                }
            }

            Bundle args = getArguments();
            long locId = -1;

            if (args != null) {
                locId = args.getLong(KEY_LOCATION_ID);
            }

            File cacheFile;

            if (locId >= 0) {
                cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);

                if (!cacheFile.exists()) {
                    return;
                }
            } else {
                cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

                if (!cacheFile.exists()) {
                    return;
                }
            }

            BufferedReader cacheReader = new BufferedReader(new FileReader(cacheFile));
            char[] buf = new char[1024];
            StringBuilder result = new StringBuilder();
            int read = cacheReader.read(buf);

            while (read >= 0) {
                result.append(buf, 0, read);
                read = cacheReader.read(buf);
            }

            cacheReader.close();

            if (!result.toString().contains("<html>")) {
                parseWeatherData(context, appWidgetId, result.toString(), true, false);
            }

        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    void parseWeatherData(Context context, int appWidgetId, String parseString, boolean updateFromCache, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WeatherContentFragment parseWeatherData appWidgetId: " + appWidgetId + " cache: " + updateFromCache + " scheduled: " + scheduledUpdate);

        View view = getView();

        if (view == null) {
            return;
        }

        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        if (!showWeather) {
            return;
        }

        if (parseString.isEmpty() || parseString.contains("<html>")) {
            return;
        }

        parseString = parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1);

        if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {
            return;
        }

        try {
            JSONTokener parser = new JSONTokener(parseString);
            int tempScale = Preferences.getTempScale(context, appWidgetId);

            JSONObject query = (JSONObject) parser.nextValue();
            JSONObject weatherJSON;

            if (query.has("list")) {

                JSONArray list = query.getJSONArray("list");

                if (list.length() == 0) {
                    return;
                }

                weatherJSON = list.getJSONObject(0);
            } else {
                weatherJSON = query;
            }

            int cityId = weatherJSON.getInt("id");
            String location = weatherJSON.getString("name");

            int lnLoc = location.length();
            SpannableString spStrLoc = new SpannableString(location);
            spStrLoc.setSpan(new StyleSpan(Typeface.BOLD), 0, lnLoc, 0);

            TextView text = view.findViewById(R.id.textViewLocToday);
            text.setText(spStrLoc);

            long timestamp = weatherJSON.getLong("dt");
            Date time = new Date(timestamp * 1000);

            JSONObject main = null;

            try {
                main = weatherJSON.getJSONObject("main");
            } catch (JSONException ignored) {
            }

            try {
                double currentTemp = (main != null ? main.getDouble("temp") - 273.15 : 0);
                text = view.findViewById(R.id.textViewTempToday);

                if (tempScale == 1) {
                    text.setText((int) (currentTemp * 1.8 + 32) + "°");
                } else {
                    text.setText((int) currentTemp + "°");
                }

            } catch (JSONException ignored) {
            }

            JSONObject windJSON = null;

            try {
                windJSON = weatherJSON.getJSONObject("wind");
            } catch (JSONException ignored) {
            }

            try {
                double speed = windJSON != null ? windJSON.getDouble("speed") : 0;
            } catch (JSONException ignored) {
            }

            try {
                double deg = windJSON != null ? windJSON.getDouble("deg") : 0;
            } catch (JSONException ignored) {
            }

            try {
                double humidityValue = weatherJSON.getJSONObject("main").getDouble("humidity");
            } catch (JSONException ignored) {
            }

            try {
                JSONArray weathers = weatherJSON.getJSONArray("weather");
                for (int i = 0; i < weathers.length(); i++) {
                    JSONObject weather = weathers.getJSONObject(i);
                    int weatherId = weather.getInt("id");
                    String weatherMain = weather.getString("main");
                    String weatherDesc = weather.getString("description");
                    weatherDesc = weatherDesc.substring(0, 1).toUpperCase() + weatherDesc.substring(1);
                    String iconName = weather.getString("icon");
                    String iconNameAlt = iconName + "d";

                    text = view.findViewById(R.id.textViewDescToday);
                    text.setText(weatherDesc);

                    ImageView image = view.findViewById(R.id.imageViewWeatherToday);

                    int icons = Preferences.getWeatherIcons(context, appWidgetId);
                    WeatherIcon[] imageArr = WeatherConditions.weatherIcons[icons];
                    int iconId = imageArr[0].iconId;

                    image.setImageResource(iconId);

                    float lat = Preferences.getLocationLat(context, appWidgetId);
                    float lon = Preferences.getLocationLon(context, appWidgetId);
                    boolean bDay = true;

                    if (!Float.isNaN(lat) && !Float.isNaN(lon)) {
                        SunriseSunsetCalculator calc;
                        SunriseSunsetLocation loc = new SunriseSunsetLocation(String.valueOf(lat), String.valueOf(lon));
                        calc = new SunriseSunsetCalculator(loc, TimeZone.getDefault());
                        Calendar calendarForDate = Calendar.getInstance();
                        Calendar civilSunriseCalendarForDate = calc.getCivilSunriseCalendarForDate(calendarForDate);
                        Calendar civilSunsetCalendarForDate = calc.getCivilSunsetCalendarForDate(calendarForDate);

                        bDay = !(calendarForDate.before(civilSunriseCalendarForDate) || calendarForDate.after(civilSunsetCalendarForDate));
                    }

                    for (WeatherIcon anImageArr : imageArr) {
                        if (iconName.equals(anImageArr.iconName) || iconNameAlt.equals(anImageArr.iconName)) {
                            if (anImageArr.bDay != bDay) {
                                image.setImageResource(anImageArr.altIconId);
                            } else {
                                image.setImageResource(anImageArr.iconId);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                //no weather type
            }

            if (!updateFromCache) {
                Preferences.setLastRefresh(context, appWidgetId, System.currentTimeMillis());

                if (scheduledUpdate) {
                    Preferences.setWeatherSuccess(context, appWidgetId, true);
                }
            }

            long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

            text = (TextView) view.findViewById(R.id.textViewLast);

            if (lastRefresh > 0) {
                boolean bShow24Hrs = Preferences.getShow24Hrs(context, appWidgetId);
                int iDateFormatItem = Preferences.getDateFormatItem(context, appWidgetId);
                Date resultdate = new Date(lastRefresh);

                String currentTime;

                if (bShow24Hrs) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    currentTime = sdf.format(resultdate);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    currentTime = sdf.format(resultdate);
                }

                String currentDate = "";
                String[] mTestArray = getResources().getStringArray(R.array.dateFormat);

                SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
                currentDate = sdf.format(resultdate);

                if (text != null) {
                    text.setText(currentDate + ", " + currentTime);
                }
            } else {
                if (text != null) {
                    text.setText(getResources().getString(R.string.lastrefreshnever));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void readCachedForecastData(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "WeatherContentFragment readCachedForecastData appWidgetId: " + appWidgetId);

        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        if (!showWeather) {
            return;
        }

        try {
            File parentDirectory = new File(context.getFilesDir().getAbsolutePath());

            if (!parentDirectory.exists()) {
                Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                if (!parentDirectory.mkdirs()) {
                    Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                }
            }

            Bundle args = getArguments();
            long locId = -1;

            if (args != null) {
                locId = args.getLong(KEY_LOCATION_ID);
            }

            File cacheFile;

            if (locId >= 0) {
                cacheFile = new File(parentDirectory, "forecast_cache_loc_" + locId);

                if (!cacheFile.exists()) {
                    return;
                }
            } else {
                cacheFile = new File(parentDirectory, "forecast_cache_" + appWidgetId);

                if (!cacheFile.exists()) {
                    return;
                }
            }

            BufferedReader cacheReader = new BufferedReader(new FileReader(cacheFile));
            char[] buf = new char[1024];
            StringBuilder result = new StringBuilder();
            int read = cacheReader.read(buf);

            while (read >= 0) {
                result.append(buf, 0, read);
                read = cacheReader.read(buf);
            }

            cacheReader.close();

            if (!result.toString().contains("<html>")) {
                parseForecastData(context, appWidgetId, result.toString(), true, false);
            }

        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("unused")
    void parseForecastData(Context context, int appWidgetId, String parseString, boolean updateFromCache, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WeatherContentFragment parseForecastData appWidgetId: " + appWidgetId + " cache: " + updateFromCache + " scheduled: " + scheduledUpdate);

        View view = getView();

        if (view == null) {
            return;
        }

        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        if (!showWeather) {
            return;
        }

        if (parseString.isEmpty() || parseString.contains("<html>")) {
            return;
        }

        parseString = parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1);

        if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {
            return;
        }

        try {
            JSONTokener parser = new JSONTokener(parseString);
            int tempScale = Preferences.getTempScale(context, appWidgetId);

            JSONObject query = (JSONObject) parser.nextValue();
            JSONArray list = query.getJSONArray("list");

            if (list.length() == 0) {
                return;
            }

            for (int i = 0; i < list.length(); i++) {
                if (i == 6) {
                    break;
                }

                JSONObject weatherJSON = list.getJSONObject(i);
                long timestamp = weatherJSON.getLong("dt");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp * 1000);

                TextView textDay = null;
                TextView textDate = null;
                TextView textTempHigh = null;
                TextView textTempLow = null;
                TextView textMain = null;
                TextView textDesc = null;
                ImageView image = null;

                int icons = Preferences.getWeatherIcons(context, appWidgetId);
                WeatherIcon[] imageArr = WeatherConditions.weatherIcons[icons];
                int iconId = imageArr[0].iconId;

                String sTempHigh = "";
                String sTempLow = "";
                String weatherDesc = "";
                String date;
                String day;

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                date = sdf.format(calendar.getTime());

                sdf = new SimpleDateFormat("EEE");
                day = sdf.format(calendar.getTime());

                JSONObject tempJSON = null;

                try {
                    tempJSON = weatherJSON.getJSONObject("temp");
                } catch (JSONException ignored) {
                }

                if (i == 0) {// today
                    double minTemp = (tempJSON != null ? tempJSON.getDouble("min") - 273.15 : 0);
                    double maxTemp = (tempJSON != null ? tempJSON.getDouble("max") - 273.15 : 0);

                    TextView textLow = view.findViewById(R.id.textViewWeatherTodayLow);
                    TextView textHigh = view.findViewById(R.id.textViewWeatherTodayHigh);

                    if (tempScale == 1) {
                        textLow.setText(getString(R.string.temp_low) + (int) (minTemp * 1.8 + 32) + "°");
                        textHigh.setText(getString(R.string.temp_high) + (int) (maxTemp * 1.8 + 32) + "°");
                    } else {
                        textLow.setText(getString(R.string.temp_low) + (int) minTemp + "°");

                        textHigh.setText(getString(R.string.temp_high) + (int) maxTemp + "°");
                    }

                    continue;
                }

                try {
                    double temp = (tempJSON != null ? tempJSON.getDouble("max") - 273.15 : 0);

                    if (tempScale == 1) {
                        sTempHigh = "H: " + (int) (temp * 1.8 + 32) + "°";
                    } else {
                        sTempHigh = "H: " + (int) temp + "°";
                    }

                    temp = (tempJSON != null ? tempJSON.getDouble("min") - 273.15 : 0);

                    if (tempScale == 1) {
                        sTempLow = "L: " + (int) (temp * 1.8 + 32) + "°";
                    } else {
                        sTempLow = "L: " + (int) temp + "°";
                    }
                } catch (JSONException ignored) {
                }

                try {
                    JSONArray weathers = weatherJSON.getJSONArray("weather");

                    JSONObject weather = weathers.getJSONObject(0);
                    int weatherId = weather.getInt("id");

                    weatherDesc = weather.getString("description");
                    weatherDesc = weatherDesc.substring(0, 1).toUpperCase() + weatherDesc.substring(1);
                    String iconName = weather.getString("icon");
                    String iconNameAlt = iconName + "d";

                    for (WeatherIcon anImageArr : imageArr) {
                        if (iconName.equals(anImageArr.iconName) || iconNameAlt.equals(anImageArr.iconName)) {
                            iconId = anImageArr.iconId;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    //no weather type
                }

                switch (i) {
                    case 1:
                        textDay = view.findViewById(R.id.textViewDay1);
                        textDate = view.findViewById(R.id.textViewDate1);
                        textTempHigh = view.findViewById(R.id.textViewTempHigh1);
                        textTempLow = view.findViewById(R.id.textViewTempLow1);
                        textDesc = view.findViewById(R.id.textViewDesc1);
                        image = view.findViewById(R.id.imageViewWeather1);
                        break;
                    case 2:
                        textDay = view.findViewById(R.id.textViewDay2);
                        textDate = view.findViewById(R.id.textViewDate2);
                        textTempHigh = view.findViewById(R.id.textViewTempHigh2);
                        textTempLow = view.findViewById(R.id.textViewTempLow2);
                        textDesc = view.findViewById(R.id.textViewDesc2);
                        image = view.findViewById(R.id.imageViewWeather2);
                        break;
                    case 3:
                        textDay = view.findViewById(R.id.textViewDay3);
                        textDate = view.findViewById(R.id.textViewDate3);
                        textTempHigh = view.findViewById(R.id.textViewTempHigh3);
                        textTempLow = view.findViewById(R.id.textViewTempLow3);
                        textDesc = view.findViewById(R.id.textViewDesc3);
                        image = view.findViewById(R.id.imageViewWeather3);
                        break;
                    case 4:
                        textDay = view.findViewById(R.id.textViewDay4);
                        textDate = view.findViewById(R.id.textViewDate4);
                        textTempHigh = view.findViewById(R.id.textViewTempHigh4);
                        textTempLow = view.findViewById(R.id.textViewTempLow4);
                        textDesc = view.findViewById(R.id.textViewDesc4);
                        image = view.findViewById(R.id.imageViewWeather4);
                        break;
                    case 5:
                        textDay = view.findViewById(R.id.textViewDay5);
                        textDate = view.findViewById(R.id.textViewDate5);
                        textTempHigh = view.findViewById(R.id.textViewTempHigh5);
                        textTempLow = view.findViewById(R.id.textViewTempLow5);
                        textDesc = view.findViewById(R.id.textViewDesc5);
                        image = view.findViewById(R.id.imageViewWeather5);
                        break;
                    default:
                        break;
                }

                if (textDay != null) {
                    textDay.setText(day);
                }
                if (textDate != null) {
                    textDate.setText(date);
                }
                if (textTempHigh != null) {
                    textTempHigh.setText(sTempHigh);
                }
                if (textTempLow != null) {
                    textTempLow.setText(sTempLow);
                }
                if (textDesc != null) {
                    textDesc.setText(weatherDesc);
                }
                if (image != null) {
                    image.setImageResource(iconId);
                }
            }

            if (!updateFromCache) {
                Preferences.setLastRefresh(context, appWidgetId, System.currentTimeMillis());

                if (scheduledUpdate) {
                    Preferences.setForecastSuccess(context, appWidgetId, true);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
