package com.zoromatic.widgets;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;

public class WriteSettingsActivity extends ThemeActivity {

    private static String LOG_TAG = "WriteSettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        displayDialog();
    }

    private void displayDialog() {
        LayoutInflater li = LayoutInflater.from(getDialogContext());
        View writeSettings = li.inflate(R.layout.writesettings, null);

        String theme = Preferences.getMainTheme(getDialogContext());

        AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder(getDialogContext(),
                theme.compareToIgnoreCase("light") == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material);

        alertDialogBuilder.setView(writeSettings);

        alertDialogBuilder
                .setTitle(getResources().getString(R.string.title_activity_write_settings))
                .setMessage(getString(R.string.marshmallow_text))
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAllowWritingSettingsActivity();
                        finish();
                    }
                })
                .setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            onBackPressed();
                        }

                        return true;
                    }
                });

        AlertDialogPro dialog = alertDialogBuilder.show();
        dialog.setCanceledOnTouchOutside(false);

        if (dialog != null) {
            final Resources res = getResources();
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark,
                    outValue,
                    true);
            int primaryColor = outValue.resourceId;
            int color;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                color = res.getColor(primaryColor, getTheme());
            else
                color = res.getColor(primaryColor);

            // Title
            final int titleId = res.getIdentifier("alertTitle", "id", "android");
            final View titleView = dialog.findViewById(titleId);
            if (titleView != null) {
                ((TextView) titleView).setTextColor(color);
            }

            // Title divider
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = dialog.findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(color);
            }
        }
    }

    private Context getDialogContext() {
        final Context context;
        String theme = Preferences.getMainTheme(this);
        int colorScheme = Preferences.getMainColorScheme(this);

        if (theme.compareToIgnoreCase("light") == 0) {
            switch (colorScheme) {
                case 0:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightBlack);
                    break;
                case 1:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightWhite);
                    break;
                case 2:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightRed);
                    break;
                case 3:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightPink);
                    break;
                case 4:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightPurple);
                    break;
                case 5:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightDeepPurple);
                    break;
                case 6:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightIndigo);
                    break;
                case 7:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightBlue);
                    break;
                case 8:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightLightBlue);
                    break;
                case 9:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightCyan);
                    break;
                case 10:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightTeal);
                    break;
                case 11:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightGreen);
                    break;
                case 12:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightLightGreen);
                    break;
                case 13:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightLime);
                    break;
                case 14:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightYellow);
                    break;
                case 15:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightAmber);
                    break;
                case 16:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightOrange);
                    break;
                case 17:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightDeepOrange);
                    break;
                case 18:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightBrown);
                    break;
                case 19:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightGrey);
                    break;
                case 20:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightBlueGrey);
                    break;
                default:
                    context = new ContextThemeWrapper(this, R.style.ThemeLightBlack);
                    break;
            }

        } else {
            switch (colorScheme) {
                case 0:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkBlack);
                    break;
                case 1:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkWhite);
                    break;
                case 2:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkRed);
                    break;
                case 3:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkPink);
                    break;
                case 4:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkPurple);
                    break;
                case 5:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkDeepPurple);
                    break;
                case 6:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkIndigo);
                    break;
                case 7:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkBlue);
                    break;
                case 8:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkLightBlue);
                    break;
                case 9:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkCyan);
                    break;
                case 10:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkTeal);
                    break;
                case 11:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkGreen);
                    break;
                case 12:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkLightGreen);
                    break;
                case 13:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkLime);
                    break;
                case 14:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkYellow);
                    break;
                case 15:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkAmber);
                    break;
                case 16:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkOrange);
                    break;
                case 17:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkDeepOrange);
                    break;
                case 18:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkBrown);
                    break;
                case 19:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkGrey);
                    break;
                case 20:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkBlueGrey);
                    break;
                default:
                    context = new ContextThemeWrapper(this, R.style.ThemeDarkBlack);
                    break;
            }
        }

        return context;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.setTitle(getResources().getString(R.string.brightnessdesc));
    }

    public void startAllowWritingSettingsActivity() {
        Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        startActivity(settingsIntent);
    }
}