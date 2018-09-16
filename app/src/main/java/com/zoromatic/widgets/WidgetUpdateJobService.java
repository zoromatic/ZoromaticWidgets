package com.zoromatic.widgets;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WidgetUpdateJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {
        Intent startIntent = new Intent(getApplicationContext(), WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetIntentDefinitions.WEATHER_UPDATE);
        startIntent.putExtra(WidgetInfoReceiver.SCHEDULED_UPDATE, true);
        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, params.getJobId());

        startService(startIntent);

        jobFinished(params, true); // see this, we are saying we just finished the job

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        return false;
    }
}
