package com.zoromatic.widgets;

import java.util.Locale;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

public class ZoromaticWidgetsApplication extends Application {
    private static final String LOG_TAG = "ZoromaticWidgets";

    @Override
    public void onCreate() {
        super.onCreate();

        String lang = Preferences.getLanguageOptions(this);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals("")) {
                lang = langDef;
            } else {
                lang = "en";
            }

            Preferences.setLanguageOptions(this, lang);
        }

        // Change locale settings in the application
        Resources res = getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Preferences.getForegroundService(this)) {
            Intent allowForegroundServiceIntent = new Intent(this, AllowForegroundServiceActivity.class);
            startActivity(allowForegroundServiceIntent);
        }
    }
}