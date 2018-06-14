package com.zoromatic.widgets;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RemoteViews;

public class DigitalClockAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "DigitalClockWidget";
    private static final int UPDATE_WIDGET_JOB_ID = 5689;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
        startIntent.putExtra(WidgetInfoReceiver.UPDATE_WEATHER, true);
        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        context.startService(startIntent);

        updateWidgets(context, appWidgetIds, false, false);

        for (int appWidgetId : appWidgetIds) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scheduleJob(context, appWidgetId);
            } else {
                setAlarm(context, appWidgetId);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) ||
                action.equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                action.equals("com.motorola.blur.home.ACTION_SET_WIDGET_SIZE") ||
                action.equals("mobi.intuitit.android.hpp.ACTION_READY")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context,
                    DigitalClockAppWidgetProvider.class);

            if (thisWidget != null) {
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (appWidgetIds.length <= 0) {
                    return;
                }

                Intent startIntent = new Intent(context, WidgetUpdateService.class);
                startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
                startIntent.putExtra(WidgetInfoReceiver.UPDATE_WEATHER, true);
                startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

                context.startService(startIntent);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onDeleted");

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


            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager alarmManager = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                PendingIntent pending = createClockTickIntent(context, appWidgetId);
                alarmManager.cancel(pending);
                pending.cancel();
            }
        }

        super.onDeleted(context, appWidgetIds);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob(Context context, int appWidgetId) {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(
                Context.JOB_SCHEDULER_SERVICE);

        final ComponentName name = new ComponentName(context, WidgetUpdateJobService.class);
        final int result;

        if (jobScheduler != null) {
            result = jobScheduler.schedule(getJobInfo(appWidgetId/*UPDATE_WIDGET_JOB_ID*/, 1, name));

            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.d(LOG_TAG, "Scheduled job successfully!");
            }
        }
    }

    private JobInfo getJobInfo(final int id, final long hour,
                               final ComponentName name) {
        final JobInfo jobInfo;
        final long interval = TimeUnit.HOURS.toMillis(hour);
        final boolean isPersistent = true;
        //final int networkType = JobInfo.NETWORK_TYPE_ANY;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(id, name)
                    .setMinimumLatency(interval)
                    //.setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                jobInfo = new JobInfo.Builder(id, name)
                        .setPeriodic(interval)
                        //.setRequiredNetworkType(networkType)
                        .setPersisted(isPersistent)
                        .build();
            } else {
                jobInfo = null;
            }
        }

        return jobInfo;
    }

    public static void setAlarm(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider setAlarm");

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null)
            return;

        Calendar calendar = Calendar.getInstance();
        long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

        if (lastRefresh == 0) {
            lastRefresh = System.currentTimeMillis();
        }

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

        //refreshInterval = 5*60*1000; // test on 5 minutes
        calendar.setTimeInMillis(lastRefresh);
        //long startAlarm = calendar.getTimeInMillis() + refreshInterval;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                refreshInterval, createClockTickIntent(context, appWidgetId));
    }

    private static PendingIntent createClockTickIntent(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider createClockTickIntent");
        Intent intent = new Intent(WidgetUpdateService.WEATHER_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, true);

        return PendingIntent.getBroadcast(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}