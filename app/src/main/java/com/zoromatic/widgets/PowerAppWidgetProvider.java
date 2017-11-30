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

        WidgetManager widgetManager = new WidgetManager(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = widgetManager.buildPowerUpdate(WidgetManager.POWER_WIDGET_UPDATE_ALL, appWidgetId);

            if (remoteViews != null) {
                widgetManager.updatePowerWidgetStatus(remoteViews, WidgetManager.POWER_WIDGET_UPDATE_ALL, appWidgetId);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "PowerAppWidgetProvider onReceive");

        super.onReceive(context, intent);

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        WidgetManager widgetManager = new WidgetManager(context);
        widgetManager.toggleWidgets(intent.getAction());

        for (int appWidgetId : appWidgetManager.getAppWidgetIds(new ComponentName(context, PowerAppWidgetProvider.class))) {
            RemoteViews remoteViews = widgetManager.buildPowerUpdate(intent.getAction(), appWidgetId);

            if (remoteViews != null) {
                widgetManager.updatePowerWidgetStatus(remoteViews, intent.getAction(), appWidgetId);
            }
        }
    }
};
