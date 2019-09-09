package com.zoromatic.widgets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.SunriseSunsetLocation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.BatteryManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint({"SimpleDateFormat", "CutPasteId"})
public class WidgetItemAdapter extends BaseAdapter {
    private static String LOG_TAG = "WidgetItemAdapter";
    Context context;
    private List<WidgetRowItem> WidgetRowItems;
    private boolean mActivityDelete = false;

    WidgetItemAdapter(Context context, List<WidgetRowItem> items, boolean activityDelete) {
        this.context = context;
        this.WidgetRowItems = items;
        this.mActivityDelete = activityDelete;
    }

    /*private view holder class*/
    private class ViewHolder {
        LinearLayout linearLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        WidgetRowItem widgetRowItem = (WidgetRowItem) getItem(position);

        String className = widgetRowItem.getClassName();
        int appWidgetId = widgetRowItem.getAppWidgetId();

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (mInflater != null) {
                convertView = mInflater.inflate(R.layout.widget_row, parent, false);
                holder = new ViewHolder();
                holder.linearLayout = convertView.findViewById(R.id.viewHolder);

                if (className.equals(PowerAppWidgetProvider.class.getSimpleName())) {
                    mInflater.inflate(R.layout.powerwidget, holder.linearLayout);

                    updatePowerWidget(holder, appWidgetId);

                } else if (className.equals(DigitalClockAppWidgetProvider.class.getSimpleName())) {
                    mInflater.inflate(R.layout.digitalclockwidget, holder.linearLayout);

                    updateClockWidget(holder, appWidgetId);
                    updateWeatherWidget(holder, appWidgetId);
                }

                convertView.setTag(holder);
            }
        }

        if (convertView != null) {
            ImageView image = convertView.findViewById(R.id.iconWidget);
            if (image != null) {
                image.setVisibility(mActivityDelete ? View.GONE : View.VISIBLE);

                final Resources res = context.getResources();
                final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

                final LetterTileProvider tileProvider = new LetterTileProvider(context);
                final Bitmap letterTile = tileProvider.getLetterTile(className.substring(0, 1), className.substring(0, 1), tileSize, tileSize);

                image.setImageBitmap(letterTile);
            }

            CheckBox checkBox = convertView.findViewById(R.id.checkBoxSelect);
            if (checkBox != null) {
                checkBox.setVisibility(mActivityDelete ? View.VISIBLE : View.GONE);

                if (!mActivityDelete) {
                    checkBox.setChecked(false);
                }
            }
        }

        return convertView;
    }

    public void setActivityDelete(boolean activityDelete) {
        mActivityDelete = activityDelete;
    }

    @SuppressWarnings("deprecation")
    private void updatePowerWidget(ViewHolder holder, int appWidgetId) {
        boolean bShowBluetooth = Preferences.getShowBluetooth(context, appWidgetId);
        boolean bShowGps = Preferences.getShowGps(context, appWidgetId);
        boolean bShowMobile = Preferences.getShowMobile(context, appWidgetId);
        boolean bShowRinger = Preferences.getShowRinger(context, appWidgetId);
        boolean bShowWifi = Preferences.getShowWiFi(context, appWidgetId);
        boolean bShowAirplane = Preferences.getShowAirplane(context, appWidgetId);
        boolean bShowBrightness = Preferences.getShowBrightness(context, appWidgetId);
        boolean bShowNfc = Preferences.getShowNfc(context, appWidgetId);
        boolean bShowSync = Preferences.getShowSync(context, appWidgetId);
        boolean bShowOrientation = Preferences.getShowOrientation(context, appWidgetId);
        boolean bShowTorch = Preferences.getShowTorch(context, appWidgetId);
        boolean bShowBatteryStatus = Preferences.getShowBatteryStatus(context, appWidgetId);
        boolean bShowSettings = Preferences.getShowSettings(context, appWidgetId);

        int colorOn = WidgetIntentDefinitions.WIDGET_COLOR_ON;
        int colorOff = WidgetIntentDefinitions.WIDGET_COLOR_OFF;
        int colorBackground = WidgetIntentDefinitions.WIDGET_COLOR_BACKGROUND;
        int colorTextOn = WidgetIntentDefinitions.WIDGET_COLOR_TEXT_ON;
        int colorTextOff = WidgetIntentDefinitions.WIDGET_COLOR_TEXT_OFF;

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            colorOn = Preferences.getColorOn(context, appWidgetId);
            colorOff = Preferences.getColorOff(context, appWidgetId);
            colorBackground = Preferences.getColorBackground(context, appWidgetId);
            colorTextOn = Preferences.getColorTextOn(context, appWidgetId);
            colorTextOff = Preferences.getColorTextOff(context, appWidgetId);
        }

        ImageView tempImageViewBackground = holder.linearLayout.findViewById(R.id.backgroundImage);

        if (tempImageViewBackground != null) {
            int iOpacity = Preferences.getPowerOpacity(context, appWidgetId);
            tempImageViewBackground.setAlpha((int) iOpacity * 255 / 100);
            tempImageViewBackground.setColorFilter(colorBackground);
        }

        View tempViewWidget = holder.linearLayout.findViewById(R.id.loadingWidget);

        if (tempViewWidget != null) {
            tempViewWidget.setVisibility(View.GONE);
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.bluetoothWidget);

        if (tempViewWidget != null) {
            if (bShowBluetooth) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.bluetooth_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewBluetooth);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewBluetooth);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewBluetoothInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }

            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.gpsWidget);

        if (tempViewWidget != null) {
            if (bShowGps) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.gps_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewGps);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewGps);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewGpsInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.mobileWidget);

        if (tempViewWidget != null) {
            if (bShowMobile) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.data_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewMobile);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewMobile);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewMobileInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.ringerWidget);

        if (tempViewWidget != null) {
            if (bShowRinger) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.ringer_normal);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewRinger);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewRinger);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewRingerInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.wifiWidget);

        if (tempViewWidget != null) {
            if (bShowWifi) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.wifi_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewWiFi);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewWiFi);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewWiFiInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.airplaneWidget);

        if (tempViewWidget != null) {
            if (bShowAirplane) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.airplane_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewAirplane);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewAirplane);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewAirplaneInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.brightnessWidget);

        if (tempViewWidget != null) {
            if (bShowBrightness) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.brightness_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewBrightness);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewBrightness);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewBrightnessInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.nfcWidget);

        if (tempViewWidget != null) {
            if (bShowNfc) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.nfc_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewNfc);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewNfc);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewNfcInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.syncWidget);

        if (tempViewWidget != null) {
            if (bShowSync) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.sync_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewSync);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewSync);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewSyncInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.orientationWidget);

        if (tempViewWidget != null) {
            if (bShowOrientation) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.orientation_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewOrientation);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewOrientation);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewOrientationInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.torchWidget);

        if (tempViewWidget != null) {
            if (bShowTorch) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOn, R.drawable.flashlight_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewTorch);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewTorch);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOn);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewTorchInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOn);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.batteryStatusWidget);

        if (tempViewWidget != null) {
            if (bShowBatteryStatus) {
                tempViewWidget.setVisibility(View.VISIBLE);

                int color = 0;
                String font = "fonts/Roboto.ttf";

                int level = -1;
                int status = BatteryManager.BATTERY_STATUS_UNKNOWN;

                Intent batteryIntent = context.registerReceiver(
                        null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int rawLevel = 0;

                if (batteryIntent != null) {
                    rawLevel = batteryIntent.getIntExtra(
                            BatteryManager.EXTRA_LEVEL, -1);
                    status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS,
                            BatteryManager.BATTERY_STATUS_UNKNOWN);
                    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                            -1);
                    if (rawLevel >= 0 && scale > 0) {
                        level = (rawLevel * 100) / scale;
                    }
                }

                String strLevel = String.valueOf(level);

                int threshold2 = Preferences.getThresholdBattery2(context, appWidgetId);
                int threshold3 = Preferences.getThresholdBattery3(context, appWidgetId);
                int threshold4 = Preferences.getThresholdBattery4(context, appWidgetId);

                int color1 = Preferences.getColorBattery1(context, appWidgetId);
                int color2 = Preferences.getColorBattery2(context, appWidgetId);
                int color3 = Preferences.getColorBattery3(context, appWidgetId);
                int color4 = Preferences.getColorBattery4(context, appWidgetId);

                if (level < threshold2) {
                    color = color1;
                } else if (level >= threshold2 && level < threshold3) {
                    color = color2;
                } else if (level >= threshold3 && level < threshold4) {
                    color = color3;
                } else {
                    color = color4;
                }

                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewBatteryStatus);

                if (tempImageView != null) {
                    tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, strLevel, color, font, true, 96));
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewBatteryStatusCharge);

                if (tempImageView != null) {
                    if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                        BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, color, R.drawable.high_voltage);
                        if (bitmapDrawable != null) {
                            tempImageView.setVisibility(View.VISIBLE);
                            tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                        } else {
                            tempImageView.setVisibility(View.GONE);
                        }
                    } else {
                        tempImageView.setVisibility(View.GONE);
                    }
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewBatteryStatusInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(color);
                }

            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

        tempViewWidget = holder.linearLayout.findViewById(R.id.settingsWidget);

        if (tempViewWidget != null) {
            if (bShowSettings) {
                tempViewWidget.setVisibility(View.VISIBLE);

                BitmapDrawable bitmapDrawable = WidgetManager.setIconColor(context, colorOff, R.drawable.settings_on);
                ImageView tempImageView = tempViewWidget.findViewById(R.id.imageViewSettings);

                if (bitmapDrawable != null && tempImageView != null) {
                    tempImageView.setImageBitmap(bitmapDrawable.getBitmap());
                }

                TextView tempTextView = tempViewWidget.findViewById(R.id.textViewSettings);

                if (tempTextView != null) {
                    tempTextView.setTextColor(colorTextOff);
                }

                tempImageView = tempViewWidget.findViewById(R.id.imageViewSettingsInd);

                if (tempImageView != null) {
                    tempImageView.setColorFilter(colorOff);
                }
            } else {
                tempViewWidget.setVisibility(View.GONE);
            }
        }

    }

    @SuppressWarnings("deprecation")
    private void updateClockWidget(ViewHolder holder, int appWidgetId) {
        boolean bShowDate = Preferences.getShowDate(context, appWidgetId);
        boolean bShow24Hrs = Preferences.getShow24Hrs(context, appWidgetId);
        boolean bShowBattery = Preferences.getShowBattery(context, appWidgetId);
        int iClockSkinItem = Preferences.getClockSkin(context, appWidgetId);
        int iDateFormatItem = Preferences.getDateFormatItem(context, appWidgetId);
        int iOpacity = Preferences.getOpacity(context, appWidgetId);
        int iFontItem = Preferences.getFontItem(context, appWidgetId);
        boolean bold = Preferences.getBoldText(context, appWidgetId);
        int iDateFontItem = Preferences.getDateFontItem(context, appWidgetId);
        boolean dateBold = Preferences.getDateBoldText(context, appWidgetId);
        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        /*String currentHour, currentMinute;

        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        currentMinute = String.format(sdfMinute.format(new Date()));

        if (bShow24Hrs) {
            SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
            currentHour = String.format(sdfHour.format(new Date()));
        } else {
            SimpleDateFormat sdfHour = new SimpleDateFormat("hh");
            currentHour = String.format(sdfHour.format(new Date()));
        }*/
        String currentTime;

        if (bShow24Hrs) {
            SimpleDateFormat sdfHour = new SimpleDateFormat("HH : mm");
            currentTime = String.format(sdfHour.format(new Date()));
        } else {
            SimpleDateFormat sdfHour = new SimpleDateFormat("hh : mm");
            currentTime = String.format(sdfHour.format(new Date()));
        }

        View tempView = holder.linearLayout.findViewById(R.id.loadingWidget);

        if (tempView != null) {
            tempView.setVisibility(View.GONE);
        }

        tempView = holder.linearLayout.findViewById(R.id.clockWidget);

        if (tempView != null) {
            tempView.setVisibility(View.VISIBLE);
        }

        tempView = holder.linearLayout.findViewById(R.id.imageViewDate);

        if (tempView != null) {
            if (bShowDate || bShowBattery) {
                tempView.setVisibility(View.VISIBLE);
            } else {
                tempView.setVisibility(View.GONE);
            }
        }

        int systemClockColor = Color.WHITE;
        int iClockColorItem = Preferences.getClockColorItem(context, appWidgetId);

        if (iClockColorItem >= 0) {
            switch (iClockColorItem) {
                case 0:
                    systemClockColor = Color.BLACK;
                    break;
                case 1:
                    systemClockColor = Color.DKGRAY;
                    break;
                case 2:
                    systemClockColor = Color.GRAY;
                    break;
                case 3:
                    systemClockColor = Color.LTGRAY;
                    break;
                case 4:
                    systemClockColor = Color.WHITE;
                    break;
                case 5:
                    systemClockColor = Color.RED;
                    break;
                case 6:
                    systemClockColor = Color.GREEN;
                    break;
                case 7:
                    systemClockColor = Color.BLUE;
                    break;
                case 8:
                    systemClockColor = Color.YELLOW;
                    break;
                case 9:
                    systemClockColor = Color.CYAN;
                    break;
                case 10:
                    systemClockColor = Color.MAGENTA;
                    break;
                default:
                    systemClockColor = Color.WHITE;
                    break;
            }

            Preferences.setClockColorItem(context, appWidgetId, -1);
            Preferences.setClockColor(context, appWidgetId, systemClockColor);
        } else {
            systemClockColor = Preferences.getClockColor(context, appWidgetId);
        }

        int systemDateColor = Color.WHITE;
        int iDateColorItem = Preferences.getDateColorItem(context, appWidgetId);

        if (iDateColorItem >= 0) {
            switch (iDateColorItem) {
                case 0:
                    systemDateColor = Color.BLACK;
                    break;
                case 1:
                    systemDateColor = Color.DKGRAY;
                    break;
                case 2:
                    systemDateColor = Color.GRAY;
                    break;
                case 3:
                    systemDateColor = Color.LTGRAY;
                    break;
                case 4:
                    systemDateColor = Color.WHITE;
                    break;
                case 5:
                    systemDateColor = Color.RED;
                    break;
                case 6:
                    systemDateColor = Color.GREEN;
                    break;
                case 7:
                    systemDateColor = Color.BLUE;
                    break;
                case 8:
                    systemDateColor = Color.YELLOW;
                    break;
                case 9:
                    systemDateColor = Color.CYAN;
                    break;
                case 10:
                    systemDateColor = Color.MAGENTA;
                    break;
                default:
                    systemDateColor = Color.WHITE;
                    break;
            }

            Preferences.setDateColorItem(context, appWidgetId, -1);
            Preferences.setDateColor(context, appWidgetId, systemDateColor);
        } else {
            systemDateColor = Preferences.getDateColor(context, appWidgetId);
        }

        int systemWidgetColor = Color.BLACK;
        int iWidgetColorItem = Preferences.getWidgetColorItem(context, appWidgetId);

        if (iWidgetColorItem >= 0) {
            switch (iWidgetColorItem) {
                case 0:
                    systemWidgetColor = Color.BLACK;
                    break;
                case 1:
                    systemWidgetColor = Color.DKGRAY;
                    break;
                case 2:
                    systemWidgetColor = Color.GRAY;
                    break;
                case 3:
                    systemWidgetColor = Color.LTGRAY;
                    break;
                case 4:
                    systemWidgetColor = Color.WHITE;
                    break;
                case 5:
                    systemWidgetColor = Color.RED;
                    break;
                case 6:
                    systemWidgetColor = Color.GREEN;
                    break;
                case 7:
                    systemWidgetColor = Color.BLUE;
                    break;
                case 8:
                    systemWidgetColor = Color.YELLOW;
                    break;
                case 9:
                    systemWidgetColor = Color.CYAN;
                    break;
                case 10:
                    systemWidgetColor = Color.MAGENTA;
                    break;
                default:
                    systemWidgetColor = Color.BLACK;
                    break;
            }

            Preferences.setWidgetColorItem(context, appWidgetId, -1);
            Preferences.setWidgetColor(context, appWidgetId, systemWidgetColor);
        } else {
            systemWidgetColor = Preferences.getWidgetColor(context, appWidgetId);
        }

        String[] mFontArray = context.getResources().getStringArray(R.array.fontPathValues);

        String font = "fonts/Roboto.ttf";

        if (mFontArray.length > iFontItem) {
            font = mFontArray[iFontItem];
        }

        String dateFont = "fonts/Roboto.ttf";

        if (mFontArray.length > iDateFontItem) {
            dateFont = mFontArray[iDateFontItem];
        }

        /*ImageView tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockHour);

        if (tempImageView != null) {
            tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, currentHour, systemClockColor, font, bold, 96));
        }

        tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockMinute);

        if (tempImageView != null) {
            tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, currentMinute, systemClockColor, font, bold, 96));
        }

        tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockSpace);

        if (tempImageView != null) {
            tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, ":", systemClockColor, font, bold, 96));
        }*/
        ImageView tempImageView = holder.linearLayout.findViewById(R.id.imageViewTime);

        if (tempImageView != null) {
            tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, currentTime, systemClockColor, font, bold, 96));
        }

        String currentDate = "";
        String[] mTestArray = context.getResources().getStringArray(R.array.dateFormat);

        if (bShowDate) {
            SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
            currentDate = String.format(sdf.format(new Date()));
        }

        if (bShowBattery) {

            Intent intentBattery = new Intent(Intent.ACTION_BATTERY_CHANGED);

            int rawlevel = intentBattery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intentBattery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;

            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }

            if (level == -1) {
                Intent batteryIntent = context
                        .registerReceiver(null,
                                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int rawlevel1 = batteryIntent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, -1);
                int scale1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
                        -1);
                if (rawlevel1 >= 0 && scale1 > 0) {
                    level = (rawlevel1 * 100) / scale1;
                }
            }

            currentDate = currentDate + (bShowDate ? "\t [" : "") + level + "%" + (bShowDate ? "]" : "");
        }

        tempImageView = holder.linearLayout.findViewById(R.id.imageViewDate);

        if (tempImageView != null) {
            tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, currentDate, systemDateColor, dateFont, dateBold, 14));
        }

        ImageView tempImageViewBackground = holder.linearLayout.findViewById(R.id.backgroundImage);

        if (tempImageViewBackground != null) {
            tempImageViewBackground.setAlpha((int) iOpacity * 255 / 100);
            tempImageViewBackground.setColorFilter(systemWidgetColor);
        }

        /*tempView = holder.linearLayout.findViewById(R.id.imageViewClockSpace);

        if (tempView != null) {
            tempView.setVisibility(View.INVISIBLE);
        }

        switch (iClockSkinItem) {
            case 0:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockHour);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_left);
                }

                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockMinute);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_right);
                }

                break;
            case 1:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockHour);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_left_light);
                }

                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockMinute);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_right_light);
                }

                break;
            case 2:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockHour);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(0);
                }

                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockMinute);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(0);
                }

                tempView = holder.linearLayout.findViewById(R.id.imageViewClockSpace);

                if (tempView != null) {
                    tempView.setVisibility(View.VISIBLE);
                }

                break;
            default:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockHour);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_left);
                }

                tempImageView = holder.linearLayout.findViewById(R.id.imageViewClockMinute);

                if (tempImageView != null) {
                    tempImageView.setBackgroundResource(R.drawable.bck_right);
                }
                break;
        }*/

        switch (iClockSkinItem) {
            case 0:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewTime);

                if (tempImageView != null) {
                    tempImageView.setBackgroundColor(Color.BLACK);
                }

                break;
            case 1:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewTime);

                if (tempImageView != null) {
                    tempImageView.setBackgroundColor(Color.WHITE);
                }

                break;
            case 2:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewTime);

                if (tempImageView != null) {
                    tempImageView.setBackgroundColor(Color.TRANSPARENT);
                }

                break;
            default:
                tempImageView = holder.linearLayout.findViewById(R.id.imageViewTime);

                if (tempImageView != null) {
                    tempImageView.setBackgroundColor(Color.BLACK);
                }

                break;
        }

        // weather widget
        if (!showWeather) {
            tempView = holder.linearLayout.findViewById(R.id.weatherLayout);

            if (tempView != null) {
                tempView.setVisibility(View.GONE);
            }

            tempView = holder.linearLayout.findViewById(R.id.imageViewWeather);

            if (tempView != null) {
                tempView.setVisibility(View.GONE);
            }

            tempView = holder.linearLayout.findViewById(R.id.viewButtonRefresh);

            if (tempView != null) {
                tempView.setVisibility(View.GONE);
            }

            LinearLayout linearLayout = holder.linearLayout.findViewById(R.id.clockWidget);

            if (linearLayout != null) {
                linearLayout.setWeightSum(0.65f);
            }
        } else {
            tempView = holder.linearLayout.findViewById(R.id.weatherLayout);

            if (tempView != null) {
                tempView.setVisibility(View.VISIBLE);
            }

            tempView = holder.linearLayout.findViewById(R.id.imageViewWeather);

            if (tempView != null) {
                tempView.setVisibility(View.VISIBLE);
            }

            tempView = holder.linearLayout.findViewById(R.id.viewButtonRefresh);

            if (tempView != null) {
                tempView.setVisibility(View.VISIBLE);
            }

            LinearLayout linearLayout = holder.linearLayout.findViewById(R.id.clockWidget);

            if (linearLayout != null) {
                linearLayout.setWeightSum(1.0f);
            }
        }
    }

    @SuppressWarnings("unused")
    private void updateWeatherWidget(ViewHolder holder, int appWidgetId) {

        boolean showWeather = Preferences.getShowWeather(context, appWidgetId);

        if (!showWeather) {
            return;
        }

        File parentDirectory = new File(context.getFilesDir().getAbsolutePath());

        if (!parentDirectory.exists()) {
            Log.e(LOG_TAG, "Cache file parent directory does not exist.");

            if (!parentDirectory.mkdirs()) {
                Log.e(LOG_TAG, "Cannot create cache file parent directory.");
            }
        }

        File cacheFile = null;

        long locId = Preferences.getLocationId(context, appWidgetId);

        if (locId >= 0) {
            cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);

            if (!cacheFile.exists()) {
                cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

                if (!cacheFile.exists()) {
                    return;
                }
            }
        } else {
            cacheFile = new File(parentDirectory, "weather_cache_" + appWidgetId);

            if (!cacheFile.exists()) {
                return;
            }
        }

        BufferedReader cacheReader = null;
        try {
            cacheReader = new BufferedReader(new FileReader(cacheFile));
            char[] buf = new char[1024];
            StringBuilder result = new StringBuilder();
            int read = 0;
            read = cacheReader.read(buf);

            while (read >= 0) {
                result.append(buf, 0, read);
                read = cacheReader.read(buf);
            }

            cacheReader.close();

            String parseString = result.toString();

            if (!parseString.equals("") && !parseString.contains("<html>")) {
                parseString.trim();

                if (parseString.endsWith("\n"))
                    parseString = parseString.substring(0, parseString.length() - 1);

                String start = parseString.substring(0, 1);
                String end = parseString.substring(parseString.length() - 1, parseString.length());

                if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                        && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]"))) {
                    return;
                }


                int iFontItem = Preferences.getWeatherFontItem(context, appWidgetId);
                boolean bold = Preferences.getWeatherBoldText(context, appWidgetId);

                int systemWeatherColor = Color.WHITE;
                int iWeatherColorItem = Preferences.getWeatherColorItem(context, appWidgetId);

                if (iWeatherColorItem >= 0) {
                    switch (iWeatherColorItem) {
                        case 0:
                            systemWeatherColor = Color.BLACK;
                            break;
                        case 1:
                            systemWeatherColor = Color.DKGRAY;
                            break;
                        case 2:
                            systemWeatherColor = Color.GRAY;
                            break;
                        case 3:
                            systemWeatherColor = Color.LTGRAY;
                            break;
                        case 4:
                            systemWeatherColor = Color.WHITE;
                            break;
                        case 5:
                            systemWeatherColor = Color.RED;
                            break;
                        case 6:
                            systemWeatherColor = Color.GREEN;
                            break;
                        case 7:
                            systemWeatherColor = Color.BLUE;
                            break;
                        case 8:
                            systemWeatherColor = Color.YELLOW;
                            break;
                        case 9:
                            systemWeatherColor = Color.CYAN;
                            break;
                        case 10:
                            systemWeatherColor = Color.MAGENTA;
                            break;
                        default:
                            systemWeatherColor = Color.WHITE;
                            break;
                    }

                    Preferences.setWeatherColorItem(context, appWidgetId, -1);
                    Preferences.setWeatherColor(context, appWidgetId, systemWeatherColor);
                } else {
                    systemWeatherColor = Preferences.getWeatherColor(context, appWidgetId);
                }

                String[] mFontArray = context.getResources().getStringArray(R.array.fontPathValues);

                String font = "fonts/Roboto.ttf";

                if (mFontArray.length > iFontItem) {
                    font = mFontArray[iFontItem];
                }

                try {
                    JSONTokener parser = new JSONTokener(parseString);
                    int tempScale = Preferences.getTempScale(context, appWidgetId);

                    JSONObject query = (JSONObject) parser.nextValue();
                    JSONObject weatherJSON = null;

                    if (query.has("list")) {

                        JSONArray list = query.getJSONArray("list");

                        if (list.length() == 0) {
                            return;
                        }

                        weatherJSON = list.getJSONObject(0);
                    } else {
                        weatherJSON = query;
                    }

                    int cityId = weatherJSON.getInt("id");
                    String location = weatherJSON.getString("name");
                    String temp = "";

                    int locationType = Preferences.getLocationType(context, appWidgetId);

                    if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                        Preferences.setLocation(context, appWidgetId, location);
                    }

                    ImageView tempImageView = holder.linearLayout.findViewById(R.id.imageViewLoc);

                    if (tempImageView != null) {
                        tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, location, systemWeatherColor, font, bold, 12));
                    }

                    long timestamp = weatherJSON.getLong("dt");
                    Date time = new Date(timestamp * 1000);

                    JSONObject main = null;
                    try {
                        main = weatherJSON.getJSONObject("main");
                    } catch (JSONException e) {
                    }
                    try {
                        double currentTemp = 0;
                        if (main != null) {
                            currentTemp = main.getDouble("temp") - 273.15;

                            if (tempScale == 1) {
                                temp = String.valueOf((int) (currentTemp * 1.8 + 32)) + "°";
                            } else {
                                temp = String.valueOf((int) currentTemp) + "°";
                            }

                            tempImageView = holder.linearLayout.findViewById(R.id.imageViewTemp);

                            if (tempImageView != null) {
                                tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, temp, systemWeatherColor, font, bold, 32));
                            }
                        }

                    } catch (JSONException e) {
                    }

                    JSONObject windJSON = null;
                    try {
                        windJSON = weatherJSON.getJSONObject("wind");
                    } catch (JSONException e) {
                    }
                    try {
                        if (windJSON != null) {
                            double speed = windJSON.getDouble("speed");
                        }
                    } catch (JSONException e) {
                    }
                    try {
                        if (windJSON != null) {
                            double deg = windJSON.getDouble("deg");
                        }
                    } catch (JSONException e) {
                    }

                    try {
                        double humidityValue = weatherJSON.getJSONObject("main").getDouble("humidity");
                    } catch (JSONException e) {
                    }

                    try {
                        JSONArray weathers = weatherJSON.getJSONArray("weather");
                        for (int i = 0; i < weathers.length(); i++) {
                            JSONObject weather = weathers.getJSONObject(i);
                            int weatherId = weather.getInt("id");
                            String weatherMain = weather.getString("main");
                            String weatherDesc = weather.getString("description");
                            weatherDesc = weatherDesc.substring(0, 1).toUpperCase() + weatherDesc.substring(1);
                            String iconName = weather.getString("icon");
                            String iconNameAlt = iconName + "d";

                            WeatherConditions conditions = new WeatherConditions();

                            int icons = Preferences.getWeatherIcons(context, appWidgetId);
                            int resource = R.drawable.tick_weather_04d;
                            WeatherIcon[] imageArr;

                            tempImageView = holder.linearLayout.findViewById(R.id.imageViewDesc);

                            if (tempImageView != null) {
                                tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, weatherDesc, systemWeatherColor, font, bold, 12));
                            }

                            switch (icons) {
                                case 0:
                                    resource = R.drawable.tick_weather_04d;
                                    imageArr = conditions.m_ImageArrTick;
                                    break;
                                case 1:
                                    resource = R.drawable.touch_weather_04d;
                                    imageArr = conditions.m_ImageArrTouch;
                                    break;
                                case 2:
                                    resource = R.drawable.icon_set_weather_04d;
                                    imageArr = conditions.m_ImageArrIconSet;
                                    break;
                                case 3:
                                    resource = R.drawable.weezle_weather_04d;
                                    imageArr = conditions.m_ImageArrWeezle;
                                    break;
                                case 4:
                                    resource = R.drawable.simple_weather_04d;
                                    imageArr = conditions.m_ImageArrSimple;
                                    break;
                                case 5:
                                    resource = R.drawable.novacons_weather_04d;
                                    imageArr = conditions.m_ImageArrNovacons;
                                    break;
                                case 6:
                                    resource = R.drawable.sticker_weather_04d;
                                    imageArr = conditions.m_ImageArrSticker;
                                    break;
                                case 7:
                                    resource = R.drawable.plain_weather_04d;
                                    imageArr = conditions.m_ImageArrPlain;
                                    break;
                                case 8:
                                    resource = R.drawable.flat_weather_04d;
                                    imageArr = conditions.m_ImageArrFlat;
                                    break;
                                case 9:
                                    resource = R.drawable.dvoid_weather_04d;
                                    imageArr = conditions.m_ImageArrDvoid;
                                    break;
                                case 10:
                                    resource = R.drawable.ikonko_weather_04d;
                                    imageArr = conditions.m_ImageArrIkonko;
                                    break;
                                case 11:
                                    resource = R.drawable.smooth_weather_04d;
                                    imageArr = conditions.m_ImageArrSmooth;
                                    break;
                                case 12:
                                    resource = R.drawable.bubble_weather_04d;
                                    imageArr = conditions.m_ImageArrBubble;
                                    break;
                                case 13:
                                    resource = R.drawable.stylish_weather_04d;
                                    imageArr = conditions.m_ImageArrStylish;
                                    break;
                                case 14:
                                    resource = R.drawable.garmahis_weather_04d;
                                    imageArr = conditions.m_ImageArrGarmahis;
                                    break;
                                case 15:
                                    resource = R.drawable.iconbest_weather_04d;
                                    imageArr = conditions.m_ImageArrIconBest;
                                    break;
                                case 16:
                                    resource = R.drawable.cartoon_weather_04d;
                                    imageArr = conditions.m_ImageArrCartoon;
                                    break;
                                case 17:
                                    resource = R.drawable.flaticon_weather_04d;
                                    imageArr = conditions.m_ImageArrFlaticon;
                                    break;
                                case 18:
                                    resource = R.drawable.icon8_weather_04d;
                                    imageArr = conditions.m_ImageArrIcon8;
                                    break;
                                case 19:
                                    resource = R.drawable.crystal_weather_04d;
                                    imageArr = conditions.m_ImageArrCrystal;
                                    break;
                                case 20:
                                    resource = R.drawable.deszone_weather_04d;
                                    imageArr = conditions.m_ImageArrDesZone;
                                    break;
                                case 21:
                                    resource = R.drawable.modern_weather_04d;
                                    imageArr = conditions.m_ImageArrModern;
                                    break;
                                case 22:
                                    resource = R.drawable.simplistic_weather_04d;
                                    imageArr = conditions.m_ImageArrSimplistic;
                                    break;
                                default:
                                    resource = R.drawable.tick_weather_04d;
                                    imageArr = conditions.m_ImageArrTick;
                                    break;
                            }

                            tempImageView = holder.linearLayout.findViewById(R.id.imageViewWeather);

                            if (tempImageView != null) {
                                tempImageView.setImageResource(resource);
                            }

                            float lat = Preferences.getLocationLat(context, appWidgetId);
                            float lon = Preferences.getLocationLon(context, appWidgetId);
                            boolean bDay = true;

                            if (!Float.isNaN(lat) && !Float.isNaN(lon)) {
                                SunriseSunsetCalculator calc;
                                SunriseSunsetLocation loc = new SunriseSunsetLocation(String.valueOf(lat), String.valueOf(lon));
                                calc = new SunriseSunsetCalculator(loc, TimeZone.getDefault());
                                Calendar calendarForDate = Calendar.getInstance();
                                Calendar civilSunriseCalendarForDate = calc.getCivilSunriseCalendarForDate(calendarForDate);
                                Calendar civilSunsetCalendarForDate = calc.getCivilSunsetCalendarForDate(calendarForDate);

                                bDay = !calendarForDate.before(civilSunriseCalendarForDate) && !calendarForDate.after(civilSunsetCalendarForDate);
                            }

                            for (WeatherIcon anImageArr : imageArr) {
                                if (iconName.equals(anImageArr.iconName) || iconNameAlt.equals(anImageArr.iconName)) {

                                    if (anImageArr.bDay != bDay) {
                                        tempImageView = holder.linearLayout.findViewById(R.id.imageViewWeather);

                                        if (tempImageView != null) {
                                            tempImageView.setImageResource(anImageArr.altIconId);
                                        }
                                    } else {
                                        tempImageView = holder.linearLayout.findViewById(R.id.imageViewWeather);

                                        if (tempImageView != null) {
                                            tempImageView.setImageResource(anImageArr.iconId);
                                        }
                                    }
                                }
                            }

                            tempImageView = holder.linearLayout.findViewById(R.id.imageViewLast);
                            long lastRefresh = Preferences.getLastRefresh(context, appWidgetId);

                            if (lastRefresh > 0) {
                                boolean bShow24Hrs = Preferences.getShow24Hrs(context, appWidgetId);
                                int iDateFormatItem = Preferences.getDateFormatItem(context, appWidgetId);
                                Date resultDate = new Date(lastRefresh);

                                String currentTime;

                                if (bShow24Hrs) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                    currentTime = String.format(sdf.format(resultDate));
                                } else {
                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                                    currentTime = String.format(sdf.format(resultDate));
                                }

                                String currentDate = "";
                                String[] mTestArray = context.getResources().getStringArray(R.array.dateFormat);

                                SimpleDateFormat sdf = new SimpleDateFormat(mTestArray[iDateFormatItem]);
                                currentDate = String.format(sdf.format(resultDate));

                                if (tempImageView != null) {
                                    tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, currentDate + ", " + currentTime, systemWeatherColor, font, bold, 12));
                                }

                                LinearLayout linearLayout = holder.linearLayout.findViewById(R.id.refresh_container);

                                if (linearLayout != null) {
                                    linearLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                if (tempImageView != null) {
                                    tempImageView.setImageBitmap(WidgetManager.getFontBitmap(context, context.getResources().getString(R.string.lastrefreshnever), systemWeatherColor, font, bold, 12));
                                }

                                LinearLayout linearLayout = holder.linearLayout.findViewById(R.id.refresh_container);

                                if (linearLayout != null) {
                                    linearLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        //no weather type
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return WidgetRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return WidgetRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return WidgetRowItems.indexOf(getItem(position));
    }
}