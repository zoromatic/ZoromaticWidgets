package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZoromaticWidgetsActivity extends ThemeActivity {

    static final String LOG_TAG = "ZoromaticWidgetsActivity";

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeActivity();

        startService(new Intent(this, WidgetUpdateService.class));

        WidgetManager widgetManager = new WidgetManager(this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));

        widgetManager.updateClockWidgets(this, appWidgetIds, false, false); // do not update weather
        widgetManager.updatePowerWidgets(this, WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL);

        /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));

        if (appWidgetIds.length > 0) {
            new DigitalClockAppWidgetProvider().updateWidgets(this, appWidgetIds, true, false);
        }

        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PowerAppWidgetProvider.class));

        if (appWidgetIds.length > 0) {
            new PowerAppWidgetProvider().updateWidgets(this, appWidgetIds, Intent.ACTION_CONFIGURATION_CHANGED, false);
        }*/

        //finish();
    }

    private void initializeActivity() {
        setContentView(R.layout.main);

        Intent settingsIntent = new Intent(getApplicationContext(), ZoromaticWidgetsPreferenceActivity.class);

        settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, ZoromaticWidgetsPreferenceFragment.class.getName());
        settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

        startActivity(settingsIntent);

        finish();
    }
}