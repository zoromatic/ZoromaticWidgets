package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZoromaticWidgetsActivity extends ThemeActivity {

    static final String LOG_TAG = "ZoromaticWidgetsActivity";

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializeActivity();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));

        if (appWidgetIds.length > 0) {
            new DigitalClockAppWidgetProvider().updateWidgets(this, appWidgetIds, true, false);
        }

        finish();
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