package com.zoromatic.widgets;

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

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.SunriseSunsetLocation;
import com.zoromatic.widgets.LocationProvider.LocationResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
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
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import static java.lang.Thread.sleep;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class WidgetUpdateService extends Service {
    private static String LOG_TAG = "WidgetUpdateService";
    public static String BLUETOOTH_WIDGET_UPDATE = "com.zoromatic.widgets.BLUETOOTH_WIDGET_UPDATE";
    public static String WIFI_WIDGET_UPDATE = "com.zoromatic.widgets.WIFI_WIDGET_UPDATE";
    public static String MOBILE_DATA_WIDGET_UPDATE = "com.zoromatic.widgets.MOBILE_DATA_WIDGET_UPDATE";
    public static String GPS_WIDGET_UPDATE = "com.zoromatic.widgets.GPS_WIDGET_UPDATE";
    public static String RINGER_WIDGET_UPDATE = "com.zoromatic.widgets.RINGER_WIDGET_UPDATE";
    public static String CLOCK_WIDGET_UPDATE = "com.zoromatic.widgets.CLOCK_WIDGET_UPDATE";
    public static String WEATHER_UPDATE = "com.zoromatic.widgets.WEATHER_UPDATE";
    public static String AIRPLANE_WIDGET_UPDATE = "com.zoromatic.widgets.AIRPLANE_WIDGET_UPDATE";
    public static String BRIGHTNESS_WIDGET_UPDATE = "com.zoromatic.widgets.BRIGHTNESS_WIDGET_UPDATE";
    public static String TORCH_WIDGET_UPDATE = "com.zoromatic.widgets.TORCH_WIDGET_UPDATE";
    public static String NFC_WIDGET_UPDATE = "com.zoromatic.widgets.NFC_WIDGET_UPDATE";
    public static String SYNC_WIDGET_UPDATE = "com.zoromatic.widgets.SYNC_WIDGET_UPDATE";
    public static String ORIENTATION_WIDGET_UPDATE = "com.zoromatic.widgets.ORIENTATION_WIDGET_UPDATE";

    public static String UPDATE_SINGLE_BATTERY_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BATTERY_WIDGET";
    public static String UPDATE_SINGLE_BLUETOOTH_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BLUETOOTH_WIDGET";
    public static String UPDATE_SINGLE_WIFI_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_WIFI_WIDGET";
    public static String UPDATE_SINGLE_MOBILE_DATA_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_MOBILE_DATA_WIDGET";
    public static String UPDATE_SINGLE_GPS_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_GPS_WIDGET";
    public static String UPDATE_SINGLE_RINGER_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_RINGER_WIDGET";
    public static String UPDATE_SINGLE_CLOCK_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_CLOCK_WIDGET";
    public static String UPDATE_SINGLE_AIRPLANE_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_AIRPLANE_WIDGET";
    public static String UPDATE_SINGLE_BRIGHTNESS_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BRIGHTNESS_WIDGET";
    public static String UPDATE_SINGLE_TORCH_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_TORCH_WIDGET";
    public static String UPDATE_SINGLE_NFC_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_NFC_WIDGET";
    public static String UPDATE_SINGLE_SYNC_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_SYNC_WIDGET";
    public static String UPDATE_SINGLE_ORIENTATION_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_ORIENTATION_WIDGET";

    public static String AUTO_ROTATE_CHANGED = "com.zoromatic.widgets.AUTO_ROTATE_CHANGED";
    public static String UPDATE_FORECAST = "com.zoromatic.widgets.UPDATE_FORECAST";
    public static String UPDATE_WIDGETS = "com.zoromatic.widgets.UPDATE_WIDGETS";
    public static String LOCATION_PROVIDERS_CHANGED = "android.location.PROVIDERS_CHANGED";
    public static String LOCATION_GPS_ENABLED_CHANGED = "android.location.GPS_ENABLED_CHANGE";
    public static String NFC_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";
    public static String SYNC_CONN_STATUS_CHANGED = "com.android.sync.SYNC_CONN_STATUS_CHANGED";
    public static String FLASHLIGHT_CHANGED = "COM_FLASHLIGHT";
    public static String BRIGHTNESS_CHANGED = "com.zoromatic.widgets.BRIGHTNESS_CHANGED";

    public static String POWER_WIDGET_UPDATE_ALL = "com.zoromatic.widgets.POWER_WIDGET_UPDATE_ALL";
    public static String POWER_BLUETOOTH_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_BLUETOOTH_WIDGET_UPDATE";
    public static String POWER_WIFI_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_WIFI_WIDGET_UPDATE";
    public static String POWER_MOBILE_DATA_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_MOBILE_DATA_WIDGET_UPDATE";
    public static String POWER_GPS_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_GPS_WIDGET_UPDATE";
    public static String POWER_RINGER_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_RINGER_WIDGET_UPDATE";
    public static String POWER_AIRPLANE_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_AIRPLANE_WIDGET_UPDATE";
    public static String POWER_BRIGHTNESS_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_BRIGHTNESS_WIDGET_UPDATE";
    public static String POWER_NFC_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_NFC_WIDGET_UPDATE";
    public static String POWER_SYNC_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_SYNC_WIDGET_UPDATE";
    public static String POWER_ORIENTATION_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_ORIENTATION_WIDGET_UPDATE";
    public static String POWER_TORCH_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_TORCH_WIDGET_UPDATE";
    public static String APPWIDGET_RESIZE = "com.sec.android.widgetapp.APPWIDGET_RESIZE";
    public static String APPWIDGET_UPDATE_OPTIONS = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";
    public static String BATTERY_NOTIFICATION = "com.zoromatic.widgets.BATTERY_NOTIFICATION";

    protected static long GPS_UPDATE_TIME_INTERVAL = 3000; // milliseconds
    protected static float GPS_UPDATE_DISTANCE_INTERVAL = 0; // meters
    private WidgetGPSListener mGpsListener = null;
    private WidgetLocationListener mLocListener = null;
    private BrightnessObserver mSettingsObserver;
    private RotationObserver mRotationObserver;
    private static Camera camera;
    private static boolean flashOn = false;

   private static IntentFilter mIntentFilter;
    private WidgetInfoReceiver mWidgetInfo = null;

    public static String WEATHER_SERVICE_COORD_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";
    public static String WEATHER_SERVICE_ID_URL = "http://api.openweathermap.org/data/2.5/weather?id=%d&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";
    public static String WEATHER_FORECAST_COORD_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=7&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";
    public static String WEATHER_FORECAST_ID_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?id=%d&cnt=7&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";

    static int WIDGET_COLOR_ON = Color.rgb(0x35, 0xB6, 0xE5);
    static int WIDGET_COLOR_OFF = Color.rgb(0xC0, 0xC0, 0xC0);
    static int WIDGET_COLOR_TRANSITION = Color.rgb(0xFF, 0x8C, 0x00);
    static int WIDGET_COLOR_BACKGROUND = Color.rgb(0x00, 0x00, 0x00);
    static int WIDGET_COLOR_TEXT_ON = Color.rgb(0xFF, 0xFF, 0xFF);
    static int WIDGET_COLOR_TEXT_OFF = Color.rgb(0xFF, 0xFF, 0xFF);

    private final static String COMMAND_L_ON = "svc data enable\n ";
    private final static String COMMAND_L_OFF = "svc data disable\n ";
    private final static String COMMAND_SU = "su";

    private int mBatteryLevel = -1;
    private int mBatteryScale = -1;
    private int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;

    static {

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mIntentFilter.addAction(CLOCK_WIDGET_UPDATE);
        mIntentFilter.addAction(WEATHER_UPDATE);

        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction(LOCATION_PROVIDERS_CHANGED);
        mIntentFilter.addAction(LOCATION_GPS_ENABLED_CHANGED);
        mIntentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        mIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mIntentFilter.addAction(NFC_ADAPTER_STATE_CHANGED);
        mIntentFilter.addAction(SYNC_CONN_STATUS_CHANGED);
        mIntentFilter.addAction(AUTO_ROTATE_CHANGED);
        mIntentFilter.addAction(FLASHLIGHT_CHANGED);
        mIntentFilter.addAction(BRIGHTNESS_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_CAMERA_BUTTON);
        mIntentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        mIntentFilter.addAction(UPDATE_SINGLE_BATTERY_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_BLUETOOTH_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_WIFI_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_MOBILE_DATA_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_GPS_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_RINGER_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_CLOCK_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_AIRPLANE_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_BRIGHTNESS_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_TORCH_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_NFC_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_SYNC_WIDGET);
        mIntentFilter.addAction(UPDATE_SINGLE_ORIENTATION_WIDGET);

        mIntentFilter.addAction(UPDATE_WIDGETS);

        mIntentFilter.addAction(BLUETOOTH_WIDGET_UPDATE);
        mIntentFilter.addAction(WIFI_WIDGET_UPDATE);
        mIntentFilter.addAction(MOBILE_DATA_WIDGET_UPDATE);
        mIntentFilter.addAction(GPS_WIDGET_UPDATE);
        mIntentFilter.addAction(RINGER_WIDGET_UPDATE);
        mIntentFilter.addAction(AIRPLANE_WIDGET_UPDATE);
        mIntentFilter.addAction(BRIGHTNESS_WIDGET_UPDATE);
        mIntentFilter.addAction(NFC_WIDGET_UPDATE);
        mIntentFilter.addAction(SYNC_WIDGET_UPDATE);
        mIntentFilter.addAction(ORIENTATION_WIDGET_UPDATE);
        mIntentFilter.addAction(TORCH_WIDGET_UPDATE);

        mIntentFilter.addAction(POWER_BLUETOOTH_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_WIFI_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_MOBILE_DATA_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_GPS_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_RINGER_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_AIRPLANE_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_BRIGHTNESS_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_NFC_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_SYNC_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_ORIENTATION_WIDGET_UPDATE);
        mIntentFilter.addAction(POWER_TORCH_WIDGET_UPDATE);

        mIntentFilter.addAction(POWER_WIDGET_UPDATE_ALL);

        mIntentFilter.addAction(APPWIDGET_RESIZE);
        mIntentFilter.addAction(APPWIDGET_UPDATE_OPTIONS);
    }

    /**
     * Observer to watch for changes to the Auto Rotation setting
     */
    private static class RotationObserver extends ContentObserver {

        private Context mContext;

        RotationObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
        }

        void startObserving() {
            ContentResolver resolver = mContext.getContentResolver();
            // Listen to accelerometer
            resolver.registerContentObserver(Settings.System.getUriFor
                            (Settings.System.ACCELEROMETER_ROTATION),
                    true, this);
        }

        void stopObserving() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            Intent intent = new Intent(AUTO_ROTATE_CHANGED);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * Observer to watch for changes to the BRIGHTNESS setting
     */
    private static class BrightnessObserver extends ContentObserver {

        private Context mContext;

        BrightnessObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
        }

        void startObserving() {
            ContentResolver resolver = mContext.getContentResolver();
            // Listen to brightness and brightness mode
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, this);
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this);
        }

        void stopObserving() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            Intent brightnessIntent = new Intent(mContext, WidgetUpdateService.class);
            brightnessIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, BRIGHTNESS_CHANGED);
            mContext.startService(brightnessIntent);
        }
    }

    /**
     * Watch for changes to LOCATION setting
     */
    private class WidgetGPSListener implements GpsStatus.Listener {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:

                    break;
                case GpsStatus.GPS_EVENT_STARTED:

                    break;
                case GpsStatus.GPS_EVENT_STOPPED:

                    break;
            }
        }
    }

    private class WidgetLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            try {
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;

                if (currentApiVersion < 9) {
                    sendBroadcast(new Intent(LOCATION_GPS_ENABLED_CHANGED));
                } else {
                    sendBroadcast(new Intent(LOCATION_PROVIDERS_CHANGED));
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            try {
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;

                if (currentApiVersion < 9) {
                    sendBroadcast(new Intent(LOCATION_GPS_ENABLED_CHANGED));
                } else {
                    sendBroadcast(new Intent(LOCATION_PROVIDERS_CHANGED));
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    public WidgetUpdateService(){
        Log.d(LOG_TAG, "WidgetUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        try {
            if (mSettingsObserver != null) {
                mSettingsObserver.stopObserving();
                mSettingsObserver = null;
            }

            if (mRotationObserver != null) {
                mRotationObserver.stopObserving();
                mRotationObserver = null;
            }

            mGpsListener = null;
            mLocListener = null;

            if (mWidgetInfo != null) {
                unregisterReceiver(mWidgetInfo);
                mWidgetInfo = null;
            }

            if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmService != null) {
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "WidgetUpdateService onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            showNotification();
        }

        if (mWidgetInfo == null) {
            mWidgetInfo = new WidgetInfoReceiver();
            registerReceiver(mWidgetInfo, mIntentFilter);

            sendBroadcast(new Intent(FLASHLIGHT_CHANGED));
            sendBroadcast(new Intent(BRIGHTNESS_CHANGED));
        }

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (mGpsListener == null && mLocListener == null) {
            if (locManager != null) {
                boolean gps_enabled = false;

                try {
                    gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }

                if (gps_enabled) {
                    mGpsListener = new WidgetGPSListener();
                    mLocListener = new WidgetLocationListener();

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Intent permissionsIntent = new Intent(this, SetPermissionsActivity.class);
                                permissionsIntent.putExtra(SetPermissionsActivity.PERMISSIONS_TYPE, SetPermissionsActivity.PERMISSIONS_REQUEST_LOCATION);
                                permissionsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(permissionsIntent);
                            } else {
                                locManager.addGpsStatusListener(mGpsListener);
                                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        GPS_UPDATE_TIME_INTERVAL, GPS_UPDATE_DISTANCE_INTERVAL,
                                        mLocListener);
                            }
                        } else {
                            locManager.addGpsStatusListener(mGpsListener);
                            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    GPS_UPDATE_TIME_INTERVAL, GPS_UPDATE_DISTANCE_INTERVAL,
                                    mLocListener);
                        }
                    } catch (SecurityException e) {
                        Log.e(LOG_TAG, "", e);
                    }
                }
            }
        }

        if (mSettingsObserver == null) {
            mSettingsObserver = new BrightnessObserver(new Handler(), this);
            mSettingsObserver.startObserving();
        }

        if (mRotationObserver == null) {
            mRotationObserver = new RotationObserver(new Handler(), this);
            mRotationObserver.startObserving();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        if (appWidgetManager == null)
            return START_NOT_STICKY;

        if (intent == null) { // prepare data for clock update
            Log.d(LOG_TAG, "WidgetUpdateService onStartCommand intent = null");

            intent = new Intent(this, WidgetUpdateService.class);
            intent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_TICK);

            ComponentName thisWidget = new ComponentName(this,
                    DigitalClockAppWidgetProvider.class);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            if (appWidgetIds.length > 0)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        }

        Bundle extras = intent.getExtras();

        if (extras == null) {
            Log.d(LOG_TAG, "WidgetUpdateService onStartCommand extras = null");

            intent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_TICK);

            ComponentName thisWidget = new ComponentName(this,
                    DigitalClockAppWidgetProvider.class);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            if (appWidgetIds.length > 0)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        }

        String intentExtra = null;
        boolean updateWeather = false;

        if (extras != null) {
            intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);
            updateWeather = extras.getBoolean(WidgetInfoReceiver.UPDATE_WEATHER, false);
        }

        boolean scheduledUpdate = intent.getBooleanExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, false);

        if (intentExtra != null && intentExtra.equals(BATTERY_NOTIFICATION)) {
            updateNotificationBatteryStatus(intent);
        }

        if (intentExtra != null && intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)) {
            updateNotificationBatteryStatus(intent);

            int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);

            int level = -1;

            if (rawLevel >= 0 && scale > 0) {
                level = (rawLevel * 100) / scale;
            }

            if (level == -1) {
                Intent batteryIntent = getApplicationContext().registerReceiver(
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

                int level1 = -1;

                if (rawLevel1 >= 0 && scale1 > 0) {
                    level1 = (rawLevel1 * 100) / scale1;
                }

                level = level1;
                scale = scale1;
                status = status1;
            }

            // prevent frequent updates
            if (mBatteryLevel != -1 && mBatteryScale != -1 && mBatteryStatus != BatteryManager.BATTERY_STATUS_UNKNOWN &&
                    level != -1 && scale != -1 && status != BatteryManager.BATTERY_STATUS_UNKNOWN &&
                    level == mBatteryLevel && scale == mBatteryScale && status == mBatteryStatus) {
                return START_STICKY;
            }

            mBatteryLevel = level;
            mBatteryScale = scale;
            mBatteryStatus = status;

            updateNotificationBatteryStatus(intent);
        }

        if (intentExtra != null && intentExtra.equals(Intent.ACTION_LOCALE_CHANGED)) {
            String langDef = Locale.getDefault().getLanguage();
            Preferences.setLanguageOptions(this, langDef);
        }

        String lang = Preferences.getLanguageOptions(this);
        boolean confUpdated = false;

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals(""))
                lang = langDef;
            else
                lang = "en";

            Preferences.setLanguageOptions(this, lang);

            // Change locale settings in the application
            Resources res = this.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(lang.toLowerCase());
            res.updateConfiguration(conf, dm);

            confUpdated = true;
        }

        if (intentExtra != null && (intentExtra.equals(UPDATE_WIDGETS) || intentExtra.equals(Intent.ACTION_CONFIGURATION_CHANGED)
                || intentExtra.equals(Intent.ACTION_BOOT_COMPLETED) || intentExtra.equals(Intent.ACTION_LOCALE_CHANGED)
                || intentExtra.equals(APPWIDGET_RESIZE) || intentExtra.equals(APPWIDGET_UPDATE_OPTIONS))) {

            updateNotificationBatteryStatus(intent);

            ComponentName thisWidget;
            //ComponentName powerWidget;
            RemoteViews remoteViews;

            // update all widgets
            if (!confUpdated) {
                // Change locale settings in the application
                Resources res = this.getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.locale = new Locale(lang.toLowerCase());
                res.updateConfiguration(conf, dm);
            }

            // update clock & weather widgets
            thisWidget = new ComponentName(this,
                    DigitalClockAppWidgetProvider.class);

            if (thisWidget != null) {
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds.length > 0) {
                    for (int appWidgetId : appWidgetIds) {

                        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

                        if (showWeather) {
                            remoteViews = buildClockUpdate(appWidgetId);
                            updateClockStatus(remoteViews, appWidgetId, true);

                            // translate weather info if locale is changed
                            if (intentExtra.equals(UPDATE_WIDGETS) || intentExtra.equals(Intent.ACTION_LOCALE_CHANGED)) {
                                updateWeatherStatus(remoteViews, appWidgetId, true);
                            }

                            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                        }
                    }
                }
            }

            // update toggle widgets
            Intent newIntent;

            try {
                //update power widget
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, POWER_WIDGET_UPDATE_ALL);

                WidgetManager widgetManager = new WidgetManager(this);
                widgetManager.updatePowerWidgets(this, intentExtra);

                // update single widgets
                thisWidget = new ComponentName(this, BatteryAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_BATTERY_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, AirplaneAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_AIRPLANE_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, BluetoothAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_BLUETOOTH_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, BrightnessAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_BRIGHTNESS_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, GpsAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_GPS_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, MobileAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_MOBILE_DATA_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, NfcAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_NFC_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, OrientationAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_ORIENTATION_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, RingerAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_RINGER_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, SyncAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_SYNC_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, TorchAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_TORCH_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

                thisWidget = new ComponentName(this, WifiAppWidgetProvider.class);
                newIntent = new Intent(this, WidgetUpdateService.class);
                newIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, UPDATE_SINGLE_WIFI_WIDGET);

                remoteViews = buildUpdate(newIntent);
                if (remoteViews != null) {
                    appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        } else {
            if (intentExtra != null && (intentExtra.equals(Intent.ACTION_TIME_CHANGED)
                    || intentExtra.equals(Intent.ACTION_TIME_TICK)
                    || intentExtra.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || intentExtra.equals(CLOCK_WIDGET_UPDATE)
                    || intentExtra.equals(WEATHER_UPDATE))) {

                int appWidgetIds[] = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                int appWidgetIdSingle = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

                RemoteViews updateViews;

                if (appWidgetIds != null) {
                    for (int appWidgetId : appWidgetIds) {

                        updateViews = buildClockUpdate(appWidgetId);

                        if (intentExtra.equals(WEATHER_UPDATE))
                            updateWeatherStatus(updateViews, appWidgetId, scheduledUpdate);
                        else
                            updateClockStatus(updateViews, appWidgetId, updateWeather);

                        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
                    }
                } else {
                    if (appWidgetIdSingle != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        updateViews = buildClockUpdate(appWidgetIdSingle);

                        if (intentExtra.equals(WEATHER_UPDATE))
                            updateWeatherStatus(updateViews, appWidgetIdSingle, scheduledUpdate);
                        else
                            updateClockStatus(updateViews, appWidgetIdSingle, updateWeather);

                        appWidgetManager.updateAppWidget(appWidgetIdSingle, updateViews);
                    }
                }
            } else {

                if (intentExtra != null) {

                    // single toggle and battery widgets
                    try {
                        // Push update for this widget to the home screen
                        ComponentName thisWidget = null;

                        if (intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)
                                || intentExtra.equals(UPDATE_SINGLE_BATTERY_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    BatteryAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                                || intentExtra.equals(BLUETOOTH_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_BLUETOOTH_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    BluetoothAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                                || intentExtra.equals(WIFI_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_WIFI_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    WifiAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                                || intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_MOBILE_DATA_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    MobileAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(LOCATION_PROVIDERS_CHANGED)
                                || intentExtra.equals(LOCATION_GPS_ENABLED_CHANGED)
                                || intentExtra.equals(GPS_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_GPS_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    GpsAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                                || intentExtra.equals(RINGER_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_RINGER_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    RingerAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                                || intentExtra.equals(AIRPLANE_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_AIRPLANE_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    AirplaneAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(BRIGHTNESS_CHANGED)
                                || intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_BRIGHTNESS_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    BrightnessAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(NFC_ADAPTER_STATE_CHANGED)
                                || intentExtra.equals(NFC_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_NFC_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    NfcAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(SYNC_CONN_STATUS_CHANGED)
                                || intentExtra.equals(SYNC_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_SYNC_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    SyncAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(AUTO_ROTATE_CHANGED)
                                || intentExtra.equals(ORIENTATION_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_ORIENTATION_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    OrientationAppWidgetProvider.class);
                        }

                        if (intentExtra.equals(FLASHLIGHT_CHANGED)
                                || intentExtra.equals(TORCH_WIDGET_UPDATE)
                                || intentExtra.equals(UPDATE_SINGLE_TORCH_WIDGET)) {
                            thisWidget = new ComponentName(this,
                                    TorchAppWidgetProvider.class);
                        }

                        if (thisWidget != null) {

                            toggleWidgets(intent);
                            RemoteViews updateViews = buildUpdate(intent);

                            if (updateViews != null) {

                                appWidgetManager.updateAppWidget(thisWidget,
                                        updateViews);
                            }
                        }

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "", e);
                    }

                    // power widgets
                    if (
                            intentExtra.equals(Intent.ACTION_BATTERY_CHANGED) ||
                                    intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED) ||
                                    intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ||
                                    intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
                                    intentExtra.equals(LOCATION_PROVIDERS_CHANGED) ||
                                    intentExtra.equals(LOCATION_GPS_ENABLED_CHANGED) ||
                                    intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) ||
                                    intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED) ||
                                    intentExtra.equals(BRIGHTNESS_CHANGED) ||
                                    intentExtra.equals(NFC_ADAPTER_STATE_CHANGED) ||
                                    intentExtra.equals(SYNC_CONN_STATUS_CHANGED) ||
                                    intentExtra.equals(AUTO_ROTATE_CHANGED) ||
                                    intentExtra.equals(FLASHLIGHT_CHANGED) ||

                                    intentExtra.equals(POWER_BLUETOOTH_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_WIFI_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_MOBILE_DATA_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_GPS_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_RINGER_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_AIRPLANE_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_BRIGHTNESS_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_NFC_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_SYNC_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_ORIENTATION_WIDGET_UPDATE) ||
                                    intentExtra.equals(POWER_TORCH_WIDGET_UPDATE) ||

                                    intentExtra.equals(POWER_WIDGET_UPDATE_ALL)) {


                        WidgetManager widgetManager = new WidgetManager(this);
                        widgetManager.updatePowerWidgets(this, intentExtra);
                    }
                }
            }
        }

        return START_STICKY;
    }

    private void showNotification() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIdsDigitalClock = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));
        int[] appWidgetIdsPower = appWidgetManager.getAppWidgetIds(new ComponentName(this, PowerAppWidgetProvider.class));

        if (appWidgetIdsDigitalClock.length > 0 || appWidgetIdsPower.length > 0) {
            Intent notificationIntent = new Intent(this, DigitalClockAppWidgetPreferenceActivity.class);
            //notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.icon)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_NO_CLEAR)
                    .setWhen(0)
                    .setPriority(Notification.PRIORITY_HIGH).build();
            startForeground(101,
                    notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public RemoteViews buildUpdate(Intent intent) {
        Log.d(LOG_TAG, "WidgetUpdateService buildUpdate");

        // Build an update that holds the updated widget contents
        Bundle extras = intent.getExtras();

        if (extras == null)
            return null;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (intentExtra == null)
            return null;

        RemoteViews updateViews = null;

        if (intentExtra.equals(UPDATE_SINGLE_BATTERY_WIDGET) || intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.batterywidget);

            try {
                updateBatteryStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                Intent batteryInfo = new Intent(this, BatteryInfoActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, batteryInfo,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.batteryWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_BLUETOOTH_WIDGET) || intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                || intentExtra.equals(BLUETOOTH_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.bluetoothwidget);

            try {
                updateBluetoothStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(BLUETOOTH_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.bluetoothWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_WIFI_WIDGET) || intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                || intentExtra.equals(WIFI_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.wifiwidget);

            try {
                updateWifiStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(WIFI_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.wifiWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_MOBILE_DATA_WIDGET) || intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                || intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.mobilewidget);

            try {
                updateDataStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;

                if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(MOBILE_DATA_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                            pendingIntent);
                } else {
                    Process p;
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
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                                0, new Intent(MOBILE_DATA_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                                pendingIntent);
                    } else {
                        Intent intentData = new Intent(Intent.ACTION_MAIN);
                        intentData.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                                intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                                pendingIntent);
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);

                Intent intentData = new Intent(Intent.ACTION_MAIN);
                intentData.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_GPS_WIDGET) || intentExtra.equals(LOCATION_PROVIDERS_CHANGED)
                || intentExtra.equals(LOCATION_GPS_ENABLED_CHANGED)
                || intentExtra.equals(GPS_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.gpswidget);

            try {
                updateGpsStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleGPS()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(GPS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                Intent locationIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                ResolveInfo resolveInfo = getPackageManager().resolveActivity(
                        locationIntent, PackageManager.MATCH_DEFAULT_ONLY);
                PendingIntent pendingIntent;

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(this, 0,
                            locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                }
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_RINGER_WIDGET) || intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                || intentExtra.equals(RINGER_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.ringerwidget);

            try {
                updateRingerStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(RINGER_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.ringerWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_AIRPLANE_WIDGET) || intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                || intentExtra.equals(AIRPLANE_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.airplanewidget);

            try {
                updateAirplaneMode(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleAirplane()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(AIRPLANE_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                Intent airplaneIntent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                ResolveInfo resolveInfo = this.getPackageManager().resolveActivity(
                        airplaneIntent, PackageManager.MATCH_DEFAULT_ONLY);
                PendingIntent pendingIntent;

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(this, 0,
                            airplaneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } else {
                    Intent wirelessIntent = new Intent(
                            Settings.ACTION_WIRELESS_SETTINGS);
                    ResolveInfo resolveWirelessInfo = getPackageManager().resolveActivity(
                            wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (resolveWirelessInfo != null) {
                        pendingIntent = PendingIntent.getActivity(this, 0,
                                wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                                pendingIntent);
                    }
                }
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_BRIGHTNESS_WIDGET) || intentExtra.equals(BRIGHTNESS_CHANGED)
                || intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.brightnesswidget);

            try {
                updateBrightness(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {

                int nOptions = Preferences.getBrightnessOptions(this);

                PendingIntent pendingIntent;

                switch (nOptions) {
                    case 0: // toggle
                        pendingIntent = PendingIntent.getBroadcast(this,
                                0, new Intent(BRIGHTNESS_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                    case 1: // dialog
                        Intent brightnessIntent = new Intent(this, BrightnessActivity.class);

                        pendingIntent = PendingIntent.getActivity(this,
                                0, brightnessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                    default:
                        pendingIntent = PendingIntent.getBroadcast(this,
                                0, new Intent(BRIGHTNESS_WIDGET_UPDATE),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        break;
                }

                updateViews.setOnClickPendingIntent(R.id.brightnessWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_NFC_WIDGET) || intentExtra.equals(NFC_ADAPTER_STATE_CHANGED)
                || intentExtra.equals(NFC_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.nfcwidget);

            try {
                updateNfcStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            if (canToggleNfc()) {
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(NFC_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                            pendingIntent);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }
            } else {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
                    NfcAdapter adapter = null;

                    if (manager != null) {
                        adapter = manager.getDefaultAdapter();
                    }

                    if (adapter != null) {
                        Intent wirelessIntent;

                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
                            wirelessIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        else
                            wirelessIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        ResolveInfo resolveInfo = getPackageManager().resolveActivity(
                                wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        PendingIntent pendingIntent;

                        if (resolveInfo != null) {
                            pendingIntent = PendingIntent.getActivity(this, 0,
                                    wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                                    pendingIntent);
                        }
                    }
                }
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_SYNC_WIDGET) || intentExtra.equals(SYNC_CONN_STATUS_CHANGED)
                || intentExtra.equals(SYNC_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.syncwidget);

            try {
                updateSyncStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(SYNC_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.syncWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_ORIENTATION_WIDGET) || intentExtra.equals(AUTO_ROTATE_CHANGED)
                || intentExtra.equals(ORIENTATION_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.orientationwidget);

            try {
                updateOrientation(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(ORIENTATION_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.orientationWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        if (intentExtra.equals(UPDATE_SINGLE_TORCH_WIDGET) || intentExtra.equals(FLASHLIGHT_CHANGED)
                || intentExtra.equals(TORCH_WIDGET_UPDATE)) {
            updateViews = new RemoteViews(getPackageName(), R.layout.torchwidget);

            try {
                updateTorchStatus(updateViews, intent, AppWidgetManager.INVALID_APPWIDGET_ID);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }

            try {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(TORCH_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.torchWidget,
                        pendingIntent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        return updateViews;
    }

    public RemoteViews buildPowerUpdate(Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService buildPowerUpdate");

        // Build an update that holds the updated widget contents
        Bundle extras = intent.getExtras();

        if (extras == null)
            return null;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (intentExtra == null)
            return null;

        RemoteViews updateViews = new RemoteViews(getPackageName(),
                R.layout.powerwidget);

        updateViews.setViewVisibility(R.id.loadingWidget,
                View.GONE);

        boolean bShowBluetooth = Preferences.getShowBluetooth(this, appWidgetId);
        boolean bShowGps = Preferences.getShowGps(this, appWidgetId);
        boolean bShowMobile = Preferences.getShowMobile(this, appWidgetId);
        boolean bShowRinger = Preferences.getShowRinger(this, appWidgetId);
        boolean bShowWifi = Preferences.getShowWiFi(this, appWidgetId);
        boolean bShowAirplane = Preferences.getShowAirplane(this, appWidgetId);
        boolean bShowBrightness = Preferences.getShowBrightness(this, appWidgetId);
        boolean bShowNfc = Preferences.getShowNfc(this, appWidgetId);
        boolean bShowSync = Preferences.getShowSync(this, appWidgetId);
        boolean bShowOrientation = Preferences.getShowOrientation(this, appWidgetId);
        boolean bShowTorch = Preferences.getShowTorch(this, appWidgetId);
        boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(this, appWidgetId);
        boolean bShowSettings = Preferences.getShowSettings(this, appWidgetId);

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

        Intent prefIntent = new Intent(this, PowerAppWidgetPreferenceActivity.class);

        prefIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        prefIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent settingsIntent = PendingIntent.getActivity(this,
                appWidgetId, prefIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.settingsWidget, settingsIntent);

        int colorOff = WIDGET_COLOR_OFF;
        int colorBackground = WIDGET_COLOR_BACKGROUND;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorBackground = Preferences.getColorBackground(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        BitmapDrawable bitmapDrawable = setIconColor(this, colorOff, R.drawable.settings_on);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewSettings, bitmapDrawable.getBitmap());

        updateViews.setInt(R.id.imageViewSettingsInd, "setColorFilter", colorOff);

        int iOpacity = Preferences.getPowerOpacity(this, appWidgetId);

        updateViews.setInt(R.id.backgroundImage, "setAlpha", iOpacity * 255 / 100);
        updateViews.setInt(R.id.backgroundImage, "setColorFilter", colorBackground);

        updateViews.setTextColor(R.id.textViewSettings, colorTextOff);

        try {
            Intent batteryInfo = new Intent(this, BatteryInfoActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, batteryInfo,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.batteryStatusWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_BLUETOOTH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.bluetoothWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_WIFI_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.wifiWidget,
                    pendingIntent);

            int currentApiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(POWER_MOBILE_DATA_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            } else {
                Intent intentData = new Intent(Intent.ACTION_MAIN);
                intentData.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                pendingIntent = PendingIntent.getActivity(this, 0,
                        intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            }

            if (canToggleGPS()) {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(POWER_GPS_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                        pendingIntent);
            } else {
                Intent locationIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                ResolveInfo resolveInfo = getPackageManager().resolveActivity(
                        locationIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(this, 0,
                            locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                }
            }

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_RINGER_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.ringerWidget,
                    pendingIntent);

            if (canToggleAirplane()) {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(POWER_AIRPLANE_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                        pendingIntent);

            } else {
                Intent airplaneIntent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                ResolveInfo resolveInfo = this.getPackageManager().resolveActivity(
                        airplaneIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(this, 0,
                            airplaneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } else {
                    Intent wirelessIntent = new Intent(
                            Settings.ACTION_WIRELESS_SETTINGS);
                    ResolveInfo resolveWirelessInfo = getPackageManager().resolveActivity(
                            wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (resolveWirelessInfo != null) {
                        pendingIntent = PendingIntent.getActivity(this, 0,
                                wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                                pendingIntent);
                    }
                }
            }

            int nOptions = Preferences.getBrightnessOptions(this);

            switch (nOptions) {
                case 0: // toggle
                    pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                case 1: // dialog
                    Intent brightnessIntent = new Intent(this, BrightnessActivity.class);

                    pendingIntent = PendingIntent.getActivity(this,
                            0, brightnessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                default:
                    pendingIntent = PendingIntent.getBroadcast(this,
                            0, new Intent(POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
            }

            updateViews.setOnClickPendingIntent(R.id.brightnessWidget,
                    pendingIntent);

            if (canToggleNfc()) {
                pendingIntent = PendingIntent.getBroadcast(this,
                        0, new Intent(POWER_NFC_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                        pendingIntent);
            } else {
                if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD_MR1 &&
                        getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
                    NfcAdapter adapter = null;

                    if (manager != null) {
                        adapter = manager.getDefaultAdapter();
                    }

                    if (adapter != null) {
                        Intent wirelessIntent;

                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
                            wirelessIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        else
                            wirelessIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        ResolveInfo resolveInfo = getPackageManager().resolveActivity(
                                wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                        if (resolveInfo != null) {
                            pendingIntent = PendingIntent.getActivity(this, 0,
                                    wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                                    pendingIntent);
                        }
                    }
                }
            }

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_SYNC_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.syncWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_ORIENTATION_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.orientationWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(this,
                    0, new Intent(POWER_TORCH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.torchWidget,
                    pendingIntent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }

        return updateViews;
    }

    public void updatePowerWidgetStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {

        if (updateViews == null)
            return;

        if (intent == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            return;

        try {
            boolean bShowBluetooth = Preferences.getShowBluetooth(this, appWidgetId);
            boolean bShowGps = Preferences.getShowGps(this, appWidgetId);
            boolean bShowMobile = Preferences.getShowMobile(this, appWidgetId);
            boolean bShowRinger = Preferences.getShowRinger(this, appWidgetId);
            boolean bShowWifi = Preferences.getShowWiFi(this, appWidgetId);
            boolean bShowAirplane = Preferences.getShowAirplane(this, appWidgetId);
            boolean bShowBrightness = Preferences.getShowBrightness(this, appWidgetId);
            boolean bShowNfc = Preferences.getShowNfc(this, appWidgetId);
            boolean bShowSync = Preferences.getShowSync(this, appWidgetId);
            boolean bShowOrientation = Preferences.getShowOrientation(this, appWidgetId);
            boolean bShowTorch = Preferences.getShowTorch(this, appWidgetId);
            boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(this, appWidgetId);
            //boolean bShowSettings = Preferences.getShowSettings(this, appWidgetId);

            if (bShowBatteryStatus)
                updateBatteryStatus(updateViews, intent, appWidgetId);
            if (bShowBluetooth)
                updateBluetoothStatus(updateViews, intent, appWidgetId);
            if (bShowWifi)
                updateWifiStatus(updateViews, intent, appWidgetId);
            if (bShowMobile)
                updateDataStatus(updateViews, intent, appWidgetId);
            if (bShowGps)
                updateGpsStatus(updateViews, intent, appWidgetId);
            if (bShowRinger)
                updateRingerStatus(updateViews, intent, appWidgetId);
            if (bShowAirplane)
                updateAirplaneMode(updateViews, intent, appWidgetId);
            if (bShowBrightness)
                updateBrightness(updateViews, intent, appWidgetId);
            if (bShowNfc)
                updateNfcStatus(updateViews, intent, appWidgetId);
            if (bShowSync)
                updateSyncStatus(updateViews, intent, appWidgetId);
            if (bShowOrientation)
                updateOrientation(updateViews, intent, appWidgetId);
            if (bShowTorch)
                updateTorchStatus(updateViews, intent, appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }


    public RemoteViews buildClockUpdate(int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService buildClockUpdate");

        RemoteViews updateViews = new RemoteViews(getPackageName(),
                R.layout.digitalclockwidget);

        updateViews.setViewVisibility(R.id.loadingWidget,
                View.GONE);

        updateViews.setViewVisibility(R.id.clockWidget,
                View.VISIBLE);

        boolean bShowDate = Preferences.getShowDate(this, appWidgetId);
        boolean bShowBattery = Preferences.getShowBattery(this, appWidgetId);
        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

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
            Intent intent = new Intent(this, DigitalClockAppWidgetPreferenceActivity.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.clockWidget, pendingIntent);
        }

        // start clock&alarms application when clicked on clock's hours
        {
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
                    getPackageManager().getActivityInfo(cn, PackageManager.GET_META_DATA);
                    alarmClockIntent.setComponent(cn);
                    foundClockImpl = true;
                    break;
                } catch (NameNotFoundException nf) {
                    Log.d(LOG_TAG, "WidgetUpdateService buildClockUpdate nameNotFound");
                }
            }

            if (foundClockImpl) {
                PendingIntent pendingIntentClock = PendingIntent.getActivity(
                        this, appWidgetId, alarmClockIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                updateViews.setOnClickPendingIntent(R.id.imageViewClockHour,
                        pendingIntentClock);
            }
        }

        // start weather forecast activity when clicked on weather icon
        Intent weatherForecastIntent = new Intent(this, WeatherForecastActivity.class);
        weatherForecastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingForecastIntent = PendingIntent.getActivity(this,
                appWidgetId, weatherForecastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        updateViews.setOnClickPendingIntent(R.id.imageViewWeather, pendingForecastIntent);

        // refresh weather data
        Intent refreshIntent = new Intent(this, WidgetUpdateService.class);
        refreshIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.WEATHER_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingRefreshIntent = PendingIntent.getService(this, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        updateViews.setOnClickPendingIntent(R.id.viewButtonRefresh, pendingRefreshIntent);

        return (updateViews);
    }

    @SuppressLint("StaticFieldLeak")
    public void toggleWidgets(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        final String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (intentExtra == null)
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                int nOptions = Preferences.getBrightnessOptions(getApplicationContext()); // 0 - toggle, 1 - dialog

                if (intentExtra.equals(BLUETOOTH_WIDGET_UPDATE) || intentExtra.equals(POWER_BLUETOOTH_WIDGET_UPDATE)) {
                    toggleBluetoothState();
                }

                if (intentExtra.equals(WIFI_WIDGET_UPDATE) || intentExtra.equals(POWER_WIFI_WIDGET_UPDATE)) {
                    toggleWiFi();
                }

                if (intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE) || intentExtra.equals(POWER_MOBILE_DATA_WIDGET_UPDATE)) {
                    toggleData();
                }

                if (intentExtra.equals(GPS_WIDGET_UPDATE) || intentExtra.equals(POWER_GPS_WIDGET_UPDATE)) {
                    toggleGps();
                }

                if (intentExtra.equals(RINGER_WIDGET_UPDATE) || intentExtra.equals(POWER_RINGER_WIDGET_UPDATE)) {
                    toggleRinger();
                }

                if (intentExtra.equals(AIRPLANE_WIDGET_UPDATE) || intentExtra.equals(POWER_AIRPLANE_WIDGET_UPDATE)) {
                    toggleAirplaneMode();
                }

                if (nOptions == 0 && (intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE) || intentExtra.equals(POWER_BRIGHTNESS_WIDGET_UPDATE))) {
                    toggleBrightness();
                }

                if (intentExtra.equals(NFC_WIDGET_UPDATE) || intentExtra.equals(POWER_NFC_WIDGET_UPDATE)) {
                    toggleNfc();
                }

                if (intentExtra.equals(SYNC_WIDGET_UPDATE) || intentExtra.equals(POWER_SYNC_WIDGET_UPDATE)) {
                    toggleSync();
                }

                if (intentExtra.equals(ORIENTATION_WIDGET_UPDATE) || intentExtra.equals(POWER_ORIENTATION_WIDGET_UPDATE)) {
                    toggleOrientation();
                }

                if (intentExtra.equals(TORCH_WIDGET_UPDATE) || intentExtra.equals(POWER_TORCH_WIDGET_UPDATE)) {
                    toggleTorch();
                }
                return null;
            }
        }.execute();
    }

    public void updateBatteryStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateBatteryStatus");

        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);
        int level = -1;
        int icon = R.drawable.battery_widget_75;

        if (rawLevel >= 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
        }

        if (level == -1) {
            Intent batteryIntent = this.registerReceiver(
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
                    else if (level >= 25 && level < 50)
                        icon = R.drawable.battery_widget_50_charging;
                    else if (level >= 50 && level < 75)
                        icon = R.drawable.battery_widget_75_charging;
                    else
                        icon = R.drawable.battery_widget_100_charging;
                    break;
                case (BatteryManager.BATTERY_STATUS_DISCHARGING):
                case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
                    if (level < 25)
                        icon = R.drawable.battery_widget_25;
                    else if (level >= 25 && level < 50)
                        icon = R.drawable.battery_widget_50;
                    else if (level >= 50 && level < 75)
                        icon = R.drawable.battery_widget_75;
                    else
                        icon = R.drawable.battery_widget_100;
                    break;
            }

            updateViews.setImageViewResource(R.id.imageViewBattery, icon);
        } else {
            int threshold2 = Preferences.getThresholdBattery2(this, appWidgetId);
            int threshold3 = Preferences.getThresholdBattery3(this, appWidgetId);
            int threshold4 = Preferences.getThresholdBattery4(this, appWidgetId);

            int color1 = Preferences.getColorBattery1(this, appWidgetId);
            int color2 = Preferences.getColorBattery2(this, appWidgetId);
            int color3 = Preferences.getColorBattery3(this, appWidgetId);
            int color4 = Preferences.getColorBattery4(this, appWidgetId);

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

            updateViews.setImageViewBitmap(R.id.imageViewBatteryStatus, getFontBitmap(this, strLevel, color, font, true, 144));
            updateViews.setInt(R.id.imageViewBatteryStatusInd, "setColorFilter", color);

            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.VISIBLE);
                BitmapDrawable bitmapDrawable = setIconColor(this, color, R.drawable.high_voltage);

                if (bitmapDrawable != null)
                    updateViews.setImageViewBitmap(R.id.imageViewBatteryStatusCharge, bitmapDrawable.getBitmap());
            } else {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.GONE);
            }
        }
    }

    @SuppressLint("NewApi")
    public void updateNotificationBatteryStatus(Intent intent) {
        Log.d(LOG_TAG, "WidgetUpdateService updateNotificationBatteryStatus");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (!Preferences.getShowBatteryNotif(this)) {
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
        int icons = Preferences.getBatteryIcons(this);
        int icon = (icons == 1 ? R.drawable.battery_font_charge_000 : R.drawable.battery_charge_000);

        if (rawLevel >= 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
        }

        if (level == -1) {
            Intent batteryIntent = getApplicationContext().registerReceiver(
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

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setColor(0);
        }

        notification.setDefaults(Notification.FLAG_NO_CLEAR);
        notification.setSmallIcon(icon);
        notification.setWhen(0);
        notification.setPriority(Notification.PRIORITY_HIGH);
        notification.setOngoing(true);

        Intent batteryInfo = new Intent(this, BatteryInfoActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, batteryInfo,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence batteryStatus = getResources().getText(R.string.batterycurrentstatusfull);

        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batteryunknown);
        } else if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batterycharging);
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batterydisharging);
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batterydisharging);
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batteryfull);
        } else {
            batteryStatus = batteryStatus + " " + getResources().getText(R.string.batteryunknown);
        }

        notification.setContentTitle(getResources().getText(R.string.batterylevelfull) + " " + level + "%");
        notification.setContentText(batteryStatus);
        notification.setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(R.string.batterynotification, notification.build());
        }
    }

    public boolean canToggleAirplane() {
        return VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1;
    }

    public boolean getAirplaneMode() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public void setAirplaneMode(boolean airplaneMode) {

        if (canToggleAirplane()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            } else {
                Settings.Global.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            }

            // Post an intent to reload
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", airplaneMode);
            sendBroadcast(intent);
        }
    }

    public void toggleAirplaneMode() {
        Boolean airplaneMode = getAirplaneMode();
        // ignore toggle requests if the Airplane mode is currently changing
        // state
        setAirplaneMode(!airplaneMode);
    }

    @SuppressWarnings("unused")
    public void updateAirplaneMode(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateAirplaneMode");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_AIRPLANE_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(AIRPLANE_WIDGET_UPDATE) && !intentExtra.equals(POWER_AIRPLANE_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewAirplane, getResources().getText(R.string.airplane));

        Boolean airplaneMode = getAirplaneMode();

        BitmapDrawable bitmapDrawable = setIconColor(this, airplaneMode ? colorOn : colorOff, R.drawable.airplane_on);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewAirplane, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewAirplane, airplaneMode ? colorTextOn : colorTextOff);
        updateViews.setInt(R.id.imageViewAirplaneInd, "setColorFilter", airplaneMode ? colorOn : colorOff);
    }

    protected Boolean getBluetoothState() {

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

    public void setBluetoothState(boolean bluetoothState) {

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

    public void toggleBluetoothState() {
        Boolean bluetoothState = getBluetoothState();
        // ignore toggle requests if the BlueTooth is currently changing state
        if (bluetoothState != null) {
            setBluetoothState(!bluetoothState);
        }
    }

    public void updateBluetoothStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateBluetoothStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_BLUETOOTH_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(BLUETOOTH_WIDGET_UPDATE) && !intentExtra.equals(POWER_BLUETOOTH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBluetooth, getResources().getText(R.string.bluetooth));

        Boolean bluetoothState = getBluetoothState();

        if (bluetoothState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, bluetoothState ? colorOn : colorOff, R.drawable.bluetooth_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewBluetooth, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewBluetooth, bluetoothState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewBluetoothInd, "setColorFilter", bluetoothState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.bluetooth_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewBluetooth, bitmapDrawable.getBitmap());

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

    protected Boolean getFlashOn() {

        return flashOn;
    }

    public void setTorchState(boolean torchState) {

        Log.v(LOG_TAG, "setTorchState - " + torchState);

        PackageManager packageManager = getPackageManager();

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
                        Parameters param = camera.getParameters();
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
                        Parameters param = camera.getParameters();
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

            sendBroadcast(new Intent(FLASHLIGHT_CHANGED));
        }
    }


    public void toggleTorch() {
        Boolean torchState = getFlashOn();
        // ignore toggle requests if the flashlight is currently changing state
        if (torchState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Intent permissionsIntent = new Intent(this, SetPermissionsActivity.class);
                    permissionsIntent.putExtra(SetPermissionsActivity.PERMISSIONS_TYPE, SetPermissionsActivity.PERMISSIONS_REQUEST_CAMERA);
                    permissionsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(permissionsIntent);
                } else {
                    setTorchState(!torchState);
                }
            } else {
                setTorchState(!torchState);
            }
        }
    }

    public void updateTorchStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateTorchStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(FLASHLIGHT_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_TORCH_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(TORCH_WIDGET_UPDATE) && !intentExtra.equals(POWER_TORCH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewTorch, getResources().getText(R.string.torch));

        Boolean torchState = getFlashOn();

        if (torchState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, torchState ? colorOn : colorOff, R.drawable.flashlight_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewTorch, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewTorch, torchState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", torchState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.flashlight_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewTorch, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewTorch, colorTextOn);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getWifiState() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    public void setWifiState(boolean wifiState) {

        Log.v(LOG_TAG, "setWifiState - " + wifiState);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null)
            wifiManager.setWifiEnabled(wifiState);
    }

    public void toggleWiFi() {
        Boolean wifiState = getWifiState();
        // ignore toggle requests if the WiFi is currently changing state
        if (wifiState != null) {
            setWifiState(!wifiState);
        }
    }

    public void updateWifiStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateWifiStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) && !intentExtra.equals(UPDATE_SINGLE_WIFI_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WIFI_WIDGET_UPDATE) && !intentExtra.equals(POWER_WIFI_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewWiFi, getResources().getText(R.string.wifi));

        Boolean wifiState = getWifiState();

        if (wifiState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, wifiState ? colorOn : colorOff, R.drawable.wifi_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewWiFi, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewWiFi, wifiState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", wifiState ? colorOn : colorOff);

            if (wifiState) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.wifi_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewWiFi, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewWiFi, colorTextOn);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getSyncStatus() {

        Boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
        Log.v(LOG_TAG, "getSyncStatus - " + syncStatus);

        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {

        Log.v(LOG_TAG, "setSyncStatus - " + syncStatus);

        ContentResolver.setMasterSyncAutomatically(syncStatus);
    }

    public void toggleSync() {
        Boolean syncStatus = getSyncStatus();
        // ignore toggle requests if the Sync is currently changing status
        if (syncStatus != null) {
            setSyncStatus(!syncStatus);
        }
    }

    public void updateSyncStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateSyncStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(SYNC_CONN_STATUS_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_SYNC_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(SYNC_WIDGET_UPDATE) && !intentExtra.equals(POWER_SYNC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewSync, getResources().getText(R.string.sync));

        Boolean syncStatus = getSyncStatus();

        if (syncStatus != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, syncStatus ? colorOn : colorOff, R.drawable.sync_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewSync, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewSync, syncStatus ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", syncStatus ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.sync_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewSync, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewSync, colorTextOn);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getOrientation() {

        Boolean orientation = (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);

        Log.v(LOG_TAG, "getOrientation - " + orientation);

        // false = auto-rotation is disabled
        // true = auto-rotation is enabled
        return orientation;
    }

    public void setOrientation(boolean orientation) {

        Log.v(LOG_TAG, "setOrientation - " + orientation);

        Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, orientation ? 1 : 0);
    }

    public void toggleOrientation() {
        Boolean orientation = getOrientation();
        // ignore toggle requests if the orientation is currently changing state
        if (orientation != null) {
            setOrientation(!orientation);
        }
    }

    public void updateOrientation(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateOrientation");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(AUTO_ROTATE_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_ORIENTATION_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(ORIENTATION_WIDGET_UPDATE) && !intentExtra.equals(POWER_ORIENTATION_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewOrientation, getResources().getText(R.string.orientation));

        Boolean orientation = getOrientation();

        if (orientation != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, orientation ? colorOn : colorOff, R.drawable.orientation_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewOrientation, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewOrientation, orientation ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", orientation ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.orientation_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewOrientation, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewOrientation, colorTextOn);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", colorTransition);
        }
    }

    public boolean canToggleNfc() {
        return false;
    }

    protected Boolean getNfcState() {
        Boolean nfcState = null;

        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD_MR1 &&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);

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

    public void setNfcState(boolean nfcState) {

    }

    public void toggleNfc() {
        Boolean nfcState = getNfcState();
        // ignore toggle requests if the NFC is currently changing state
        if (nfcState != null) {
            setNfcState(!nfcState);
        }
    }

    public void updateNfcStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateNfcStatus");

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentApiVersion < 10) {
            return;
        }

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(NFC_ADAPTER_STATE_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_NFC_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(NFC_WIDGET_UPDATE) && !intentExtra.equals(POWER_NFC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewNfc, getResources().getText(R.string.nfc));

        Boolean nfcState = getNfcState();

        if (nfcState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, nfcState ? colorOn : colorOff, R.drawable.nfc_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewNfc, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewNfc, nfcState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", nfcState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.nfc_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewNfc, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewNfc, colorTextOn);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getMobileState() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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

    public void setMobileState(boolean mobileState) {

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

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

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

        ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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

    public void setMobileStateLollipop(boolean mobileState) {

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

    public void toggleData() {
        Boolean mobileState = getMobileState();
        // ignore toggle requests if the Mobile Data is currently changing
        // state
        if (mobileState != null) {
            setMobileState(!mobileState);
        }
    }

    public void updateDataStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateMobileStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION) && !intentExtra.equals(UPDATE_SINGLE_MOBILE_DATA_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE) && !intentExtra.equals(POWER_MOBILE_DATA_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewMobile, getResources().getText(R.string.mobile));

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
            BitmapDrawable bitmapDrawable = setIconColor(this, mobileState ? colorOn : colorOff, R.drawable.data_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewMobile, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewMobile, mobileState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", mobileState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.data_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewMobile, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewMobile, colorTextOn);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", colorTransition);
        }
    }

    private boolean canToggleGPS() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo;

        try {
            packageInfo = packageManager.getPackageInfo("com.android.settings",
                    PackageManager.GET_RECEIVERS);
        } catch (NameNotFoundException e) {
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

    protected Boolean getGpsState() {

        Boolean gpsState;

        LocationManager locationManager = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));

        if (locationManager == null) {
            return null;
        }

        gpsState = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.v(LOG_TAG, "getGpsState - " + gpsState);

        return gpsState;

    }

    public void setGpsState(boolean gpsState) {

        if (canToggleGPS()) {
            if (gpsState) {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            } else {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        }

    }

    public void toggleGps() {
        Boolean gpsState = getGpsState();
        // ignore toggle requests if the GPS is currently changing
        // state
        if (gpsState != null) {
            setGpsState(!gpsState);
        }
    }

    public void updateGpsStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateGpsStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(LOCATION_PROVIDERS_CHANGED) && !intentExtra.equals(LOCATION_GPS_ENABLED_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_GPS_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(GPS_WIDGET_UPDATE) && !intentExtra.equals(POWER_GPS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOn(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewGps, getResources().getText(R.string.gps));

        Boolean gpsState = getGpsState();

        if (gpsState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(this, gpsState ? colorOn : colorOff, R.drawable.gps_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewGps, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewGps, gpsState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", gpsState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(this, colorTransition, R.drawable.gps_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewGps, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewGps, colorTextOn);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", colorTransition);
        }
    }

    protected int getRingerState() {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerState = AudioManager.RINGER_MODE_NORMAL;

        if (audioManager != null) {
            ringerState = audioManager.getRingerMode();
        }

        Log.v(LOG_TAG, "getRingerState - " + ringerState);

        return ringerState;

    }

    public void setRingerState(int ringerState) {

        Log.v(LOG_TAG, "setRingerState - " + ringerState);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && VERSION.SDK_INT >= VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);

            return;
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.allsoundsmuted), Toast.LENGTH_LONG).show();
                }
            }, 1000);

            audioManager.setRingerMode(ringerState);
        }

        if (ringerState == AudioManager.RINGER_MODE_VIBRATE) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            if (v != null) {
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            }
        }

    }

    public void toggleRinger() {
        int ringerState = getRingerState();

        int nToggle = Preferences.getSoundOptions(this);

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

    public void updateRingerStatus(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateRingerStatus");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) && !intentExtra.equals(UPDATE_SINGLE_RINGER_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(RINGER_WIDGET_UPDATE) && !intentExtra.equals(POWER_RINGER_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTransition = Preferences.getColorTransition(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewRinger, getResources().getText(R.string.ringer));

        int resource;
        int color = colorOn;
        int textColor = colorTextOn;

        int ringerState = getRingerState();

        switch (ringerState) {
            case AudioManager.RINGER_MODE_SILENT:
                resource = R.drawable.ringer_silent;
                color = colorOff;
                textColor = colorTextOff;
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                resource = R.drawable.ringer_vibrate;
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                resource = R.drawable.ringer_normal;
                break;

            default:
                resource = R.drawable.ringer_normal;
                color = colorTransition;
                break;
        }

        BitmapDrawable bitmapDrawable = setIconColor(this, color, resource);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewRinger, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewRinger, textColor);
        updateViews.setInt(R.id.imageViewRingerInd, "setColorFilter", color);
    }

    public void toggleBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Settings.System.canWrite(this)) {
                realToggleBrightness();
            } else {
                Intent writeSettingsIntent = new Intent(this, WriteSettingsActivity.class);
                writeSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(writeSettingsIntent);
            }
        } else {
            realToggleBrightness();
        }
    }

    private void realToggleBrightness() {
        try {
            ContentResolver cr = this.getContentResolver();
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

            Settings.System.putInt(this.getContentResolver(),
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
            brightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException snfe) {
            brightness = 255;
        }

        return brightness;
    }

    public boolean isAutoBrightness() {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    public void setBrightness(int brightness) {

        Log.v(LOG_TAG, "setBrightness - " + brightness);
    }

    public void updateBrightness(RemoteViews updateViews, Intent intent, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService updateBrightness");

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        String intentExtra = extras.getString(WidgetInfoReceiver.INTENT_EXTRA);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && intentExtra != null && !intentExtra.equals(BRIGHTNESS_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_BRIGHTNESS_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE) && !intentExtra.equals(POWER_BRIGHTNESS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(this, appWidgetId);
            colorOff = Preferences.getColorOff(this, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(this, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(this, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBrightness, getResources().getText(R.string.brightness));

        int resource;
        int brightness = getBrightness();
        boolean off = false;

        if (isAutoBrightness()) {
            resource = R.drawable.brightness_auto;
        } else {
            if (brightness < 50) {
                resource = R.drawable.brightness_on; // set gray background
                off = true;
            } else {
                if (brightness >= 50 && brightness < 150) {
                    resource = R.drawable.brightness_mid;
                } else {
                    resource = R.drawable.brightness_on;
                }
            }
        }

        BitmapDrawable bitmapDrawable = setIconColor(this, off ? colorOff : colorOn, resource);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewBrightness, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewBrightness, off ? colorTextOff : colorTextOn);
        updateViews.setInt(R.id.imageViewBrightnessInd, "setColorFilter", off ? colorOff : colorOn);
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

    public static int convertDiptoPix(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public void updateClockStatus(RemoteViews updateViews, int appWidgetId, boolean updateWeather) {
        Log.d(LOG_TAG, "WidgetUpdateService updateClockStatus");

        if (updateViews == null) {
            updateViews = new RemoteViews(getPackageName(),
                    R.layout.digitalclockwidget);
        }

        boolean bShowDate = Preferences.getShowDate(this, appWidgetId);
        boolean bShow24Hrs = Preferences.getShow24Hrs(this, appWidgetId);
        boolean bShowBattery = Preferences.getShowBattery(this, appWidgetId);
        int iClockSkinItem = Preferences.getClockSkin(this, appWidgetId);
        int iDateFormatItem = Preferences.getDateFormatItem(this, appWidgetId);
        int iOpacity = Preferences.getOpacity(this, appWidgetId);
        int iFontItem = Preferences.getFontItem(this, appWidgetId);
        boolean bold = Preferences.getBoldText(this, appWidgetId);
        int iDateFontItem = Preferences.getDateFontItem(this, appWidgetId);
        boolean dateBold = Preferences.getDateBoldText(this, appWidgetId);
        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        String currentHour, currentMinute;

        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        currentMinute = sdfMinute.format(new Date());

        if (bShow24Hrs) {
            SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
            currentHour = sdfHour.format(new Date());
        } else {
            SimpleDateFormat sdfHour = new SimpleDateFormat("hh");
            currentHour = sdfHour.format(new Date());
        }

        if (bShowBattery || bShowDate) {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.imageViewDate,
                    View.GONE);
        }

        int systemClockColor;
        int iClockColorItem = Preferences.getClockColorItem(this, appWidgetId);

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

            Preferences.setClockColorItem(this, appWidgetId, -1);
            Preferences.setClockColor(this, appWidgetId, systemClockColor);
        } else {
            systemClockColor = Preferences.getClockColor(this, appWidgetId);
        }

        int systemDateColor;
        int iDateColorItem = Preferences.getDateColorItem(this, appWidgetId);

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

            Preferences.setDateColorItem(this, appWidgetId, -1);
            Preferences.setDateColor(this, appWidgetId, systemDateColor);
        } else {
            systemDateColor = Preferences.getDateColor(this, appWidgetId);
        }

        int systemWidgetColor;
        int iWidgetColorItem = Preferences.getWidgetColorItem(this, appWidgetId);

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

            Preferences.setWidgetColorItem(this, appWidgetId, -1);
            Preferences.setWidgetColor(this, appWidgetId, systemWidgetColor);
        } else {
            systemWidgetColor = Preferences.getWidgetColor(this, appWidgetId);
        }

        String[] mFontArray = getResources().getStringArray(R.array.fontPathValues);

        String font = "fonts/Roboto.ttf";

        if (mFontArray.length > iFontItem)
            font = mFontArray[iFontItem];

        String dateFont = "fonts/Roboto.ttf";

        if (mFontArray.length > iDateFontItem)
            dateFont = mFontArray[iDateFontItem];

        updateViews.setImageViewBitmap(R.id.imageViewClockHour, getFontBitmap(this, currentHour, systemClockColor, font, bold, 96));
        updateViews.setImageViewBitmap(R.id.imageViewClockMinute, getFontBitmap(this, currentMinute, systemClockColor, font, bold, 96));
        updateViews.setImageViewBitmap(R.id.imageViewClockSpace, getFontBitmap(this, ":", systemClockColor, font, bold, 96));

        String currentDate = "";
        String[] mTestArray = getResources().getStringArray(R.array.dateFormat);

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
                Intent batteryIntent = this
                        .registerReceiver(null,
                                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int rawLevel1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
                int scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                        -1);
                if (rawLevel1 >= 0 && scale1 > 0) {
                    level = (rawLevel1 * 100) / scale1;
                }
            }

            currentDate = currentDate + (bShowDate ? "\t [" : "") + level + "%" + (bShowDate ? "]" : "");
        }

        updateViews.setImageViewBitmap(R.id.imageViewDate, getFontBitmap(this, currentDate, systemDateColor, dateFont, dateBold, 14));

        updateViews.setInt(R.id.backgroundImage, "setAlpha", iOpacity * 255 / 100);
        updateViews.setInt(R.id.backgroundImage, "setColorFilter", systemWidgetColor);

        updateViews.setViewVisibility(R.id.imageViewClockSpace, View.INVISIBLE);

        switch (iClockSkinItem) {
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
            int iWeatherColorItem = Preferences.getWeatherColorItem(this, appWidgetId);

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

                Preferences.setWeatherColorItem(this, appWidgetId, -1);
                Preferences.setWeatherColor(this, appWidgetId, systemWeatherColor);
            } else {
                systemWeatherColor = Preferences.getWeatherColor(this, appWidgetId);
            }

            updateViews.setInt(R.id.viewButtonRefresh, "setColorFilter", systemWeatherColor);
        }

        if (showWeather) {
            readCachedWeatherData(updateViews, appWidgetId);
        }
    }

    public void updateWeatherStatus(final RemoteViews updateViews, final int appWidgetId, final boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService updateWeatherStatus appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        if (!showWeather)
            return;

        updateViews.setViewVisibility(R.id.viewButtonRefresh, View.GONE);
        updateViews.setViewVisibility(R.id.progressRefresh, View.VISIBLE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);

        try {
            if (!scheduledUpdate)
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.updatingweather), Toast.LENGTH_LONG).show();

            boolean bWiFiOnly = Preferences.getRefreshWiFiOnly(this, appWidgetId);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = null;

            if (connectivityManager != null) {
                mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            }

            if (mWifi != null && scheduledUpdate && bWiFiOnly && !mWifi.isConnected()) {
                Preferences.setWeatherSuccess(this, appWidgetId, false);
                readCachedWeatherData(updateViews, appWidgetId);

                return;
            }

            NetworkInfo activeNetworkInfo = null;

            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }

            if (activeNetworkInfo == null) {
                if (scheduledUpdate)
                    Preferences.setWeatherSuccess(this, appWidgetId, false);

                readCachedWeatherData(updateViews, appWidgetId);
                return;
            }

            int locationType = Preferences.getLocationType(this, appWidgetId);

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                // obtain location first
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = null;

                if (locationManager != null) {
                    providers = locationManager.getProviders(new Criteria(), true);
                }

                if (!providers.isEmpty()) {

                    LocationResult locationResult = new LocationResult() {
                        @Override
                        public void gotLocation(Location location) {

                            if (location != null) {
                                Preferences.setLocationLat(WidgetUpdateService.this, appWidgetId, (float) location.getLatitude());
                                Preferences.setLocationLon(WidgetUpdateService.this, appWidgetId, (float) location.getLongitude());
                                Preferences.setLocationId(WidgetUpdateService.this, appWidgetId, -1);

                                startWeatherUpdate(updateViews, appWidgetId, scheduledUpdate);
                            } else {
                                Preferences.setWeatherSuccess(WidgetUpdateService.this, appWidgetId, false);
                                readCachedWeatherData(updateViews, appWidgetId);
                            }
                        }
                    };

                    LocationProvider loc = new LocationProvider();
                    loc.getLocation(this, locationResult);
                } else {
                    Preferences.setWeatherSuccess(WidgetUpdateService.this, appWidgetId, false);
                    readCachedWeatherData(updateViews, appWidgetId);
                }
            } else {
                startWeatherUpdate(updateViews, appWidgetId, scheduledUpdate);
            }

        } catch (Exception e) {
            e.printStackTrace();

            Preferences.setWeatherSuccess(this, appWidgetId, false);
            readCachedWeatherData(updateViews, appWidgetId);
        }
    }

    public void startWeatherUpdate(RemoteViews updateViews, int appWidgetId, boolean scheduledUpdate) {

        SQLiteDbAdapter dbHelper = new SQLiteDbAdapter(this);

        // create location in database from old preferences
        if (Preferences.getLocationType(this, appWidgetId) == ConfigureLocationActivity.LOCATION_TYPE_CUSTOM) {
            dbHelper.open();
            Cursor locationsCursor = dbHelper.fetchAllLocations();

            float latPref = Preferences.getLocationLat(this, appWidgetId);
            float lonPref = Preferences.getLocationLon(this, appWidgetId);
            String locationPref = Preferences.getLocation(this, appWidgetId);
            long locationIDPref = Preferences.getLocationId(this, appWidgetId);
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

        int locationType = Preferences.getLocationType(this, appWidgetId);

        if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
            HttpTaskInfo info = new HttpTaskInfo();
            info.updateViews = updateViews;
            info.appWidgetId = appWidgetId;
            info.scheduledUpdate = scheduledUpdate;
            info.locationId = -1;
            info.context = this;

            info.last = locations.size() <= 0;

            new HttpTask().execute(info);
        }

        for (int i = 0; i < locations.size(); i++) {
            HttpTaskInfo info = new HttpTaskInfo();
            info.updateViews = updateViews;
            info.appWidgetId = appWidgetId;
            info.scheduledUpdate = scheduledUpdate;
            info.locationId = locations.get(i);
            info.context = this;

            info.last = i == locations.size() - 1;

            new HttpTask().execute(info);
        }
    }

    private class HttpTask extends AsyncTask<HttpTaskInfo, Void, Boolean> {

        RemoteViews updateViews = null;
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        boolean scheduledUpdate = false;
        long locId = -1;
        Context context = null;
        boolean last = false;

        public Boolean doInBackground(HttpTaskInfo... info) {
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

                Intent intent = new Intent(UPDATE_FORECAST);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                sendBroadcast(intent);
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
    boolean getCurrentWeatherData(RemoteViews updateViews, int appWidgetId, long locId, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService getCurrentWeatherData appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        if (!showWeather)
            return false;

        float lat = Preferences.getLocationLat(this, appWidgetId);
        float lon = Preferences.getLocationLon(this, appWidgetId);

        String lang = Preferences.getLanguageOptions(this);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals(""))
                lang = langDef;
            else
                lang = "en";
        }

        if ((locId == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
            if (scheduledUpdate)
                Preferences.setWeatherSuccess(WidgetUpdateService.this, appWidgetId, false);

            readCachedWeatherData(updateViews, appWidgetId);

            return false;
        }

        boolean ret = true;
        try {
            Reader responseReader;
            HttpClient client = new DefaultHttpClient();
            HttpGet request;

            if (locId >= 0) {
                request = new HttpGet(String.format(WEATHER_SERVICE_ID_URL, locId/*Preferences.getLocationId(WidgetUpdateService.this, appWidgetId)*/, lang));
            } else if (lat != -222 && lon != -222 && !Float.isNaN(lat) && !Float.isNaN(lon)) {
                request = new HttpGet(String.format(WEATHER_SERVICE_COORD_URL, lat, lon, lang));
            } else {
                if (scheduledUpdate)
                    Preferences.setWeatherSuccess(WidgetUpdateService.this, appWidgetId, false);

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

                        long locIdTemp = Preferences.getLocationId(this, appWidgetId);

                        if (locId >= 0 && locId == locIdTemp || locId == -1 && !Float.isNaN(lat) && !Float.isNaN(lon))
                            parseWeatherData(updateViews, appWidgetId, result.toString(), false, scheduledUpdate);

                        // save cache
                        File parentDirectory = new File(WidgetUpdateService.this.getFilesDir().getAbsolutePath());

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
                Preferences.setWeatherSuccess(WidgetUpdateService.this, appWidgetId, false);

            readCachedWeatherData(updateViews, appWidgetId);

            ret = false;
        }

        return ret;
    }

    @SuppressWarnings("unused")
    boolean getForecastData(RemoteViews updateViews, int appWidgetId, long locId, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService getForecastData appWidgetId: " + appWidgetId + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        if (!showWeather)
            return false;

        float lat = Preferences.getLocationLat(this, appWidgetId);
        float lon = Preferences.getLocationLon(this, appWidgetId);

        String lang = Preferences.getLanguageOptions(this);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals(""))
                lang = langDef;
            else
                lang = "en";
        }

        if ((locId == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
            if (scheduledUpdate)
                Preferences.setForecastSuccess(WidgetUpdateService.this, appWidgetId, false);

            return false;
        }

        boolean ret = true;
        try {
            Reader responseReader;
            HttpClient client = new DefaultHttpClient();
            HttpGet request;

            if (locId >= 0) {
                request = new HttpGet(String.format(WEATHER_FORECAST_ID_URL, locId/*Preferences.getLocationId(WidgetUpdateService.this, appWidgetId)*/, lang));
            } else if (lat != -222 && lon != -222 && !Float.isNaN(lat) && !Float.isNaN(lon)) {
                request = new HttpGet(String.format(WEATHER_FORECAST_COORD_URL, Preferences.getLocationLat(WidgetUpdateService.this, appWidgetId),
                        Preferences.getLocationLon(WidgetUpdateService.this, appWidgetId), lang));
            } else {
                if (scheduledUpdate)
                    Preferences.setForecastSuccess(WidgetUpdateService.this, appWidgetId, false);

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
                        File parentDirectory = new File(WidgetUpdateService.this.getFilesDir().getAbsolutePath());

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
                Preferences.setForecastSuccess(WidgetUpdateService.this, appWidgetId, false);

            ret = false;
        }

        return ret;
    }

    @SuppressWarnings("unused")
    void readCachedWeatherData(RemoteViews updateViews, int appWidgetId) {
        Log.d(LOG_TAG, "WidgetUpdateService readCachedWeatherData appWidgetId: " + appWidgetId);

        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        if (!showWeather)
            return;

        updateViews.setViewVisibility(R.id.viewButtonRefresh, View.VISIBLE);
        updateViews.setViewVisibility(R.id.progressRefresh, View.GONE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);

        Intent intent = new Intent(UPDATE_FORECAST);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        sendBroadcast(intent);

        try {
            File parentDirectory = new File(this.getFilesDir().getAbsolutePath());

            if (!parentDirectory.exists()) {
                Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                if (!parentDirectory.mkdirs()) {
                    Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                }
            }

            File cacheFile;

            long locId = Preferences.getLocationId(this, appWidgetId);

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
    void parseWeatherData(RemoteViews updateViews, int appWidgetId, String parseString, boolean updateFromCache, boolean scheduledUpdate) {
        Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData appWidgetId: " + appWidgetId + " cache: " + updateFromCache + " scheduled: " + scheduledUpdate);

        boolean showWeather = Preferences.getShowWeather(this, appWidgetId);

        if (!showWeather)
            return;

        if (updateViews == null) {
            updateViews = new RemoteViews(getPackageName(),
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

        int iFontItem = Preferences.getWeatherFontItem(this, appWidgetId);
        boolean bold = Preferences.getWeatherBoldText(this, appWidgetId);

        int systemWeatherColor;
        int iWeatherColorItem = Preferences.getWeatherColorItem(this, appWidgetId);

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

            Preferences.setWeatherColorItem(this, appWidgetId, -1);
            Preferences.setWeatherColor(this, appWidgetId, systemWeatherColor);
        } else {
            systemWeatherColor = Preferences.getWeatherColor(this, appWidgetId);
        }

        String[] mFontArray = getResources().getStringArray(R.array.fontPathValues);

        String font = "fonts/Roboto.ttf";

        if (mFontArray.length > iFontItem)
            font = mFontArray[iFontItem];

        try {
            JSONTokener parser = new JSONTokener(parseString);
            int tempScale = Preferences.getTempScale(this, appWidgetId);

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

            int locationType = Preferences.getLocationType(this, appWidgetId);

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                Preferences.setLocation(WidgetUpdateService.this, appWidgetId, location);
            }

            updateViews.setImageViewBitmap(R.id.imageViewLoc, getFontBitmap(this, location, systemWeatherColor, font, bold, 12));

            long timestamp = weatherJSON.getLong("dt");
            Date time = new Date(timestamp * 1000);

            JSONObject main = null;
            try {
                main = weatherJSON.getJSONObject("main");
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }
            try {
                double currentTemp = main.getDouble("temp") - 273.15;

                if (tempScale == 1)
                    temp = String.valueOf((int) (currentTemp * 1.8 + 32)) + "°";
                else
                    temp = String.valueOf((int) currentTemp) + "°";

                updateViews.setImageViewBitmap(R.id.imageViewTemp, getFontBitmap(this, temp, systemWeatherColor, font, bold, 32));

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
                double speed = windJSON.getDouble("speed");
            } catch (JSONException e) {
                Log.d(LOG_TAG, "WidgetUpdateService parseWeatherData JSONException");
            }
            try {
                double deg = windJSON.getDouble("deg");
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

                    int icons = Preferences.getWeatherIcons(this, appWidgetId);
                    int resource;
                    WeatherIcon[] imageArr;

                    updateViews.setImageViewBitmap(R.id.imageViewDesc, getFontBitmap(this, weatherDesc, systemWeatherColor, font, bold, 12));

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
                        default:
                            resource = R.drawable.tick_weather_04d;
                            imageArr = conditions.m_ImageArrTick;
                            break;
                    }

                    updateViews.setImageViewResource(R.id.imageViewWeather, resource);

                    float lat = Preferences.getLocationLat(this, appWidgetId);
                    float lon = Preferences.getLocationLon(this, appWidgetId);
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
                Preferences.setLastRefresh(this, appWidgetId, System.currentTimeMillis());

                if (scheduledUpdate)
                    Preferences.setWeatherSuccess(this, appWidgetId, true);
            }

            long lastRefresh = Preferences.getLastRefresh(this, appWidgetId);

            if (lastRefresh > 0) {
                boolean bShow24Hrs = Preferences.getShow24Hrs(this, appWidgetId);
                int iDateFormatItem = Preferences.getDateFormatItem(this, appWidgetId);
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

                updateViews.setImageViewBitmap(R.id.imageViewLast,
                        getFontBitmap(this, currentDate + ", " + currentTime, systemWeatherColor, font, bold, 12));
            } else {
                updateViews.setImageViewBitmap(R.id.imageViewLast,
                        getFontBitmap(this, getResources().getString(R.string.lastrefreshnever), systemWeatherColor, font, bold, 12));
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
}
