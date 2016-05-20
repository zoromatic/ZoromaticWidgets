package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GpsAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "GPSWidget";

    @SuppressLint("InlinedApi")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "GpsAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.UPDATE_SINGLE_GPS_WIDGET);

        context.startService(startIntent);
    }
};
