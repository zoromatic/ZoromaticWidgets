package com.zoromatic.widgets;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margaritov.preference.colorpicker.ColorPickerPreference;
import com.zoromatic.widgets.PreferenceFragment;

@SuppressLint({"NewApi", "SimpleDateFormat"})
public class PowerAppWidgetPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;
    public static final int RESULT_FIRST_USER = 1;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Context context = null;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        context = (Context) getActivity();

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

            setPreferences(paramBundle);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    void setPreferences(Bundle savedInstanceState) {
        PreferenceManager localPrefs = getPreferenceManager();
        localPrefs.setSharedPreferencesName(Preferences.PREF_NAME);

        Bundle bundle = getArguments();

        if (bundle != null) {

            mAppWidgetId = bundle.getInt(PowerAppWidgetPreferenceActivity.APPWIDGETID, AppWidgetManager.INVALID_APPWIDGET_ID);

            String category = bundle.getString("category");

            if (category != null) {
                if (category.equals(getString(R.string.category_general))) {
                    addPreferencesFromResource(R.xml.powerwidget_prefs_general);
                } else if (category.equals(getString(R.string.category_look))) {
                    addPreferencesFromResource(R.xml.powerwidget_prefs_look);
                } else {
                    addPreferencesFromResource(R.xml.powerwidget_prefs_legacy);
                }
            } else {
                addPreferencesFromResource(R.xml.powerwidget_prefs_legacy);
            }
        } else {
            addPreferencesFromResource(R.xml.powerwidget_prefs_legacy);
        }

        CheckBoxPreference bluetooth = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BLUETOOTH_KEY);

        if (bluetooth != null) {
            boolean bShow = Preferences.getShowBluetooth(context, mAppWidgetId);
            bluetooth.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            bluetooth.setSummary(show);
        }

        CheckBoxPreference gps = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_GPS_KEY);

        if (gps != null) {
            boolean bShow = Preferences.getShowGps(context, mAppWidgetId);
            gps.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            gps.setSummary(show);
        }

        CheckBoxPreference mobile = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_MOBILE_KEY);

        if (mobile != null) {
            boolean bShow = Preferences.getShowMobile(context, mAppWidgetId);
            mobile.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            mobile.setSummary(show);
        }

        CheckBoxPreference ringer = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_RINGER_KEY);

        if (ringer != null) {
            boolean bShow = Preferences.getShowRinger(context, mAppWidgetId);
            ringer.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            ringer.setSummary(show);
        }

        CheckBoxPreference wifi = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_WIFI_KEY);

        if (wifi != null) {
            boolean bShow = Preferences.getShowWiFi(context, mAppWidgetId);
            wifi.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            wifi.setSummary(show);
        }

        CheckBoxPreference airplane = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_AIRPLANE_KEY);

        if (airplane != null) {
            boolean bShow = Preferences.getShowAirplane(context, mAppWidgetId);
            airplane.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            airplane.setSummary(show);
        }

        CheckBoxPreference brightness = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BRIGHTNESS_KEY);

        if (brightness != null) {
            boolean bShow = Preferences.getShowBrightness(context, mAppWidgetId);
            brightness.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            brightness.setSummary(show);
        }

        CheckBoxPreference nfc = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_NFC_KEY);

        if (nfc != null) {
            boolean bShow = Preferences.getShowNfc(context, mAppWidgetId);
            nfc.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            nfc.setSummary(show);
        }

        CheckBoxPreference sync = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_SYNC_KEY);

        if (sync != null) {
            boolean bShow = Preferences.getShowSync(context, mAppWidgetId);
            sync.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            sync.setSummary(show);
        }

        CheckBoxPreference orientation = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_ORIENTATION_KEY);

        if (orientation != null) {
            boolean bShow = Preferences.getShowOrientation(context, mAppWidgetId);
            orientation.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            orientation.setSummary(show);
        }

        CheckBoxPreference torch = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_TORCH_KEY);

        if (torch != null) {
            boolean bShow = Preferences.getShowTorch(context, mAppWidgetId);
            torch.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showtoggle);
            } else {
                show = getResources().getString(R.string.hidetoggle);
            }

            torch.setSummary(show);
        }

        CheckBoxPreference battery = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BATTERY_STATUS_KEY);

        if (battery != null) {
            boolean bShow = Preferences.getShowBatteryStatus(context, mAppWidgetId);
            battery.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showbattery);
            } else {
                show = getResources().getString(R.string.hidebattery);
            }

            battery.setSummary(show);
        }

        CheckBoxPreference settings = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_SETTINGS_KEY);

        if (settings != null) {
            boolean bShow = Preferences.getShowSettings(context, mAppWidgetId);
            settings.setChecked(bShow);

            String show = "";

            if (bShow) {
                show = getResources().getString(R.string.showsettings);
            } else {
                show = getResources().getString(R.string.hidesettings);
            }

            settings.setSummary(show);
        }

        ColorPickerPreference onColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_ON_KEY);

        if (onColorPicker != null) {
            onColorPicker.onColorChanged(Preferences.getColorOn(context, mAppWidgetId));
        }

        ColorPickerPreference offColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_OFF_KEY);

        if (offColorPicker != null) {
            offColorPicker.onColorChanged(Preferences.getColorOff(context, mAppWidgetId));
        }

        ColorPickerPreference transitionColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TRANSITION_KEY);

        if (transitionColorPicker != null) {
            transitionColorPicker.onColorChanged(Preferences.getColorTransition(context, mAppWidgetId));
        }

        ColorPickerPreference backgroundColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BACKGROUND_KEY);

        if (backgroundColorPicker != null) {
            backgroundColorPicker.onColorChanged(Preferences.getColorBackground(context, mAppWidgetId));
        }

        SeekBarPreference transparency = (SeekBarPreference) findPreference(Preferences.PREF_POWER_TRANSPARENCY_KEY);

        if (transparency != null) {
            transparency.setProgress(Preferences.getPowerOpacity(context, mAppWidgetId));
            transparency.setSummary(String.valueOf(Preferences.getPowerOpacity(context, mAppWidgetId)) + " %");
        }

        ColorPickerPreference textColorOnPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TEXT_ON_KEY);

        if (textColorOnPicker != null) {
            textColorOnPicker.onColorChanged(Preferences.getColorTextOn(context, mAppWidgetId));
        }

        ColorPickerPreference textColorOffPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TEXT_OFF_KEY);

        if (textColorOffPicker != null) {
            textColorOffPicker.onColorChanged(Preferences.getColorTextOff(context, mAppWidgetId));
        }

        ColorPickerPreference batteryColor1Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY1_KEY);

        if (batteryColor1Picker != null) {
            batteryColor1Picker.onColorChanged(Preferences.getColorBattery1(context, mAppWidgetId));
        }

        ColorPickerPreference batteryColor2Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY2_KEY);

        if (batteryColor2Picker != null) {
            batteryColor2Picker.onColorChanged(Preferences.getColorBattery2(context, mAppWidgetId));
        }

        ColorPickerPreference batteryColor3Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY3_KEY);

        if (batteryColor3Picker != null) {
            batteryColor3Picker.onColorChanged(Preferences.getColorBattery3(context, mAppWidgetId));
        }

        ColorPickerPreference batteryColor4Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY4_KEY);

        if (batteryColor4Picker != null) {
            batteryColor4Picker.onColorChanged(Preferences.getColorBattery4(context, mAppWidgetId));
        }

        NumberPickerPreference batteryThreshold2Picker = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY2_KEY);

        if (batteryThreshold2Picker != null) {
            batteryThreshold2Picker.setValue(Preferences.getThresholdBattery2(context, mAppWidgetId));
            String summary = String.valueOf(Preferences.getThresholdBattery2(context, mAppWidgetId));
            batteryThreshold2Picker.setSummary(summary);
        }

        NumberPickerPreference batteryThreshold3Picker = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY3_KEY);

        if (batteryThreshold3Picker != null) {
            batteryThreshold3Picker.setValue(Preferences.getThresholdBattery3(context, mAppWidgetId));
            String summary = String.valueOf(Preferences.getThresholdBattery3(context, mAppWidgetId));
            batteryThreshold3Picker.setSummary(summary);
        }

        NumberPickerPreference batteryThreshold4Picker = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY4_KEY);

        if (batteryThreshold4Picker != null) {
            batteryThreshold4Picker.setValue(Preferences.getThresholdBattery4(context, mAppWidgetId));
            String summary = String.valueOf(Preferences.getThresholdBattery4(context, mAppWidgetId));
            batteryThreshold4Picker.setSummary(summary);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
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
    public void onViewStateRestored(Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
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
                    || key.equalsIgnoreCase(getResources().getString(R.string.category_look))) {
                Intent settingsIntent = new Intent(context, PowerAppWidgetPreferenceActivity.class);
                settingsIntent.setAction(key);
                settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                startActivity(settingsIntent);
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        if (key.equals(Preferences.PREF_SHOW_BLUETOOTH_KEY)) {
            CheckBoxPreference bluetooth = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BLUETOOTH_KEY);

            if (bluetooth != null) {
                boolean bShow = bluetooth.isChecked();

                Preferences.setShowBluetooth(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                bluetooth.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_GPS_KEY)) {
            CheckBoxPreference gps = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_GPS_KEY);

            if (gps != null) {
                boolean bShow = gps.isChecked();

                Preferences.setShowGps(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                gps.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_MOBILE_KEY)) {
            CheckBoxPreference mobile = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_MOBILE_KEY);

            if (mobile != null) {
                boolean bShow = mobile.isChecked();

                Preferences.setShowMobile(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                mobile.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_RINGER_KEY)) {
            CheckBoxPreference ringer = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_RINGER_KEY);

            if (ringer != null) {
                boolean bShow = ringer.isChecked();

                Preferences.setShowRinger(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                ringer.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_WIFI_KEY)) {
            CheckBoxPreference wifi = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_WIFI_KEY);

            if (wifi != null) {
                boolean bShow = wifi.isChecked();

                Preferences.setShowWiFi(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                wifi.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_AIRPLANE_KEY)) {
            CheckBoxPreference airplane = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_AIRPLANE_KEY);

            if (airplane != null) {
                boolean bShow = airplane.isChecked();

                Preferences.setShowAirplane(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                airplane.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_BRIGHTNESS_KEY)) {
            CheckBoxPreference brightness = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BRIGHTNESS_KEY);

            if (brightness != null) {
                boolean bShow = brightness.isChecked();

                Preferences.setShowBrightness(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                brightness.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_NFC_KEY)) {
            CheckBoxPreference nfc = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_NFC_KEY);

            if (nfc != null) {
                boolean bShow = nfc.isChecked();

                Preferences.setShowNfc(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                nfc.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_SYNC_KEY)) {
            CheckBoxPreference sync = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_SYNC_KEY);

            if (sync != null) {
                boolean bShow = sync.isChecked();

                Preferences.setShowSync(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                sync.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_ORIENTATION_KEY)) {
            CheckBoxPreference orientation = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_ORIENTATION_KEY);

            if (orientation != null) {
                boolean bShow = orientation.isChecked();

                Preferences.setShowOrientation(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                orientation.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_TORCH_KEY)) {
            CheckBoxPreference torch = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_TORCH_KEY);

            if (torch != null) {
                boolean bShow = torch.isChecked();

                Preferences.setShowTorch(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showtoggle);
                } else {
                    show = getResources().getString(R.string.hidetoggle);
                }

                torch.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_BATTERY_STATUS_KEY)) {
            CheckBoxPreference battery = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_BATTERY_STATUS_KEY);

            if (battery != null) {
                boolean bShow = battery.isChecked();

                Preferences.setShowBatteryStatus(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showbattery);
                } else {
                    show = getResources().getString(R.string.hidebattery);
                }

                battery.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_SHOW_SETTINGS_KEY)) {
            CheckBoxPreference settings = (CheckBoxPreference) findPreference(Preferences.PREF_SHOW_SETTINGS_KEY);

            if (settings != null) {
                boolean bShow = settings.isChecked();

                Preferences.setShowSettings(context, mAppWidgetId, bShow);

                String show = "";

                if (bShow) {
                    show = getResources().getString(R.string.showsettings);
                } else {
                    show = getResources().getString(R.string.hidesettings);
                }

                settings.setSummary(show);
            }
        }

        if (key.equals(Preferences.PREF_COLOR_BACKGROUND_KEY)) {
            ColorPickerPreference backgroundColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BACKGROUND_KEY);

            if (backgroundColorPicker != null) {
                Preferences.setColorBackground(context, mAppWidgetId, backgroundColorPicker.getValue());
                backgroundColorPicker.onColorChanged(Preferences.getColorBackground(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_POWER_TRANSPARENCY_KEY)) {
            SeekBarPreference transparency = (SeekBarPreference) findPreference(Preferences.PREF_POWER_TRANSPARENCY_KEY);

            if (transparency != null) {
                Preferences.setPowerOpacity(context, mAppWidgetId, transparency.getProgress());
                transparency.setSummary(String.valueOf(transparency.getProgress()) + " %");
            }
        }

        if (key.equals(Preferences.PREF_COLOR_ON_KEY)) {
            ColorPickerPreference onColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_ON_KEY);

            if (onColorPicker != null) {
                Preferences.setColorOn(context, mAppWidgetId, onColorPicker.getValue());
                onColorPicker.onColorChanged(Preferences.getColorOn(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_OFF_KEY)) {
            ColorPickerPreference offColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_OFF_KEY);

            if (offColorPicker != null) {
                Preferences.setColorOff(context, mAppWidgetId, offColorPicker.getValue());
                offColorPicker.onColorChanged(Preferences.getColorOff(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_TRANSITION_KEY)) {
            ColorPickerPreference transitionColorPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TRANSITION_KEY);

            if (transitionColorPicker != null) {
                Preferences.setColorTransition(context, mAppWidgetId, transitionColorPicker.getValue());
                transitionColorPicker.onColorChanged(Preferences.getColorTransition(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_TEXT_ON_KEY)) {
            ColorPickerPreference textColorOnPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TEXT_ON_KEY);

            if (textColorOnPicker != null) {
                Preferences.setColorTextOn(context, mAppWidgetId, textColorOnPicker.getValue());
                textColorOnPicker.onColorChanged(Preferences.getColorTextOn(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_TEXT_OFF_KEY)) {
            ColorPickerPreference textColorOffPicker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_TEXT_OFF_KEY);

            if (textColorOffPicker != null) {
                Preferences.setColorTextOff(context, mAppWidgetId, textColorOffPicker.getValue());
                textColorOffPicker.onColorChanged(Preferences.getColorTextOff(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_BATTERY1_KEY)) {
            ColorPickerPreference batteryColor1Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY1_KEY);

            if (batteryColor1Picker != null) {
                Preferences.setColorBattery1(context, mAppWidgetId, batteryColor1Picker.getValue());
                batteryColor1Picker.onColorChanged(Preferences.getColorBattery1(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_BATTERY2_KEY)) {
            ColorPickerPreference batteryColor2Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY2_KEY);

            if (batteryColor2Picker != null) {
                Preferences.setColorBattery2(context, mAppWidgetId, batteryColor2Picker.getValue());
                batteryColor2Picker.onColorChanged(Preferences.getColorBattery2(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_BATTERY3_KEY)) {
            ColorPickerPreference batteryColor3Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY3_KEY);

            if (batteryColor3Picker != null) {
                Preferences.setColorBattery3(context, mAppWidgetId, batteryColor3Picker.getValue());
                batteryColor3Picker.onColorChanged(Preferences.getColorBattery3(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_COLOR_BATTERY4_KEY)) {
            ColorPickerPreference batteryColor4Picker = (ColorPickerPreference) findPreference(Preferences.PREF_COLOR_BATTERY4_KEY);

            if (batteryColor4Picker != null) {
                Preferences.setColorBattery4(context, mAppWidgetId, batteryColor4Picker.getValue());
                batteryColor4Picker.onColorChanged(Preferences.getColorBattery4(context, mAppWidgetId));
            }
        }

        if (key.equals(Preferences.PREF_THRESHOLD_BATTERY2_KEY)) {
            NumberPickerPreference thresholdBattery2 = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY2_KEY);

            if (thresholdBattery2 != null) {
                Preferences.setThresholdBattery2(context, mAppWidgetId, thresholdBattery2.getValue());
                thresholdBattery2.setSummary(String.valueOf(thresholdBattery2.getValue()));
            }
        }

        if (key.equals(Preferences.PREF_THRESHOLD_BATTERY3_KEY)) {
            NumberPickerPreference thresholdBattery3 = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY3_KEY);

            if (thresholdBattery3 != null) {
                Preferences.setThresholdBattery3(context, mAppWidgetId, thresholdBattery3.getValue());
                thresholdBattery3.setSummary(String.valueOf(thresholdBattery3.getValue()));
            }
        }

        if (key.equals(Preferences.PREF_THRESHOLD_BATTERY4_KEY)) {
            NumberPickerPreference thresholdBattery4 = (NumberPickerPreference) findPreference(Preferences.PREF_THRESHOLD_BATTERY4_KEY);

            if (thresholdBattery4 != null) {
                Preferences.setThresholdBattery2(context, mAppWidgetId, thresholdBattery4.getValue());
                thresholdBattery4.setSummary(String.valueOf(thresholdBattery4.getValue()));
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
