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
import java.io.FileNotFoundException;
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
//import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

        mContext = (Context) getActivity();
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

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //new Handler().postDelayed(new Runnable() {
                //    @Override public void run() {
                ((WeatherForecastActivity) getActivity()).refreshData();
                //swipeLayout.setRefreshing(false);
                //    }
                //}, 5000);
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

            File cacheFile = null;

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

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
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

        if (parseString.equals(null) || parseString.equals("") || parseString.contains("<html>")) {
            return;
        }

        parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1, parseString.length());

        if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {
            return;
        }

        try {
            JSONTokener parser = new JSONTokener(parseString);
            int tempScale = Preferences.getTempScale(context, appWidgetId);

            JSONObject query = (JSONObject) parser.nextValue();
            JSONObject weatherJSON = null;

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
            //String location = Preferences.getLocation(context, appWidgetId);

            int lnLoc = location.length();
            SpannableString spStrLoc = new SpannableString(location);
            spStrLoc.setSpan(new StyleSpan(Typeface.BOLD), 0, lnLoc, 0);

            TextView text = (TextView) view.findViewById(R.id.textViewLocToday);
            text.setText(spStrLoc);

            //setTitle(location);

            long timestamp = weatherJSON.getLong("dt");
            Date time = new Date(timestamp * 1000);

            JSONObject main = null;
            try {
                main = weatherJSON.getJSONObject("main");
            } catch (JSONException e) {
            }
            try {
                double currentTemp = main.getDouble("temp") - 273.15;
                text = (TextView) view.findViewById(R.id.textViewTempToday);

                if (tempScale == 1) {
                    text.setText(String.valueOf((int) (currentTemp * 1.8 + 32)) + "°");
                } else {
                    text.setText(String.valueOf((int) currentTemp) + "°");
                }


            } catch (JSONException e) {
            }

            JSONObject windJSON = null;
            try {
                windJSON = weatherJSON.getJSONObject("wind");
            } catch (JSONException e) {
            }
            try {
                double speed = windJSON.getDouble("speed");
            } catch (JSONException e) {
            }
            try {
                double deg = windJSON.getDouble("deg");
            } catch (JSONException e) {
            }

            try {
                double humidityValue = weatherJSON.getJSONObject("main").getDouble("humidity");
            } catch (JSONException e) {
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

                    text = (TextView) view.findViewById(R.id.textViewDescToday);
                    text.setText(weatherDesc);

                    ImageView image = (ImageView) view.findViewById(R.id.imageViewWeatherToday);

                    WeatherConditions conditions = new WeatherConditions();

                    int icons = Preferences.getWeatherIcons(context, appWidgetId);
                    int resource = R.drawable.tick_weather_04d;
                    WeatherIcon[] imageArr;

                    switch (icons) {
                        case 0:
                            resource = R.drawable.tick_weather_04d;
                            imageArr = conditions.m_ImageArrTick;
                            break;
                        case 1:
                            resource = R.drawable.touch_weather_04d;
                            imageArr = conditions.m_ImageArrTouch;
                            break;
                        case 2:
                            resource = R.drawable.icon_set_weather_04d;
                            imageArr = conditions.m_ImageArrIconSet;
                            break;
                        case 3:
                            resource = R.drawable.weezle_weather_04d;
                            imageArr = conditions.m_ImageArrWeezle;
                            break;
                        case 4:
                            resource = R.drawable.simple_weather_04d;
                            imageArr = conditions.m_ImageArrSimple;
                            break;
                        case 5:
                            resource = R.drawable.novacons_weather_04d;
                            imageArr = conditions.m_ImageArrNovacons;
                            break;
                        case 6:
                            resource = R.drawable.sticker_weather_04d;
                            imageArr = conditions.m_ImageArrSticker;
                            break;
                        case 7:
                            resource = R.drawable.plain_weather_04d;
                            imageArr = conditions.m_ImageArrPlain;
                            break;
                        case 8:
                            resource = R.drawable.flat_weather_04d;
                            imageArr = conditions.m_ImageArrFlat;
                            break;
                        case 9:
                            resource = R.drawable.dvoid_weather_04d;
                            imageArr = conditions.m_ImageArrDvoid;
                            break;
                        case 10:
                            resource = R.drawable.ikonko_weather_04d;
                            imageArr = conditions.m_ImageArrIkonko;
                            break;
                        case 11:
                            resource = R.drawable.smooth_weather_04d;
                            imageArr = conditions.m_ImageArrSmooth;
                            break;
                        case 12:
                            resource = R.drawable.bubble_weather_04d;
                            imageArr = conditions.m_ImageArrBubble;
                            break;
                        case 13:
                            resource = R.drawable.stylish_weather_04d;
                            imageArr = conditions.m_ImageArrStylish;
                            break;
                        case 14:
                            resource = R.drawable.garmahis_weather_04d;
                            imageArr = conditions.m_ImageArrGarmahis;
                            break;
                        case 15:
                            resource = R.drawable.iconbest_weather_04d;
                            imageArr = conditions.m_ImageArrIconBest;
                            break;
                        case 16:
                            resource = R.drawable.cartoon_weather_04d;
                            imageArr = conditions.m_ImageArrCartoon;
                            break;
                        default:
                            resource = R.drawable.tick_weather_04d;
                            imageArr = conditions.m_ImageArrTick;
                            break;
                    }

                    image.setImageResource(resource);

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

                        if (calendarForDate.before(civilSunriseCalendarForDate) || calendarForDate.after(civilSunsetCalendarForDate)) {
                            bDay = false;
                        } else {
                            bDay = true;
                        }
                    }

                    for (int j = 0; j < imageArr.length; j++) {
                        if (iconName.equals(imageArr[j].iconName) || iconNameAlt.equals(imageArr[j].iconName)) {
                            if (imageArr[j].bDay != bDay) {
                                image.setImageResource(imageArr[j].altIconId);
                            } else {
                                image.setImageResource(imageArr[j].iconId);
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

            File cacheFile = null;

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

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
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

        if (parseString.equals(null) || parseString.equals("") || parseString.contains("<html>")) {
            return;
        }

        parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1, parseString.length());

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

            for (int i = 1; i < list.length(); i++) {
                if (i == 6) {
                    break;
                }

                JSONObject weatherJSON = list.getJSONObject(i);
                long timestamp = weatherJSON.getLong("dt");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp * 1000);

                TextView textDate = null;
                TextView textTempHigh = null;
                TextView textTempLow = null;
                TextView textMain = null;
                TextView textDesc = null;
                ImageView image = null;

                WeatherConditions conditions = new WeatherConditions();

                int icons = Preferences.getWeatherIcons(context, appWidgetId);
                int iconID = R.drawable.tick_weather_04d;
                WeatherIcon[] imageArr;

                switch (icons) {
                    case 0:
                        iconID = R.drawable.tick_weather_04d;
                        imageArr = conditions.m_ImageArrTick;
                        break;
                    case 1:
                        iconID = R.drawable.touch_weather_04d;
                        imageArr = conditions.m_ImageArrTouch;
                        break;
                    case 2:
                        iconID = R.drawable.icon_set_weather_04d;
                        imageArr = conditions.m_ImageArrIconSet;
                        break;
                    case 3:
                        iconID = R.drawable.weezle_weather_04d;
                        imageArr = conditions.m_ImageArrWeezle;
                        break;
                    case 4:
                        iconID = R.drawable.simple_weather_04d;
                        imageArr = conditions.m_ImageArrSimple;
                        break;
                    case 5:
                        iconID = R.drawable.novacons_weather_04d;
                        imageArr = conditions.m_ImageArrNovacons;
                        break;
                    case 6:
                        iconID = R.drawable.sticker_weather_04d;
                        imageArr = conditions.m_ImageArrSticker;
                        break;
                    case 7:
                        iconID = R.drawable.plain_weather_04d;
                        imageArr = conditions.m_ImageArrPlain;
                        break;
                    case 8:
                        iconID = R.drawable.flat_weather_04d;
                        imageArr = conditions.m_ImageArrFlat;
                        break;
                    case 9:
                        iconID = R.drawable.dvoid_weather_04d;
                        imageArr = conditions.m_ImageArrDvoid;
                        break;
                    case 10:
                        iconID = R.drawable.ikonko_weather_04d;
                        imageArr = conditions.m_ImageArrIkonko;
                        break;
                    case 11:
                        iconID = R.drawable.smooth_weather_04d;
                        imageArr = conditions.m_ImageArrSmooth;
                        break;
                    case 12:
                        iconID = R.drawable.bubble_weather_04d;
                        imageArr = conditions.m_ImageArrBubble;
                        break;
                    case 13:
                        iconID = R.drawable.stylish_weather_04d;
                        imageArr = conditions.m_ImageArrStylish;
                        break;
                    case 14:
                        iconID = R.drawable.garmahis_weather_04d;
                        imageArr = conditions.m_ImageArrGarmahis;
                        break;
                    case 15:
                        iconID = R.drawable.iconbest_weather_04d;
                        imageArr = conditions.m_ImageArrIconBest;
                        break;
                    case 16:
                        iconID = R.drawable.cartoon_weather_04d;
                        imageArr = conditions.m_ImageArrCartoon;
                        break;
                    default:
                        iconID = R.drawable.tick_weather_04d;
                        imageArr = conditions.m_ImageArrTick;
                        break;
                }

                String sTempHigh = "";
                String sTempLow = "";
                String weatherMain = "";
                String weatherDesc = "";
                String date = "";

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                date = String.format(sdf.format(calendar.getTime()));

                JSONObject tempJSON = null;
                try {
                    tempJSON = weatherJSON.getJSONObject("temp");
                } catch (JSONException e) {
                }
                try {
                    double temp = tempJSON.getDouble("max") - 273.15;

                    if (tempScale == 1) {
                        sTempHigh = "H: " + String.valueOf((int) (temp * 1.8 + 32)) + "°";
                    } else {
                        sTempHigh = "H: " + String.valueOf((int) temp) + "°";
                    }

                    temp = tempJSON.getDouble("min") - 273.15;

                    if (tempScale == 1) {
                        sTempLow = "L: " + String.valueOf((int) (temp * 1.8 + 32)) + "°";
                    } else {
                        sTempLow = "L: " + String.valueOf((int) temp) + "°";
                    }
                } catch (JSONException e) {
                }

                try {
                    JSONArray weathers = weatherJSON.getJSONArray("weather");

                    JSONObject weather = weathers.getJSONObject(0);
                    int weatherId = weather.getInt("id");
                    weatherMain = weather.getString("main");
                    weatherDesc = weather.getString("description");
                    weatherDesc = weatherDesc.substring(0, 1).toUpperCase() + weatherDesc.substring(1);
                    String iconName = weather.getString("icon");
                    String iconNameAlt = iconName + "d";

                    for (int k = 0; k < imageArr.length; k++) {
                        if (iconName.equals(imageArr[k].iconName) || iconNameAlt.equals(imageArr[k].iconName)) {
                            iconID = imageArr[k].iconId;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    //no weather type
                }

                switch (i) {
                    case 1:
                        textDate = (TextView) view.findViewById(R.id.textViewDate1);
                        textTempHigh = (TextView) view.findViewById(R.id.textViewTempHigh1);
                        textTempLow = (TextView) view.findViewById(R.id.textViewTempLow1);
                        textDesc = (TextView) view.findViewById(R.id.textViewDesc1);
                        image = (ImageView) view.findViewById(R.id.imageViewWeather1);
                        break;
                    case 2:
                        textDate = (TextView) view.findViewById(R.id.textViewDate2);
                        textTempHigh = (TextView) view.findViewById(R.id.textViewTempHigh2);
                        textTempLow = (TextView) view.findViewById(R.id.textViewTempLow2);
                        textDesc = (TextView) view.findViewById(R.id.textViewDesc2);
                        image = (ImageView) view.findViewById(R.id.imageViewWeather2);
                        break;
                    case 3:
                        textDate = (TextView) view.findViewById(R.id.textViewDate3);
                        textTempHigh = (TextView) view.findViewById(R.id.textViewTempHigh3);
                        textTempLow = (TextView) view.findViewById(R.id.textViewTempLow3);
                        textDesc = (TextView) view.findViewById(R.id.textViewDesc3);
                        image = (ImageView) view.findViewById(R.id.imageViewWeather3);
                        break;
                    case 4:
                        textDate = (TextView) view.findViewById(R.id.textViewDate4);
                        textTempHigh = (TextView) view.findViewById(R.id.textViewTempHigh4);
                        textTempLow = (TextView) view.findViewById(R.id.textViewTempLow4);
                        textDesc = (TextView) view.findViewById(R.id.textViewDesc4);
                        image = (ImageView) view.findViewById(R.id.imageViewWeather4);
                        break;
                    case 5:
                        textDate = (TextView) view.findViewById(R.id.textViewDate5);
                        textTempHigh = (TextView) view.findViewById(R.id.textViewTempHigh5);
                        textTempLow = (TextView) view.findViewById(R.id.textViewTempLow5);
                        textDesc = (TextView) view.findViewById(R.id.textViewDesc5);
                        image = (ImageView) view.findViewById(R.id.imageViewWeather5);
                        break;
                    default:
                        break;
                }

                textDate.setText(date);
                textTempHigh.setText(sTempHigh);
                textTempLow.setText(sTempLow);
                textDesc.setText(weatherDesc);
                image.setImageResource(iconID);
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

    public static long daysBetween(final Calendar startDate, final Calendar endDate) {
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
        long endInstant = endDate.getTimeInMillis();
        int presumedDays = (int) ((endInstant - startDate.getTimeInMillis()) / MILLIS_IN_DAY);
        Calendar cursor = (Calendar) startDate.clone();
        cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
        long instant = cursor.getTimeInMillis();
        if (instant == endInstant) {
            return presumedDays;
        }
        final int step = instant < endInstant ? 1 : -1;
        do {
            cursor.add(Calendar.DAY_OF_MONTH, step);
            presumedDays += step;
        }
        while (cursor.getTimeInMillis() != endInstant);
        return presumedDays;
    }
}
