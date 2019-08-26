package com.zoromatic.widgets;

import java.io.File;
import java.util.Calendar;
import java.util.List;

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
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class DigitalClockAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "DigitalClockWidget";
    private static final int UPDATE_WIDGET_JOB_ID = 5689;

    static JobScheduler jobScheduler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent startIntent = new Intent(context, WidgetUpdateService.class);
        startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
        //startIntent.putExtra(WidgetIntentDefinitions.UPDATE_WEATHER, true);
        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            ContextCompat.startForegroundService(context, startIntent);
        else
            context.startService(startIntent);

        for (int appWidgetId : appWidgetIds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        if (action != null && (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) ||
                action.equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED) ||
                action.equals("com.motorola.blur.home.ACTION_SET_WIDGET_SIZE") ||
                action.equals("mobi.intuitit.android.hpp.ACTION_READY"))) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context,
                    DigitalClockAppWidgetProvider.class);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            if (appWidgetIds.length <= 0 && !action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
                return;
            }

            Intent startIntent = new Intent(context, WidgetUpdateService.class);
            startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
            //startIntent.putExtra(WidgetIntentDefinitions.UPDATE_WEATHER, true);
            startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ContextCompat.startForegroundService(context, startIntent);
            else
                context.startService(startIntent);
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

            // cancel previous alarms / jobs
            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager alarmManager = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                PendingIntent pending = createWeatherUpdateEvent(context, appWidgetId);
                if (alarmManager != null) {
                    alarmManager.cancel(pending);
                }
                pending.cancel();
            } else {
                if (jobScheduler != null) {
                    List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                    for (JobInfo jobInfo : allPendingJobs) {
                        int jobId = jobInfo.getId();

                        if (jobId == appWidgetId)
                            jobScheduler.cancel(jobId);
                    }
                }
            }
        }

        super.onDeleted(context, appWidgetIds);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context, int appWidgetId) {
        final ComponentName componentName = new ComponentName(context, WidgetUpdateJobService.class);
        int result;

        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (jobScheduler != null) {
            // cancel previous jobs
            List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
            for (JobInfo jobInfo : allPendingJobs) {
                int jobId = jobInfo.getId();

                if (jobId == appWidgetId)
                    jobScheduler.cancel(jobId);
            }

            result = jobScheduler.schedule(getJobInfo(context, appWidgetId/*UPDATE_WIDGET_JOB_ID*/, componentName));

            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.d(LOG_TAG, "Scheduled job successfully!");
            }
        }
    }

    private static JobInfo getJobInfo(Context context, final int appWidgetId, final ComponentName componentName) {
        final JobInfo jobInfo;
        //final long interval = TimeUnit.HOURS.toMillis(hour);
        final boolean isPersistent = true;
        //final int networkType = JobInfo.NETWORK_TYPE_ANY;

        int refreshIntervalCode = Preferences.getRefreshInterval(context, appWidgetId);
        int refreshInterval; // default is 3 hours

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

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(appWidgetId, componentName)
                    .setMinimumLatency(refreshInterval)
                    //.setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                jobInfo = new JobInfo.Builder(appWidgetId, componentName)
                        .setPeriodic(refreshInterval)
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

        // cancel previous alarms
        PendingIntent pending = createWeatherUpdateEvent(context, appWidgetId);
        alarmManager.cancel(pending);
        pending.cancel();

        Calendar calendar = Calendar.getInstance();
        long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

        if (lastRefresh == 0) {
            lastRefresh = System.currentTimeMillis();
        }

        int refreshIntervalCode = Preferences.getRefreshInterval(context, appWidgetId);
        int refreshInterval; // default is 3 hours

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
                refreshInterval, createWeatherUpdateEvent(context, appWidgetId));
    }

    private static PendingIntent createWeatherUpdateEvent(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "DigitalClockAppWidgetProvider createWeatherUpdateEvent");
        Intent intent = new Intent(WidgetIntentDefinitions.WEATHER_UPDATE);
        intent.putExtra(WidgetIntentDefinitions.SCHEDULED_UPDATE, true);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        return PendingIntent.getBroadcast(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}