package com.zoromatic.widgets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressLint("NewApi")
public class ZoromaticWidgetsPreferenceActivity extends ThemeAppCompatActivity {
    ZoromaticWidgetsPreferenceFragment prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prefs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceFragment existingFragment = (PreferenceFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (existingFragment == null || !existingFragment.getClass().equals(ZoromaticWidgetsPreferenceFragment.class)) {
            prefs = new ZoromaticWidgetsPreferenceFragment();
            String action = getIntent().getAction();

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
                actionBar.setTitle(R.string.app_prefs);
            }

            if (action != null) {
                Bundle bundle = new Bundle();
                bundle.putString("category", action);
                prefs.setArguments(bundle);

                if (actionBar != null) {
                    if (action.equals(getString(R.string.category_general))) {
                        actionBar.setTitle(R.string.app_prefs);
                    } else if (action.equals(getString(R.string.category_theme))) {
                        actionBar.setTitle(R.string.theme_colors);
                    } else if (action.equals(getString(R.string.category_foreground))) {
                        actionBar.setTitle(R.string.foreground_service);
                    } else if (action.equals(getString(R.string.category_notification))) {
                        actionBar.setTitle(R.string.batterynotification);
                    } else {
                        actionBar.setTitle(R.string.app_prefs);
                    }
                }
            }

            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, prefs)
                    .commit();
        } else {
            String action = getIntent().getAction();

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
                actionBar.setTitle(R.string.app_prefs);
            }

            if (action != null) {
                if (actionBar != null) {
                    if (action.equals(getString(R.string.category_general))) {
                        actionBar.setTitle(R.string.app_prefs);
                    } else if (action.equals(getString(R.string.category_theme))) {
                        actionBar.setTitle(R.string.theme_colors);
                    } else if (action.equals(getString(R.string.category_foreground))) {
                        actionBar.setTitle(R.string.foreground_service);
                    } else if (action.equals(getString(R.string.category_notification))) {
                        actionBar.setTitle(R.string.batterynotification);
                    } else {
                        actionBar.setTitle(R.string.app_prefs);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String action = getIntent().getAction();

        if (action != null) {
            if (action.equals(getString(R.string.category_notification))) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
                    actionBar.setTitle(R.string.batterynotification);
                }

                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.on_off_menu, menu);

                MenuItem switchItem = menu.findItem(R.id.on_off_switch);
                SwitchCompat switchCompat = (SwitchCompat) switchItem.getActionView();

                if (switchCompat != null) {
                    switchCompat.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Preferences.setShowBatteryNotif(getApplicationContext(), isChecked);

                            if (prefs == null) {
                                PreferenceFragment existingFragment = (PreferenceFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

                                if (existingFragment != null && existingFragment.getClass().equals(ZoromaticWidgetsPreferenceFragment.class)) {
                                    prefs = (ZoromaticWidgetsPreferenceFragment) existingFragment;
                                }
                            }

                            if (prefs != null) {
                                ListPreference batteryIcons = (ListPreference) prefs.findPreference(Preferences.PREF_BATTERY_ICONS);

                                if (batteryIcons != null) {
                                    batteryIcons.setEnabled(isChecked);
                                }

                                Intent startIntent = new Intent(getApplicationContext(), WidgetUpdateService.class);
                                startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.BATTERY_NOTIFICATION);

                                startService(startIntent);
                            }

                        }
                    });

                    switchCompat.setChecked(Preferences.getShowBatteryNotif(getApplicationContext()));

                    int[][] states = new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked},
                    };

                    int colorScheme = Preferences.getMainColorScheme(this);
                    TypedArray a;

                    if (colorScheme == 1 || colorScheme == 14) { // white & yellow
                        a = getApplicationContext().obtainStyledAttributes(R.style.ColorSwitchBlack, R.styleable.ColorSwitch);
                    } else {
                        a = getApplicationContext().obtainStyledAttributes(R.style.ColorSwitchWhite, R.styleable.ColorSwitch);
                    }

                    int colorOn = a.getColor(R.styleable.ColorSwitch_switch_colorControlActivated, Color.DKGRAY);
                    int colorOff = a.getColor(R.styleable.ColorSwitch_switch_colorSwitchThumbNormal, Color.DKGRAY);
                    int colorTrack = a.getColor(R.styleable.ColorSwitch_switch_colorForeground, Color.GRAY);

                    a.recycle();

                    int[] thumbColors = new int[]{
                            colorOff,
                            colorOn,
                    };

                    int[] trackColors = new int[]{
                            colorTrack,
                            colorTrack,
                    };

                    DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()),
                            new ColorStateList(states, thumbColors));
                    DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getTrackDrawable()),
                            new ColorStateList(states, trackColors));
                }
            }
        }
        return true;
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

    @Override
    public void onBackPressed() {
        String action = getIntent().getAction();

        if (action != null) {
            if (action.equals(getString(R.string.category_theme)) || action.equals(getString(R.string.category_notification))) {
                setResult(RESULT_OK);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Preferences.getForegroundService(this)
                && !Preferences.getForegroundServiceDontShow(this)) {
            Intent allowForegroundServiceIntent = new Intent(this, AllowForegroundServiceActivity.class);
            startActivity(allowForegroundServiceIntent);
        }

        super.onBackPressed();
    }
}
