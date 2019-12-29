package com.zoromatic.widgets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.SunriseSunsetLocation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Thread.sleep;

/**
 * Created by zoran on 11/24/2017.
 * Contains methods for updating, setting and getting info
 * about AppWidgets
 */

public class WidgetManager {
    private static String LOG_TAG = "WidgetManager";

    private static int WIDGET_COLOR_ON = Color.rgb(0x35, 0xB6, 0xE5);
    private static int WIDGET_COLOR_OFF = Color.rgb(0xC0, 0xC0, 0xC0);
    private static int WIDGET_COLOR_TRANSITION = Color.rgb(0xFF, 0x8C, 0x00);
    private static int WIDGET_COLOR_BACKGROUND = Color.rgb(0x00, 0x00, 0x00);
    private static int WIDGET_COLOR_TEXT_ON = Color.rgb(0xFF, 0xFF, 0xFF);
    private static int WIDGET_COLOR_TEXT_OFF = Color.rgb(0xFF, 0xFF, 0xFF);

    private final static String COMMAND_L_ON = "svc data enable\n ";
    private final static String COMMAND_L_OFF = "svc data disable\n ";
    private final static String COMMAND_SU = "su";

    private static Camera camera;
    private static boolean flashOn = false;
    private Context mContext;

    /**
     * Creates a new instance of <code>WidgetManager</code> with the given parameters.
     *
     * @param context  The context of caller object
     */
    WidgetManager(Context context) {
        mContext = context;
    }

    public void destroyCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private RemoteViews buildPowerUpdate(String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager buildPowerUpdate");

        if (intentExtra == null)
            return null;

        RemoteViews updateViews = new RemoteViews(mContext.getPackageName(),
                R.layout.powerwidget);

        updateViews.setViewVisibility(R.id.loadingWidget,
                View.GONE);

        boolean bShowBluetooth = Preferences.getShowBluetooth(mContext, appWidgetId);
        boolean bShowGps = Preferences.getShowGps(mContext, appWidgetId);
        boolean bShowMobile = Preferences.getShowMobile(mContext, appWidgetId);
        boolean bShowRinger = Preferences.getShowRinger(mContext, appWidgetId);
        boolean bShowWifi = Preferences.getShowWiFi(mContext, appWidgetId);
        boolean bShowAirplane = Preferences.getShowAirplane(mContext, appWidgetId);
        boolean bShowBrightness = Preferences.getShowBrightness(mContext, appWidgetId);
        boolean bShowNfc = Preferences.getShowNfc(mContext, appWidgetId);
        boolean bShowSync = Preferences.getShowSync(mContext, appWidgetId);
        boolean bShowOrientation = Preferences.getShowOrientation(mContext, appWidgetId);
        boolean bShowTorch = Preferences.getShowTorch(mContext, appWidgetId);
        boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(mContext, appWidgetId);
        boolean bShowSettings = Preferences.getShowSettings(mContext, appWidgetId);

        if (bShowBluetooth) {
            updateViews.setViewVisibility(R.id.bluetoothWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.bluetoothWidget,
                    View.GONE);
        }

        if (bShowGps) {
            updateViews.setViewVisibility(R.id.gpsWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.gpsWidget,
                    View.GONE);
        }

        if (bShowMobile) {
            updateViews.setViewVisibility(R.id.mobileWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.mobileWidget,
                    View.GONE);
        }

        if (bShowRinger) {
            updateViews.setViewVisibility(R.id.ringerWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.ringerWidget,
                    View.GONE);
        }

        if (bShowWifi) {
            updateViews.setViewVisibility(R.id.wifiWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.wifiWidget,
                    View.GONE);
        }

        if (bShowAirplane) {
            updateViews.setViewVisibility(R.id.airplaneWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.airplaneWidget,
                    View.GONE);
        }

        if (bShowBrightness) {
            updateViews.setViewVisibility(R.id.brightnessWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.brightnessWidget,
                    View.GONE);
        }

        if (bShowNfc) {
            updateViews.setViewVisibility(R.id.nfcWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.nfcWidget,
                    View.GONE);
        }

        if (bShowSync) {
            updateViews.setViewVisibility(R.id.syncWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.syncWidget,
                    View.GONE);
        }

        if (bShowOrientation) {
            updateViews.setViewVisibility(R.id.orientationWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.orientationWidget,
                    View.GONE);
        }

        if (bShowTorch) {
            updateViews.setViewVisibility(R.id.torchWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.torchWidget,
                    View.GONE);
        }

        if (bShowBatteryStatus) {
            updateViews.setViewVisibility(R.id.batteryStatusWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.batteryStatusWidget,
                    View.GONE);
        }

        if (bShowSettings) {
            updateViews.setViewVisibility(R.id.settingsWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.settingsWidget,
                    View.GONE);
        }

        Intent prefIntent = new Intent(mContext, PowerAppWidgetPreferenceActivity.class);

        prefIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        prefIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent settingsIntent = PendingIntent.getActivity(mContext,
                appWidgetId, prefIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.settingsWidget, settingsIntent);

        int colorOff = WIDGET_COLOR_OFF;
        int colorBackground = WIDGET_COLOR_BACKGROUND;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorBackground = Preferences.getColorBackground(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setImageViewBitmap(R.id.imageViewSettings, getFontBitmap(mContext, mContext.getString(R.string.icon_settings),
                colorOff, "fonts/MaterialIcons.ttf", true, 144));

        updateViews.setInt(R.id.imageViewSettingsInd, "setColorFilter", colorOff);

        int iOpacity = Preferences.getPowerOpacity(mContext, appWidgetId);

        updateViews.setInt(R.id.backgroundImage, "setAlpha", iOpacity * 255 / 100);
        updateViews.setInt(R.id.backgroundImage, "setColorFilter", colorBackground);

        updateViews.setTextColor(R.id.textViewSettings, colorTextOff);

        try {
            Intent batteryInfo = new Intent(mContext, BatteryInfoActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    mContext, 0, batteryInfo,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.batteryStatusWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_BLUETOOTH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.bluetoothWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_WIFI_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.wifiWidget,
                    pendingIntent);

            int currentApiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(WidgetIntentDefinitions.POWER_MOBILE_DATA_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            } else {
                Intent intentData = new Intent(Intent.ACTION_MAIN);
                intentData.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                pendingIntent = PendingIntent.getActivity(mContext, 0,
                        intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            }

            if (canToggleGPS()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(WidgetIntentDefinitions.POWER_GPS_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                        pendingIntent);
            } else {
                Intent locationIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                        locationIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(mContext, 0,
                            locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                }
            }

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_RINGER_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.ringerWidget,
                    pendingIntent);

            if (canToggleAirplane()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(WidgetIntentDefinitions.POWER_AIRPLANE_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                        pendingIntent);

            } else {
                Intent airplaneIntent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                        airplaneIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(mContext, 0,
                            airplaneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } else {
                    Intent wirelessIntent = new Intent(
                            Settings.ACTION_WIRELESS_SETTINGS);
                    ResolveInfo resolveWirelessInfo = mContext.getPackageManager().resolveActivity(
                            wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (resolveWirelessInfo != null) {
                        pendingIntent = PendingIntent.getActivity(mContext, 0,
                                wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                                pendingIntent);
                    }
                }
            }

            int nOptions = Preferences.getBrightnessOptions(mContext);

            switch (nOptions) {
                case 0: // toggle
                    pendingIntent = PendingIntent.getBroadcast(mContext,
                            0, new Intent(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                case 1: // dialog
                    Intent brightnessIntent = new Intent(mContext, BrightnessActivity.class);

                    pendingIntent = PendingIntent.getActivity(mContext,
                            0, brightnessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                default:
                    pendingIntent = PendingIntent.getBroadcast(mContext,
                            0, new Intent(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
            }

            updateViews.setOnClickPendingIntent(R.id.brightnessWidget,
                    pendingIntent);

            if (canToggleNfc()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(WidgetIntentDefinitions.POWER_NFC_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                        pendingIntent);
            } else {
                if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    NfcManager manager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
                    NfcAdapter adapter = null;

                    if (manager != null) {
                        adapter = manager.getDefaultAdapter();
                    }

                    if (adapter != null) {
                        Intent wirelessIntent;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            wirelessIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        else
                            wirelessIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                                wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                        if (resolveInfo != null) {
                            pendingIntent = PendingIntent.getActivity(mContext, 0,
                                    wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                                    pendingIntent);
                        }
                    }
                }
            }

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_SYNC_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.syncWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_ORIENTATION_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.orientationWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(WidgetIntentDefinitions.POWER_TORCH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.torchWidget,
                    pendingIntent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }

        return updateViews;
    }

    private void updatePowerWidgetStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {

        if (updateViews == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            return;

        try {
            boolean bShowBluetooth = Preferences.getShowBluetooth(mContext, appWidgetId);
            boolean bShowGps = Preferences.getShowGps(mContext, appWidgetId);
            boolean bShowMobile = Preferences.getShowMobile(mContext, appWidgetId);
            boolean bShowRinger = Preferences.getShowRinger(mContext, appWidgetId);
            boolean bShowWifi = Preferences.getShowWiFi(mContext, appWidgetId);
            boolean bShowAirplane = Preferences.getShowAirplane(mContext, appWidgetId);
            boolean bShowBrightness = Preferences.getShowBrightness(mContext, appWidgetId);
            boolean bShowNfc = Preferences.getShowNfc(mContext, appWidgetId);
            boolean bShowSync = Preferences.getShowSync(mContext, appWidgetId);
            boolean bShowOrientation = Preferences.getShowOrientation(mContext, appWidgetId);
            boolean bShowTorch = Preferences.getShowTorch(mContext, appWidgetId);
            boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(mContext, appWidgetId);
            //boolean bShowSettings = Preferences.getShowSettings(this, appWidgetId);

            if (bShowBatteryStatus)
                updateBatteryStatus(updateViews, intentExtra, appWidgetId);
            if (bShowBluetooth)
                updateBluetoothStatus(updateViews, intentExtra, appWidgetId);
            if (bShowWifi)
                updateWifiStatus(updateViews, intentExtra, appWidgetId);
            if (bShowMobile)
                updateDataStatus(updateViews, intentExtra, appWidgetId);
            if (bShowGps)
                updateGpsStatus(updateViews, intentExtra, appWidgetId);
            if (bShowRinger)
                updateRingerStatus(updateViews, intentExtra, appWidgetId);
            if (bShowAirplane)
                updateAirplaneMode(updateViews, intentExtra, appWidgetId);
            if (bShowBrightness)
                updateBrightness(updateViews, intentExtra, appWidgetId);
            if (bShowNfc)
                updateNfcStatus(updateViews, intentExtra, appWidgetId);
            if (bShowSync)
                updateSyncStatus(updateViews, intentExtra, appWidgetId);
            if (bShowOrientation)
                updateOrientation(updateViews, intentExtra, appWidgetId);
            if (bShowTorch)
                updateTorchStatus(updateViews, intentExtra, appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }

    private RemoteViews buildClockUpdate(int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService buildClockUpdate");

        RemoteViews updateViews = new RemoteViews(mContext.getPackageName(),
                R.layout.digitalclockwidget);

        updateViews.setViewVisibility(R.id.loadingWidget,
                View.GONE);

        updateViews.setViewVisibility(R.id.clockWidget,
                View.VISIBLE);

        boolean bShowDate = Preferences.getShowDate(mContext, appWidgetId);
        boolean bShowBattery = Preferences.getShowBattery(mContext, appWidgetId);
        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (bShowBattery || bShowDate) {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.GONE);
        }

        if (!showWeather) {
            updateViews.setViewVisibility(R.id.weatherLayout, View.GONE);
            updateViews.setViewVisibility(R.id.imageViewWeather, View.GONE);
            updateViews.setViewVisibility(R.id.refresh_container, View.GONE);
            updateViews.setViewVisibility(R.id.viewButtonRefresh, View.GONE);
            updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
            updateViews.setFloat(R.id.clockWidget, "setWeightSum", 0.65f);
        } else {
            updateViews.setViewVisibility(R.id.weatherLayout, View.VISIBLE);
            updateViews.setViewVisibility(R.id.imageViewWeather, View.VISIBLE);
            updateViews.setViewVisibility(R.id.refresh_container, View.VISIBLE);
            updateViews.setViewVisibility(R.id.viewButtonRefresh, View.VISIBLE);
            updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
            updateViews.setFloat(R.id.clockWidget, "setWeightSum", 1.0f);
        }

        // start preferences when clicked on clock's minutes
        {
            Intent intent = new Intent(mContext, DigitalClockAppWidgetPreferenceActivity.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                    appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.clockWidget, pendingIntent);
        }

        // start clock&alarms application when clicked on clock's hours
        /*{
            Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

            // Verify clock implementation
            String clockImpls[][] = {
                    {"Standard Alarm Clock", "com.android.alarmclock", "com.android.alarmclock.AlarmClock"},
                    {"Standard Desk Clock", "com.android.deskclock", "com.android.deskclock.DeskClock"},
                    {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl"},
                    {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
                    {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock", "com.motorola.blur.alarmclock.AlarmClock"},
                    {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"},
                    {"Sony Alarm", "com.sonyericsson.alarm", "com.sonyericsson.alarm.Alarm"},
                    {"Sony Ericsson Xperia Z", "com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock"},
                    {"ASUS Alarm Clock", "com.asus.alarmclock", "com.asus.alarmclock.AlarmClock"},
                    {"ASUS Desk Clock", "com.asus.deskclock", "com.asus.deskclock.DeskClock"},
            };

            boolean foundClockImpl = false;

            for (String[] clockImpl : clockImpls) {
                String packageName = clockImpl[1];
                String className = clockImpl[2];

                try {
                    ComponentName cn = new ComponentName(packageName, className);
                    mContext.getPackageManager().getActivityInfo(cn, PackageManager.GET_META_DATA);
                    alarmClockIntent.setComponent(cn);
                    foundClockImpl = true;
                    break;
                } catch (PackageManager.NameNotFoundException nf) {
                    Log.d(LOG_TAG, "WidgetUpdateService buildClockUpdate nameNotFound");
                }
            }

            if (foundClockImpl) {
                PendingIntent pendingIntentClock = PendingIntent.getActivity(
                        mContext, appWidgetId, alarmClockIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                updateViews.setOnClickPendingIntent(R.id.imageViewClockHour,
                        pendingIntentClock);
            }
        }*/

        // start weather forecast activity when clicked on weather icon
        Intent weatherForecastIntent = new Intent(mContext, WeatherForecastActivity.class);
        weatherForecastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingForecastIntent = PendingIntent.getActivity(mContext,
                appWidgetId, weatherForecastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        updateViews.setOnClickPendingIntent(R.id.imageViewWeather, pendingForecastIntent);
        updateViews.setOnClickPendingIntent(R.id.weatherLayout, pendingForecastIntent);

        // refresh weather data
        Intent refreshIntent = new Intent(WidgetIntentDefinitions.WEATHER_UPDATE);
        //refreshIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.WEATHER_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingRefreshIntent = PendingIntent.getBroadcast(mContext, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.viewButtonRefresh, pendingRefreshIntent);

        return (updateViews);
    }

    private void updateClockStatus(RemoteViews updateViews, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateClockStatus");

        if (updateViews == null) {
            updateViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.digitalclockwidget);
        }

        boolean bShowDate = Preferences.getShowDate(mContext, appWidgetId);
        boolean bShow24Hrs = Preferences.getShow24Hrs(mContext, appWidgetId);
        boolean bShowBattery = Preferences.getShowBattery(mContext, appWidgetId);
        int iClockSkinItem = Preferences.getClockSkin(mContext, appWidgetId);
        int iDateFormatItem = Preferences.getDateFormatItem(mContext, appWidgetId);
        int iOpacity = Preferences.getOpacity(mContext, appWidgetId);
        int iFontItem = Preferences.getFontItem(mContext, appWidgetId);
        boolean bold = Preferences.getBoldText(mContext, appWidgetId);
        int iDateFontItem = Preferences.getDateFontItem(mContext, appWidgetId);
        boolean dateBold = Preferences.getDateBoldText(mContext, appWidgetId);
        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        /*String currentHour, currentMinute;

        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        currentMinute = sdfMinute.format(new Date());

        if (bShow24Hrs) {
            SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
            currentHour = sdfHour.format(new Date());
        } else {
            SimpleDateFormat sdfHour = new SimpleDateFormat("hh");
            currentHour = sdfHour.format(new Date());
        }*/

        String currentTime;

        if (bShow24Hrs) {
            SimpleDateFormat sdfHour = new SimpleDateFormat("HH : mm");
            currentTime = sdfHour.format(new Date());
        } else {
            SimpleDateFormat sdfHour = new SimpleDateFormat("hh : mm");
            currentTime = sdfHour.format(new Date());
        }

        if (bShowBattery || bShowDate) {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.GONE);
        }

        int systemClockColor;
        int iClockColorItem = Preferences.getClockColorItem(mContext, appWidgetId);

        if (iClockColorItem >= 0) {
            switch (iClockColorItem) {
                case 0:
                    systemClockColor = Color.BLACK;
                    break;
                case 1:
                    systemClockColor = Color.DKGRAY;
                    break;
                case 2:
                    systemClockColor = Color.GRAY;
                    break;
                case 3:
                    systemClockColor = Color.LTGRAY;
                    break;
                case 4:
                    systemClockColor = Color.WHITE;
                    break;
                case 5:
                    systemClockColor = Color.RED;
                    break;
                case 6:
                    systemClockColor = Color.GREEN;
                    break;
                case 7:
                    systemClockColor = Color.BLUE;
                    break;
                case 8:
                    systemClockColor = Color.YELLOW;
                    break;
                case 9:
                    systemClockColor = Color.CYAN;
                    break;
                case 10:
                    systemClockColor = Color.MAGENTA;
                    break;
                default:
                    systemClockColor = Color.WHITE;
                    break;
            }

            Preferences.setClockColorItem(mContext, appWidgetId, -1);
            Preferences.setClockColor(mContext, appWidgetId, systemClockColor);
        } else {
            systemClockColor = Preferences.getClockColor(mContext, appWidgetId);
        }

        int systemDateColor;
        int iDateColorItem = Preferences.getDateColorItem(mContext, appWidgetId);

        if (iDateColorItem >= 0) {
            switch (iDateColorItem) {
                case 0:
                    systemDateColor = Color.BLACK;
                    break;
                case 1:
                    systemDateColor = Color.DKGRAY;
                    break;
                case 2:
                    systemDateColor = Color.GRAY;
                    break;
                case 3:
                    systemDateColor = Color.LTGRAY;
                    break;
                case 4:
                    systemDateColor = Color.WHITE;
                    break;
                case 5:
                    systemDateColor = Color.RED;
                    break;
                case 6:
                    systemDateColor = Color.GREEN;
                    break;
                case 7:
                    systemDateColor = Color.BLUE;
                    break;
                case 8:
                    systemDateColor = Color.YELLOW;
                    break;
                case 9:
                    systemDateColor = Color.CYAN;
                    break;
                case 10:
                    systemDateColor = Color.MAGENTA;
                    break;
                default:
                    systemDateColor = Color.WHITE;
                    break;
            }

            Preferences.setDateColorItem(mContext, appWidgetId, -1);
            Preferences.setDateColor(mContext, appWidgetId, systemDateColor);
        } else {
            systemDateColor = Preferences.getDateColor(mContext, appWidgetId);
        }

        int systemWidgetColor;
        int iWidgetColorItem = Preferences.getWidgetColorItem(mContext, appWidgetId);

        if (iWidgetColorItem >= 0) {
            switch (iWidgetColorItem) {
                case 0:
                    systemWidgetColor = Color.BLACK;
                    break;
                case 1:
                    systemWidgetColor = Color.DKGRAY;
                    break;
                case 2:
                    systemWidgetColor = Color.GRAY;
                    break;
                case 3:
                    systemWidgetColor = Color.LTGRAY;
                    break;
                case 4:
                    systemWidgetColor = Color.WHITE;
                    break;
                case 5:
                    systemWidgetColor = Color.RED;
                    break;
                case 6:
                    systemWidgetColor = Color.GREEN;
                    break;
                case 7:
                    systemWidgetColor = Color.BLUE;
                    break;
                case 8:
                    systemWidgetColor = Color.YELLOW;
                    break;
                case 9:
                    systemWidgetColor = Color.CYAN;
                    break;
                case 10:
                    systemWidgetColor = Color.MAGENTA;
                    break;
                default:
                    systemWidgetColor = Color.BLACK;
                    break;
            }

            Preferences.setWidgetColorItem(mContext, appWidgetId, -1);
            Preferences.setWidgetColor(mContext, appWidgetId, systemWidgetColor);
        } else {
            systemWidgetColor = Preferences.getWidgetColor(mContext, appWidgetId);
        }

        String[] mFontArray = mContext.getResources().getStringArray(R.array.fontPathValues);

        String font = "fonts/Roboto.ttf";

        if (mFontArray.length > iFontItem)
            font = mFontArray[iFontItem];

        String dateFont = "fonts/Roboto.ttf";

        if (mFontArray.length > iDateFontItem)
            dateFont = mFontArray[iDateFontItem];

        /*updateViews.setImageViewBitmap(R.id.imageViewClockHour, getFontBitmap(mContext, currentHour, systemClockColor, font, bold, 96));
        updateViews.setImageViewBitmap(R.id.imageViewClockMinute, getFontBitmap(mContext, currentMinute, systemClockColor, font, bold, 96));
        updateViews.setImageViewBitmap(R.id.imageViewClockSpace, getFontBitmap(mContext, ":", systemClockColor, font, bold, 96));*/
        updateViews.setImageViewBitmap(R.id.imageViewTime, getFontBitmap(mContext, currentTime, systemClockColor, font, bold, 256));

        String currentDate = "";
        String[] mTestArray = mContext.getResources().getStringArray(R.array.dateFormat);

        if (bShowDate) {
            SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
            currentDate = sdf.format(new Date());
        }

        if (bShowBattery) {

            Intent intentBattery = new Intent(Intent.ACTION_BATTERY_CHANGED);

            int rawLevel = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;

            if (rawLevel >= 0 && scale > 0) {
                level = (rawLevel * 100) / scale;
            }

            if (level == -1) {
                Intent batteryIntent = mContext
                        .registerReceiver(null,
                                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int rawLevel1 = 0;

                if (batteryIntent != null) {
                    rawLevel1 = batteryIntent.getIntExtra(
                            BatteryManager.EXTRA_LEVEL, -1);

                    int scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                            -1);
                    if (rawLevel1 >= 0 && scale1 > 0) {
                        level = (rawLevel1 * 100) / scale1;
                    }
                }
            }

            currentDate = currentDate + (bShowDate ? "\t [" : "") + level + "%" + (bShowDate ? "]" : "");
        }

        updateViews.setImageViewBitmap(R.id.imageViewDate, getFontBitmap(mContext, currentDate, systemDateColor, dateFont, dateBold, 14));

        updateViews.setInt(R.id.backgroundImage, "setAlpha", iOpacity * 255 / 100);
        updateViews.setInt(R.id.backgroundImage, "setColorFilter", systemWidgetColor);

        //updateViews.setViewVisibility(R.id.imageViewClockSpace, View.INVISIBLE);

        /*switch (iClockSkinItem) {
            case 0:
                updateViews.setInt(R.id.imageViewClockHour, "setBackgroundResource", R.drawable.bck_left);
                updateViews.setInt(R.id.imageViewClockMinute, "setBackgroundResource", R.drawable.bck_right);
                break;
            case 1:
                updateViews.setInt(R.id.imageViewClockHour, "setBackgroundResource", R.drawable.bck_left_light);
                updateViews.setInt(R.id.imageViewClockMinute, "setBackgroundResource", R.drawable.bck_right_light);
                break;
            case 2:
                updateViews.setInt(R.id.imageViewClockHour, "setBackgroundResource", 0);
                updateViews.setInt(R.id.imageViewClockMinute, "setBackgroundResource", 0);
                updateViews.setViewVisibility(R.id.imageViewClockSpace, View.VISIBLE);
                break;
            default:
                updateViews.setInt(R.id.imageViewClockHour, "setBackgroundResource", R.drawable.bck_left);
                updateViews.setInt(R.id.imageViewClockMinute, "setBackgroundResource", R.drawable.bck_right);
                break;
        }*/
        switch (iClockSkinItem) {
            case 0:
                updateViews.setInt(R.id.imageViewTime, "setBackgroundColor", Color.BLACK);
                break;
            case 1:
                updateViews.setInt(R.id.imageViewTime, "setBackgroundColor", Color.WHITE);
                break;
            case 2:
                updateViews.setInt(R.id.imageViewTime, "setBackgroundColor", Color.TRANSPARENT);
                break;
            default:
                updateViews.setInt(R.id.imageViewTime, "setBackgroundColor", Color.BLACK);
                break;
        }

        if (!showWeather) {
            updateViews.setViewVisibility(R.id.weatherLayout, View.GONE);
            updateViews.setViewVisibility(R.id.imageViewWeather, View.GONE);
            updateViews.setViewVisibility(R.id.refresh_container, View.GONE);
            updateViews.setViewVisibility(R.id.viewButtonRefresh, View.GONE);
            updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
            updateViews.setFloat(R.id.clockWidget, "setWeightSum", 0.65f);
        } else {
            updateViews.setViewVisibility(R.id.weatherLayout, View.VISIBLE);
            updateViews.setViewVisibility(R.id.imageViewWeather, View.VISIBLE);
            updateViews.setViewVisibility(R.id.refresh_container, View.VISIBLE);
            updateViews.setViewVisibility(R.id.viewButtonRefresh, View.VISIBLE);
            updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
            updateViews.setFloat(R.id.clockWidget, "setWeightSum", 1.0f);

            int systemWeatherColor;
            int iWeatherColorItem = Preferences.getWeatherColorItem(mContext, appWidgetId);

            if (iWeatherColorItem >= 0) {
                switch (iWeatherColorItem) {
                    case 0:
                        systemWeatherColor = Color.BLACK;
                        break;
                    case 1:
                        systemWeatherColor = Color.DKGRAY;
                        break;
                    case 2:
                        systemWeatherColor = Color.GRAY;
                        break;
                    case 3:
                        systemWeatherColor = Color.LTGRAY;
                        break;
                    case 4:
                        systemWeatherColor = Color.WHITE;
                        break;
                    case 5:
                        systemWeatherColor = Color.RED;
                        break;
                    case 6:
                        systemWeatherColor = Color.GREEN;
                        break;
                    case 7:
                        systemWeatherColor = Color.BLUE;
                        break;
                    case 8:
                        systemWeatherColor = Color.YELLOW;
                        break;
                    case 9:
                        systemWeatherColor = Color.CYAN;
                        break;
                    case 10:
                        systemWeatherColor = Color.MAGENTA;
                        break;
                    default:
                        systemWeatherColor = Color.WHITE;
                        break;
                }

                Preferences.setWeatherColorItem(mContext, appWidgetId, -1);
                Preferences.setWeatherColor(mContext, appWidgetId, systemWeatherColor);
            } else {
                systemWeatherColor = Preferences.getWeatherColor(mContext, appWidgetId);
            }

            updateViews.setInt(R.id.viewButtonRefresh, "setColorFilter", systemWeatherColor);
        }

        if (showWeather) {
            readCachedWeatherData(updateViews, appWidgetId);
        }
    }

    private void updateWeatherStatus(final RemoteViews updateViews, final int appWidgetId, final boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService updateWeatherStatus appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (!showWeather)
            return;

        updateViews.setViewVisibility(R.id.viewButtonRefresh, View.GONE);
        updateViews.setViewVisibility(R.id.progressRefresh, View.VISIBLE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);

        try {
            if (!scheduledUpdate)
                Toast.makeText(mContext, mContext.getResources().getText(R.string.updatingweather), Toast.LENGTH_LONG).show();

            boolean bWiFiOnly = Preferences.getRefreshWiFiOnly(mContext, appWidgetId);
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = null;

            if (connectivityManager != null) {
                mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            }

            if (scheduledUpdate && bWiFiOnly && mWifi != null && !mWifi.isConnected()) {
                Preferences.setWeatherSuccess(mContext, appWidgetId, false);
                readCachedWeatherData(updateViews, appWidgetId);

                return;
            }

            NetworkInfo activeNetworkInfo = null;

            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (activeNetworkInfo == null) {
                if (scheduledUpdate)
                    Preferences.setWeatherSuccess(mContext, appWidgetId, false);

                readCachedWeatherData(updateViews, appWidgetId);
                return;
            }

            int locationType = Preferences.getLocationType(mContext, appWidgetId);

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                // obtain location first
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = null;

                if (locationManager != null) {
                    providers = locationManager.getProviders(new Criteria(), true);
                }

                if (providers!= null && !providers.isEmpty()) {

                    LocationProvider.LocationResult locationResult = new LocationProvider.LocationResult() {
                        @Override
                        public void gotLocation(Location location) {

                            if (location != null) {
                                Preferences.setLocationLat(mContext, appWidgetId, (float) location.getLatitude());
                                Preferences.setLocationLon(mContext, appWidgetId, (float) location.getLongitude());
                                Preferences.setLocationId(mContext, appWidgetId, -1);

                                startWeatherUpdate(updateViews, appWidgetId, scheduledUpdate);
                            } else {
                                Preferences.setWeatherSuccess(mContext, appWidgetId, false);
                                readCachedWeatherData(updateViews, appWidgetId);
                            }
                        }
                    };

                    LocationProvider loc = new LocationProvider();
                    loc.getLocation(mContext, locationResult);
                } else {
                    Preferences.setWeatherSuccess(mContext, appWidgetId, false);
                    readCachedWeatherData(updateViews, appWidgetId);
                }
            } else {
                startWeatherUpdate(updateViews, appWidgetId, scheduledUpdate);
            }

        } catch (Exception e) {
            e.printStackTrace();

            Preferences.setWeatherSuccess(mContext, appWidgetId, false);
            readCachedWeatherData(updateViews, appWidgetId);
        }
    }

    private void startWeatherUpdate(RemoteViews updateViews, int appWidgetId, boolean scheduledUpdate) {

        SQLiteDbAdapter dbHelper = new SQLiteDbAdapter(mContext);

        // create location in database from old preferences
        if (Preferences.getLocationType(mContext, appWidgetId) == ConfigureLocationActivity.LOCATION_TYPE_CUSTOM) {
            dbHelper.open();
            Cursor locationsCursor = dbHelper.fetchAllLocations();

            float latPref = Preferences.getLocationLat(mContext, appWidgetId);
            float lonPref = Preferences.getLocationLon(mContext, appWidgetId);
            String locationPref = Preferences.getLocation(mContext, appWidgetId);
            long locationIDPref = Preferences.getLocationId(mContext, appWidgetId);
            boolean bFound = false;

            if (locationsCursor != null && locationsCursor.getCount() > 0) {
                locationsCursor.moveToFirst();

                do {
                    long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                    if (locId != -1 && locationIDPref == locId) {
                        bFound = true;
                    }

                    locationsCursor.moveToNext();

                } while (!locationsCursor.isAfterLast());
            }

            if (!bFound && locationIDPref >= 0) {
                dbHelper.createLocation(locationIDPref, latPref, lonPref, locationPref);
            }

            dbHelper.close();
        }

        dbHelper.open();
        Cursor locationsCursor = dbHelper.fetchAllLocations();
        List<Long> locations = new ArrayList<>();

        if (locationsCursor != null && locationsCursor.getCount() > 0) {
            locationsCursor.moveToFirst();

            do {
                long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                if (locId != -1) {
                    locations.add(locId);
                }

                locationsCursor.moveToNext();

            } while (!locationsCursor.isAfterLast());
        }

        dbHelper.close();

        int locationType = Preferences.getLocationType(mContext, appWidgetId);

        if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
            WidgetManager.HttpTaskInfo info = new WidgetManager.HttpTaskInfo();
            info.updateViews = updateViews;
            info.appWidgetId = appWidgetId;
            info.scheduledUpdate = scheduledUpdate;
            info.locationId = -1;
            info.context = mContext;

            info.last = locations.size() <= 0;

            new WidgetManager.HttpTask().execute(info);
        }

        for (int i = 0; i < locations.size(); i++) {
            WidgetManager.HttpTaskInfo info = new WidgetManager.HttpTaskInfo();
            info.updateViews = updateViews;
            info.appWidgetId = appWidgetId;
            info.scheduledUpdate = scheduledUpdate;
            info.locationId = locations.get(i);
            info.context = mContext;

            info.last = i == locations.size() - 1;

            new WidgetManager.HttpTask().execute(info);
        }
    }

    private class HttpTask extends AsyncTask<WidgetManager.HttpTaskInfo, Void, Boolean> {

        RemoteViews updateViews = null;
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        boolean scheduledUpdate = false;
        long locId = -1;
        Context context = null;
        boolean last = false;

        public Boolean doInBackground(WidgetManager.HttpTaskInfo... info) {
            updateViews = info[0].updateViews;
            appWidgetId = info[0].appWidgetId;
            scheduledUpdate = info[0].scheduledUpdate;
            locId = info[0].locationId;
            context = info[0].context;
            last = info[0].last;

            boolean current = getCurrentWeatherData(updateViews, appWidgetId, locId, scheduledUpdate);
            boolean forecast = getForecastData(updateViews, appWidgetId, locId, scheduledUpdate);

            return (current || forecast);
        }

        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                //sendBroadcast(new Intent(UPDATE_FORECAST));

				/*Intent intent = new Intent(WEATHER_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	    		sendBroadcast(intent);*/

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, updateViews);
            }

            if (last) {
                updateViews.setViewVisibility(R.id.viewButtonRefresh, View.VISIBLE);
                updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, updateViews);

                Intent intent = new Intent(WidgetIntentDefinitions.UPDATE_FORECAST);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                mContext.sendBroadcast(intent);
            }
        }
    }

    class HttpTaskInfo {
        RemoteViews updateViews;
        int appWidgetId;
        boolean scheduledUpdate;
        long locationId;
        Context context;
        boolean last;
    }

    @SuppressWarnings("unused")
    private boolean getCurrentWeatherData(RemoteViews updateViews, int appWidgetId, long locId, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService getCurrentWeatherData appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (!showWeather)
            return false;

        float lat = Preferences.getLocationLat(mContext, appWidgetId);
        float lon = Preferences.getLocationLon(mContext, appWidgetId);

        String lang = Preferences.getLanguageOptions(mContext);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals(""))
                lang = langDef;
            else
                lang = "en";
        }

        if ((locId == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
            if (scheduledUpdate)
                Preferences.setWeatherSuccess(mContext, appWidgetId, false);

            readCachedWeatherData(updateViews, appWidgetId);

            return false;
        }

        boolean ret = true;
        try {
            Reader responseReader;
            HttpClient client = new DefaultHttpClient();
            HttpGet request;

            if (locId >= 0) {
                request = new HttpGet(String.format(WidgetIntentDefinitions.WEATHER_SERVICE_ID_URL, locId/*Preferences.getLocationId(mContext, appWidgetId)*/, lang));
            } else if (lat != -222 && lon != -222 && !Float.isNaN(lat) && !Float.isNaN(lon)) {
                request = new HttpGet(String.format(WidgetIntentDefinitions.WEATHER_SERVICE_COORD_URL, lat, lon, lang));
            } else {
                if (scheduledUpdate)
                    Preferences.setWeatherSuccess(mContext, appWidgetId, false);

                readCachedWeatherData(updateViews, appWidgetId);

                return false;
            }

            HttpResponse response = client.execute(request);

            StatusLine status = response.getStatusLine();
            Log.d(LOG_TAG, "Request returned status " + status);

            HttpEntity entity = response.getEntity();
            responseReader = new InputStreamReader(entity.getContent());

            char[] buf = new char[1024];
            StringBuilder result = new StringBuilder();
            int read = responseReader.read(buf);

            while (read >= 0) {
                result.append(buf, 0, read);
                read = responseReader.read(buf);
            }

            if (result.length() > 0) {
                String parseString = result.toString();

                if (!parseString.isEmpty() && !parseString.contains("<html>")) {
                    parseString = parseString.trim();

                    if (parseString.endsWith("\n"))
                        parseString = parseString.substring(0, parseString.length() - 1);

                    String start = parseString.substring(0, 1);
                    String end = parseString.substring(parseString.length() - 1, parseString.length());

                    if ((start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                            || (start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {

                        long locIdTemp = Preferences.getLocationId(mContext, appWidgetId);

                        if (locId >= 0 && locId == locIdTemp || locId == -1 && !Float.isNaN(lat) && !Float.isNaN(lon))
                            parseWeatherData(updateViews, appWidgetId, result.toString(), false, scheduledUpdate);

                        // save cache
                        File parentDirectory = new File(mContext.getFilesDir().getAbsolutePath());

                        if (!parentDirectory.exists()) {
                            Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                            if (!parentDirectory.mkdirs()) {
                                Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                            }
                        }

                        File cacheFile;

                        if (locId >= 0)
                            cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);
                        else
                            cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

                        cacheFile.createNewFile();

                        final BufferedWriter cacheWriter = new BufferedWriter(new FileWriter(cacheFile), result.length());
                        cacheWriter.write(result.toString());
                        cacheWriter.close();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (scheduledUpdate)
                Preferences.setWeatherSuccess(mContext, appWidgetId, false);

            readCachedWeatherData(updateViews, appWidgetId);

            ret = false;
        }

        return ret;
    }

    @SuppressWarnings("unused")
    private boolean getForecastData(RemoteViews updateViews, int appWidgetId, long locId, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService getForecastData appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (!showWeather)
            return false;

        float lat = Preferences.getLocationLat(mContext, appWidgetId);
        float lon = Preferences.getLocationLon(mContext, appWidgetId);

        String lang = Preferences.getLanguageOptions(mContext);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals(""))
                lang = langDef;
            else
                lang = "en";
        }

        if ((locId == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
            if (scheduledUpdate)
                Preferences.setForecastSuccess(mContext, appWidgetId, false);

            return false;
        }

        boolean ret = true;
        try {
            Reader responseReader;
            HttpClient client = new DefaultHttpClient();
            HttpGet request;

            if (locId >= 0) {
                request = new HttpGet(String.format(WidgetIntentDefinitions.WEATHER_FORECAST_ID_URL, locId/*Preferences.getLocationId(mContext, appWidgetId)*/, lang));
            } else if (lat != -222 && lon != -222 && !Float.isNaN(lat) && !Float.isNaN(lon)) {
                request = new HttpGet(String.format(WidgetIntentDefinitions.WEATHER_FORECAST_COORD_URL, Preferences.getLocationLat(mContext, appWidgetId),
                        Preferences.getLocationLon(mContext, appWidgetId), lang));
            } else {
                if (scheduledUpdate)
                    Preferences.setForecastSuccess(mContext, appWidgetId, false);

                return false;
            }

            HttpResponse response = client.execute(request);

            StatusLine status = response.getStatusLine();
            Log.d(LOG_TAG, "Request returned status " + status);

            HttpEntity entity = response.getEntity();
            responseReader = new InputStreamReader(entity.getContent());

            char[] buf = new char[1024];
            StringBuilder result = new StringBuilder();
            int read = responseReader.read(buf);

            while (read >= 0) {
                result.append(buf, 0, read);
                read = responseReader.read(buf);
            }

            if (result.length() > 0) {
                String parseString = result.toString();

                if (!parseString.isEmpty() && !parseString.contains("<html>")) {
                    parseString = parseString.trim();

                    if (parseString.endsWith("\n"))
                        parseString = parseString.substring(0, parseString.length() - 1);

                    String start = parseString.substring(0, 1);
                    String end = parseString.substring(parseString.length() - 1, parseString.length());

                    if ((start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                            || (start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {
                        // save cache
                        File parentDirectory = new File(mContext.getFilesDir().getAbsolutePath());

                        if (!parentDirectory.exists()) {
                            Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                            if (!parentDirectory.mkdirs()) {
                                Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                            }
                        }

                        File cacheFile;

                        if (locId >= 0)
                            cacheFile = new File(parentDirectory, "forecast_cache_loc_" + locId);
                        else
                            cacheFile = new File(parentDirectory, "forecast_cache_" + appWidgetId);

                        cacheFile.createNewFile();

                        final BufferedWriter cacheWriter = new BufferedWriter(new FileWriter(cacheFile), result.length());
                        cacheWriter.write(result.toString());
                        cacheWriter.close();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (scheduledUpdate)
                Preferences.setForecastSuccess(mContext, appWidgetId, false);

            ret = false;
        }

        return ret;
    }

    @SuppressWarnings("unused")
    private void readCachedWeatherData(RemoteViews updateViews, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService readCachedWeatherData appWidgetId: " + appWidgetId);

        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (!showWeather)
            return;

        updateViews.setViewVisibility(R.id.viewButtonRefresh, View.VISIBLE);
        updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);

        Intent intent = new Intent(WidgetIntentDefinitions.UPDATE_FORECAST);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        mContext.sendBroadcast(intent);

        try {
            File parentDirectory = new File(mContext.getFilesDir().getAbsolutePath());

            if (!parentDirectory.exists()) {
                Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                if (!parentDirectory.mkdirs()) {
                    Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                }
            }

            File cacheFile;

            long locId = Preferences.getLocationId(mContext, appWidgetId);

            if (locId >= 0) {
                cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);

                if (!cacheFile.exists()) {
                    cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

                    if (!cacheFile.exists())
                        return;
                }
            } else {
                cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

                if (!cacheFile.exists())
                    return;
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

            parseWeatherData(updateViews, appWidgetId, result.toString(), true, false);

        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void parseWeatherData(RemoteViews updateViews, int appWidgetId, String parseString, boolean updateFromCache, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData appWidgetId: " + appWidgetId + " cache: " + updateFromCache + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(mContext, appWidgetId);

        if (!showWeather)
            return;

        if (updateViews == null) {
            updateViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.digitalclockwidget);
        }

        if (parseString.isEmpty() || parseString.contains("<html>"))
            return;

        parseString = parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1, parseString.length());

        if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]")))
            return;

        int iFontItem = Preferences.getWeatherFontItem(mContext, appWidgetId);
        boolean bold = Preferences.getWeatherBoldText(mContext, appWidgetId);

        int systemWeatherColor;
        int iWeatherColorItem = Preferences.getWeatherColorItem(mContext, appWidgetId);

        if (iWeatherColorItem >= 0) {
            switch (iWeatherColorItem) {
                case 0:
                    systemWeatherColor = Color.BLACK;
                    break;
                case 1:
                    systemWeatherColor = Color.DKGRAY;
                    break;
                case 2:
                    systemWeatherColor = Color.GRAY;
                    break;
                case 3:
                    systemWeatherColor = Color.LTGRAY;
                    break;
                case 4:
                    systemWeatherColor = Color.WHITE;
                    break;
                case 5:
                    systemWeatherColor = Color.RED;
                    break;
                case 6:
                    systemWeatherColor = Color.GREEN;
                    break;
                case 7:
                    systemWeatherColor = Color.BLUE;
                    break;
                case 8:
                    systemWeatherColor = Color.YELLOW;
                    break;
                case 9:
                    systemWeatherColor = Color.CYAN;
                    break;
                case 10:
                    systemWeatherColor = Color.MAGENTA;
                    break;
                default:
                    systemWeatherColor = Color.WHITE;
                    break;
            }

            Preferences.setWeatherColorItem(mContext, appWidgetId, -1);
            Preferences.setWeatherColor(mContext, appWidgetId, systemWeatherColor);
        } else {
            systemWeatherColor = Preferences.getWeatherColor(mContext, appWidgetId);
        }

        String[] mFontArray = mContext.getResources().getStringArray(R.array.fontPathValues);

        String font = "fonts/Roboto.ttf";

        if (mFontArray.length > iFontItem)
            font = mFontArray[iFontItem];

        try {
            JSONTokener parser = new JSONTokener(parseString);
            int tempScale = Preferences.getTempScale(mContext, appWidgetId);

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
            String temp;

            int locationType = Preferences.getLocationType(mContext, appWidgetId);

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                Preferences.setLocation(mContext, appWidgetId, location);
            }

            updateViews.setImageViewBitmap(R.id.imageViewLoc, getFontBitmap(mContext, location, systemWeatherColor, font, bold, 12));

            long timestamp = weatherJSON.getLong("dt");
            Date time = new Date(timestamp * 1000);

            JSONObject main = null;
            try {
                main = weatherJSON.getJSONObject("main");
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }
            try {
                double currentTemp = 0;
                if (main != null) {
                    currentTemp = main.getDouble("temp") - 273.15;

                    if (tempScale == 1)
                        temp = String.valueOf((int) (currentTemp * 1.8 + 32)) + "°";
                    else
                        temp = String.valueOf((int) currentTemp) + "°";

                    updateViews.setImageViewBitmap(R.id.imageViewTemp, getFontBitmap(mContext, temp, systemWeatherColor, font, bold, 32));
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }

            JSONObject windJSON = null;
            try {
                windJSON = weatherJSON.getJSONObject("wind");
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }
            try {
                if (windJSON != null) {
                    double speed = windJSON.getDouble("speed");
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }
            try {
                if (windJSON != null) {
                    double deg = windJSON.getDouble("deg");
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }

            try {
                double humidityValue = weatherJSON.getJSONObject("main").getDouble("humidity");
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
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

                    WeatherConditions conditions = new WeatherConditions();

                    int icons = Preferences.getWeatherIcons(mContext, appWidgetId);
                    int resource;
                    WeatherIcon[] imageArr;

                    updateViews.setImageViewBitmap(R.id.imageViewDesc, getFontBitmap(mContext, weatherDesc, systemWeatherColor, font, bold, 12));

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
                        case 17:
                            resource = R.drawable.flaticon_weather_04d;
                            imageArr = conditions.m_ImageArrFlaticon;
                            break;
                        case 18:
                            resource = R.drawable.icon8_weather_04d;
                            imageArr = conditions.m_ImageArrIcon8;
                            break;
                        case 19:
                            resource = R.drawable.crystal_weather_04d;
                            imageArr = conditions.m_ImageArrCrystal;
                            break;
                        case 20:
                            resource = R.drawable.deszone_weather_04d;
                            imageArr = conditions.m_ImageArrDesZone;
                            break;
                        case 21:
                            resource = R.drawable.modern_weather_04d;
                            imageArr = conditions.m_ImageArrModern;
                            break;
                        case 22:
                            resource = R.drawable.simplistic_weather_04d;
                            imageArr = conditions.m_ImageArrSimplistic;
                            break;
                        default:
                            resource = R.drawable.tick_weather_04d;
                            imageArr = conditions.m_ImageArrTick;
                            break;
                    }

                    updateViews.setImageViewResource(R.id.imageViewWeather, resource);

                    float lat = Preferences.getLocationLat(mContext, appWidgetId);
                    float lon = Preferences.getLocationLon(mContext, appWidgetId);
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

                            if (anImageArr.bDay != bDay)
                                updateViews.setImageViewResource(R.id.imageViewWeather, anImageArr.altIconId);
                            else
                                updateViews.setImageViewResource(R.id.imageViewWeather, anImageArr.iconId);
                        }
                    }
                }
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }

            if (!updateFromCache) {
                Preferences.setLastRefresh(mContext, appWidgetId, System.currentTimeMillis());

                //if (scheduledUpdate)
                Preferences.setWeatherSuccess(mContext, appWidgetId, true);

                // reset automated refresh service / job
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DigitalClockAppWidgetProvider.scheduleJob(mContext, appWidgetId);
                } else {
                    DigitalClockAppWidgetProvider.setAlarm(mContext, appWidgetId);
                }
            }

            long lastRefresh = Preferences.getLastRefresh(mContext, appWidgetId);

            if (lastRefresh > 0) {
                boolean bShow24Hrs = Preferences.getShow24Hrs(mContext, appWidgetId);
                int iDateFormatItem = Preferences.getDateFormatItem(mContext, appWidgetId);
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
                String[] mTestArray = mContext.getResources().getStringArray(R.array.dateFormat);

                SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
                currentDate = sdf.format(resultDate);

                updateViews.setImageViewBitmap(R.id.imageViewLast,
                        getFontBitmap(mContext, currentDate + ", " + currentTime, systemWeatherColor, font, bold, 12));
            } else {
                updateViews.setImageViewBitmap(R.id.imageViewLast,
                        getFontBitmap(mContext, mContext.getResources().getString(R.string.lastrefreshnever), systemWeatherColor, font, bold, 12));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static BitmapDrawable setIconColor(Context context, int color, int drawable) {
        if (color == 0) {
            color = 0xffffffff;
        }

        final Resources res = context.getResources();
        Drawable maskDrawable = res.getDrawable(drawable);
        if (!(maskDrawable instanceof BitmapDrawable)) {
            return null;
        }

        Bitmap maskBitmap = ((BitmapDrawable) maskDrawable).getBitmap();
        final int width = maskBitmap.getWidth();
        final int height = maskBitmap.getHeight();

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawBitmap(maskBitmap, 0, 0, null);

        Paint maskedPaint = new Paint();
        maskedPaint.setColor(color);
        maskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        canvas.drawRect(0, 0, width, height, maskedPaint);

        return new BitmapDrawable(res, outBitmap);
    }

    public static Bitmap getFontBitmap(Context context, String text, int color, String font, boolean bold, float fontSizeSP) {
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setFakeBoldText(bold);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, (float) pad, fontSizePX, paint);

        return bitmap;
    }

    private static int convertDiptoPix(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    private void updateBatteryStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateBatteryStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BATTERY_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)) {
            return;
        }

        Intent intentBattery = new Intent(Intent.ACTION_BATTERY_CHANGED);

        int rawLevel = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intentBattery.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);

        int level = -1;
        int icon = R.drawable.battery_widget_75;

        if (rawLevel >= 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
        }

        if (level == -1) {
            Intent batteryIntent = mContext.registerReceiver(
                    null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int rawLevel1 = 0;

            if (batteryIntent != null) {
                rawLevel1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
            }

            int scale1 = 0;

            if (batteryIntent != null) {
                scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                        -1);
            }

            int status1 = BatteryManager.BATTERY_STATUS_UNKNOWN;

            if (batteryIntent != null) {
                status1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
            }

            if (rawLevel1 >= 0 && scale1 > 0) {
                level = (rawLevel1 * 100) / scale1;
            }

            status = status1;
        }

        String strLevel = String.valueOf(level);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            int lnColon = strLevel.length();
            SpannableString spStrLevel = new SpannableString(strLevel);
            spStrLevel.setSpan(new StyleSpan(Typeface.BOLD), 0, lnColon, 0);

            updateViews.setTextViewText(R.id.textViewBatteryStatus, spStrLevel);

            switch (status) {

                case (BatteryManager.BATTERY_STATUS_UNKNOWN):
                    icon = R.drawable.battery_widget_75;
                    break;
                case (BatteryManager.BATTERY_STATUS_FULL):
                    icon = R.drawable.battery_widget_100;
                    break;
                case (BatteryManager.BATTERY_STATUS_CHARGING):
                    if (level < 25)
                        icon = R.drawable.battery_widget_25_charging;
                    else if (level < 50)
                        icon = R.drawable.battery_widget_50_charging;
                    else if (level < 75)
                        icon = R.drawable.battery_widget_75_charging;
                    else
                        icon = R.drawable.battery_widget_100_charging;
                    break;
                case (BatteryManager.BATTERY_STATUS_DISCHARGING):
                case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
                    if (level < 25)
                        icon = R.drawable.battery_widget_25;
                    else if (level < 50)
                        icon = R.drawable.battery_widget_50;
                    else if (level < 75)
                        icon = R.drawable.battery_widget_75;
                    else
                        icon = R.drawable.battery_widget_100;
                    break;
            }

            updateViews.setImageViewResource(R.id.imageViewBattery, icon);
        } else {
            int threshold2 = Preferences.getThresholdBattery2(mContext, appWidgetId);
            int threshold3 = Preferences.getThresholdBattery3(mContext, appWidgetId);
            int threshold4 = Preferences.getThresholdBattery4(mContext, appWidgetId);

            int color1 = Preferences.getColorBattery1(mContext, appWidgetId);
            int color2 = Preferences.getColorBattery2(mContext, appWidgetId);
            int color3 = Preferences.getColorBattery3(mContext, appWidgetId);
            int color4 = Preferences.getColorBattery4(mContext, appWidgetId);

            int color;
            String font = "fonts/Roboto.ttf";

            if (level < threshold2)
                color = color1;
            else if (level >= threshold2 && level < threshold3)
                color = color2;
            else if (level >= threshold3 && level < threshold4)
                color = color3;
            else
                color = color4;

            updateViews.setImageViewBitmap(R.id.imageViewBatteryStatus, getFontBitmap(mContext, strLevel, color, font, true, 144));
            updateViews.setInt(R.id.imageViewBatteryStatusInd, "setColorFilter", color);

            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.VISIBLE);
                BitmapDrawable bitmapDrawable = setIconColor(mContext, color, R.drawable.high_voltage);

                if (bitmapDrawable != null)
                    updateViews.setImageViewBitmap(R.id.imageViewBatteryStatusCharge, bitmapDrawable.getBitmap());
            } else {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.GONE);
            }
        }
    }

    @SuppressLint("NewApi")
    public void updateNotificationBatteryStatus(Context context, Intent intent) {
        Log.d(LOG_TAG, "WidgetUpdateService updateNotificationBatteryStatus");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!Preferences.getShowBatteryNotif(context)) {
            if (notificationManager != null) {
                notificationManager.cancel(R.string.batterynotification);
            }
            return;
        }

        int rawLevel = intent
                .getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);
        int level = -1;
        int icons = Preferences.getBatteryIcons(context);
        int icon = (icons == 1 ? R.drawable.battery_font_charge_000 : R.drawable.battery_charge_000);

        if (rawLevel >= 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
        }

        if (level == -1) {
            Intent batteryIntent = context.getApplicationContext().registerReceiver(
                    null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int rawLevel1 = 0;

            if (batteryIntent != null) {
                rawLevel1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            }

            int scale1 = 0;

            if (batteryIntent != null) {
                scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            }

            int status1 = BatteryManager.BATTERY_STATUS_UNKNOWN;

            if (batteryIntent != null) {
                status1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
            }

            if (rawLevel1 >= 0 && scale1 > 0) {
                level = (rawLevel1 * 100) / scale1;
            }

            status = status1;
        }

        switch (status) {

            case (BatteryManager.BATTERY_STATUS_UNKNOWN):
                icon = (icons == 1 ? R.drawable.battery_font_000 : R.drawable.battery_000);
                break;
            case (BatteryManager.BATTERY_STATUS_FULL):
                icon = (icons == 1 ? R.drawable.battery_font_full : R.drawable.battery_full);
                break;
            case (BatteryManager.BATTERY_STATUS_CHARGING):
                icon = (icons == 1 ? R.drawable.battery_font_charge_000 : R.drawable.battery_charge_000) + level;
                break;
            case (BatteryManager.BATTERY_STATUS_DISCHARGING):
                icon = (icons == 1 ? R.drawable.battery_font_000 : R.drawable.battery_000) + level;
                break;
            case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
                icon = (icons == 1 ? R.drawable.battery_font_000 : R.drawable.battery_000) + level;
                break;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(0);
        }

        notificationBuilder.setDefaults(Notification.FLAG_NO_CLEAR);
        notificationBuilder.setSmallIcon(icon);
        notificationBuilder.setWhen(0);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setOngoing(true);

        Intent batteryInfo = new Intent(context, BatteryInfoActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, batteryInfo,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence batteryStatus = context.getResources().getText(R.string.batterycurrentstatusfull);

        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batteryunknown);
        } else if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batterycharging);
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batterydisharging);
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batterydisharging);
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batteryfull);
        } else {
            batteryStatus = batteryStatus + " " + context.getResources().getText(R.string.batteryunknown);
        }

        notificationBuilder.setContentTitle(context.getResources().getText(R.string.batterylevelfull) + " " + level + "%");
        notificationBuilder.setContentText(batteryStatus);
        notificationBuilder.setContentIntent(pendingIntent);

        if (notificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String CHANNEL_ID = context.getString(R.string.battery_notification_channel_id);
                CharSequence channelName = context.getString(R.string.battery_notification_channel);
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                notificationManager.createNotificationChannel(notificationChannel);

                notificationBuilder.setChannelId(CHANNEL_ID);
            }

            notificationManager.notify(R.string.batterynotification, notificationBuilder.build());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void toggleWidgets(final String intentExtra) {
        if (intentExtra == null)
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                int nOptions = Preferences.getBrightnessOptions(mContext); // 0 - toggle, 1 - dialog

                if (intentExtra.equals(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_BLUETOOTH_WIDGET_UPDATE)) {
                    toggleBluetoothState();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_WIFI_WIDGET_UPDATE)) {
                    toggleWiFi();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_MOBILE_DATA_WIDGET_UPDATE)) {
                    toggleData();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.GPS_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_GPS_WIDGET_UPDATE)) {
                    toggleGps();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_RINGER_WIDGET_UPDATE)) {
                    toggleRinger();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_AIRPLANE_WIDGET_UPDATE)) {
                    toggleAirplaneMode();
                }

                if (nOptions == 0 && (intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE))) {
                    toggleBrightness();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.NFC_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_NFC_WIDGET_UPDATE)) {
                    toggleNfc();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_SYNC_WIDGET_UPDATE)) {
                    toggleSync();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_ORIENTATION_WIDGET_UPDATE)) {
                    toggleOrientation();
                }

                if (intentExtra.equals(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE) || intentExtra.equals(WidgetIntentDefinitions.POWER_TORCH_WIDGET_UPDATE)) {
                    toggleTorch();
                }
                return null;
            }
        }.execute();
    }

    private boolean canToggleAirplane() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    private boolean getAirplaneMode() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    private void setAirplaneMode(boolean airplaneMode) {

        if (canToggleAirplane()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            } else {
                Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            }

            // Post an intent to reload
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", airplaneMode);
            mContext.sendBroadcast(intent);
        }
    }

    private void toggleAirplaneMode() {
        Boolean airplaneMode = getAirplaneMode();
        // ignore toggle requests if the Airplane mode is currently changing
        // state
        setAirplaneMode(!airplaneMode);
    }

    @SuppressWarnings("unused")
    private void updateAirplaneMode(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateAirplaneMode");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_AIRPLANE_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_AIRPLANE_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewAirplane, mContext.getResources().getText(R.string.airplane));

        Boolean airplaneMode = getAirplaneMode();

        updateViews.setImageViewBitmap(R.id.imageViewAirplane, getFontBitmap(mContext, mContext.getString(R.string.icon_airplane),
                airplaneMode ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

        updateViews.setTextColor(R.id.textViewAirplane, airplaneMode ? colorTextOn : colorTextOff);
        updateViews.setInt(R.id.imageViewAirplaneInd, "setColorFilter", airplaneMode ? colorOn : colorOff);
    }

    private Boolean getBluetoothState() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        Boolean bluetoothState = null;

        if (bluetoothAdapter != null) {
            switch (bluetoothAdapter.getState()) {
                case BluetoothAdapter.STATE_OFF:
                    bluetoothState = false;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    bluetoothState = null;
                    break;
                case BluetoothAdapter.STATE_ON:
                    bluetoothState = true;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    bluetoothState = null;
                    break;
                default:
                    bluetoothState = false;
            }
        }

        Log.v(LOG_TAG, "getBluetoothState - " + bluetoothState);

        return bluetoothState;

    }

    private void setBluetoothState(boolean bluetoothState) {

        Log.v(LOG_TAG, "setBluetoothState - " + bluetoothState);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null) {
            if (bluetoothState) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        }
    }

    private void toggleBluetoothState() {
        Boolean bluetoothState = getBluetoothState();
        // ignore toggle requests if the BlueTooth is currently changing state
        if (bluetoothState != null) {
            setBluetoothState(!bluetoothState);
        }
    }

    private void updateBluetoothStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateBluetoothStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BLUETOOTH_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_BLUETOOTH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBluetooth, mContext.getResources().getText(R.string.bluetooth));

        Boolean bluetoothState = getBluetoothState();

        if (bluetoothState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewBluetooth, getFontBitmap(mContext, mContext.getString(R.string.icon_bluetooth),
                    bluetoothState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewBluetooth, bluetoothState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewBluetoothInd, "setColorFilter", bluetoothState ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewBluetooth, getFontBitmap(mContext, mContext.getString(R.string.icon_bluetooth),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewBluetooth, colorTextOn);
            updateViews.setInt(R.id.imageViewBluetoothInd, "setColorFilter", colorTransition);
        }
    }

    private boolean isFlashSupported(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    private boolean isCameraSupported(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Boolean getFlashOn() {

        return flashOn;
    }

    private void setTorchState(boolean torchState) {

        Log.v(LOG_TAG, "setTorchState - " + torchState);

        PackageManager packageManager = mContext.getPackageManager();

        if (isCameraSupported(packageManager) && isFlashSupported(packageManager)) {

            if (camera == null)
                try {
                    camera = Camera.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    flashOn = false;
                }

            if (torchState) {
                if (camera != null) {
                    try {
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(param);
                        sleep(300);
                        camera.startPreview();
                        flashOn = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        flashOn = false;
                    }
                }
            } else {
                if (camera != null) {
                    try {
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(param);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        flashOn = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            mContext.sendBroadcast(new Intent(WidgetIntentDefinitions.FLASHLIGHT_CHANGED));
        }
    }


    private void toggleTorch() {
        Boolean torchState = getFlashOn();
        // ignore toggle requests if the flashlight is currently changing state
        if (torchState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Intent permissionsIntent = new Intent(mContext, SetPermissionsActivity.class);
                    permissionsIntent.putExtra(SetPermissionsActivity.PERMISSIONS_TYPE, SetPermissionsActivity.PERMISSIONS_REQUEST_CAMERA);
                    permissionsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(permissionsIntent);
                } else {
                    setTorchState(!torchState);
                }
            } else {
                setTorchState(!torchState);
            }
        }
    }

    private void updateTorchStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateTorchStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.FLASHLIGHT_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_TORCH_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_TORCH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewTorch, mContext.getResources().getText(R.string.torch));

        Boolean torchState = getFlashOn();

        if (torchState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewTorch, getFontBitmap(mContext, mContext.getString(R.string.icon_flashlight),
                    torchState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewTorch, torchState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", torchState ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewTorch, getFontBitmap(mContext, mContext.getString(R.string.icon_flashlight),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewTorch, colorTextOn);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", colorTransition);
        }
    }

    private Boolean getWifiState() {

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Boolean wifiState = null;

        if (wifiManager != null) {
            switch (wifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiState = false;
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    wifiState = null;
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiState = true;
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    wifiState = null;
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    wifiState = false;
                    break;
                default:
                    wifiState = false;
            }
        }

        Log.v(LOG_TAG, "getWifiState - " + wifiState);

        return wifiState;

    }

    private void setWifiState(boolean wifiState) {

        Log.v(LOG_TAG, "setWifiState - " + wifiState);

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null)
            wifiManager.setWifiEnabled(wifiState);
    }

    private void toggleWiFi() {
        Boolean wifiState = getWifiState();
        // ignore toggle requests if the WiFi is currently changing state
        if (wifiState != null) {
            setWifiState(!wifiState);
        }
    }

    private void updateWifiStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateWifiStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_WIFI_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIFI_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewWiFi, mContext.getResources().getText(R.string.wifi));

        Boolean wifiState = getWifiState();

        if (wifiState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewWiFi, getFontBitmap(mContext, mContext.getString(R.string.icon_wifi),
                    wifiState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewWiFi, wifiState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", wifiState ? colorOn : colorOff);

            if (wifiState) {
                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    if (wifiInfo != null) {
                        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                            String ssid = wifiInfo.getSSID();
                            ssid = ssid.replace("\"", "");

                            if (!ssid.contains("<unknown ssid>"))
                                updateViews.setTextViewText(R.id.textViewWiFi, ssid);
                        }
                    }
                }
            }

        } else {
            updateViews.setImageViewBitmap(R.id.imageViewWiFi, getFontBitmap(mContext, mContext.getString(R.string.icon_wifi),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewWiFi, colorTextOn);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", colorTransition);
        }
    }

    private Boolean getSyncStatus() {

        Boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
        Log.v(LOG_TAG, "getSyncStatus - " + syncStatus);

        return syncStatus;
    }

    private void setSyncStatus(boolean syncStatus) {

        Log.v(LOG_TAG, "setSyncStatus - " + syncStatus);

        ContentResolver.setMasterSyncAutomatically(syncStatus);
    }

    private void toggleSync() {
        Boolean syncStatus = getSyncStatus();
        // ignore toggle requests if the Sync is currently changing status
        if (syncStatus != null) {
            setSyncStatus(!syncStatus);
        }
    }

    private void updateSyncStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateSyncStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_SYNC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewSync, mContext.getResources().getText(R.string.sync));

        Boolean syncStatus = getSyncStatus();

        if (syncStatus != null) {
            updateViews.setImageViewBitmap(R.id.imageViewSync, getFontBitmap(mContext, mContext.getString(R.string.icon_sync),
                    syncStatus ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewSync, syncStatus ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", syncStatus ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewSync, getFontBitmap(mContext, mContext.getString(R.string.icon_sync),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewSync, colorTextOn);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getOrientation() {

        Boolean orientation = (Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);

        Log.v(LOG_TAG, "getOrientation - " + orientation);

        // false = auto-rotation is disabled
        // true = auto-rotation is enabled
        return orientation;
    }

    public void setOrientation(boolean orientation) {

        Log.v(LOG_TAG, "setOrientation - " + orientation);

        Settings.System.putInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, orientation ? 1 : 0);
    }

    private void toggleOrientation() {
        Boolean orientation = getOrientation();
        // ignore toggle requests if the orientation is currently changing state
        if (orientation != null) {
            setOrientation(!orientation);
        }
    }

    private void updateOrientation(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateOrientation");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_ORIENTATION_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_ORIENTATION_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewOrientation, mContext.getResources().getText(R.string.orientation));

        Boolean orientation = getOrientation();

        if (orientation != null) {
            updateViews.setImageViewBitmap(R.id.imageViewOrientation, getFontBitmap(mContext, mContext.getString(R.string.icon_orientation),
                    orientation ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewOrientation, orientation ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", orientation ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewOrientation, getFontBitmap(mContext, mContext.getString(R.string.icon_orientation),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewOrientation, colorTextOn);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", colorTransition);
        }
    }

    private boolean canToggleNfc() {
        return false;
    }

    private Boolean getNfcState() {
        Boolean nfcState = null;

        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            NfcManager manager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);

            if (manager != null) {
                NfcAdapter adapter = manager.getDefaultAdapter();
                nfcState = adapter != null && adapter.isEnabled();
            }
        } else {
            nfcState = false;
        }

        Log.v(LOG_TAG, "getNfcState - " + nfcState);

        return nfcState;

    }

    private void setNfcState(boolean nfcState) {

    }

    private void toggleNfc() {
        Boolean nfcState = getNfcState();
        // ignore toggle requests if the NFC is currently changing state
        if (nfcState != null) {
            setNfcState(!nfcState);
        }
    }

    private void updateNfcStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateNfcStatus");

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentApiVersion < 10) {
            return;
        }

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_NFC_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.NFC_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_NFC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewNfc, mContext.getResources().getText(R.string.nfc));

        Boolean nfcState = getNfcState();

        if (nfcState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewNfc, getFontBitmap(mContext, mContext.getString(R.string.icon_nfc),
                    nfcState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewNfc, nfcState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", nfcState ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewNfc, getFontBitmap(mContext, mContext.getString(R.string.icon_nfc),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewNfc, colorTextOn);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", colorTransition);
        }
    }

    private Boolean getMobileState() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Boolean mobileState = null;

        if (telephonyManager != null) {
            int extraDataState = telephonyManager.getDataState();

            switch (extraDataState) {
                case TelephonyManager.DATA_CONNECTED:
                    mobileState = true;
                    break;

                case TelephonyManager.DATA_DISCONNECTED:
                    mobileState = false;
                    break;

                case TelephonyManager.DATA_CONNECTING:
                    mobileState = null;
                    break;

                case TelephonyManager.DATA_SUSPENDED:
                    mobileState = null;
                    break;
                default:
                    mobileState = false;
            }
        }

        Log.v(LOG_TAG, "getMobileState - " + mobileState);

        return mobileState;

    }

    private void setMobileState(boolean mobileState) {

        Log.v(LOG_TAG, "setMobileState - " + mobileState);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentApiVersion <= android.os.Build.VERSION_CODES.FROYO) {
            setMobileStateFroyo(mobileState);
        } else if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
            setMobileStateGingerbread(mobileState);
        } else {
            setMobileStateLollipop(mobileState);
        }
    }

    private void setMobileStateFroyo(boolean mobileState) {

        Method dataConnSwitchmethod = null;
        Class telephonyManagerClass = null;
        Object ITelephonyStub = null;
        Class ITelephonyClass = null;

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null)
            return;

        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass()
                    .getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method getITelephonyMethod = null;
        try {
            if (telephonyManagerClass != null) {
                getITelephonyMethod = telephonyManagerClass
                        .getDeclaredMethod("getITelephony");
            }
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (getITelephonyMethod != null) {
            getITelephonyMethod.setAccessible(true);
        }
        try {
            if (getITelephonyMethod != null) {
                ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            if (ITelephonyStub != null) {
                ITelephonyClass = Class
                        .forName(ITelephonyStub.getClass().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!mobileState) {
            try {
                if (ITelephonyClass != null) {
                    dataConnSwitchmethod = ITelephonyClass
                            .getDeclaredMethod("disableDataConnectivity");
                }
            } catch (SecurityException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (ITelephonyClass != null) {
                    dataConnSwitchmethod = ITelephonyClass
                            .getDeclaredMethod("enableDataConnectivity");
                }
            } catch (SecurityException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (dataConnSwitchmethod != null) {
            dataConnSwitchmethod.setAccessible(true);
        }
        try {
            if (dataConnSwitchmethod != null) {
                dataConnSwitchmethod.invoke(ITelephonyStub);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setMobileStateGingerbread(boolean mobileState) {

        ConnectivityManager conman = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conman == null)
            return;

        Class conmanClass = null;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field iConnectivityManagerField = null;
        try {
            if (conmanClass != null) {
                iConnectivityManagerField = conmanClass
                        .getDeclaredField("mService");
            }
        } catch (SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (iConnectivityManagerField != null) {
            iConnectivityManagerField.setAccessible(true);
        }
        Object iConnectivityManager = null;
        try {
            if (iConnectivityManagerField != null) {
                iConnectivityManager = iConnectivityManagerField.get(conman);
            }
        } catch (IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        Class iConnectivityManagerClass = null;
        try {
            if (iConnectivityManager != null) {
                iConnectivityManagerClass = Class.forName(iConnectivityManager
                        .getClass().getName());
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        Method setMobileDataEnabledMethod = null;
        try {
            if (iConnectivityManagerClass != null) {
                setMobileDataEnabledMethod = iConnectivityManagerClass
                        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            }
        } catch (SecurityException | NoSuchMethodException e1) {
            e1.printStackTrace();
        }

        try {
            if (setMobileDataEnabledMethod != null) {
                setMobileDataEnabledMethod.setAccessible(true);
                setMobileDataEnabledMethod.invoke(iConnectivityManager, mobileState);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setMobileStateLollipop(boolean mobileState) {

        String command;

        if (mobileState)
            command = COMMAND_L_ON;
        else
            command = COMMAND_L_OFF;

        try {
            Process su = Runtime.getRuntime().exec(COMMAND_SU);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes(command);
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void toggleData() {
        Boolean mobileState = getMobileState();
        // ignore toggle requests if the Mobile Data is currently changing
        // state
        if (mobileState != null) {
            setMobileState(!mobileState);
        }
    }

    private void updateDataStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateMobileStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_MOBILE_DATA_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_MOBILE_DATA_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewMobile, mContext.getResources().getText(R.string.mobile));

        /*if (intentExtra != null && intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            // HACK - mobile data state change is delayed, delay getting info
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        Boolean mobileState = getMobileState();

        if (mobileState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewMobile, getFontBitmap(mContext, mContext.getString(R.string.icon_data),
                    mobileState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewMobile, mobileState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", mobileState ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewMobile, getFontBitmap(mContext, mContext.getString(R.string.icon_data),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewMobile, colorTextOn);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", colorTransition);
        }
    }

    private boolean canToggleGPS() {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo;

        try {
            packageInfo = packageManager.getPackageInfo("com.android.settings",
                    PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            return false; // package not found
        }

        if (packageInfo != null) {
            for (ActivityInfo actInfo : packageInfo.receivers) {
                // test if receiver is exported. if so, we can toggle GPS.
                if (actInfo.name
                        .equals("com.android.settings.widget.SettingsAppWidgetProvider")
                        && actInfo.exported) {
                    return true;
                }
            }
        }

        return false; // default
    }

    private Boolean getGpsState() {

        Boolean gpsState;

        LocationManager locationManager = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));

        if (locationManager == null) {
            return null;
        }

        gpsState = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.v(LOG_TAG, "getGpsState - " + gpsState);

        return gpsState;

    }

    private void setGpsState(boolean gpsState) {

        if (canToggleGPS()) {
            if (gpsState) {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                mContext.sendBroadcast(poke);
            } else {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                mContext.sendBroadcast(poke);
            }
        }

    }

    private void toggleGps() {
        Boolean gpsState = getGpsState();
        // ignore toggle requests if the GPS is currently changing
        // state
        if (gpsState != null) {
            setGpsState(!gpsState);
        }
    }

    private void updateGpsStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateGpsStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED) && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_GPS_WIDGET)
                && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL) && !intentExtra.equals(WidgetIntentDefinitions.GPS_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_GPS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOn(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewGps, mContext.getResources().getText(R.string.gps));

        Boolean gpsState = getGpsState();

        if (gpsState != null) {
            updateViews.setImageViewBitmap(R.id.imageViewGps, getFontBitmap(mContext, mContext.getString(R.string.icon_gps),
                    gpsState ? colorOn : colorOff, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewGps, gpsState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", gpsState ? colorOn : colorOff);
        } else {
            updateViews.setImageViewBitmap(R.id.imageViewGps, getFontBitmap(mContext, mContext.getString(R.string.icon_gps),
                    colorTransition, "fonts/MaterialIcons.ttf", true, 144));

            updateViews.setTextColor(R.id.textViewGps, colorTextOn);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", colorTransition);
        }
    }

    private int getRingerState() {

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int ringerState = AudioManager.RINGER_MODE_NORMAL;

        if (audioManager != null) {
            ringerState = audioManager.getRingerMode();
        }

        Log.v(LOG_TAG, "getRingerState - " + ringerState);

        return ringerState;

    }

    private void setRingerState(int ringerState) {

        Log.v(LOG_TAG, "setRingerState - " + ringerState);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);

            return;
        }

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager == null)
            return;

        audioManager.setRingerMode(ringerState);

        if (ringerState == AudioManager.RINGER_MODE_SILENT && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                sleep(500); // Lollipop hack!
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getText(R.string.allsoundsmuted), Toast.LENGTH_LONG).show();
                }
            }, 1000);

            audioManager.setRingerMode(ringerState);
        }

        if (ringerState == AudioManager.RINGER_MODE_VIBRATE) {
            Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

            if (v != null) {
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            }
        }

    }

    private void toggleRinger() {
        int ringerState = getRingerState();

        int nToggle = Preferences.getSoundOptions(mContext);

        switch (nToggle) {
            case 0:
                setRingerState((ringerState + 2) % 3); // Sound/Vibration/Silent
                break;
            case 1:
                setRingerState((ringerState + 1) % 3); // Sound/Silent/Vibration
                break;
            case 2:
                setRingerState(ringerState == AudioManager.RINGER_MODE_NORMAL ?
                        AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL); // Sound/Vibration
                break;
            case 3:
                setRingerState(ringerState == AudioManager.RINGER_MODE_NORMAL ?
                        AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL); // Sound/Silent
                break;
            default:
                setRingerState((ringerState + 2) % 3); // same as 0
                break;
        }
    }

    private void updateRingerStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateRingerStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_RINGER_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_RINGER_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewRinger, mContext.getResources().getText(R.string.ringer));

        int color = colorOn;
        int textColor = colorTextOn;
        String text = "";

        int ringerState = getRingerState();

        switch (ringerState) {
            case AudioManager.RINGER_MODE_SILENT:
                color = colorOff;
                textColor = colorTextOff;
                text = mContext.getString(R.string.icon_ringer_silent);
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                text = mContext.getString(R.string.icon_ringer_vibrate);
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                text = mContext.getString(R.string.icon_ringer_normal);
                break;

            default:
                color = colorTransition;
                text = mContext.getString(R.string.icon_ringer_normal);
                break;
        }

        updateViews.setImageViewBitmap(R.id.imageViewRinger, getFontBitmap(mContext, text,
                color, "fonts/MaterialIcons.ttf", true, 144));

        updateViews.setTextColor(R.id.textViewRinger, textColor);
        updateViews.setInt(R.id.imageViewRingerInd, "setColorFilter", color);
    }

    private void toggleBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Settings.System.canWrite(mContext)) {
                realToggleBrightness();
            } else {
                Intent writeSettingsIntent = new Intent(mContext, WriteSettingsActivity.class);
                writeSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(writeSettingsIntent);
            }
        } else {
            realToggleBrightness();
        }
    }

    private void realToggleBrightness() {
        try {
            ContentResolver cr = this.mContext.getContentResolver();
            int brightness = Settings.System.getInt(cr,
                    Settings.System.SCREEN_BRIGHTNESS);
            int brightnessMode;

            //Only get brightness setting if available
            brightnessMode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);

            // Rotate AUTO -> MINIMUM -> DEFAULT -> MAXIMUM
            // Technically, not a toggle...
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                brightness = 30;
                brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
            } else if (brightness < 128) {
                brightness = 128;
            } else if (brightness < 255) {
                brightness = 255;
            } else {
                brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
                brightness = 30;
            }

            Settings.System.putInt(this.mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    brightnessMode);

            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOG_TAG, "toggleBrightness: " + e);
        }
    }

    protected int getBrightness() {

        int brightness;

        try {
            brightness = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException snfe) {
            brightness = 255;
        }

        return brightness;
    }

    private boolean isAutoBrightness() {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    public void setBrightness(int brightness) {

        Log.v(LOG_TAG, "setBrightness - " + brightness);
    }

    private void updateBrightness(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetManager updateBrightness");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_CHANGED)
                && !intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BRIGHTNESS_WIDGET) && !intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE) && !intentExtra.equals(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBrightness, mContext.getResources().getText(R.string.brightness));

        int brightness = getBrightness();
        boolean off = false;
        String text = "";

        if (isAutoBrightness()) {
            text = mContext.getString(R.string.icon_brightness_auto);
        } else {
            if (brightness < 50) {
                off = true;
                text = mContext.getString(R.string.icon_brightness);
            } else {
                if (brightness < 150) {
                    text = mContext.getString(R.string.icon_brightness_mid);
                } else {
                    text = mContext.getString(R.string.icon_brightness);
                }
            }
        }

        updateViews.setImageViewBitmap(R.id.imageViewBrightness, getFontBitmap(mContext, text,
                off ? colorOff : colorOn, "fonts/MaterialIcons.ttf", true, 144));

        updateViews.setTextColor(R.id.textViewBrightness, off ? colorTextOff : colorTextOn);
        updateViews.setInt(R.id.imageViewBrightnessInd, "setColorFilter", off ? colorOff : colorOn);
    }

    public void updateClockWidgets(Context context, int[] appWidgetIds, boolean updateWeather, boolean scheduledUpdate) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteViews;

        for (int appWidgetId : appWidgetIds) {
            remoteViews = buildClockUpdate(appWidgetId);
            updateClockStatus(remoteViews, appWidgetId);

            boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

            if (showWeather) {
                if (updateWeather)
                    updateWeatherStatus(remoteViews, appWidgetId, scheduledUpdate);
                else
                    readCachedWeatherData(remoteViews, appWidgetId);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    public void updatePowerWidgets(Context context, String intentExtra) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PowerAppWidgetProvider.class));
        toggleWidgets(intentExtra);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = buildPowerUpdate(intentExtra, appWidgetId);

            if (remoteViews != null) {
                updatePowerWidgetStatus(remoteViews, intentExtra, appWidgetId);
            }
        }
    }

    private RemoteViews buildUpdate(Context context, String intentExtra) {
        Log.d(LOG_TAG, "WidgetUpdateService buildUpdate");

        // Build an update that holds the updated widget contents
        if (intentExtra == null)
            return null;

        RemoteViews updateViews = null;

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BATTERY_WIDGET) || intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.batterywidget);

            try {
                updateBatteryStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                Intent batteryInfo = new Intent(context, BatteryInfoActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, batteryInfo,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.batteryWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BLUETOOTH_WIDGET) || intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.bluetoothwidget);

            try {
                updateBluetoothStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.bluetoothWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_WIFI_WIDGET) || intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.wifiwidget);

            try {
                updateWifiStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.wifiWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_MOBILE_DATA_WIDGET) || intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.mobilewidget);

            try {
                updateDataStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;

                if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                            0, new Intent(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                            pendingIntent);
                } else {
                    /*Process p;
                    // Perform su to get root privileges
                    p = Runtime.getRuntime().exec("su");

                    // Attempt to write a file to a root-only
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

                    // Close the terminal
                    os.writeBytes("exit\n");
                    os.flush();

                    p.waitFor();

                    if (p.exitValue() != 255) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                0, new Intent(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                                pendingIntent);
                    } else {*/
                        Intent intentData = new Intent(Intent.ACTION_MAIN);
                        intentData.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                                pendingIntent);
                    //}
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);

                Intent intentData = new Intent(Intent.ACTION_MAIN);
                intentData.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_GPS_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.GPS_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.gpswidget);

            try {
                updateGpsStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleGPS()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                            0, new Intent(WidgetIntentDefinitions.GPS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                Intent locationIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                        locationIntent, PackageManager.MATCH_DEFAULT_ONLY);
                PendingIntent pendingIntent;

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(context, 0,
                            locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                }
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_RINGER_WIDGET) || intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.ringerwidget);

            try {
                updateRingerStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.ringerWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_AIRPLANE_WIDGET) || intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.airplanewidget);

            try {
                updateAirplaneMode(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleAirplane()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                            0, new Intent(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                Intent airplaneIntent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                        airplaneIntent, PackageManager.MATCH_DEFAULT_ONLY);
                PendingIntent pendingIntent;

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(context, 0,
                            airplaneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } else {
                    Intent wirelessIntent = new Intent(
                            Settings.ACTION_WIRELESS_SETTINGS);
                    ResolveInfo resolveWirelessInfo = context.getPackageManager().resolveActivity(
                            wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (resolveWirelessInfo != null) {
                        pendingIntent = PendingIntent.getActivity(context, 0,
                                wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                                pendingIntent);
                    }
                }
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BRIGHTNESS_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.brightnesswidget);

            try {
                updateBrightness(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {

                int nOptions = Preferences.getBrightnessOptions(context);

                PendingIntent pendingIntent;

                switch (nOptions) {
                    case 0: // toggle
                        pendingIntent = PendingIntent.getBroadcast(context,
                                0, new Intent(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                    case 1: // dialog
                        Intent brightnessIntent = new Intent(context, BrightnessActivity.class);

                        pendingIntent = PendingIntent.getActivity(context,
                                0, brightnessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                    default:
                        pendingIntent = PendingIntent.getBroadcast(context,
                                0, new Intent(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                }

                updateViews.setOnClickPendingIntent(R.id.brightnessWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_NFC_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.NFC_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.nfcwidget);

            try {
                updateNfcStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleNfc()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                            0, new Intent(WidgetIntentDefinitions.NFC_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
                    NfcAdapter adapter = null;

                    if (manager != null) {
                        adapter = manager.getDefaultAdapter();
                    }

                    if (adapter != null) {
                        Intent wirelessIntent;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            wirelessIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        else
                            wirelessIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                                wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        PendingIntent pendingIntent;

                        if (resolveInfo != null) {
                            pendingIntent = PendingIntent.getActivity(context, 0,
                                    wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                                    pendingIntent);
                        }
                    }
                }
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.syncwidget);

            try {
                updateSyncStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.syncWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_ORIENTATION_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.orientationwidget);

            try {
                updateOrientation(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.orientationWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_TORCH_WIDGET) || intentExtra.equals(WidgetIntentDefinitions.FLASHLIGHT_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(context.getPackageName(), R.layout.torchwidget);

            try {
                updateTorchStatus(updateViews, intentExtra, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, new Intent(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.torchWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        return updateViews;
    }

    public void updateSingleWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisWidget = new ComponentName(context, BatteryAppWidgetProvider.class);
        RemoteViews remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_BATTERY_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, AirplaneAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_AIRPLANE_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, BluetoothAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_BLUETOOTH_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, BrightnessAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_BRIGHTNESS_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, GpsAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_GPS_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, MobileAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_MOBILE_DATA_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, NfcAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_NFC_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, OrientationAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_ORIENTATION_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, RingerAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_RINGER_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, SyncAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, TorchAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_TORCH_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        thisWidget = new ComponentName(context, WifiAppWidgetProvider.class);
        remoteViews = buildUpdate(context, WidgetIntentDefinitions.UPDATE_SINGLE_WIFI_WIDGET);
        if (remoteViews != null) {
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }

    public void updateSingleWidget(Context context, String intentExtra) {
        ComponentName thisWidget = null;

        if (intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BATTERY_WIDGET)) {
            thisWidget = new ComponentName(context,
                    BatteryAppWidgetProvider.class);
        }

        if (intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BLUETOOTH_WIDGET)) {
            thisWidget = new ComponentName(context,
                    BluetoothAppWidgetProvider.class);
        }

        if (intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_WIFI_WIDGET)) {
            thisWidget = new ComponentName(context,
                    WifiAppWidgetProvider.class);
        }

        if (intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_MOBILE_DATA_WIDGET)) {
            thisWidget = new ComponentName(context,
                    MobileAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.GPS_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_GPS_WIDGET)) {
            thisWidget = new ComponentName(context,
                    GpsAppWidgetProvider.class);
        }

        if (intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                || intentExtra.equals(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_RINGER_WIDGET)) {
            thisWidget = new ComponentName(context,
                    RingerAppWidgetProvider.class);
        }

        if (intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_AIRPLANE_WIDGET)) {
            thisWidget = new ComponentName(context,
                    AirplaneAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_BRIGHTNESS_WIDGET)) {
            thisWidget = new ComponentName(context,
                    BrightnessAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.NFC_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_NFC_WIDGET)) {
            thisWidget = new ComponentName(context,
                    NfcAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET)) {
            thisWidget = new ComponentName(context,
                    SyncAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_ORIENTATION_WIDGET)) {
            thisWidget = new ComponentName(context,
                    OrientationAppWidgetProvider.class);
        }

        if (intentExtra.equals(WidgetIntentDefinitions.FLASHLIGHT_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE)
                || intentExtra.equals(WidgetIntentDefinitions.UPDATE_SINGLE_TORCH_WIDGET)) {
            thisWidget = new ComponentName(context,
                    TorchAppWidgetProvider.class);
        }

        if (thisWidget != null) {

            toggleWidgets(intentExtra);
            RemoteViews updateViews = buildUpdate(context, intentExtra);

            if (updateViews != null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(thisWidget, updateViews);
            }
        }
    }
}
