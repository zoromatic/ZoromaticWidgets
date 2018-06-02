package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class PowerAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "PowerAppWidgetProvider";

    @SuppressLint("InlinedApi")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "PowerAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.POWER_WIDGET_UPDATE_ALL);

        context.startService(startIntent);

        updateWidgets(context, appWidgetIds, Intent.ACTION_CONFIGURATION_CHANGED, false);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.POWER_WIDGET_UPDATE_ALL);

        context.startService(startIntent);
    }

    public void updateWidgets(Context context, int[] appWidgetIds, String intentAction, boolean startService) {
        if (startService)
            context.startService(new Intent(context, WidgetUpdateService.class));

        WidgetManager widgetManager = new WidgetManager(context);
        widgetManager.updatePowerWidgets(context, intentAction);
    }
}
