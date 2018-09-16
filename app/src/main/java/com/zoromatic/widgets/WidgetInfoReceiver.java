package com.zoromatic.widgets;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;

public class WidgetInfoReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "WidgetInfoReceiver";
    public static final String INTENT_EXTRA = "INTENT_EXTRA";
    public static final String UPDATE_WEATHER = "UPDATE_WEATHER";
    public static final String SCHEDULED_UPDATE = "SCHEDULED_UPDATE";

    private int mBatteryLevel = -1;
    private int mBatteryScale = -1;
    private int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            /*String action = intent.getAction();

            if (action == null)
                return;
            if ((action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIME_TICK)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED) || action.equals(Intent.ACTION_LOCALE_CHANGED)
                    || action.equals(Intent.ACTION_CONFIGURATION_CHANGED) || action.equals(Intent.ACTION_BOOT_COMPLETED)
                    || action.equals(WidgetIntentDefinitions.CLOCK_WIDGET_UPDATE) || action.equals(WidgetIntentDefinitions.WEATHER_UPDATE))) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, DigitalClockAppWidgetProvider.class));

                if (appWidgetIds.length > 0) {
                    if (action.equals(WidgetIntentDefinitions.WEATHER_UPDATE))
                        new DigitalClockAppWidgetProvider().updateWidgets(context, appWidgetIds, true, true);
                    else
                        new DigitalClockAppWidgetProvider().updateWidgets(context, appWidgetIds, true, false);
                }
            } else {

                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                            BatteryManager.BATTERY_STATUS_UNKNOWN);

                    int level = -1;

                    if (rawLevel >= 0 && scale > 0) {
                        level = (rawLevel * 100) / scale;
                    }

                    if (level == -1) {
                        Intent batteryIntent = context.registerReceiver(
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
                        return;
                    }

                    mBatteryLevel = level;
                    mBatteryScale = scale;
                    mBatteryStatus = status;

                    //updateNotificationBatteryStatus(intent);
                }

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PowerAppWidgetProvider.class));

                if (appWidgetIds.length > 0) {
                    new PowerAppWidgetProvider().updateWidgets(context, appWidgetIds, action);
                }
            }*/

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            String action = intent.getAction();

            Log.d(LOG_TAG, "WidgetInfoReceiver onReceive " + action);

            int[] appWidgetIds;
            ComponentName thisWidget = null;

            Intent startIntent = new Intent(context, WidgetUpdateService.class);
            startIntent.putExtra(INTENT_EXTRA, action);

            if (action != null) {
                if (action.equals(WidgetIntentDefinitions.WEATHER_UPDATE)) {
                    int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);

                    boolean scheduledUpdate = intent.getBooleanExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, false);

                    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                        return;
                    }

                    startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    startIntent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, scheduledUpdate);
                } else {
                    if (action.equals(Intent.ACTION_TIME_CHANGED)
                            || action.equals(Intent.ACTION_TIME_TICK)
                            || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                            || action.equals(WidgetIntentDefinitions.CLOCK_WIDGET_UPDATE)
                            || action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {

                        thisWidget = new ComponentName(context,
                                DigitalClockAppWidgetProvider.class);

                        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                                || action.equals(WidgetIntentDefinitions.CLOCK_WIDGET_UPDATE)
                                || action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {

                            startIntent.putExtra(WidgetInfoReceiver.UPDATE_WEATHER, true);
                        }
                    }

                    if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                        thisWidget = new ComponentName(context,
                                BatteryAppWidgetProvider.class);
                    }

                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                            || action.equals(WidgetIntentDefinitions.BLUETOOTH_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                BluetoothAppWidgetProvider.class);
                    }

                    if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                            || action.equals(WidgetIntentDefinitions.WIFI_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                WifiAppWidgetProvider.class);
                    }

                    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                            || action.equals(WidgetIntentDefinitions.MOBILE_DATA_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                MobileAppWidgetProvider.class);

                        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                            // HACK - mobile data state change is delayed, delay getting info
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (action.equals(WidgetIntentDefinitions.LOCATION_PROVIDERS_CHANGED)
                            || action.equals(WidgetIntentDefinitions.LOCATION_GPS_ENABLED_CHANGED)
                            || action.equals(WidgetIntentDefinitions.GPS_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                GpsAppWidgetProvider.class);
                    }

                    if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                            || action.equals(WidgetIntentDefinitions.RINGER_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                RingerAppWidgetProvider.class);
                    }

                    if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                            || action.equals(WidgetIntentDefinitions.AIRPLANE_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                AirplaneAppWidgetProvider.class);
                    }

                    if (action.equals(WidgetIntentDefinitions.BRIGHTNESS_CHANGED)
                            || action.equals(WidgetIntentDefinitions.BRIGHTNESS_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                BrightnessAppWidgetProvider.class);
                    }

                    if (action.equals(WidgetIntentDefinitions.NFC_ADAPTER_STATE_CHANGED)
                            || action.equals(WidgetIntentDefinitions.NFC_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                NfcAppWidgetProvider.class);
                    }

                    if (action.equals(WidgetIntentDefinitions.SYNC_CONN_STATUS_CHANGED)
                            || action.equals(WidgetIntentDefinitions.SYNC_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                SyncAppWidgetProvider.class);
                    }

                    if (action.equals(WidgetIntentDefinitions.AUTO_ROTATE_CHANGED)
                            || action.equals(WidgetIntentDefinitions.ORIENTATION_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                OrientationAppWidgetProvider.class);
                    }

                    if (action.equals(WidgetIntentDefinitions.FLASHLIGHT_CHANGED)
                            || action.equals(WidgetIntentDefinitions.TORCH_WIDGET_UPDATE)) {
                        thisWidget = new ComponentName(context,
                                TorchAppWidgetProvider.class);
                    }

                    if (thisWidget != null) {
                        appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                        if (appWidgetIds.length > 0) {
                            startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                        }
                    }
                }

                if (action.equals(WidgetIntentDefinitions.WEATHER_UPDATE) || thisWidget != null) {
                    if (action.equals(WidgetIntentDefinitions.WEATHER_UPDATE)) {
                        Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(startIntent) " + WidgetIntentDefinitions.WEATHER_UPDATE);
                    } else {
                        if (thisWidget != null) {
                            Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(startIntent) " + thisWidget.getClassName());
                        }
                    }
                }

                context.startService(startIntent);

                // refresh weather data if scheduled refresh failed
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = null;

                    if (connectivityManager != null) {
                        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    }

                    if (activeNetworkInfo != null) {
                        thisWidget = new ComponentName(context,
                                DigitalClockAppWidgetProvider.class);

                        appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                        if (appWidgetIds.length <= 0) {
                            return;
                        }

                        for (int appWidgetId : appWidgetIds) {
                            boolean weatherSuccess = Preferences.getWeatherSuccess(context, appWidgetId);

                            // check also if refresh interval passed
                            long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

                            if (lastRefresh == 0) {
                                lastRefresh = System.currentTimeMillis();
                            }

                            long currentTime = System.currentTimeMillis();
                            int refreshIntervalCode = Preferences.getRefreshInterval(context, appWidgetId);
                            int refreshInterval = 3 * 3600 * 1000; // default is 3 hours

                            switch (refreshIntervalCode) {
                                case 0:
                                    refreshInterval = (int) (0.5 * 3600 * 1000);
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 6:
                                    refreshInterval = refreshIntervalCode * 3600 * 1000;
                                    break;
                                default:
                                    refreshInterval = 3 * 3600 * 1000;
                                    break;
                            }

                            if (lastRefresh > 0 && currentTime > 0 && refreshInterval > 0) {
                                if (currentTime - lastRefresh > refreshInterval) {
                                    weatherSuccess = false;
                                }
                            }

                            if (!weatherSuccess) {
                                Intent refreshIntent = new Intent(context, WidgetUpdateService.class);
                                refreshIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetIntentDefinitions.WEATHER_UPDATE);
                                refreshIntent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, true);
                                refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                                Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(refreshIntent) " + thisWidget.getClassName());
                                context.startService(refreshIntent);

                                /*if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    DigitalClockAppWidgetProvider.scheduleJob(context, appWidgetId);
                                } else {
                                    DigitalClockAppWidgetProvider.setAlarm(context, appWidgetId);
                                }

                                Log.d(LOG_TAG, "WidgetInfoReceiver onReceive restart alarms / jobs " + thisWidget.getClassName());*/
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }

    }

}
