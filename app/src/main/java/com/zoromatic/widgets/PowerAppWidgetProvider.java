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

        context.startService(new Intent(context, WidgetUpdateService.class));
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.powerwidget);
        appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
    }

    public void updateWidgets(Context context, int[] appWidgetIds, String intentAction) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        WidgetManager widgetManager = new WidgetManager(context);

        widgetManager.updatePowerWidgets(context, intentAction);

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.powerwidget);
        appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
    }

    public void updateWidget(Context context, int appWidgetId, String intentAction) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        WidgetManager widgetManager = new WidgetManager(context);

        widgetManager.updatePowerWidgets(context, intentAction);

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.powerwidget);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
