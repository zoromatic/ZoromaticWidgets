package com.zoromatic.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class SyncAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "SyncWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "SyncAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.UPDATE_SINGLE_SYNC_WIDGET);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            ContextCompat.startForegroundService(context, startIntent);
        else
            context.startService(startIntent);
    }
};
