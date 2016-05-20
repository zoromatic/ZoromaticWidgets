package com.zoromatic.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OrientationAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "OrientationWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "OrientationAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.UPDATE_SINGLE_ORIENTATION_WIDGET);

        context.startService(startIntent);
    }
};
