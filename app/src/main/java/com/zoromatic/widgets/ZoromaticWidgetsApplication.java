package com.zoromatic.widgets;

import java.util.Locale;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            ContextCompat.startForegroundService(this, new Intent(this, WidgetUpdateService.class));
        else
            startService(new Intent(this, WidgetUpdateService.class));

        WidgetManager widgetManager = new WidgetManager(this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, DigitalClockAppWidgetProvider.class));

        widgetManager.updateClockWidgets(this, appWidgetIds, false, false); // do not update weather
        widgetManager.updatePowerWidgets(this, WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL);
        widgetManager.updateNotificationBatteryStatus(this, new Intent(Intent.ACTION_BATTERY_CHANGED));

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Preferences.getForegroundService(this)) {
            Intent allowForegroundServiceIntent = new Intent(this, AllowForegroundServiceActivity.class);
            startActivity(allowForegroundServiceIntent);
        }*/

        FirebaseMessaging.getInstance().subscribeToTopic("test")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscription successful";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        Log.d(LOG_TAG, msg);
                    }
                });

    }
}