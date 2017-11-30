package com.zoromatic.widgets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.Thread.sleep;

/**
 * Created by zoran on 11/24/2017.
 * Contains methods for updating, setting and getting info
 * about AppWidgets
 */

public class WidgetManager {
    private static String WIDGET_MANAGER_LOG_TAG = "WidgetManager";

    public static String BLUETOOTH_WIDGET_UPDATE = "com.zoromatic.widgets.BLUETOOTH_WIDGET_UPDATE";
    public static String WIFI_WIDGET_UPDATE = "com.zoromatic.widgets.WIFI_WIDGET_UPDATE";
    public static String MOBILE_DATA_WIDGET_UPDATE = "com.zoromatic.widgets.MOBILE_DATA_WIDGET_UPDATE";
    public static String GPS_WIDGET_UPDATE = "com.zoromatic.widgets.GPS_WIDGET_UPDATE";
    public static String RINGER_WIDGET_UPDATE = "com.zoromatic.widgets.RINGER_WIDGET_UPDATE";
    public static String CLOCK_WIDGET_UPDATE = "com.zoromatic.widgets.CLOCK_WIDGET_UPDATE";
    public static String WEATHER_UPDATE = "com.zoromatic.widgets.WEATHER_UPDATE";
    public static String AIRPLANE_WIDGET_UPDATE = "com.zoromatic.widgets.AIRPLANE_WIDGET_UPDATE";
    public static String BRIGHTNESS_WIDGET_UPDATE = "com.zoromatic.widgets.BRIGHTNESS_WIDGET_UPDATE";
    public static String TORCH_WIDGET_UPDATE = "com.zoromatic.widgets.TORCH_WIDGET_UPDATE";
    public static String NFC_WIDGET_UPDATE = "com.zoromatic.widgets.NFC_WIDGET_UPDATE";
    public static String SYNC_WIDGET_UPDATE = "com.zoromatic.widgets.SYNC_WIDGET_UPDATE";
    public static String ORIENTATION_WIDGET_UPDATE = "com.zoromatic.widgets.ORIENTATION_WIDGET_UPDATE";

    public static String UPDATE_SINGLE_BATTERY_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BATTERY_WIDGET";
    public static String UPDATE_SINGLE_BLUETOOTH_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BLUETOOTH_WIDGET";
    public static String UPDATE_SINGLE_WIFI_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_WIFI_WIDGET";
    public static String UPDATE_SINGLE_MOBILE_DATA_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_MOBILE_DATA_WIDGET";
    public static String UPDATE_SINGLE_GPS_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_GPS_WIDGET";
    public static String UPDATE_SINGLE_RINGER_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_RINGER_WIDGET";
    public static String UPDATE_SINGLE_CLOCK_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_CLOCK_WIDGET";
    public static String UPDATE_SINGLE_AIRPLANE_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_AIRPLANE_WIDGET";
    public static String UPDATE_SINGLE_BRIGHTNESS_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_BRIGHTNESS_WIDGET";
    public static String UPDATE_SINGLE_TORCH_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_TORCH_WIDGET";
    public static String UPDATE_SINGLE_NFC_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_NFC_WIDGET";
    public static String UPDATE_SINGLE_SYNC_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_SYNC_WIDGET";
    public static String UPDATE_SINGLE_ORIENTATION_WIDGET = "com.zoromatic.widgets.UPDATE_SINGLE_ORIENTATION_WIDGET";

    public static String AUTO_ROTATE_CHANGED = "com.zoromatic.widgets.AUTO_ROTATE_CHANGED";
    public static String UPDATE_FORECAST = "com.zoromatic.widgets.UPDATE_FORECAST";
    public static String UPDATE_WIDGETS = "com.zoromatic.widgets.UPDATE_WIDGETS";
    public static String LOCATION_PROVIDERS_CHANGED = "android.location.PROVIDERS_CHANGED";
    public static String LOCATION_GPS_ENABLED_CHANGED = "android.location.GPS_ENABLED_CHANGE";
    public static String NFC_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED";
    public static String SYNC_CONN_STATUS_CHANGED = "com.android.sync.SYNC_CONN_STATUS_CHANGED";
    public static String FLASHLIGHT_CHANGED = "COM_FLASHLIGHT";
    public static String BRIGHTNESS_CHANGED = "com.zoromatic.widgets.BRIGHTNESS_CHANGED";

    public static String POWER_WIDGET_UPDATE_ALL = "com.zoromatic.widgets.POWER_WIDGET_UPDATE_ALL";
    public static String POWER_BLUETOOTH_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_BLUETOOTH_WIDGET_UPDATE";
    public static String POWER_WIFI_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_WIFI_WIDGET_UPDATE";
    public static String POWER_MOBILE_DATA_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_MOBILE_DATA_WIDGET_UPDATE";
    public static String POWER_GPS_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_GPS_WIDGET_UPDATE";
    public static String POWER_RINGER_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_RINGER_WIDGET_UPDATE";
    public static String POWER_AIRPLANE_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_AIRPLANE_WIDGET_UPDATE";
    public static String POWER_BRIGHTNESS_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_BRIGHTNESS_WIDGET_UPDATE";
    public static String POWER_NFC_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_NFC_WIDGET_UPDATE";
    public static String POWER_SYNC_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_SYNC_WIDGET_UPDATE";
    public static String POWER_ORIENTATION_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_ORIENTATION_WIDGET_UPDATE";
    public static String POWER_TORCH_WIDGET_UPDATE = "com.zoromatic.widgets.POWER_TORCH_WIDGET_UPDATE";
    public static String APPWIDGET_RESIZE = "com.sec.android.widgetapp.APPWIDGET_RESIZE";
    public static String APPWIDGET_UPDATE_OPTIONS = "android.appwidget.action.APPWIDGET_UPDATE_OPTIONS";

    static int WIDGET_COLOR_ON = Color.rgb(0x35, 0xB6, 0xE5);
    static int WIDGET_COLOR_OFF = Color.rgb(0xC0, 0xC0, 0xC0);
    static int WIDGET_COLOR_TRANSITION = Color.rgb(0xFF, 0x8C, 0x00);
    static int WIDGET_COLOR_BACKGROUND = Color.rgb(0x00, 0x00, 0x00);
    static int WIDGET_COLOR_TEXT_ON = Color.rgb(0xFF, 0xFF, 0xFF);
    static int WIDGET_COLOR_TEXT_OFF = Color.rgb(0xFF, 0xFF, 0xFF);

    private final static String COMMAND_L_ON = "svc data enable\n ";
    private final static String COMMAND_L_OFF = "svc data disable\n ";
    private final static String COMMAND_SU = "su";

    private static Camera camera;
    private static boolean flashOn = false;
    private Context mContext;

    /**
     * Creates a new instance of <code>WidgetManager</code> with the given parameters.
     *
     * @param context  The context of caller object
     */
    public WidgetManager(Context context) {
        mContext = context;
    }

    public RemoteViews buildPowerUpdate(String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager buildPowerUpdate");

        if (intentExtra == null)
            return null;

        RemoteViews updateViews = new RemoteViews(mContext.getPackageName(),
                R.layout.powerwidget);

        updateViews.setViewVisibility(R.id.loadingWidget,
                View.GONE);

        boolean bShowBluetooth = Preferences.getShowBluetooth(mContext, appWidgetId);
        boolean bShowGps = Preferences.getShowGps(mContext, appWidgetId);
        boolean bShowMobile = Preferences.getShowMobile(mContext, appWidgetId);
        boolean bShowRinger = Preferences.getShowRinger(mContext, appWidgetId);
        boolean bShowWifi = Preferences.getShowWiFi(mContext, appWidgetId);
        boolean bShowAirplane = Preferences.getShowAirplane(mContext, appWidgetId);
        boolean bShowBrightness = Preferences.getShowBrightness(mContext, appWidgetId);
        boolean bShowNfc = Preferences.getShowNfc(mContext, appWidgetId);
        boolean bShowSync = Preferences.getShowSync(mContext, appWidgetId);
        boolean bShowOrientation = Preferences.getShowOrientation(mContext, appWidgetId);
        boolean bShowTorch = Preferences.getShowTorch(mContext, appWidgetId);
        boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(mContext, appWidgetId);
        boolean bShowSettings = Preferences.getShowSettings(mContext, appWidgetId);

        if (bShowBluetooth) {
            updateViews.setViewVisibility(R.id.bluetoothWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.bluetoothWidget,
                    View.GONE);
        }

        if (bShowGps) {
            updateViews.setViewVisibility(R.id.gpsWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.gpsWidget,
                    View.GONE);
        }

        if (bShowMobile) {
            updateViews.setViewVisibility(R.id.mobileWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.mobileWidget,
                    View.GONE);
        }

        if (bShowRinger) {
            updateViews.setViewVisibility(R.id.ringerWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.ringerWidget,
                    View.GONE);
        }

        if (bShowWifi) {
            updateViews.setViewVisibility(R.id.wifiWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.wifiWidget,
                    View.GONE);
        }

        if (bShowAirplane) {
            updateViews.setViewVisibility(R.id.airplaneWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.airplaneWidget,
                    View.GONE);
        }

        if (bShowBrightness) {
            updateViews.setViewVisibility(R.id.brightnessWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.brightnessWidget,
                    View.GONE);
        }

        if (bShowNfc) {
            updateViews.setViewVisibility(R.id.nfcWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.nfcWidget,
                    View.GONE);
        }

        if (bShowSync) {
            updateViews.setViewVisibility(R.id.syncWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.syncWidget,
                    View.GONE);
        }

        if (bShowOrientation) {
            updateViews.setViewVisibility(R.id.orientationWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.orientationWidget,
                    View.GONE);
        }

        if (bShowTorch) {
            updateViews.setViewVisibility(R.id.torchWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.torchWidget,
                    View.GONE);
        }

        if (bShowBatteryStatus) {
            updateViews.setViewVisibility(R.id.batteryStatusWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.batteryStatusWidget,
                    View.GONE);
        }

        if (bShowSettings) {
            updateViews.setViewVisibility(R.id.settingsWidget,
                    View.VISIBLE);
        } else {
            updateViews.setViewVisibility(R.id.settingsWidget,
                    View.GONE);
        }

        Intent prefIntent = new Intent(mContext, PowerAppWidgetPreferenceActivity.class);

        prefIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        prefIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent settingsIntent = PendingIntent.getActivity(mContext,
                appWidgetId, prefIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.settingsWidget, settingsIntent);

        int colorOff = WIDGET_COLOR_OFF;
        int colorBackground = WIDGET_COLOR_BACKGROUND;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorBackground = Preferences.getColorBackground(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        BitmapDrawable bitmapDrawable = setIconColor(mContext, colorOff, R.drawable.settings_on);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewSettings, bitmapDrawable.getBitmap());

        updateViews.setInt(R.id.imageViewSettingsInd, "setColorFilter", colorOff);

        int iOpacity = Preferences.getPowerOpacity(mContext, appWidgetId);

        updateViews.setInt(R.id.backgroundImage, "setAlpha", iOpacity * 255 / 100);
        updateViews.setInt(R.id.backgroundImage, "setColorFilter", colorBackground);

        updateViews.setTextColor(R.id.textViewSettings, colorTextOff);

        try {
            Intent batteryInfo = new Intent(mContext, BatteryInfoActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    mContext, 0, batteryInfo,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.batteryStatusWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_BLUETOOTH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.bluetoothWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_WIFI_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.wifiWidget,
                    pendingIntent);

            int currentApiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(POWER_MOBILE_DATA_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            } else {
                Intent intentData = new Intent(Intent.ACTION_MAIN);
                intentData.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intentData.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                pendingIntent = PendingIntent.getActivity(mContext, 0,
                        intentData, PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.mobileWidget,
                        pendingIntent);
            }

            if (canToggleGPS()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(POWER_GPS_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                        pendingIntent);
            } else {
                Intent locationIntent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                        locationIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(mContext, 0,
                            locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.gpsWidget,
                            pendingIntent);
                }
            }

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_RINGER_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.ringerWidget,
                    pendingIntent);

            if (canToggleAirplane()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(POWER_AIRPLANE_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                        pendingIntent);

            } else {
                Intent airplaneIntent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                        airplaneIntent, PackageManager.MATCH_DEFAULT_ONLY);

                if (resolveInfo != null) {
                    pendingIntent = PendingIntent.getActivity(mContext, 0,
                            airplaneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                            pendingIntent);
                } else {
                    Intent wirelessIntent = new Intent(
                            Settings.ACTION_WIRELESS_SETTINGS);
                    ResolveInfo resolveWirelessInfo = mContext.getPackageManager().resolveActivity(
                            wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (resolveWirelessInfo != null) {
                        pendingIntent = PendingIntent.getActivity(mContext, 0,
                                wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        updateViews.setOnClickPendingIntent(R.id.airplaneWidget,
                                pendingIntent);
                    }
                }
            }

            int nOptions = Preferences.getBrightnessOptions(mContext);

            switch (nOptions) {
                case 0: // toggle
                    pendingIntent = PendingIntent.getBroadcast(mContext,
                            0, new Intent(POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                case 1: // dialog
                    Intent brightnessIntent = new Intent(mContext, BrightnessActivity.class);

                    pendingIntent = PendingIntent.getActivity(mContext,
                            0, brightnessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
                default:
                    pendingIntent = PendingIntent.getBroadcast(mContext,
                            0, new Intent(POWER_BRIGHTNESS_WIDGET_UPDATE),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    break;
            }

            updateViews.setOnClickPendingIntent(R.id.brightnessWidget,
                    pendingIntent);

            if (canToggleNfc()) {
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        0, new Intent(POWER_NFC_WIDGET_UPDATE),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                        pendingIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1 &&
                        mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
                    NfcManager manager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
                    NfcAdapter adapter = null;

                    if (manager != null) {
                        adapter = manager.getDefaultAdapter();
                    }

                    if (adapter != null) {
                        Intent wirelessIntent;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            wirelessIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        else
                            wirelessIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

                        ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(
                                wirelessIntent, PackageManager.MATCH_DEFAULT_ONLY);

                        if (resolveInfo != null) {
                            pendingIntent = PendingIntent.getActivity(mContext, 0,
                                    wirelessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            updateViews.setOnClickPendingIntent(R.id.nfcWidget,
                                    pendingIntent);
                        }
                    }
                }
            }

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_SYNC_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.syncWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_ORIENTATION_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.orientationWidget,
                    pendingIntent);

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, new Intent(POWER_TORCH_WIDGET_UPDATE),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.torchWidget,
                    pendingIntent);
        } catch (Exception e) {
            Log.e(WIDGET_MANAGER_LOG_TAG, "", e);
        }

        return updateViews;
    }

    public void updatePowerWidgetStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {

        if (updateViews == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            return;

        try {
            boolean bShowBluetooth = Preferences.getShowBluetooth(mContext, appWidgetId);
            boolean bShowGps = Preferences.getShowGps(mContext, appWidgetId);
            boolean bShowMobile = Preferences.getShowMobile(mContext, appWidgetId);
            boolean bShowRinger = Preferences.getShowRinger(mContext, appWidgetId);
            boolean bShowWifi = Preferences.getShowWiFi(mContext, appWidgetId);
            boolean bShowAirplane = Preferences.getShowAirplane(mContext, appWidgetId);
            boolean bShowBrightness = Preferences.getShowBrightness(mContext, appWidgetId);
            boolean bShowNfc = Preferences.getShowNfc(mContext, appWidgetId);
            boolean bShowSync = Preferences.getShowSync(mContext, appWidgetId);
            boolean bShowOrientation = Preferences.getShowOrientation(mContext, appWidgetId);
            boolean bShowTorch = Preferences.getShowTorch(mContext, appWidgetId);
            boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(mContext, appWidgetId);
            //boolean bShowSettings = Preferences.getShowSettings(this, appWidgetId);

            if (bShowBatteryStatus)
                updateBatteryStatus(updateViews, intentExtra, appWidgetId);
            if (bShowBluetooth)
                updateBluetoothStatus(updateViews, intentExtra, appWidgetId);
            if (bShowWifi)
                updateWifiStatus(updateViews, intentExtra, appWidgetId);
            if (bShowMobile)
                updateDataStatus(updateViews, intentExtra, appWidgetId);
            if (bShowGps)
                updateGpsStatus(updateViews, intentExtra, appWidgetId);
            if (bShowRinger)
                updateRingerStatus(updateViews, intentExtra, appWidgetId);
            if (bShowAirplane)
                updateAirplaneMode(updateViews, intentExtra, appWidgetId);
            if (bShowBrightness)
                updateBrightness(updateViews, intentExtra, appWidgetId);
            if (bShowNfc)
                updateNfcStatus(updateViews, intentExtra, appWidgetId);
            if (bShowSync)
                updateSyncStatus(updateViews, intentExtra, appWidgetId);
            if (bShowOrientation)
                updateOrientation(updateViews, intentExtra, appWidgetId);
            if (bShowTorch)
                updateTorchStatus(updateViews, intentExtra, appWidgetId);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        } catch (Exception e) {
            Log.e(WIDGET_MANAGER_LOG_TAG, "", e);
        }
    }

    private static BitmapDrawable setIconColor(Context context, int color, int drawable) {
        if (color == 0) {
            color = 0xffffffff;
        }

        final Resources res = context.getResources();
        Drawable maskDrawable = res.getDrawable(drawable);
        if (!(maskDrawable instanceof BitmapDrawable)) {
            return null;
        }

        Bitmap maskBitmap = ((BitmapDrawable) maskDrawable).getBitmap();
        final int width = maskBitmap.getWidth();
        final int height = maskBitmap.getHeight();

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawBitmap(maskBitmap, 0, 0, null);

        Paint maskedPaint = new Paint();
        maskedPaint.setColor(color);
        maskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        canvas.drawRect(0, 0, width, height, maskedPaint);

        return new BitmapDrawable(res, outBitmap);
    }

    public static Bitmap getFontBitmap(Context context, String text, int color, String font, boolean bold, float fontSizeSP) {
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setFakeBoldText(bold);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, (float) pad, fontSizePX, paint);

        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public void updateBatteryStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateBatteryStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(Intent.ACTION_BATTERY_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_BATTERY_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)) {
            return;
        }

        Intent intentBattery = new Intent(Intent.ACTION_BATTERY_CHANGED);

        int rawLevel = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intentBattery.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN);

        int level = -1;
        int icon = R.drawable.battery_widget_75;

        if (rawLevel >= 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
        }

        if (level == -1) {
            Intent batteryIntent = mContext.registerReceiver(
                    null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int rawLevel1 = 0;

            if (batteryIntent != null) {
                rawLevel1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
            }

            int scale1 = 0;

            if (batteryIntent != null) {
                scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                        -1);
            }

            int status1 = BatteryManager.BATTERY_STATUS_UNKNOWN;

            if (batteryIntent != null) {
                status1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
            }

            if (rawLevel1 >= 0 && scale1 > 0) {
                level = (rawLevel1 * 100) / scale1;
            }

            status = status1;
        }

        String strLevel = String.valueOf(level);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            int lnColon = strLevel.length();
            SpannableString spStrLevel = new SpannableString(strLevel);
            spStrLevel.setSpan(new StyleSpan(Typeface.BOLD), 0, lnColon, 0);

            updateViews.setTextViewText(R.id.textViewBatteryStatus, spStrLevel);

            switch (status) {

                case (BatteryManager.BATTERY_STATUS_UNKNOWN):
                    icon = R.drawable.battery_widget_75;
                    break;
                case (BatteryManager.BATTERY_STATUS_FULL):
                    icon = R.drawable.battery_widget_100;
                    break;
                case (BatteryManager.BATTERY_STATUS_CHARGING):
                    if (level < 25)
                        icon = R.drawable.battery_widget_25_charging;
                    else if (level >= 25 && level < 50)
                        icon = R.drawable.battery_widget_50_charging;
                    else if (level >= 50 && level < 75)
                        icon = R.drawable.battery_widget_75_charging;
                    else
                        icon = R.drawable.battery_widget_100_charging;
                    break;
                case (BatteryManager.BATTERY_STATUS_DISCHARGING):
                case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
                    if (level < 25)
                        icon = R.drawable.battery_widget_25;
                    else if (level >= 25 && level < 50)
                        icon = R.drawable.battery_widget_50;
                    else if (level >= 50 && level < 75)
                        icon = R.drawable.battery_widget_75;
                    else
                        icon = R.drawable.battery_widget_100;
                    break;
            }

            updateViews.setImageViewResource(R.id.imageViewBattery, icon);
        } else {
            int threshold2 = Preferences.getThresholdBattery2(mContext, appWidgetId);
            int threshold3 = Preferences.getThresholdBattery3(mContext, appWidgetId);
            int threshold4 = Preferences.getThresholdBattery4(mContext, appWidgetId);

            int color1 = Preferences.getColorBattery1(mContext, appWidgetId);
            int color2 = Preferences.getColorBattery2(mContext, appWidgetId);
            int color3 = Preferences.getColorBattery3(mContext, appWidgetId);
            int color4 = Preferences.getColorBattery4(mContext, appWidgetId);

            int color;
            String font = "fonts/Roboto.ttf";

            if (level < threshold2)
                color = color1;
            else if (level >= threshold2 && level < threshold3)
                color = color2;
            else if (level >= threshold3 && level < threshold4)
                color = color3;
            else
                color = color4;

            updateViews.setImageViewBitmap(R.id.imageViewBatteryStatus, getFontBitmap(mContext, strLevel, color, font, true, 144));
            updateViews.setInt(R.id.imageViewBatteryStatusInd, "setColorFilter", color);

            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.VISIBLE);
                BitmapDrawable bitmapDrawable = setIconColor(mContext, color, R.drawable.high_voltage);

                if (bitmapDrawable != null)
                    updateViews.setImageViewBitmap(R.id.imageViewBatteryStatusCharge, bitmapDrawable.getBitmap());
            } else {
                updateViews.setViewVisibility(R.id.imageViewBatteryStatusCharge, View.GONE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void toggleWidgets(final String intentExtra) {
        if (intentExtra == null)
            return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                int nOptions = Preferences.getBrightnessOptions(mContext); // 0 - toggle, 1 - dialog

                if (intentExtra.equals(BLUETOOTH_WIDGET_UPDATE) || intentExtra.equals(POWER_BLUETOOTH_WIDGET_UPDATE)) {
                    toggleBluetoothState();
                }

                if (intentExtra.equals(WIFI_WIDGET_UPDATE) || intentExtra.equals(POWER_WIFI_WIDGET_UPDATE)) {
                    toggleWiFi();
                }

                if (intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE) || intentExtra.equals(POWER_MOBILE_DATA_WIDGET_UPDATE)) {
                    toggleData();
                }

                if (intentExtra.equals(GPS_WIDGET_UPDATE) || intentExtra.equals(POWER_GPS_WIDGET_UPDATE)) {
                    toggleGps();
                }

                if (intentExtra.equals(RINGER_WIDGET_UPDATE) || intentExtra.equals(POWER_RINGER_WIDGET_UPDATE)) {
                    toggleRinger();
                }

                if (intentExtra.equals(AIRPLANE_WIDGET_UPDATE) || intentExtra.equals(POWER_AIRPLANE_WIDGET_UPDATE)) {
                    toggleAirplaneMode();
                }

                if (nOptions == 0 && (intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE) || intentExtra.equals(POWER_BRIGHTNESS_WIDGET_UPDATE))) {
                    toggleBrightness();
                }

                if (intentExtra.equals(NFC_WIDGET_UPDATE) || intentExtra.equals(POWER_NFC_WIDGET_UPDATE)) {
                    toggleNfc();
                }

                if (intentExtra.equals(SYNC_WIDGET_UPDATE) || intentExtra.equals(POWER_SYNC_WIDGET_UPDATE)) {
                    toggleSync();
                }

                if (intentExtra.equals(ORIENTATION_WIDGET_UPDATE) || intentExtra.equals(POWER_ORIENTATION_WIDGET_UPDATE)) {
                    toggleOrientation();
                }

                if (intentExtra.equals(TORCH_WIDGET_UPDATE) || intentExtra.equals(POWER_TORCH_WIDGET_UPDATE)) {
                    toggleTorch();
                }
                return null;
            }
        }.execute();
    }

    public boolean canToggleAirplane() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public boolean getAirplaneMode() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(mContext.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public void setAirplaneMode(boolean airplaneMode) {

        if (canToggleAirplane()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            } else {
                Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,
                        airplaneMode ? 1 : 0);
            }

            // Post an intent to reload
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", airplaneMode);
            mContext.sendBroadcast(intent);
        }
    }

    public void toggleAirplaneMode() {
        Boolean airplaneMode = getAirplaneMode();
        // ignore toggle requests if the Airplane mode is currently changing
        // state
        setAirplaneMode(!airplaneMode);
    }

    @SuppressWarnings("unused")
    public void updateAirplaneMode(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateAirplaneMode");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_AIRPLANE_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(AIRPLANE_WIDGET_UPDATE) && !intentExtra.equals(POWER_AIRPLANE_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewAirplane, mContext.getResources().getText(R.string.airplane));

        Boolean airplaneMode = getAirplaneMode();

        BitmapDrawable bitmapDrawable = setIconColor(mContext, airplaneMode ? colorOn : colorOff, R.drawable.airplane_on);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewAirplane, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewAirplane, airplaneMode ? colorTextOn : colorTextOff);
        updateViews.setInt(R.id.imageViewAirplaneInd, "setColorFilter", airplaneMode ? colorOn : colorOff);
    }

    protected Boolean getBluetoothState() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        Boolean bluetoothState = null;

        if (bluetoothAdapter != null) {
            switch (bluetoothAdapter.getState()) {
                case BluetoothAdapter.STATE_OFF:
                    bluetoothState = false;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    bluetoothState = null;
                    break;
                case BluetoothAdapter.STATE_ON:
                    bluetoothState = true;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    bluetoothState = null;
                    break;
                default:
                    bluetoothState = false;
            }
        }

        Log.v(WIDGET_MANAGER_LOG_TAG, "getBluetoothState - " + bluetoothState);

        return bluetoothState;

    }

    public void setBluetoothState(boolean bluetoothState) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setBluetoothState - " + bluetoothState);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        if (bluetoothAdapter != null) {
            if (bluetoothState) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        }
    }

    public void toggleBluetoothState() {
        Boolean bluetoothState = getBluetoothState();
        // ignore toggle requests if the BlueTooth is currently changing state
        if (bluetoothState != null) {
            setBluetoothState(!bluetoothState);
        }
    }

    public void updateBluetoothStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateBluetoothStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_BLUETOOTH_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(BLUETOOTH_WIDGET_UPDATE) && !intentExtra.equals(POWER_BLUETOOTH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBluetooth, mContext.getResources().getText(R.string.bluetooth));

        Boolean bluetoothState = getBluetoothState();

        if (bluetoothState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, bluetoothState ? colorOn : colorOff, R.drawable.bluetooth_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewBluetooth, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewBluetooth, bluetoothState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewBluetoothInd, "setColorFilter", bluetoothState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.bluetooth_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewBluetooth, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewBluetooth, colorTextOn);
            updateViews.setInt(R.id.imageViewBluetoothInd, "setColorFilter", colorTransition);
        }
    }

    private boolean isFlashSupported(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    }

    private boolean isCameraSupported(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    protected Boolean getFlashOn() {

        return flashOn;
    }

    public void setTorchState(boolean torchState) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setTorchState - " + torchState);

        PackageManager packageManager = mContext.getPackageManager();

        if (isCameraSupported(packageManager) && isFlashSupported(packageManager)) {

            if (camera == null)
                try {
                    camera = Camera.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    flashOn = false;
                }

            if (torchState) {
                if (camera != null) {
                    try {
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(param);
                        sleep(300);
                        camera.startPreview();
                        flashOn = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        flashOn = false;
                    }
                }
            } else {
                if (camera != null) {
                    try {
                        Camera.Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(param);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        flashOn = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            mContext.sendBroadcast(new Intent(FLASHLIGHT_CHANGED));
        }
    }


    public void toggleTorch() {
        Boolean torchState = getFlashOn();
        // ignore toggle requests if the flashlight is currently changing state
        if (torchState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Intent permissionsIntent = new Intent(mContext, SetPermissionsActivity.class);
                    permissionsIntent.putExtra(SetPermissionsActivity.PERMISSIONS_TYPE, SetPermissionsActivity.PERMISSIONS_REQUEST_CAMERA);
                    permissionsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(permissionsIntent);
                } else {
                    setTorchState(!torchState);
                }
            } else {
                setTorchState(!torchState);
            }
        }
    }

    public void updateTorchStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateTorchStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(FLASHLIGHT_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_TORCH_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(TORCH_WIDGET_UPDATE) && !intentExtra.equals(POWER_TORCH_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewTorch, mContext.getResources().getText(R.string.torch));

        Boolean torchState = getFlashOn();

        if (torchState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, torchState ? colorOn : colorOff, R.drawable.flashlight_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewTorch, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewTorch, torchState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", torchState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.flashlight_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewTorch, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewTorch, colorTextOn);
            updateViews.setInt(R.id.imageViewTorchInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getWifiState() {

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Boolean wifiState = null;

        if (wifiManager != null) {
            switch (wifiManager.getWifiState()) {
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiState = false;
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    wifiState = null;
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiState = true;
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    wifiState = null;
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    wifiState = false;
                    break;
                default:
                    wifiState = false;
            }
        }

        Log.v(WIDGET_MANAGER_LOG_TAG, "getWifiState - " + wifiState);

        return wifiState;

    }

    public void setWifiState(boolean wifiState) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setWifiState - " + wifiState);

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null)
            wifiManager.setWifiEnabled(wifiState);
    }

    public void toggleWiFi() {
        Boolean wifiState = getWifiState();
        // ignore toggle requests if the WiFi is currently changing state
        if (wifiState != null) {
            setWifiState(!wifiState);
        }
    }

    public void updateWifiStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateWifiStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
                && !intentExtra.equals(UPDATE_SINGLE_WIFI_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(WIFI_WIDGET_UPDATE) && !intentExtra.equals(POWER_WIFI_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewWiFi, mContext.getResources().getText(R.string.wifi));

        Boolean wifiState = getWifiState();

        if (wifiState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, wifiState ? colorOn : colorOff, R.drawable.wifi_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewWiFi, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewWiFi, wifiState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", wifiState ? colorOn : colorOff);

            if (wifiState) {
                WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    if (wifiInfo != null) {
                        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                            String ssid = wifiInfo.getSSID();
                            ssid = ssid.replace("\"", "");

                            if (!ssid.contains("<unknown ssid>"))
                                updateViews.setTextViewText(R.id.textViewWiFi, ssid);
                        }
                    }
                }
            }

        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.wifi_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewWiFi, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewWiFi, colorTextOn);
            updateViews.setInt(R.id.imageViewWiFiInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getSyncStatus() {

        Boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
        Log.v(WIDGET_MANAGER_LOG_TAG, "getSyncStatus - " + syncStatus);

        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setSyncStatus - " + syncStatus);

        ContentResolver.setMasterSyncAutomatically(syncStatus);
    }

    public void toggleSync() {
        Boolean syncStatus = getSyncStatus();
        // ignore toggle requests if the Sync is currently changing status
        if (syncStatus != null) {
            setSyncStatus(!syncStatus);
        }
    }

    public void updateSyncStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateSyncStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(SYNC_CONN_STATUS_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_SYNC_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(SYNC_WIDGET_UPDATE) && !intentExtra.equals(POWER_SYNC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewSync, mContext.getResources().getText(R.string.sync));

        Boolean syncStatus = getSyncStatus();

        if (syncStatus != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, syncStatus ? colorOn : colorOff, R.drawable.sync_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewSync, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewSync, syncStatus ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", syncStatus ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.sync_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewSync, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewSync, colorTextOn);
            updateViews.setInt(R.id.imageViewSyncInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getOrientation() {

        Boolean orientation = (Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);

        Log.v(WIDGET_MANAGER_LOG_TAG, "getOrientation - " + orientation);

        // false = auto-rotation is disabled
        // true = auto-rotation is enabled
        return orientation;
    }

    public void setOrientation(boolean orientation) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setOrientation - " + orientation);

        Settings.System.putInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, orientation ? 1 : 0);
    }

    public void toggleOrientation() {
        Boolean orientation = getOrientation();
        // ignore toggle requests if the orientation is currently changing state
        if (orientation != null) {
            setOrientation(!orientation);
        }
    }

    public void updateOrientation(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateOrientation");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(AUTO_ROTATE_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_ORIENTATION_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(ORIENTATION_WIDGET_UPDATE) && !intentExtra.equals(POWER_ORIENTATION_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewOrientation, mContext.getResources().getText(R.string.orientation));

        Boolean orientation = getOrientation();

        if (orientation != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, orientation ? colorOn : colorOff, R.drawable.orientation_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewOrientation, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewOrientation, orientation ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", orientation ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.orientation_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewOrientation, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewOrientation, colorTextOn);
            updateViews.setInt(R.id.imageViewOrientationInd, "setColorFilter", colorTransition);
        }
    }

    public boolean canToggleNfc() {
        return false;
    }

    protected Boolean getNfcState() {
        Boolean nfcState = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1 &&
                mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            NfcManager manager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);

            if (manager != null) {
                NfcAdapter adapter = manager.getDefaultAdapter();
                nfcState = adapter != null && adapter.isEnabled();
            }
        } else {
            nfcState = false;
        }

        Log.v(WIDGET_MANAGER_LOG_TAG, "getNfcState - " + nfcState);

        return nfcState;

    }

    public void setNfcState(boolean nfcState) {

    }

    public void toggleNfc() {
        Boolean nfcState = getNfcState();
        // ignore toggle requests if the NFC is currently changing state
        if (nfcState != null) {
            setNfcState(!nfcState);
        }
    }

    public void updateNfcStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateNfcStatus");

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentApiVersion < 10) {
            return;
        }

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(NFC_ADAPTER_STATE_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_NFC_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(NFC_WIDGET_UPDATE) && !intentExtra.equals(POWER_NFC_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewNfc, mContext.getResources().getText(R.string.nfc));

        Boolean nfcState = getNfcState();

        if (nfcState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, nfcState ? colorOn : colorOff, R.drawable.nfc_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewNfc, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewNfc, nfcState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", nfcState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.nfc_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewNfc, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewNfc, colorTextOn);
            updateViews.setInt(R.id.imageViewNfcInd, "setColorFilter", colorTransition);
        }
    }

    protected Boolean getMobileState() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Boolean mobileState = null;

        if (telephonyManager != null) {
            int extraDataState = telephonyManager.getDataState();

            switch (extraDataState) {
                case TelephonyManager.DATA_CONNECTED:
                    mobileState = true;
                    break;

                case TelephonyManager.DATA_DISCONNECTED:
                    mobileState = false;
                    break;

                case TelephonyManager.DATA_CONNECTING:
                    mobileState = null;
                    break;

                case TelephonyManager.DATA_SUSPENDED:
                    mobileState = null;
                    break;
                default:
                    mobileState = false;
            }
        }

        Log.v(WIDGET_MANAGER_LOG_TAG, "getMobileState - " + mobileState);

        return mobileState;

    }

    public void setMobileState(boolean mobileState) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setMobileState - " + mobileState);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentApiVersion <= android.os.Build.VERSION_CODES.FROYO) {
            setMobileStateFroyo(mobileState);
        } else if (currentApiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
            setMobileStateGingerbread(mobileState);
        } else {
            setMobileStateLollipop(mobileState);
        }
    }

    private void setMobileStateFroyo(boolean mobileState) {

        Method dataConnSwitchmethod = null;
        Class telephonyManagerClass = null;
        Object ITelephonyStub = null;
        Class ITelephonyClass = null;

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null)
            return;

        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass()
                    .getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method getITelephonyMethod = null;
        try {
            if (telephonyManagerClass != null) {
                getITelephonyMethod = telephonyManagerClass
                        .getDeclaredMethod("getITelephony");
            }
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (getITelephonyMethod != null) {
            getITelephonyMethod.setAccessible(true);
        }
        try {
            if (getITelephonyMethod != null) {
                ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            if (ITelephonyStub != null) {
                ITelephonyClass = Class
                        .forName(ITelephonyStub.getClass().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!mobileState) {
            try {
                if (ITelephonyClass != null) {
                    dataConnSwitchmethod = ITelephonyClass
                            .getDeclaredMethod("disableDataConnectivity");
                }
            } catch (SecurityException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (ITelephonyClass != null) {
                    dataConnSwitchmethod = ITelephonyClass
                            .getDeclaredMethod("enableDataConnectivity");
                }
            } catch (SecurityException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (dataConnSwitchmethod != null) {
            dataConnSwitchmethod.setAccessible(true);
        }
        try {
            if (dataConnSwitchmethod != null) {
                dataConnSwitchmethod.invoke(ITelephonyStub);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setMobileStateGingerbread(boolean mobileState) {

        ConnectivityManager conman = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conman == null)
            return;

        Class conmanClass = null;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field iConnectivityManagerField = null;
        try {
            if (conmanClass != null) {
                iConnectivityManagerField = conmanClass
                        .getDeclaredField("mService");
            }
        } catch (SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (iConnectivityManagerField != null) {
            iConnectivityManagerField.setAccessible(true);
        }
        Object iConnectivityManager = null;
        try {
            if (iConnectivityManagerField != null) {
                iConnectivityManager = iConnectivityManagerField.get(conman);
            }
        } catch (IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        Class iConnectivityManagerClass = null;
        try {
            if (iConnectivityManager != null) {
                iConnectivityManagerClass = Class.forName(iConnectivityManager
                        .getClass().getName());
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        Method setMobileDataEnabledMethod = null;
        try {
            if (iConnectivityManagerClass != null) {
                setMobileDataEnabledMethod = iConnectivityManagerClass
                        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            }
        } catch (SecurityException | NoSuchMethodException e1) {
            e1.printStackTrace();
        }

        try {
            if (setMobileDataEnabledMethod != null) {
                setMobileDataEnabledMethod.setAccessible(true);
                setMobileDataEnabledMethod.invoke(iConnectivityManager, mobileState);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void setMobileStateLollipop(boolean mobileState) {

        String command;

        if (mobileState)
            command = COMMAND_L_ON;
        else
            command = COMMAND_L_OFF;

        try {
            Process su = Runtime.getRuntime().exec(COMMAND_SU);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes(command);
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void toggleData() {
        Boolean mobileState = getMobileState();
        // ignore toggle requests if the Mobile Data is currently changing
        // state
        if (mobileState != null) {
            setMobileState(!mobileState);
        }
    }

    public void updateDataStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateMobileStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                && !intentExtra.equals(UPDATE_SINGLE_MOBILE_DATA_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(MOBILE_DATA_WIDGET_UPDATE) && !intentExtra.equals(POWER_MOBILE_DATA_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewMobile, mContext.getResources().getText(R.string.mobile));

        /*if (intentExtra != null && intentExtra.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            // HACK - mobile data state change is delayed, delay getting info
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        Boolean mobileState = getMobileState();

        if (mobileState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, mobileState ? colorOn : colorOff, R.drawable.data_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewMobile, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewMobile, mobileState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", mobileState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.data_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewMobile, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewMobile, colorTextOn);
            updateViews.setInt(R.id.imageViewMobileInd, "setColorFilter", colorTransition);
        }
    }

    private boolean canToggleGPS() {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo;

        try {
            packageInfo = packageManager.getPackageInfo("com.android.settings",
                    PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            return false; // package not found
        }

        if (packageInfo != null) {
            for (ActivityInfo actInfo : packageInfo.receivers) {
                // test if receiver is exported. if so, we can toggle GPS.
                if (actInfo.name
                        .equals("com.android.settings.widget.SettingsAppWidgetProvider")
                        && actInfo.exported) {
                    return true;
                }
            }
        }

        return false; // default
    }

    protected Boolean getGpsState() {

        Boolean gpsState;

        LocationManager locationManager = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));

        if (locationManager == null) {
            return null;
        }

        gpsState = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.v(WIDGET_MANAGER_LOG_TAG, "getGpsState - " + gpsState);

        return gpsState;

    }

    public void setGpsState(boolean gpsState) {

        if (canToggleGPS()) {
            if (gpsState) {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                mContext.sendBroadcast(poke);
            } else {
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                mContext.sendBroadcast(poke);
            }
        }

    }

    public void toggleGps() {
        Boolean gpsState = getGpsState();
        // ignore toggle requests if the GPS is currently changing
        // state
        if (gpsState != null) {
            setGpsState(!gpsState);
        }
    }

    public void updateGpsStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateGpsStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(LOCATION_PROVIDERS_CHANGED)
                && !intentExtra.equals(LOCATION_GPS_ENABLED_CHANGED) && !intentExtra.equals(UPDATE_SINGLE_GPS_WIDGET)
                && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL) && !intentExtra.equals(GPS_WIDGET_UPDATE) && !intentExtra.equals(POWER_GPS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOn(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewGps, mContext.getResources().getText(R.string.gps));

        Boolean gpsState = getGpsState();

        if (gpsState != null) {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, gpsState ? colorOn : colorOff, R.drawable.gps_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewGps, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewGps, gpsState ? colorTextOn : colorTextOff);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", gpsState ? colorOn : colorOff);
        } else {
            BitmapDrawable bitmapDrawable = setIconColor(mContext, colorTransition, R.drawable.gps_on);

            if (bitmapDrawable != null)
                updateViews.setImageViewBitmap(R.id.imageViewGps, bitmapDrawable.getBitmap());

            updateViews.setTextColor(R.id.textViewGps, colorTextOn);
            updateViews.setInt(R.id.imageViewGpsInd, "setColorFilter", colorTransition);
        }
    }

    protected int getRingerState() {

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int ringerState = AudioManager.RINGER_MODE_NORMAL;

        if (audioManager != null) {
            ringerState = audioManager.getRingerMode();
        }

        Log.v(WIDGET_MANAGER_LOG_TAG, "getRingerState - " + ringerState);

        return ringerState;

    }

    public void setRingerState(int ringerState) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setRingerState - " + ringerState);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(intent);

            return;
        }

        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager == null)
            return;

        audioManager.setRingerMode(ringerState);

        if (ringerState == AudioManager.RINGER_MODE_SILENT && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                sleep(500); // Lollipop hack!
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getText(R.string.allsoundsmuted), Toast.LENGTH_LONG).show();
                }
            }, 1000);

            audioManager.setRingerMode(ringerState);
        }

        if (ringerState == AudioManager.RINGER_MODE_VIBRATE) {
            Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

            if (v != null) {
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            }
        }

    }

    public void toggleRinger() {
        int ringerState = getRingerState();

        int nToggle = Preferences.getSoundOptions(mContext);

        switch (nToggle) {
            case 0:
                setRingerState((ringerState + 2) % 3); // Sound/Vibration/Silent
                break;
            case 1:
                setRingerState((ringerState + 1) % 3); // Sound/Silent/Vibration
                break;
            case 2:
                setRingerState(ringerState == AudioManager.RINGER_MODE_NORMAL ?
                        AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL); // Sound/Vibration
                break;
            case 3:
                setRingerState(ringerState == AudioManager.RINGER_MODE_NORMAL ?
                        AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL); // Sound/Silent
                break;
            default:
                setRingerState((ringerState + 2) % 3); // same as 0
                break;
        }
    }

    public void updateRingerStatus(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateRingerStatus");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)
                && !intentExtra.equals(UPDATE_SINGLE_RINGER_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(RINGER_WIDGET_UPDATE) && !intentExtra.equals(POWER_RINGER_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTransition = WIDGET_COLOR_TRANSITION;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTransition = Preferences.getColorTransition(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewRinger, mContext.getResources().getText(R.string.ringer));

        int resource;
        int color = colorOn;
        int textColor = colorTextOn;

        int ringerState = getRingerState();

        switch (ringerState) {
            case AudioManager.RINGER_MODE_SILENT:
                resource = R.drawable.ringer_silent;
                color = colorOff;
                textColor = colorTextOff;
                break;

            case AudioManager.RINGER_MODE_VIBRATE:
                resource = R.drawable.ringer_vibrate;
                break;

            case AudioManager.RINGER_MODE_NORMAL:
                resource = R.drawable.ringer_normal;
                break;

            default:
                resource = R.drawable.ringer_normal;
                color = colorTransition;
                break;
        }

        BitmapDrawable bitmapDrawable = setIconColor(mContext, color, resource);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewRinger, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewRinger, textColor);
        updateViews.setInt(R.id.imageViewRingerInd, "setColorFilter", color);
    }

    public void toggleBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Settings.System.canWrite(mContext)) {
                realToggleBrightness();
            } else {
                Intent writeSettingsIntent = new Intent(mContext, WriteSettingsActivity.class);
                writeSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(writeSettingsIntent);
            }
        } else {
            realToggleBrightness();
        }
    }

    private void realToggleBrightness() {
        try {
            ContentResolver cr = this.mContext.getContentResolver();
            int brightness = Settings.System.getInt(cr,
                    Settings.System.SCREEN_BRIGHTNESS);
            int brightnessMode;

            //Only get brightness setting if available
            brightnessMode = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);

            // Rotate AUTO -> MINIMUM -> DEFAULT -> MAXIMUM
            // Technically, not a toggle...
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                brightness = 30;
                brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
            } else if (brightness < 128) {
                brightness = 128;
            } else if (brightness < 255) {
                brightness = 255;
            } else {
                brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
                brightness = 30;
            }

            Settings.System.putInt(this.mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    brightnessMode);

            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.d(WIDGET_MANAGER_LOG_TAG, "toggleBrightness: " + e);
        }
    }

    protected int getBrightness() {

        int brightness;

        try {
            brightness = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException snfe) {
            brightness = 255;
        }

        return brightness;
    }

    public boolean isAutoBrightness() {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    public void setBrightness(int brightness) {

        Log.v(WIDGET_MANAGER_LOG_TAG, "setBrightness - " + brightness);
    }

    public void updateBrightness(RemoteViews updateViews, String intentExtra, int appWidgetId) {
        Log.d(WIDGET_MANAGER_LOG_TAG, "WidgetManager updateBrightness");

        if (intentExtra == null)
            return;

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !intentExtra.equals(BRIGHTNESS_CHANGED)
                && !intentExtra.equals(UPDATE_SINGLE_BRIGHTNESS_WIDGET) && !intentExtra.equals(POWER_WIDGET_UPDATE_ALL)
                && !intentExtra.equals(BRIGHTNESS_WIDGET_UPDATE) && !intentExtra.equals(POWER_BRIGHTNESS_WIDGET_UPDATE)) {
            return;
        }

        int colorOn = WIDGET_COLOR_ON;
        int colorOff = WIDGET_COLOR_OFF;
        int colorTextOn = WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(mContext, appWidgetId);
            colorOff = Preferences.getColorOff(mContext, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(mContext, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(mContext, appWidgetId);
        }

        updateViews.setTextViewText(R.id.textViewBrightness, mContext.getResources().getText(R.string.brightness));

        int resource;
        int brightness = getBrightness();
        boolean off = false;

        if (isAutoBrightness()) {
            resource = R.drawable.brightness_auto;
        } else {
            if (brightness < 50) {
                resource = R.drawable.brightness_on; // set gray background
                off = true;
            } else {
                if (brightness >= 50 && brightness < 150) {
                    resource = R.drawable.brightness_mid;
                } else {
                    resource = R.drawable.brightness_on;
                }
            }
        }

        BitmapDrawable bitmapDrawable = setIconColor(mContext, off ? colorOff : colorOn, resource);

        if (bitmapDrawable != null)
            updateViews.setImageViewBitmap(R.id.imageViewBrightness, bitmapDrawable.getBitmap());

        updateViews.setTextColor(R.id.textViewBrightness, off ? colorTextOff : colorTextOn);
        updateViews.setInt(R.id.imageViewBrightnessInd, "setColorFilter", off ? colorOff : colorOn);
    }
}
