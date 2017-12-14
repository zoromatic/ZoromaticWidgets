package com.zoromatic.widgets;

import java.util.Locale;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

public class ZoromaticWidgetsApplication extends Application {
    private static final String LOG_TAG = "ZoromaticWidgets";

    protected static long GPS_UPDATE_TIME_INTERVAL = 3000; // milliseconds
    protected static float GPS_UPDATE_DISTANCE_INTERVAL = 0; // meters
    private WidgetGPSListener mGpsListener = null;
    private WidgetLocationListener mLocListener = null;
    private BrightnessObserver mSettingsObserver;
    private RotationObserver mRotationObserver;

    /**
     * Observer to watch for changes to the Auto Rotation setting
     */
    private static class RotationObserver extends ContentObserver {

        private Context mContext;

        RotationObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
        }

        void startObserving() {
            ContentResolver resolver = mContext.getContentResolver();
            // Listen to accelerometer
            resolver.registerContentObserver(Settings.System.getUriFor
                            (Settings.System.ACCELEROMETER_ROTATION),
                    true, this);
        }

        void stopObserving() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            PowerAppWidgetProvider.updateWidgets(mContext, WidgetManager.AUTO_ROTATE_CHANGED);
        }
    }

    /**
     * Observer to watch for changes to the BRIGHTNESS setting
     */
    private static class BrightnessObserver extends ContentObserver {

        private Context mContext;

        BrightnessObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
        }

        void startObserving() {
            ContentResolver resolver = mContext.getContentResolver();
            // Listen to brightness and brightness mode
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, this);
            resolver.registerContentObserver(Settings.System
                    .getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this);
        }

        void stopObserving() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            PowerAppWidgetProvider.updateWidgets(mContext, WidgetManager.BRIGHTNESS_CHANGED);
        }
    }

    /**
     * Watch for changes to LOCATION setting
     */
    private class WidgetGPSListener implements GpsStatus.Listener {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:

                    break;
                case GpsStatus.GPS_EVENT_STARTED:

                    break;
                case GpsStatus.GPS_EVENT_STOPPED:

                    break;
            }
        }
    }

    private class WidgetLocationListener implements LocationListener {
        private Context mContext;

        WidgetLocationListener(Context context) {
            mContext = context;
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            try {
                providerChanged();
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            try {
                providerChanged();
            } catch (Exception e) {
                Log.e(LOG_TAG, "", e);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        private void providerChanged() {
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            String intentAction;

            if (currentApiVersion < 9) {
                intentAction = WidgetManager.LOCATION_GPS_ENABLED_CHANGED;
            } else {
                intentAction = WidgetManager.LOCATION_PROVIDERS_CHANGED;
            }

            PowerAppWidgetProvider.updateWidgets(mContext, intentAction);
        }
    }

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

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (mGpsListener == null && mLocListener == null) {
            if (locManager != null) {
                boolean gps_enabled = false;

                try {
                    gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "", e);
                }

                if (gps_enabled) {
                    mGpsListener = new WidgetGPSListener();
                    mLocListener = new WidgetLocationListener(this);

                    try {
                        locManager.addGpsStatusListener(mGpsListener);
                        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                GPS_UPDATE_TIME_INTERVAL, GPS_UPDATE_DISTANCE_INTERVAL,
                                mLocListener);
                    } catch (SecurityException e) {
                        Log.e(LOG_TAG, "", e);
                    }
                }
            }
        }

        if (mSettingsObserver == null) {
            mSettingsObserver = new BrightnessObserver(new Handler(), this);
            mSettingsObserver.startObserving();
        }

        if (mRotationObserver == null) {
            mRotationObserver = new RotationObserver(new Handler(), this);
            mRotationObserver.startObserving();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        try {
            if (mSettingsObserver != null) {
                mSettingsObserver.stopObserving();
                mSettingsObserver = null;
            }

            if (mRotationObserver != null) {
                mRotationObserver.stopObserving();
                mRotationObserver = null;
            }

            mGpsListener = null;
            mLocListener = null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        }
    }
}