package com.zoromatic.widgets;

import java.io.File;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class DigitalClockAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "DigitalClockWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        /*Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
        startIntent.putExtra(WidgetInfoReceiver.UPDATE_WEATHER, true);
        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        context.startService(startIntent);*/

        updateWidgets(context, appWidgetIds, true, false);

        for (int appWidgetId : appWidgetIds) {
            setAlarm(context, appWidgetId);
        }
    }

    public void updateWidgets(Context context, int[] appWidgetIds, boolean startService, boolean updateWeather) {
        if (startService)
            context.startService(new Intent(context, WidgetUpdateService.class));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        WidgetManager widgetManager = new WidgetManager(context);
        RemoteViews remoteViews;

        for (int appWidgetId : appWidgetIds) {
            remoteViews = widgetManager.buildClockUpdate(appWidgetId);
            widgetManager.updateClockStatus(remoteViews, appWidgetId);

            if (updateWeather)
                widgetManager.updateWeatherStatus(remoteViews, appWidgetId, false);
            else
                widgetManager.readCachedWeatherData(remoteViews, appWidgetId);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

            //setAlarm(context, appWidgetId);
        }
    }

    public void updateWidget(Context context, int appWidgetId, boolean startService, boolean updateWeather) {
        if (startService)
            context.startService(new Intent(context, WidgetUpdateService.class));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        WidgetManager widgetManager = new WidgetManager(context);
        RemoteViews remoteViews;

        remoteViews = widgetManager.buildClockUpdate(appWidgetId);
        widgetManager.updateClockStatus(remoteViews, appWidgetId);

        if (updateWeather)
            widgetManager.updateWeatherStatus(remoteViews, appWidgetId, false);
        else
            widgetManager.readCachedWeatherData(remoteViews, appWidgetId);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        //setAlarm(context, appWidgetId);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onDisabled");
        super.onDisabled(context);
        context.stopService(new Intent(context, WidgetUpdateService.class));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, DigitalClockAppWidgetProvider.class));

        removeDependencies(context, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onDeleted");

        removeDependencies(context, appWidgetIds);

        super.onDeleted(context, appWidgetIds);
    }

    private void removeDependencies(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            File parentDirectory = new File(context.getFilesDir().getAbsolutePath());

            if (!parentDirectory.exists()) {
                Log.e(LOG_TAG, "Cache file parent directory does not exist.");

                if (!parentDirectory.mkdirs()) {
                    Log.e(LOG_TAG, "Cannot create cache file parent directory.");
                }
            }

            File cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);
            cacheFile.delete();

            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            PendingIntent pending = createClockTickIntent(context, appWidgetId);
            if (alarmManager != null) {
                alarmManager.cancel(pending);
            }
            pending.cancel();
        }
    }

    public static void setAlarm(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider setAlarm");

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

        if (lastRefresh == 0) {
            lastRefresh = System.currentTimeMillis();
        }

        int refreshIntervalCode = Preferences.getRefreshInterval(context, appWidgetId);
        int refreshInterval;

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
                refreshInterval = 3 * 3600 * 1000; // 3 hours
                break;
        }

        //refreshInterval = 5*60*1000; // test on 5 minutes
        calendar.setTimeInMillis(lastRefresh);
        long startAlarm = calendar.getTimeInMillis() + refreshInterval;

        if (alarmManager != null) {
            PendingIntent clockTickIntent = createClockTickIntent(context, appWidgetId);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startAlarm,
                    refreshInterval, clockTickIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, refreshInterval,
                        clockTickIntent);
            }
        }
    }

    private static PendingIntent createClockTickIntent(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider createClockTickIntent");
        Intent intent = new Intent(WidgetUpdateService.WEATHER_UPDATE);
        //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //intent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, true);

        return PendingIntent.getBroadcast(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}