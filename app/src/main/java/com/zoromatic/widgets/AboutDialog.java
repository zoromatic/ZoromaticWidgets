package com.zoromatic.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class AboutDialog extends Dialog {
    private static Context mContext = null;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String lang = Preferences.getLanguageOptions(mContext);

        if (lang.equals("")) {
            String langDef = Locale.getDefault().getLanguage();

            if (!langDef.equals("")) {
                lang = langDef;
            } else {
                lang = "en";
            }

            Preferences.setLanguageOptions(mContext, lang);
        }

        // Change locale settings in the application
        Resources res = mContext.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);

        setContentView(R.layout.about);

        TextView tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = (TextView) findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        //tv.setLinkTextColor(Color.WHITE);
        Linkify.addLinks(tv, Linkify.ALL);

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary,
                outValue,
                true);
        int primaryColor = outValue.resourceId;

        int color = res.getColor(primaryColor);

        // Title
        final int titleId = res.getIdentifier("dialogTitle", "id", "android");
        final View titleView = findViewById(titleId);
        if (titleView != null) {
            ((TextView) titleView).setTextColor(color);
        }

        // Title divider
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(color);
        }
    }

    public static String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {

            while ((line = buf.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}