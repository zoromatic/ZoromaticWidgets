package com.zoromatic.widgets;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getDialogContext(),
                theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle);

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

        AlertDialog dialog = alertDialogBuilder.show();
        dialog.setCanceledOnTouchOutside(false);

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

    @TargetApi(Build.VERSION_CODES.M)
    public void startAllowWritingSettingsActivity() {
        Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        startActivity(settingsIntent);
    }
}