package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint({"SimpleDateFormat", "NewApi"})
public class DigitalClockAppWidgetPreferenceActivity extends ThemeAppCompatActivity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    static final String APPWIDGETID = "AppWidgetId";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            // Find the widget id from the intent.
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if (extras != null) {
                if (extras.get(AppWidgetManager.EXTRA_APPWIDGET_ID) != null) {
                    mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                }
            } else {
                if (savedInstanceState != null) {
                    mAppWidgetId = savedInstanceState.getInt(APPWIDGETID);
                }
            }
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.activity_prefs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }

        PreferenceFragment existingFragment = (PreferenceFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (existingFragment == null || !existingFragment.getClass().equals(DigitalClockAppWidgetPreferenceFragment.class)) {
            DigitalClockAppWidgetPreferenceFragment prefs = new DigitalClockAppWidgetPreferenceFragment();
            String action = getIntent().getAction();

            if (action != null) {
                Bundle bundle = new Bundle();
                bundle.putString("category", action);
                bundle.putInt(DigitalClockAppWidgetPreferenceActivity.APPWIDGETID, mAppWidgetId);
                prefs.setArguments(bundle);

                if (actionBar != null) {
                    if (action.equals(getString(R.string.category_general))) {
                        actionBar.setTitle(R.string.generalsettings);
                    } else if (action.equals(getString(R.string.category_weather))) {
                        actionBar.setTitle(R.string.weathersettings);
                    } else if (action.equals(getString(R.string.category_look))) {
                        actionBar.setTitle(R.string.lookandfeel);
                    } else {
                        actionBar.setTitle(R.string.clockwidgetprefs);
                    }
                }
            }

            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, prefs)
                    .commit();
        } else {
            String action = getIntent().getAction();

            if (action != null) {
                if (actionBar != null) {
                    if (action.equals(getString(R.string.category_general))) {
                        actionBar.setTitle(R.string.generalsettings);
                    } else if (action.equals(getString(R.string.category_weather))) {
                        actionBar.setTitle(R.string.weathersettings);
                    } else if (action.equals(getString(R.string.category_look))) {
                        actionBar.setTitle(R.string.lookandfeel);
                    } else {
                        actionBar.setTitle(R.string.clockwidgetprefs);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent startIntent = new Intent(this, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, Intent.ACTION_TIME_CHANGED);
        startIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        this.startService(startIntent);

        setResult(RESULT_OK, startIntent);

        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(APPWIDGETID, mAppWidgetId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        mAppWidgetId = savedInstanceState.getInt(APPWIDGETID);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("InlinedApi")
    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            //int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = /*actionBarHeight + */statusBarHeight;
            statusBar.setBackgroundColor(color);
        } else {
            statusBar.setVisibility(View.GONE);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}