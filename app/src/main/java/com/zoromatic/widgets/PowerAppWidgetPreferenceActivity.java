package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

@SuppressLint({"SimpleDateFormat", "NewApi"})
public class PowerAppWidgetPreferenceActivity extends ThemeAppCompatActivity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    static final String APPWIDGETID = "AppWidgetId";

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

        setActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Preferences.getForegroundService(this)
                && !Preferences.getForegroundServiceDontShow(this)) {
            Intent allowForegroundServiceIntent = new Intent(this, AllowForegroundServiceActivity.class);
            startActivity(allowForegroundServiceIntent);
        }
    }

    private void setActivity() {
        setContentView(R.layout.activity_prefs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }

        PreferenceFragment existingFragment = (PreferenceFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (existingFragment == null || !existingFragment.getClass().equals(PowerAppWidgetPreferenceFragment.class)) {
            PowerAppWidgetPreferenceFragment prefs = new PowerAppWidgetPreferenceFragment();
            String action = getIntent().getAction();

            if (action != null) {
                Bundle bundle = new Bundle();
                bundle.putString("category", action);
                bundle.putInt(PowerAppWidgetPreferenceActivity.APPWIDGETID, mAppWidgetId);
                prefs.setArguments(bundle);

                if (actionBar != null) {
                    if (action.equals(getString(R.string.category_general))) {
                        actionBar.setTitle(R.string.generalsettings);
                    } else if (action.equals(getString(R.string.category_look))) {
                        actionBar.setTitle(R.string.lookandfeel);
                    } else {
                        actionBar.setTitle(R.string.power_prefs);
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
                    } else if (action.equals(getString(R.string.category_look))) {
                        actionBar.setTitle(R.string.lookandfeel);
                    } else {
                        actionBar.setTitle(R.string.power_prefs);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent startIntent = new Intent(this, WidgetUpdateService.class);
        startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.POWER_WIDGET_UPDATE_ALL);
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
}