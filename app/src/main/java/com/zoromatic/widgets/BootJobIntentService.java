package com.zoromatic.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

public class BootJobIntentService extends JobIntentService {
    public static final int JOB_ID = 0x01;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BootJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //startService(new Intent(this, WidgetUpdateService.class));

        /*WidgetManager widgetManager = new WidgetManager(this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));

        widgetManager.updateClockWidgets(this, appWidgetIds, false, false); // do not update weather
        widgetManager.updatePowerWidgets(this, WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL);*/
    }
}
