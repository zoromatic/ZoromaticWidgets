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
import android.util.Log;


public class WidgetInfoReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "WidgetInfoReceiver";
    public static final String INTENT_EXTRA = "INTENT_EXTRA";
    public static final String UPDATE_WEATHER = "UPDATE_WEATHER";
    public static final String SCHEDULED_UPDATE = "SCHEDULED_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            String action = intent.getAction();

            Log.d(LOG_TAG, "WidgetInfoReceiver onReceive " + action);

            int[] appWidgetIds = null;
            ComponentName thisWidget = null;

            Intent startIntent = new Intent(context, WidgetUpdateService.class);
            startIntent.putExtra(INTENT_EXTRA, action);

            if (action.equals(WidgetUpdateService.WEATHER_UPDATE)) {
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
                        || action.equals(WidgetUpdateService.CLOCK_WIDGET_UPDATE)
                        || action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {

                    thisWidget = new ComponentName(context,
                            DigitalClockAppWidgetProvider.class);

                    if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                            || action.equals(WidgetUpdateService.CLOCK_WIDGET_UPDATE)
                            || action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {

                        startIntent.putExtra(WidgetInfoReceiver.UPDATE_WEATHER, true);
                    }
                }

                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    thisWidget = new ComponentName(context,
                            BatteryAppWidgetProvider.class);
                }

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                        || action.equals(WidgetUpdateService.BLUETOOTH_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            BluetoothAppWidgetProvider.class);
                }

                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                        || action.equals(WidgetUpdateService.WIFI_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            WifiAppWidgetProvider.class);
                }

                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                        || action.equals(WidgetUpdateService.MOBILE_DATA_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            MobileAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.LOCATION_PROVIDERS_CHANGED)
                        || action.equals(WidgetUpdateService.LOCATION_GPS_ENABLED_CHANGED)
                        || action.equals(WidgetUpdateService.GPS_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            GpsAppWidgetProvider.class);
                }

                if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                        || action.equals(WidgetUpdateService.RINGER_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            RingerAppWidgetProvider.class);
                }

                if (action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                        || action.equals(WidgetUpdateService.AIRPLANE_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            AirplaneAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.BRIGHTNESS_CHANGED)
                        || action.equals(WidgetUpdateService.BRIGHTNESS_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            BrightnessAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.NFC_ADAPTER_STATE_CHANGED)
                        || action.equals(WidgetUpdateService.NFC_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            NfcAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.SYNC_CONN_STATUS_CHANGED)
                        || action.equals(WidgetUpdateService.SYNC_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            SyncAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.AUTO_ROTATE_CHANGED)
                        || action.equals(WidgetUpdateService.ORIENTATION_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            OrientationAppWidgetProvider.class);
                }

                if (action.equals(WidgetUpdateService.FLASHLIGHT_CHANGED)
                        || action.equals(WidgetUpdateService.TORCH_WIDGET_UPDATE)) {
                    thisWidget = new ComponentName(context,
                            TorchAppWidgetProvider.class);
                }

                if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {

                }

                if (thisWidget != null) {
                    appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                    //if (appWidgetIds.length <= 0)
                    //	return;

                    if (appWidgetIds.length > 0) {
                        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                    }
                }
            }

            if (action.equals(WidgetUpdateService.WEATHER_UPDATE) || thisWidget != null) {
                if (action.equals(WidgetUpdateService.WEATHER_UPDATE)) {
                    Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(startIntent) " + WidgetUpdateService.WEATHER_UPDATE);
                } else {
                    Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(startIntent) " + thisWidget.getClassName());
                }
                //context.startService(startIntent);
            }

            context.startService(startIntent);

            // refresh weather data if scheduled refresh failed
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if (activeNetworkInfo != null) {
                    thisWidget = new ComponentName(context,
                            DigitalClockAppWidgetProvider.class);

                    appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                    if (appWidgetIds.length <= 0) {
                        return;
                    }

                    for (int appWidgetId : appWidgetIds) {
                        boolean weatherSuccess = Preferences.getWeatherSuccess(context, appWidgetId);

                        if (!weatherSuccess) {
                            Intent refreshIntent = new Intent(context, WidgetUpdateService.class);
                            refreshIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.WEATHER_UPDATE);
                            refreshIntent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, true);
                            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                            Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(refreshIntent) " + thisWidget.getClassName());
                            context.startService(refreshIntent);
                        }
                    }
                }
            }

            /*if (
                action.equals(Intent.ACTION_BATTERY_CHANGED) ||
        		action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) ||
				action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) ||
				action.equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
				action.equals(WidgetUpdateService.LOCATION_PROVIDERS_CHANGED) ||
				action.equals(WidgetUpdateService.LOCATION_GPS_ENABLED_CHANGED) ||
				action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) ||
				action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED) ||
				action.equals(WidgetUpdateService.BRIGHTNESS_CHANGED) ||
				action.equals(WidgetUpdateService.NFC_ADAPTER_STATE_CHANGED) ||
				action.equals(WidgetUpdateService.SYNC_CONN_STATUS_CHANGED) ||
				action.equals(WidgetUpdateService.AUTO_ROTATE_CHANGED) ||
				action.equals(WidgetUpdateService.FLASHLIGHT_CHANGED) ||
				
				action.equals(WidgetUpdateService.POWER_BLUETOOTH_WIDGET_UPDATE) || 					
				action.equals(WidgetUpdateService.POWER_WIFI_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_MOBILE_DATA_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_GPS_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_RINGER_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_AIRPLANE_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_BRIGHTNESS_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_NFC_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_SYNC_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_ORIENTATION_WIDGET_UPDATE) ||
				action.equals(WidgetUpdateService.POWER_TORCH_WIDGET_UPDATE)) {
            	
				thisWidget = new ComponentName(context,
						PowerAppWidgetProvider.class);
				
				appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            	
            	if (appWidgetIds.length > 0) {
            		startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(INTENT_EXTRA, action);
                    
            		startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            	
            		Log.d(LOG_TAG, "WidgetInfoReceiver onReceive startService(startIntent) " + thisWidget.getClassName());
            		context.startService(startIntent);
            	}
			}*/

        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }

    }

}
