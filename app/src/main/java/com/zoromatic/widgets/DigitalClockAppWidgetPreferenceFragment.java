package com.zoromatic.widgets;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.margaritov.preference.colorpicker.ColorPickerPreference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint({"NewApi", "SimpleDateFormat"})
public class DigitalClockAppWidgetPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    public static final int RESULT_OK = -1;
    private static final int REQUEST_LOCATION = 0;
    private static final int REQUEST_THEME = 1;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Context context = null;

    // font and icons entries and values before sorting
    private CharSequence[] mClockFontEntries = null;
    private CharSequence[] mClockFontEntryValues = null;
    private CharSequence[] mDateFontEntries = null;
    private CharSequence[] mDateFontEntryValues = null;
    private CharSequence[] mWeatherFontEntries = null;
    private CharSequence[] mWeatherFontEntryValues = null;
    private CharSequence[] mWeatherIconEntries = null;
    private CharSequence[] mWeatherIconEntryValues = null;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        context = getActivity();

        if (context != null) {
            String lang = Preferences.getLanguageOptions(context);

            if (lang.equals("")) {
                String langDef = Locale.getDefault().getLanguage();

                if (!langDef.equals("")) {
                    lang = langDef;
                } else {
                    lang = "en";
                }

                Preferences.setLanguageOptions(context, lang);
            }

            // Change locale settings in the application
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(lang.toLowerCase());
            res.updateConfiguration(conf, dm);
        }
    }

    /*
        Sorting is done only for presentation. Original IDs are saved and read
        from SharedPreferences.
     */
    private void sortListPreferenceByEntries(ImageListPreference listPreference) {
        if (listPreference == null)
            return;

        CharSequence[] entries = listPreference.getEntries();
        CharSequence[] entryValues = listPreference.getEntryValues();
        String[] fontPaths = listPreference.getFontPaths();
        int[] imageIds = listPreference.getImageIds();

        CharSequence[] sortedEntries = new CharSequence[entries.length];
        CharSequence[] sortedEntryValues = new CharSequence[entries.length];
        String[] sortedFontPaths = new String[entries.length];
        int[] sortedImageIds = new int[entries.length];

        for (int i=0; i<entries.length; i++) {
            sortedEntries[i] = entries[i];
            sortedEntryValues[i] = entryValues[i];

            if (fontPaths != null)
                sortedFontPaths[i] = fontPaths[i];

            if (imageIds != null)
                sortedImageIds[i] = imageIds[i];
        }

        Arrays.sort(sortedEntries);
        listPreference.setEntries(sortedEntries);

        for (int i=0; i<entries.length; i++) {
            for (int j=0; j<sortedEntries.length; j++) {
                if (entries[i].equals(sortedEntries[j])) {
                    sortedEntryValues[j] = entryValues[i];

                    if (fontPaths != null) {
                        sortedFontPaths[j] = fontPaths[i];
                    }

                    if (imageIds != null) {
                        sortedImageIds[j] = imageIds[i];
                    }

                    break;
                }
            }
        }

        listPreference.setEntries(sortedEntries);
        listPreference.setEntryValues(sortedEntryValues);

        if (imageIds != null)
            listPreference.setImageIds(sortedImageIds);

        if (fontPaths != null)
            listPreference.setFontPaths(sortedFontPaths);
    }

    private void setPreferences() {
        PreferenceManager localPrefs = getPreferenceManager();
        localPrefs.setSharedPreferencesName(Preferences.PREF_NAME);

        Bundle bundle = getArguments();

        if (bundle != null) {

            mAppWidgetId = bundle.getInt(DigitalClockAppWidgetPreferenceActivity.APPWIDGETID, AppWidgetManager.INVALID_APPWIDGET_ID);

            String category = bundle.getString("category");

            if (category != null) {
                if (category.equals(getString(R.string.category_general))) {
                    addPreferencesFromResource(R.xml.digitalclockwidget_prefs_general);
                } else if (category.equals(getString(R.string.category_weather))) {
                    addPreferencesFromResource(R.xml.digitalclockwidget_prefs_weather);
                } else if (category.equals(getString(R.string.category_look))) {
                    addPreferencesFromResource(R.xml.digitalclockwidget_prefs_look);
                } else if (category.equals(getString(R.string.category_theme))) {
                    addPreferencesFromResource(R.xml.zoromaticwidgets_prefs_theme);
                } else {
                    addPreferencesFromResource(R.xml.digitalclockwidget_prefs);
                }
            } else {
                addPreferencesFromResource(R.xml.digitalclockwidget_prefs);
            }
        } else {
            addPreferencesFromResource(R.xml.digitalclockwidget_prefs);
        }

        Preference themePreference = (Preference) findPreference(getString(R.string.category_theme));

        if (themePreference != null)
            themePreference.setSummary("");

        ListPreference mainTheme = (ListPreference) findPreference(Preferences.PREF_MAIN_THEME);

        if (mainTheme != null) {

            String theme = Preferences.getMainTheme(context);

            if (theme.equals("") || mainTheme.findIndexOfValue(theme) < 0) {
                theme = "dark";
            }

            mainTheme.setValueIndex(mainTheme.findIndexOfValue(theme));
            mainTheme.setSummary(mainTheme.getEntries()[mainTheme.findIndexOfValue(theme)]);
        }

        ListPreference mainColorScheme = (ListPreference) findPreference(Preferences.PREF_MAIN_COLOR_SCHEME);

        if (mainColorScheme != null) {
            mainColorScheme.setValueIndex(Preferences.getMainColorScheme(context));
            mainColorScheme.setSummary(mainColorScheme.getEntries()[Preferences.getMainColorScheme(context)]);
        }

        ColorPickerPreference clockColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_CLOCK_COLOR_PICKER_KEY);

        if (clockColorPicker != null) {
            int clockColorItemOld = Preferences.getClockColorItem(context, mAppWidgetId);
            int clockColorOld;

            if (clockColorItemOld >= 0) {
                switch (clockColorItemOld) {
                    case 0:
                        clockColorOld = Color.BLACK;
                        break;
                    case 1:
                        clockColorOld = Color.DKGRAY;
                        break;
                    case 2:
                        clockColorOld = Color.GRAY;
                        break;
                    case 3:
                        clockColorOld = Color.LTGRAY;
                        break;
                    case 4:
                        clockColorOld = Color.WHITE;
                        break;
                    case 5:
                        clockColorOld = Color.RED;
                        break;
                    case 6:
                        clockColorOld = Color.GREEN;
                        break;
                    case 7:
                        clockColorOld = Color.BLUE;
                        break;
                    case 8:
                        clockColorOld = Color.YELLOW;
                        break;
                    case 9:
                        clockColorOld = Color.CYAN;
                        break;
                    case 10:
                        clockColorOld = Color.MAGENTA;
                        break;
                    default:
                        clockColorOld = Color.WHITE;
                        break;
                }

                clockColorPicker.onColorChanged(clockColorOld);
            } else {
                clockColorPicker.onColorChanged(Preferences.getClockColor(context, mAppWidgetId));
            }
        }

        ColorPickerPreference dateColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_DATE_COLOR_PICKER_KEY);

        if (dateColorPicker != null) {
            int dateColorItemOld = Preferences.getDateColorItem(context, mAppWidgetId);
            int dateColorOld;

            if (dateColorItemOld >= 0) {
                switch (dateColorItemOld) {
                    case 0:
                        dateColorOld = Color.BLACK;
                        break;
                    case 1:
                        dateColorOld = Color.DKGRAY;
                        break;
                    case 2:
                        dateColorOld = Color.GRAY;
                        break;
                    case 3:
                        dateColorOld = Color.LTGRAY;
                        break;
                    case 4:
                        dateColorOld = Color.WHITE;
                        break;
                    case 5:
                        dateColorOld = Color.RED;
                        break;
                    case 6:
                        dateColorOld = Color.GREEN;
                        break;
                    case 7:
                        dateColorOld = Color.BLUE;
                        break;
                    case 8:
                        dateColorOld = Color.YELLOW;
                        break;
                    case 9:
                        dateColorOld = Color.CYAN;
                        break;
                    case 10:
                        dateColorOld = Color.MAGENTA;
                        break;
                    default:
                        dateColorOld = Color.WHITE;
                        break;
                }

                dateColorPicker.onColorChanged(dateColorOld);
            } else {
                dateColorPicker.onColorChanged(Preferences.getDateColor(context, mAppWidgetId));
            }
        }

        ColorPickerPreference weatherColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_WEATHER_COLOR_PICKER_KEY);

        if (weatherColorPicker != null) {
            int weatherColorItemOld = Preferences.getWeatherColorItem(context, mAppWidgetId);
            int weatherColorOld;

            if (weatherColorItemOld >= 0) {
                switch (weatherColorItemOld) {
                    case 0:
                        weatherColorOld = Color.BLACK;
                        break;
                    case 1:
                        weatherColorOld = Color.DKGRAY;
                        break;
                    case 2:
                        weatherColorOld = Color.GRAY;
                        break;
                    case 3:
                        weatherColorOld = Color.LTGRAY;
                        break;
                    case 4:
                        weatherColorOld = Color.WHITE;
                        break;
                    case 5:
                        weatherColorOld = Color.RED;
                        break;
                    case 6:
                        weatherColorOld = Color.GREEN;
                        break;
                    case 7:
                        weatherColorOld = Color.BLUE;
                        break;
                    case 8:
                        weatherColorOld = Color.YELLOW;
                        break;
                    case 9:
                        weatherColorOld = Color.CYAN;
                        break;
                    case 10:
                        weatherColorOld = Color.MAGENTA;
                        break;
                    default:
                        weatherColorOld = Color.WHITE;
                        break;
                }

                weatherColorPicker.onColorChanged(weatherColorOld);
            } else {
                weatherColorPicker.onColorChanged(Preferences.getWeatherColor(context, mAppWidgetId));
            }
        }

        ColorPickerPreference widgetColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_WIDGET_COLOR_PICKER_KEY);

        if (widgetColorPicker != null) {
            int widgetColorItemOld = Preferences.getWidgetColorItem(context, mAppWidgetId);
            int widgetColorOld;

            if (widgetColorItemOld >= 0) {
                switch (widgetColorItemOld) {
                    case 0:
                        widgetColorOld = Color.BLACK;
                        break;
                    case 1:
                        widgetColorOld = Color.DKGRAY;
                        break;
                    case 2:
                        widgetColorOld = Color.GRAY;
                        break;
                    case 3:
                        widgetColorOld = Color.LTGRAY;
                        break;
                    case 4:
                        widgetColorOld = Color.WHITE;
                        break;
                    case 5:
                        widgetColorOld = Color.RED;
                        break;
                    case 6:
                        widgetColorOld = Color.GREEN;
                        break;
                    case 7:
                        widgetColorOld = Color.BLUE;
                        break;
                    case 8:
                        widgetColorOld = Color.YELLOW;
                        break;
                    case 9:
                        widgetColorOld = Color.CYAN;
                        break;
                    case 10:
                        widgetColorOld = Color.MAGENTA;
                        break;
                    default:
                        widgetColorOld = Color.BLACK;
                        break;
                }

                widgetColorPicker.onColorChanged(widgetColorOld);
            } else {
                widgetColorPicker.onColorChanged(Preferences.getWidgetColor(context, mAppWidgetId));
            }
        }

        CheckBoxPreference show24hrs = (CheckBoxPreference) findPreference(Preferences.PREF_24HRS_KEY);

        if (show24hrs != null) {
            show24hrs.setChecked(Preferences.getShow24Hrs(context, mAppWidgetId));
        }

        ImageListPreference clockFont = (ImageListPreference) findPreference(Preferences.PREF_FONT_KEY);

        if (clockFont != null) {
            mClockFontEntries = clockFont.getEntries();
            mClockFontEntryValues = clockFont.getEntryValues();

            sortListPreferenceByEntries(clockFont);

            CharSequence[] sortedEntryValues = clockFont.getEntryValues();
            int valueIndex = -1;
            int fontItem = Preferences.getFontItem(context, mAppWidgetId);

            for (int j=0; j<sortedEntryValues.length; j++) {
                int entryValue = Integer.parseInt(sortedEntryValues[j].toString());

                if (fontItem == entryValue) {
                    valueIndex = j;
                    break;
                }
            }

            clockFont.setValueIndex(valueIndex);
            String summaryFull = (String) mClockFontEntries[Preferences.getFontItem(context, mAppWidgetId)];
            String summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
            clockFont.setSummary(summary);
        }

        CheckBoxPreference boldText = (CheckBoxPreference) findPreference(Preferences.PREF_BOLD_TEXT_KEY);

        if (boldText != null) {
            boolean bBoldText = Preferences.getBoldText(context, mAppWidgetId);
            boldText.setChecked(bBoldText);

            String bold;

            if (bBoldText) {
                bold = getResources().getString(R.string.boldtext);
            } else {
                bold = getResources().getString(R.string.normaltext);
            }

            boldText.setSummary(bold);
        }

        ListPreference clockSkin = (ListPreference) findPreference(Preferences.PREF_CLOCK_SKIN);

        if (clockSkin != null) {
            clockSkin.setValueIndex(Preferences.getClockSkin(context, mAppWidgetId));
            clockSkin.setSummary(clockSkin.getEntries()[Preferences.getClockSkin(context, mAppWidgetId)]);
        }

        boolean bShowDate = Preferences.getShowDate(context, mAppWidgetId);

        CheckBoxPreference showDate = (CheckBoxPreference) findPreference(Preferences.PREF_DATE_KEY);

        if (showDate != null) {
            showDate.setChecked(bShowDate);

            String summary;

            if (bShowDate) {
                summary = getResources().getString(R.string.showdatewidget);
            } else {
                summary = getResources().getString(R.string.hidedatewidget);
            }

            showDate.setSummary(summary);
        }

        PreferenceCategory dateScreen = (PreferenceCategory) findPreference(Preferences.PREF_DATE_SETTINGS_KEY);

        if (dateScreen != null) {
            dateScreen.setEnabled(bShowDate);
        }

        ListPreference dateFormat = (ListPreference) findPreference(Preferences.PREF_DATEFORMAT_KEY);

        if (dateFormat != null) {
            dateFormat.setValueIndex(Preferences.getDateFormatItem(context, mAppWidgetId));
            dateFormat.setSummary(dateFormat.getEntries()[Preferences.getDateFormatItem(context, mAppWidgetId)]);
        }

        ImageListPreference dateFont = (ImageListPreference) findPreference(Preferences.PREF_DATE_FONT_KEY);

        if (dateFont != null) {
            mDateFontEntries = dateFont.getEntries();
            mDateFontEntryValues = dateFont.getEntryValues();

            sortListPreferenceByEntries(dateFont);

            CharSequence[] sortedEntryValues = dateFont.getEntryValues();
            int valueIndex = -1;
            int fontItem = Preferences.getDateFontItem(context, mAppWidgetId);

            for (int j=0; j<sortedEntryValues.length; j++) {
                int entryValue = Integer.parseInt(sortedEntryValues[j].toString());

                if (fontItem == entryValue) {
                    valueIndex = j;
                    break;
                }
            }

            dateFont.setValueIndex(valueIndex);
            String summaryFull = (String) mDateFontEntries[Preferences.getDateFontItem(context, mAppWidgetId)];
            String summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
            dateFont.setSummary(summary);
        }

        CheckBoxPreference dateBoldText = (CheckBoxPreference) findPreference(Preferences.PREF_DATE_BOLD_TEXT_KEY);

        if (dateBoldText != null) {
            boolean bBoldText = Preferences.getDateBoldText(context, mAppWidgetId);
            dateBoldText.setChecked(bBoldText);

            String bold;

            if (bBoldText) {
                bold = getResources().getString(R.string.dateboldtext);
            } else {
                bold = getResources().getString(R.string.datenormaltext);
            }

            dateBoldText.setSummary(bold);
        }
        ////

        CheckBoxPreference showBattery = (CheckBoxPreference) findPreference(Preferences.PREF_BATTERY_KEY);

        if (showBattery != null) {
            showBattery.setChecked(Preferences.getShowBattery(context, mAppWidgetId));
        }

        SeekBarPreference transparency = (SeekBarPreference) findPreference(Preferences.PREF_TRANSPARENCY_KEY);

        if (transparency != null) {
            transparency.setProgress(Preferences.getOpacity(context, mAppWidgetId));
            transparency.setSummary(String.valueOf(Preferences.getOpacity(context, mAppWidgetId)) + " %");
        }

        ListPreference refreshInterval = (ListPreference) findPreference(Preferences.PREF_REFRESH_INTERVAL_KEY);

        if (refreshInterval != null) {
            refreshInterval.setValueIndex(Preferences.getRefreshInterval(context, mAppWidgetId));
            refreshInterval.setSummary(refreshInterval.getEntries()[Preferences.getRefreshInterval(context, mAppWidgetId)]);
        }

        ListPreference tempScale = (ListPreference) findPreference(Preferences.PREF_TEMP_SCALE_KEY);

        if (tempScale != null) {
            tempScale.setValueIndex(Preferences.getTempScale(context, mAppWidgetId));
            tempScale.setSummary(tempScale.getEntries()[Preferences.getTempScale(context, mAppWidgetId)]);
        }

        ImageListPreference weatherFont = (ImageListPreference) findPreference(Preferences.PREF_WEATHER_FONT_KEY);

        if (weatherFont != null) {
            mWeatherFontEntries = weatherFont.getEntries();
            mWeatherFontEntryValues = weatherFont.getEntryValues();

            sortListPreferenceByEntries(weatherFont);

            CharSequence[] sortedEntryValues = weatherFont.getEntryValues();
            int valueIndex = -1;
            int fontItem = Preferences.getWeatherFontItem(context, mAppWidgetId);

            for (int j=0; j<sortedEntryValues.length; j++) {
                int entryValue = Integer.parseInt(sortedEntryValues[j].toString());

                if (fontItem == entryValue) {
                    valueIndex = j;
                    break;
                }
            }

            weatherFont.setValueIndex(valueIndex);
            String summaryFull = (String) mWeatherFontEntries[Preferences.getWeatherFontItem(context, mAppWidgetId)];
            String summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
            weatherFont.setSummary(summary);
        }

        CheckBoxPreference weatherBoldText = (CheckBoxPreference) findPreference(Preferences.PREF_WEATHER_BOLD_TEXT_KEY);

        if (weatherBoldText != null) {
            boolean bBoldText = Preferences.getWeatherBoldText(context, mAppWidgetId);
            weatherBoldText.setChecked(bBoldText);

            String bold;

            if (bBoldText) {
                bold = getResources().getString(R.string.weatherboldtext);
            } else {
                bold = getResources().getString(R.string.weathernormaltext);
            }

            weatherBoldText.setSummary(bold);
        }
        ////

        ImageListPreference weatherIcons = (ImageListPreference) findPreference(Preferences.PREF_WEATHER_ICONS_KEY);

        if (weatherIcons != null) {
            mWeatherIconEntries = weatherIcons.getEntries();
            mWeatherIconEntryValues = weatherIcons.getEntryValues();

            sortListPreferenceByEntries(weatherIcons);

            CharSequence[] sortedEntryValues = weatherIcons.getEntryValues();
            int valueIndex = -1;
            int iconItem = Preferences.getWeatherIcons(context, mAppWidgetId);

            for (int j=0; j<sortedEntryValues.length; j++) {
                int entryValue = Integer.parseInt(sortedEntryValues[j].toString());

                if (iconItem == entryValue) {
                    valueIndex = j;
                    break;
                }
            }

            weatherIcons.setValueIndex(valueIndex);
            weatherIcons.setSummary(mWeatherIconEntries[Preferences.getWeatherIcons(context, mAppWidgetId)]);
        }

        ListPreference forecastTheme = (ListPreference) findPreference(Preferences.PREF_FORECAST_THEME);

        if (forecastTheme != null) {

            String theme = Preferences.getForecastTheme(context, mAppWidgetId);

            if (theme.equals("") || forecastTheme.findIndexOfValue(theme) < 0) {
                theme = "dark";
            }

            forecastTheme.setValueIndex(forecastTheme.findIndexOfValue(theme));
            forecastTheme.setSummary(forecastTheme.getEntries()[forecastTheme.findIndexOfValue(theme)]);
        }

        Preference refreshNow = findPreference(Preferences.PREF_REFRESH_NOW_KEY);

        if (refreshNow != null) {
            refreshNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference p) {
                    float lat = Preferences.getLocationLat(context, mAppWidgetId);
                    float lon = Preferences.getLocationLon(context, mAppWidgetId);
                    long id = Preferences.getLocationId(context, mAppWidgetId);

                    if ((id == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
                        Toast.makeText(context.getApplicationContext(), getResources().getText(R.string.nolocationdefined), Toast.LENGTH_LONG).show();
                    } else {
                        Intent startIntent = new Intent(context, WidgetUpdateService.class);
                        startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.WEATHER_UPDATE);
                        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                        context.startService(startIntent);
                    }

                    return true;
                }
            });

            long lastRefresh = Preferences.getLastRefresh(context, mAppWidgetId);

            if (lastRefresh > 0) {
                boolean bShow24Hrs = Preferences.getShow24Hrs(context, mAppWidgetId);
                int iDateFormatItem = Preferences.getDateFormatItem(context, mAppWidgetId);
                Date resultDate = new Date(lastRefresh);

                String currentTime;

                if (bShow24Hrs) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    currentTime = sdf.format(resultDate);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    currentTime = sdf.format(resultDate);
                }

                String currentDate;
                String[] mTestArray = getResources().getStringArray(R.array.dateFormat);

                SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
                currentDate = sdf.format(resultDate);

                refreshNow.setSummary(getResources().getString(R.string.lastrefresh) + " " + currentDate + ", " + currentTime);
            } else {
                refreshNow.setSummary(getResources().getString(R.string.lastrefreshnever));
            }
        }

        PreferenceScreen locationScreen = (PreferenceScreen) findPreference(Preferences.PREF_LOCATION_SETTINGS_KEY);

        if (locationScreen != null) {
            locationScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference p) {
                    Intent locationIntent = new Intent(context, ConfigureLocationActivity.class);
                    locationIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    DigitalClockAppWidgetPreferenceFragment.this.startActivityForResult(locationIntent, REQUEST_LOCATION);

                    return true;
                }
            });

            locationScreen.setSummary(getResources().getString(R.string.defaultsummary) + ": " + Preferences.getLocation(context, mAppWidgetId));
        }

        CheckBoxPreference refreshWiFiOnly = (CheckBoxPreference) findPreference(Preferences.PREF_REFRESH_WIFI_ONLY);

        if (refreshWiFiOnly != null) {
            boolean bWiFiOnly = Preferences.getRefreshWiFiOnly(context, mAppWidgetId);
            refreshWiFiOnly.setChecked(bWiFiOnly);

            String connection;

            if (bWiFiOnly) {
                connection = getResources().getString(R.string.refreshwifionlyconnection);
            } else {
                connection = getResources().getString(R.string.refreshanyconnection);
            }

            refreshWiFiOnly.setSummary(connection);
        }

        Preference about = findPreference(Preferences.PREF_ABOUT_KEY);

        if (about != null) {
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference p) {
                    AboutDialog about = new AboutDialog(context);
                    about.setTitle("About Zoromatic Widgets");
                    about.show();
                    return true;
                }
            });
        }

        boolean bShowWeather = Preferences.getShowWeather(context, mAppWidgetId);

        CheckBoxPreference showWeather = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_WEATHER_KEY);

        if (showWeather != null) {
            showWeather.setChecked(bShowWeather);

            String summary;

            if (bShowWeather) {
                summary = getResources().getString(R.string.showweatherwidget);
            } else {
                summary = getResources().getString(R.string.hideweatherwidget);
            }

            showWeather.setSummary(summary);
        }

        PreferenceCategory weatherScreen = (PreferenceCategory) findPreference(Preferences.PREF_WEATHER_SETTINGS_KEY);

        if (weatherScreen != null) {
            weatherScreen.setEnabled(bShowWeather);
        }

        PreferenceCategory weatherLayoutScreen = (PreferenceCategory) findPreference(Preferences.PREF_WEATHER_LAYOUT_KEY);

        if (weatherLayoutScreen != null) {
            weatherLayoutScreen.setEnabled(bShowWeather);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    PreferenceScreen locationScreen = (PreferenceScreen) findPreference(Preferences.PREF_LOCATION_SETTINGS_KEY);

                    if (locationScreen != null) {
                        context = getActivity();
                        locationScreen.setSummary(getResources().getString(R.string.defaultsummary) + ": " + Preferences.getLocation(context, mAppWidgetId));
                    }
                }
                break;
            case REQUEST_THEME:
                if (getActivity().getParent() instanceof WeatherForecastActivity) {
                    Activity weatherForecastActivity = getActivity().getParent();

                    Intent intent = weatherForecastActivity.getIntent();
                    weatherForecastActivity.finish();
                    weatherForecastActivity.startActivity(intent);

                    Intent settingsIntent = new Intent(getActivity().getApplicationContext(), DigitalClockAppWidgetPreferenceActivity.class);

                    settingsIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                    startActivityForResult(settingsIntent, WeatherForecastActivity.ACTIVITY_SETTINGS);
                } else {
                    if (getActivity().getParent() != null) {
                        if (getActivity().getParent().getParent() != null) {
                            Intent intentGrandParent = getActivity().getParent().getParent().getIntent();
                            getActivity().getParent().getParent().finish();
                            getActivity().getParent().getParent().startActivity(intentGrandParent);
                        }

                        Intent intentParent = getActivity().getParent().getIntent();
                        getActivity().getParent().finish();
                        getActivity().getParent().startActivity(intentParent);
                    }

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    getActivity().startActivity(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setPreferences();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preferenceScreen == null || preference == null) {
            return false;
        }

        String key = preference.getKey();

        if (key != null) {
            if (key.equalsIgnoreCase(getResources().getString(R.string.category_general))
                    || key.equalsIgnoreCase(getResources().getString(R.string.category_weather))
                    || key.equalsIgnoreCase(getResources().getString(R.string.category_look))) {
                Intent settingsIntent = new Intent(context, DigitalClockAppWidgetPreferenceActivity.class);
                settingsIntent.setAction(key);
                settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                startActivity(settingsIntent);
            } else if (key.equalsIgnoreCase(getResources().getString(R.string.category_theme))) {
                Intent settingsIntent = new Intent(context, ZoromaticWidgetsPreferenceActivity.class);
                settingsIntent.setAction(key);
                startActivityForResult(settingsIntent, REQUEST_THEME);
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        if (key.equals(Preferences.PREF_24HRS_KEY)) {
            CheckBoxPreference show24hrs = (CheckBoxPreference) findPreference(Preferences.PREF_24HRS_KEY);

            if (show24hrs != null) {
                Preferences.setShow24Hrs(context, mAppWidgetId, show24hrs.isChecked());
            }
        }

        if (key.equals(Preferences.PREF_MAIN_THEME)) {
            ListPreference mainTheme = (ListPreference) findPreference(Preferences.PREF_MAIN_THEME);

            if (mainTheme != null) {
                String value = mainTheme.getValue();

                if (value.equals("") || mainTheme.findIndexOfValue(value) < 0) {
                    value = "dark";
                }

                Preferences.setMainTheme(context, value);

                mainTheme.setValueIndex(mainTheme.findIndexOfValue(value));
                mainTheme.setSummary(mainTheme.getEntries()[mainTheme.findIndexOfValue(value)]);

                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        }

        if (key.equals(Preferences.PREF_MAIN_COLOR_SCHEME)) {
            ListPreference mainColorScheme = (ListPreference) findPreference(Preferences.PREF_MAIN_COLOR_SCHEME);

            if (mainColorScheme != null) {
                Preferences.setMainColorScheme(context, mainColorScheme.findIndexOfValue(mainColorScheme.getValue()));
                mainColorScheme.setSummary(mainColorScheme.getEntries()[Preferences.getMainColorScheme(context)]);

                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        }

        if (key.equals(Preferences.PREF_CLOCK_COLOR_PICKER_KEY)) {
            ColorPickerPreference clockColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_CLOCK_COLOR_PICKER_KEY);

            if (clockColorPicker != null) {
                Preferences.setClockColor(context, mAppWidgetId, clockColorPicker.getValue());
                clockColorPicker.onColorChanged(Preferences.getClockColor(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_FONT_KEY)) {
            ImageListPreference clockFont = (ImageListPreference) findPreference(Preferences.PREF_FONT_KEY);

            if (clockFont != null) {
                int valueIndex = -1;

                for (int j=0; j<mClockFontEntryValues.length; j++) {
                    if (clockFont.getValue() == mClockFontEntryValues[j]) {
                        valueIndex = j;
                        break;
                    }
                }

                Preferences.setFontItem(context, mAppWidgetId, valueIndex);

                String summaryFull = (String) mClockFontEntries[Preferences.getFontItem(context, mAppWidgetId)];
                String summary = summaryFull;

                if (summaryFull.contains("[")) {
                    summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
                }

                clockFont.setSummary(summary);
            }
        }

        if (key.equals(Preferences.PREF_BOLD_TEXT_KEY)) {
            CheckBoxPreference boldText = (CheckBoxPreference) findPreference(Preferences.PREF_BOLD_TEXT_KEY);

            if (boldText != null) {
                boolean bBoldText = boldText.isChecked();

                Preferences.setBoldText(context, mAppWidgetId, bBoldText);

                String bold;

                if (bBoldText) {
                    bold = getResources().getString(R.string.boldtext);
                } else {
                    bold = getResources().getString(R.string.normaltext);
                }

                boldText.setSummary(bold);
            }
        }

        if (key.equals(Preferences.PREF_CLOCK_SKIN)) {
            ListPreference clockSkin = (ListPreference) findPreference(Preferences.PREF_CLOCK_SKIN);

            if (clockSkin != null) {
                Preferences.setClockSkin(context, mAppWidgetId, clockSkin.findIndexOfValue(clockSkin.getValue()));
                clockSkin.setSummary(clockSkin.getEntries()[Preferences.getClockSkin(context, mAppWidgetId)]);
            }
        }

        if (key.equals(Preferences.PREF_DATE_KEY)) {
            CheckBoxPreference showDate = (CheckBoxPreference) findPreference(Preferences.PREF_DATE_KEY);

            if (showDate != null) {
                boolean bShowDate = showDate.isChecked();
                Preferences.setShowDate(context, mAppWidgetId, bShowDate);

                String summary;

                if (bShowDate) {
                    summary = getResources().getString(R.string.showdatewidget);
                } else {
                    summary = getResources().getString(R.string.hidedatewidget);
                }

                showDate.setSummary(summary);

                PreferenceScreen dateScreen = (PreferenceScreen) findPreference(Preferences.PREF_DATE_SETTINGS_KEY);

                if (dateScreen != null) {
                    dateScreen.setEnabled(bShowDate);
                }
            }
        }

        if (key.equals(Preferences.PREF_DATEFORMAT_KEY)) {
            ListPreference dateFormat = (ListPreference) findPreference(Preferences.PREF_DATEFORMAT_KEY);

            if (dateFormat != null) {
                Preferences.setDateFormatItem(context, mAppWidgetId, dateFormat.findIndexOfValue(dateFormat.getValue()));
                dateFormat.setSummary(dateFormat.getEntries()[Preferences.getDateFormatItem(context, mAppWidgetId)]);
            }
        }

        if (key.equals(Preferences.PREF_DATE_COLOR_PICKER_KEY)) {
            ColorPickerPreference dateColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_DATE_COLOR_PICKER_KEY);

            if (dateColorPicker != null) {
                Preferences.setDateColor(context, mAppWidgetId, dateColorPicker.getValue());
                dateColorPicker.onColorChanged(Preferences.getDateColor(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_DATE_FONT_KEY)) {
            ImageListPreference dateFont = (ImageListPreference) findPreference(Preferences.PREF_DATE_FONT_KEY);

            if (dateFont != null) {
                int valueIndex = -1;

                for (int j=0; j<mDateFontEntryValues.length; j++) {
                    if (dateFont.getValue() == mDateFontEntryValues[j]) {
                        valueIndex = j;
                        break;
                    }
                }

                Preferences.setDateFontItem(context, mAppWidgetId, valueIndex);

                String summaryFull = (String) mDateFontEntries[Preferences.getDateFontItem(context, mAppWidgetId)];
                String summary = summaryFull;

                if (summaryFull.contains("[")) {
                    summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
                }

                dateFont.setSummary(summary);
            }
        }

        if (key.equals(Preferences.PREF_DATE_BOLD_TEXT_KEY)) {
            CheckBoxPreference dateBoldText = (CheckBoxPreference) findPreference(Preferences.PREF_DATE_BOLD_TEXT_KEY);

            if (dateBoldText != null) {
                boolean bBoldText = dateBoldText.isChecked();

                Preferences.setDateBoldText(context, mAppWidgetId, bBoldText);

                String bold;

                if (bBoldText) {
                    bold = getResources().getString(R.string.dateboldtext);
                } else {
                    bold = getResources().getString(R.string.datenormaltext);
                }

                dateBoldText.setSummary(bold);
            }
        }
        ////

        if (key.equals(Preferences.PREF_BATTERY_KEY)) {
            CheckBoxPreference showBattery = (CheckBoxPreference) findPreference(Preferences.PREF_BATTERY_KEY);

            if (showBattery != null) {
                Preferences.setShowBattery(context, mAppWidgetId, showBattery.isChecked());
            }
        }

        if (key.equals(Preferences.PREF_TRANSPARENCY_KEY)) {
            SeekBarPreference transparency = (SeekBarPreference) findPreference(Preferences.PREF_TRANSPARENCY_KEY);

            if (transparency != null) {
                Preferences.setOpacity(context, mAppWidgetId, transparency.getProgress());
                transparency.setSummary(String.valueOf(transparency.getProgress()) + " %");
            }
        }

        if (key.equals(Preferences.PREF_REFRESH_INTERVAL_KEY)) {
            ListPreference refreshInterval = (ListPreference) findPreference(Preferences.PREF_REFRESH_INTERVAL_KEY);

            if (refreshInterval != null) {
                Preferences.setRefreshInterval(context, mAppWidgetId, refreshInterval.findIndexOfValue(refreshInterval.getValue()));
                refreshInterval.setSummary(refreshInterval.getEntries()[Preferences.getRefreshInterval(context, mAppWidgetId)]);

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DigitalClockAppWidgetProvider.scheduleJob(context, mAppWidgetId);
                } else {
                    DigitalClockAppWidgetProvider.setAlarm(context, mAppWidgetId);
                }
            }
        }

        if (key.equals(Preferences.PREF_TEMP_SCALE_KEY)) {
            ListPreference tempScale = (ListPreference) findPreference(Preferences.PREF_TEMP_SCALE_KEY);

            if (tempScale != null) {
                Preferences.setTempScale(context, mAppWidgetId, tempScale.findIndexOfValue(tempScale.getValue()));
                tempScale.setSummary(tempScale.getEntries()[Preferences.getTempScale(context, mAppWidgetId)]);
            }
        }

        if (key.equals(Preferences.PREF_WEATHER_COLOR_PICKER_KEY)) {
            ColorPickerPreference weatherColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_WEATHER_COLOR_PICKER_KEY);

            if (weatherColorPicker != null) {
                Preferences.setWeatherColor(context, mAppWidgetId, weatherColorPicker.getValue());
                weatherColorPicker.onColorChanged(Preferences.getWeatherColor(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_WEATHER_FONT_KEY)) {
            ImageListPreference weatherFont = (ImageListPreference) findPreference(Preferences.PREF_WEATHER_FONT_KEY);

            if (weatherFont != null) {
                int valueIndex = -1;

                for (int j=0; j<mWeatherFontEntryValues.length; j++) {
                    if (weatherFont.getValue() == mWeatherFontEntryValues[j]) {
                        valueIndex = j;
                        break;
                    }
                }

                Preferences.setWeatherFontItem(context, mAppWidgetId, valueIndex);

                String summaryFull = (String) mWeatherFontEntries[Preferences.getWeatherFontItem(context, mAppWidgetId)];
                String summary = summaryFull;

                if (summaryFull.contains("[")) {
                    summary = summaryFull.substring(0, summaryFull.indexOf('[') - 1);
                }

                weatherFont.setSummary(summary);
            }
        }

        if (key.equals(Preferences.PREF_WEATHER_BOLD_TEXT_KEY)) {
            CheckBoxPreference weatherBoldText = (CheckBoxPreference) findPreference(Preferences.PREF_WEATHER_BOLD_TEXT_KEY);

            if (weatherBoldText != null) {
                boolean bBoldText = weatherBoldText.isChecked();

                Preferences.setWeatherBoldText(context, mAppWidgetId, bBoldText);

                String bold;

                if (bBoldText) {
                    bold = getResources().getString(R.string.weatherboldtext);
                } else {
                    bold = getResources().getString(R.string.weathernormaltext);
                }

                weatherBoldText.setSummary(bold);
            }
        }
        ////

        if (key.equals(Preferences.PREF_WEATHER_ICONS_KEY)) {
            ImageListPreference weatherIcons = (ImageListPreference) findPreference(Preferences.PREF_WEATHER_ICONS_KEY);

            if (weatherIcons != null) {
                int valueIndex = -1;

                for (int j=0; j<mWeatherIconEntryValues.length; j++) {
                    if (weatherIcons.getValue() == mWeatherIconEntryValues[j]) {
                        valueIndex = j;
                        break;
                    }
                }

                Preferences.setWeatherIcons(context, mAppWidgetId, valueIndex);
                weatherIcons.setSummary(mWeatherIconEntries[Preferences.getWeatherIcons(context, mAppWidgetId)]);
            }
        }

        if (key.equals(Preferences.PREF_FORECAST_THEME)) {
            ListPreference forecastTheme = (ListPreference) findPreference(Preferences.PREF_FORECAST_THEME);

            if (forecastTheme != null) {
                String theme = forecastTheme.getValue();

                if (theme.equals("") || forecastTheme.findIndexOfValue(theme) < 0) {
                    theme = "dark";
                }

                if (!theme.equals("") && forecastTheme.findIndexOfValue(theme) >= 0) {
                    Preferences.setForecastTheme(context, mAppWidgetId, theme);
                } else {
                    Preferences.setForecastTheme(context, mAppWidgetId, "dark");
                }

                forecastTheme.setSummary(forecastTheme.getEntries()[forecastTheme.findIndexOfValue(theme)]);
            }
        }

        if (key.equals(Preferences.PREF_WIDGET_COLOR_PICKER_KEY)) {
            ColorPickerPreference widgetColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_WIDGET_COLOR_PICKER_KEY);

            if (widgetColorPicker != null) {
                Preferences.setWidgetColor(context, mAppWidgetId, widgetColorPicker.getValue());
                widgetColorPicker.onColorChanged(Preferences.getWidgetColor(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_LOCATION_SETTINGS_KEY)) {
            PreferenceScreen location = (PreferenceScreen) findPreference(Preferences.PREF_LOCATION_SETTINGS_KEY);

            if (location != null) {
                location.setSummary(Preferences.getLocation(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_REFRESH_WIFI_ONLY)) {
            CheckBoxPreference refreshWiFiOnly = (CheckBoxPreference) findPreference(Preferences.PREF_REFRESH_WIFI_ONLY);

            if (refreshWiFiOnly != null) {
                boolean bWiFiOnly = refreshWiFiOnly.isChecked();
                Preferences.setRefreshWiFiOnly(context, mAppWidgetId, bWiFiOnly);

                String connection;

                if (bWiFiOnly) {
                    connection = getResources().getString(R.string.refreshwifionlyconnection);
                } else {
                    connection = getResources().getString(R.string.refreshanyconnection);
                }

                refreshWiFiOnly.setSummary(connection);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_WEATHER_KEY)) {
            CheckBoxPreference showWeather = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_WEATHER_KEY);

            if (showWeather != null) {
                boolean bShowWeather = showWeather.isChecked();
                Preferences.setShowWeather(context, mAppWidgetId, bShowWeather);

                String summary;

                if (bShowWeather) {
                    summary = getResources().getString(R.string.showweatherwidget);

                    Intent startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.WEATHER_UPDATE);
                    startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                    context.startService(startIntent);
                } else {
                    summary = getResources().getString(R.string.hideweatherwidget);
                }

                showWeather.setSummary(summary);

                PreferenceScreen weatherScreen = (PreferenceScreen) findPreference(Preferences.PREF_WEATHER_SETTINGS_KEY);

                if (weatherScreen != null) {
                    weatherScreen.setEnabled(bShowWeather);
                }

                PreferenceScreen weatherLayoutScreen = (PreferenceScreen) findPreference(Preferences.PREF_WEATHER_LAYOUT_KEY);

                if (weatherLayoutScreen != null) {
                    weatherLayoutScreen.setEnabled(bShowWeather);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen == null) {
            return;
        }

        // Set up a listener whenever a key changes
        preferenceScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen == null) {
            return;
        }

        // Unregister the listener whenever a key changes
        preferenceScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
