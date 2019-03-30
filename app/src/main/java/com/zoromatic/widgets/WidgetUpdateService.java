package com.zoromatic.widgets;

import java.util.Locale;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class WidgetUpdateService extends Service {
    private static String LOG_TAG = "WidgetUpdateService";

    protected static long GPS_UPDATE_TIME_INTERVAL = 3000; // milliseconds
    protected static float GPS_UPDATE_DISTANCE_INTERVAL = 0; // meters
    private WidgetGPSListener mGpsListener = null;
    private WidgetLocationListener mLocListener = null;
    private BrightnessObserver mSettingsObserver;
    private RotationObserver mRotationObserver;

    WidgetManager mWidgetManager = null;

    private static IntentFilter mIntentFilter;
    private WidgetInfoReceiver mWidgetInfo = null;

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
        mIntentFilter.addAction(WidgetIntentDefinitions.CLOCK_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.WEATHER_UPDATE);

        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED);
        mIntentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        mIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.FLASHLIGHT_CHANGED);
        mIntentFilter.addAction(WidgetIntentDefinitions.BRIGHTNESS_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_CAMERA_BUTTON);
        mIntentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);

        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_BATTERY_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_BLUETOOTH_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_WIFI_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_MOBILE_DATA_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_GPS_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_RINGER_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_CLOCK_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_AIRPLANE_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_BRIGHTNESS_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_TORCH_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_NFC_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET);
        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_SINGLE_ORIENTATION_WIDGET);

        mIntentFilter.addAction(WidgetIntentDefinitions.UPDATE_WIDGETS);

        mIntentFilter.addAction(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.GPS_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.NFC_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE);

        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_BLUETOOTH_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_WIFI_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_MOBILE_DATA_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_GPS_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_RINGER_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_AIRPLANE_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_NFC_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_SYNC_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_ORIENTATION_WIDGET_UPDATE);
        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_TORCH_WIDGET_UPDATE);

        mIntentFilter.addAction(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL);

        mIntentFilter.addAction(WidgetIntentDefinitions.APPWIDGET_RESIZE);
        mIntentFilter.addAction(WidgetIntentDefinitions.APPWIDGET_UPDATE_OPTIONS);
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
            Intent intent = new Intent(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED);
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
            brightnessIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.BRIGHTNESS_CHANGED);
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
                    sendBroadcast(new Intent(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED));
                } else {
                    sendBroadcast(new Intent(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED));
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
                    sendBroadcast(new Intent(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED));
                } else {
                    sendBroadcast(new Intent(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED));
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

        /*if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            showNotification(Preferences.getForegroundService(this));
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWidgetManager != null) {
            mWidgetManager.destroyCamera();
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

        if (mWidgetManager == null)
            mWidgetManager = new WidgetManager(this);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            showNotification(Preferences.getForegroundService(this));
        }

        if (mWidgetInfo == null) {
            mWidgetInfo = new WidgetInfoReceiver();
            registerReceiver(mWidgetInfo, mIntentFilter);

            sendBroadcast(new Intent(WidgetIntentDefinitions.FLASHLIGHT_CHANGED));
            sendBroadcast(new Intent(WidgetIntentDefinitions.BRIGHTNESS_CHANGED));
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
        }

        Bundle extras = intent.getExtras();

        if (extras == null) {
            Log.d(LOG_TAG, "WidgetUpdateService onStartCommand extras = null");

            intent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, Intent.ACTION_TIME_TICK);

            ComponentName thisWidget = new ComponentName(this,
                    DigitalClockAppWidgetProvider.class);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            if (appWidgetIds.length > 0)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        }

        String intentExtra = null;
        boolean updateWeather = false;

        if (extras != null) {
            intentExtra = extras.getString(WidgetIntentDefinitions.INTENT_EXTRA);
            updateWeather = extras.getBoolean(WidgetIntentDefinitions.UPDATE_WEATHER, false) ||
                    (intentExtra != null && intentExtra.equals(WidgetIntentDefinitions.WEATHER_UPDATE));
        }

        boolean scheduledUpdate = intent.getBooleanExtra(WidgetIntentDefinitions.SCHEDULED_UPDATE, false);

        if (intentExtra != null && intentExtra.equals(WidgetIntentDefinitions.BATTERY_NOTIFICATION)) {
            mWidgetManager.updateNotificationBatteryStatus(this, intent);
        }

        if (intentExtra != null && intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)) {
            //updateNotificationBatteryStatus(intent);

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

            mWidgetManager.updateNotificationBatteryStatus(this, intent);
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

        if (intentExtra != null && (intentExtra.equals(WidgetIntentDefinitions.UPDATE_WIDGETS) || intentExtra.equals(Intent.ACTION_CONFIGURATION_CHANGED)
                || intentExtra.equals(Intent.ACTION_BOOT_COMPLETED) || intentExtra.equals(Intent.ACTION_LOCALE_CHANGED)
                || intentExtra.equals(WidgetIntentDefinitions.APPWIDGET_RESIZE) || intentExtra.equals(WidgetIntentDefinitions.APPWIDGET_UPDATE_OPTIONS))) {

            mWidgetManager.updateNotificationBatteryStatus(this, intent);

            ComponentName thisWidget;

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
            thisWidget = new ComponentName(this, DigitalClockAppWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            mWidgetManager.updateClockWidgets(this, appWidgetIds, updateWeather, scheduledUpdate);

            // update toggle widgets
            try {
                //update power widget
                mWidgetManager.updatePowerWidgets(this, intentExtra);

                // update single widgets
                mWidgetManager.updateSingleWidgets(this);

            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        } else {
            if (intentExtra != null && (intentExtra.equals(Intent.ACTION_TIME_CHANGED)
                    || intentExtra.equals(Intent.ACTION_TIME_TICK)
                    || intentExtra.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || intentExtra.equals(WidgetIntentDefinitions.CLOCK_WIDGET_UPDATE)
                    || intentExtra.equals(WidgetIntentDefinitions.WEATHER_UPDATE))) {

                int appWidgetIds[] = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                int appWidgetIdSingle = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

                //RemoteViews updateViews;

                if (appWidgetIds != null) {
                    mWidgetManager.updateClockWidgets(this, appWidgetIds, updateWeather, scheduledUpdate);
                } else {
                    if (appWidgetIdSingle != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        int[] appWidgetIdsSingle = {appWidgetIdSingle};
                        mWidgetManager.updateClockWidgets(this, appWidgetIdsSingle, updateWeather, scheduledUpdate);
                    }
                }
            } else {

                if (intentExtra != null) {

                    // single toggle and battery widgets
                    try {
                        // Push update for this widget to the home screen
                        mWidgetManager.updateSingleWidget(this, intentExtra);

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "", e);
                    }

                    // power widgets
                    if (
                        intentExtra.equals(Intent.ACTION_BATTERY_CHANGED) ||
                        intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED) ||
                        intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ||
                        intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
                        intentExtra.equals(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED) ||
                        intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) ||
                        intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.BRIGHTNESS_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED) ||
                        intentExtra.equals(WidgetIntentDefinitions.FLASHLIGHT_CHANGED) ||

                        intentExtra.equals(WidgetIntentDefinitions.POWER_BLUETOOTH_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_WIFI_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_MOBILE_DATA_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_GPS_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_RINGER_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_AIRPLANE_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_BRIGHTNESS_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_NFC_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_SYNC_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_ORIENTATION_WIDGET_UPDATE) ||
                        intentExtra.equals(WidgetIntentDefinitions.POWER_TORCH_WIDGET_UPDATE) ||

                        intentExtra.equals(WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL)) {

                            mWidgetManager.updatePowerWidgets(this, intentExtra);
                    }
                }
            }
        }

        return START_STICKY;
    }

    private void showNotification(boolean show) {
        if (show) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIdsDigitalClock = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));
            int[] appWidgetIdsPower = appWidgetManager.getAppWidgetIds(new ComponentName(this, PowerAppWidgetProvider.class));

            if (appWidgetIdsDigitalClock.length > 0 || appWidgetIdsPower.length > 0) {
                Intent notificationIntent = new Intent(this, DigitalClockAppWidgetPreferenceActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.icon);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
                notificationBuilder.setTicker(getResources().getString(R.string.app_name));
                notificationBuilder.setContentText(getResources().getString(R.string.app_name));
                notificationBuilder.setSmallIcon(R.drawable.icon);
                notificationBuilder.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false));
                notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setOngoing(true);
                notificationBuilder.setDefaults(Notification.FLAG_NO_CLEAR);
                notificationBuilder.setWhen(0);
                notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                        && notificationManager != null) {
                    String CHANNEL_ID = getString(R.string.notification_channel_id);
                    CharSequence channelName = getString(R.string.notification_channel);
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);

                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationBuilder.setChannelId(CHANNEL_ID);
                }

                Notification notification = notificationBuilder.build();

                startForeground(101, notification);
            } else {
                stopForeground(true);
            }
        } else {
            stopForeground(true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
