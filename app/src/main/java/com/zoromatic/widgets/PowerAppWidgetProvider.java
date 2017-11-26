package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
//import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
//import android.media.AudioManager;
//import android.net.ConnectivityManager;
//import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;

public class PowerAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "PowerWidget";

    @SuppressLint("InlinedApi")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "PowerAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;        
        String[] intentExtras = new String[] {WifiManager.WIFI_STATE_CHANGED_ACTION, AudioManager.RINGER_MODE_CHANGED_ACTION,
        		ConnectivityManager.CONNECTIVITY_ACTION, BluetoothAdapter.ACTION_STATE_CHANGED,
        		WidgetUpdateService.NFC_ADAPTER_STATE_CHANGED,
        		currentapiVersion < 9?WidgetUpdateService.LOCATION_GPS_ENABLED_CHANGED:WidgetUpdateService.LOCATION_PROVIDERS_CHANGED, 
        		Intent.ACTION_AIRPLANE_MODE_CHANGED, WidgetUpdateService.AUTO_ROTATE_CHANGED, 
        		WidgetUpdateService.SYNC_CONN_STATUS_CHANGED, WidgetUpdateService.FLASHLIGHT_CHANGED,
        		WidgetUpdateService.BRIGHTNESS_CHANGED};
        
    	
    	for (String intentExtra : intentExtras) {
    		Intent startIntent = new Intent(context, WidgetUpdateService.class);
    		startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
    		startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, intentExtra);

    		context.startService(startIntent);
    	}*/

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.POWER_WIDGET_UPDATE_ALL);

        context.startService(startIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        /*Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.POWER_WIDGET_UPDATE_ALL);

        context.startService(startIntent);*/

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        WidgetManager widgetManager = new WidgetManager(context);
        Intent updateIntent = new Intent(context, WidgetUpdateService.class);
        updateIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.POWER_WIDGET_UPDATE_ALL);

        for (int appWidgetId : appWidgetManager.getAppWidgetIds(new ComponentName(context, PowerAppWidgetProvider.class))) {
            /*RemoteViews remoteViews = widgetManager.buildPowerUpdate(updateIntent, appWidgetId);

            if (remoteViews != null) {
                widgetManager.updatePowerWidgetStatus(remoteViews, updateIntent, appWidgetId);
            }*/

            appWidgetManager.updateAppWidget(appWidgetId, widgetManager.buildPowerUpdate(updateIntent, appWidgetId));
        }
    }
};
