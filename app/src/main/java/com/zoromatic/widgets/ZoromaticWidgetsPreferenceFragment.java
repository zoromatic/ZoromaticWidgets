package com.zoromatic.widgets;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ZoromaticWidgetsPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    public static final int RESULT_OK = -1;
    public boolean mAboutOpen = false;
    public static final String ABOUT_OPEN = "about_open";
    public static final int REQUEST_THEME = 0;

    Context context = null;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        context = getActivity();

        if (context != null) {
            String lang = Preferences.getLanguageOptions(context);

            if (lang.equals("")) {
                String langDef = Locale.getDefault().getLanguage();

                if (!langDef.equals("")) {
                    lang = langDef;
                } else {
                    lang = "en";
                }

                Preferences.setLanguageOptions(context, lang);
            }

            // Change locale settings in the application
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(lang.toLowerCase());
            res.updateConfiguration(conf, dm);

            //setPreferences();

            if (paramBundle != null) {
                mAboutOpen = paramBundle.getBoolean(ABOUT_OPEN);

                if (mAboutOpen) {
                    Context contextLocal = (Context) getActivity();
                    AboutDialog about = new AboutDialog(contextLocal);
                    about.setTitle(getResources().getText(R.string.about_app));
                    about.setOnDismissListener(new Dialog.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mAboutOpen = false;
                        }
                    });
                    about.show();
                }
            }
        }
    }

    public void setPreferences() {
        PreferenceManager localPrefs = getPreferenceManager();
        localPrefs.setSharedPreferencesName(Preferences.PREF_NAME);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String category = bundle.getString("category");

            if (category != null) {
                if (category.equals(getString(R.string.category_general))) {
                    addPreferencesFromResource(R.xml.zoromaticwidgets_prefs);
                } else if (category.equals(getString(R.string.category_theme))) {
                    addPreferencesFromResource(R.xml.zoromaticwidgets_prefs_theme);
                } else if (category.equals(getString(R.string.category_foreground))) {
                    addPreferencesFromResource(R.xml.zoromaticwidgets_prefs_foreground);
                } else if (category.equals(getString(R.string.category_notification))) {
                    addPreferencesFromResource(R.xml.zoromaticwidgets_prefs_notification);
                }
            } else {
                addPreferencesFromResource(R.xml.zoromaticwidgets_prefs);
            }
        } else {
            addPreferencesFromResource(R.xml.zoromaticwidgets_prefs);
        }

        ListPreference mainTheme = (ListPreference) findPreference(Preferences.PREF_MAIN_THEME);

        if (mainTheme != null) {

            String theme = Preferences.getMainTheme(context);

            if (theme.equals("") || mainTheme.findIndexOfValue(theme) < 0) {
                theme = "dark";
            }

            mainTheme.setValueIndex(mainTheme.findIndexOfValue(theme));
            mainTheme.setSummary(mainTheme.getEntries()[mainTheme.findIndexOfValue(theme)]);
        }

        ListPreference mainColorScheme = (ListPreference) findPreference(Preferences.PREF_MAIN_COLOR_SCHEME);

        if (mainColorScheme != null) {
            mainColorScheme.setValueIndex(Preferences.getMainColorScheme(context));
            mainColorScheme.setSummary(mainColorScheme.getEntries()[Preferences.getMainColorScheme(context)]);
        }

        boolean bShowBatteryNotif = Preferences.getShowBatteryNotif(context);
        
        ListPreference batteryIcons = (ListPreference) findPreference(Preferences.PREF_BATTERY_ICONS);

        if (batteryIcons != null) {
            batteryIcons.setEnabled(bShowBatteryNotif);
            batteryIcons.setValueIndex(Preferences.getBatteryIcons(context));
            batteryIcons.setSummary(batteryIcons.getEntries()[Preferences.getBatteryIcons(context)]);
        }

        ListPreference soundToggle = (ListPreference) findPreference(Preferences.PREF_SOUND_OPTIONS);

        if (soundToggle != null) {
            soundToggle.setValueIndex(Preferences.getSoundOptions(context));
            soundToggle.setSummary(soundToggle.getEntries()[Preferences.getSoundOptions(context)]);
        }

        ListPreference brightnessToggle = (ListPreference) findPreference(Preferences.PREF_BRIGHTNESS_OPTIONS);

        if (brightnessToggle != null) {
            brightnessToggle.setValueIndex(Preferences.getBrightnessOptions(context));
            brightnessToggle.setSummary(brightnessToggle.getEntries()[Preferences.getBrightnessOptions(context)]);
        }

        ListPreference language = (ListPreference) findPreference(Preferences.PREF_LANGUAGE_OPTIONS);

        if (language != null) {
            String lang = Preferences.getLanguageOptions(context);

            if (lang.equals("") || language.findIndexOfValue(lang) < 0) {
                lang = "en";
            }

            language.setValueIndex(language.findIndexOfValue(lang));
            language.setSummary(language.getEntries()[language.findIndexOfValue(lang)]);
        }

        CheckBoxPreference foregroundService = (CheckBoxPreference) findPreference(Preferences.PREF_FOREGROUND_SERVICE_KEY);

        if (foregroundService != null) {
            foregroundService.setChecked(Preferences.getForegroundService(context));
        }

        CheckBoxPreference dontShow = (CheckBoxPreference) findPreference(Preferences.PREF_FOREGROUND_SERVICE_DONT_SHOW_KEY);

        if (dontShow != null) {
            dontShow.setChecked(Preferences.getForegroundServiceDontShow(context));
        }

        Preference restart = findPreference(Preferences.PREF_RESTART_SERVICE);

        if (restart != null) {
            restart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference p) {
                    Context contextLocal = getActivity();
                    Intent startIntent = new Intent(contextLocal, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, Intent.ACTION_CONFIGURATION_CHANGED);

                    contextLocal.startService(startIntent);

                    Toast.makeText(contextLocal.getApplicationContext(), getResources().getText(R.string.restartingservice), Toast.LENGTH_LONG).show();

                    return true;
                }
            });
        }

        Preference about = findPreference(Preferences.PREF_ABOUT_KEY);

        if (about != null) {
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference p) {
                    Context contextLocal = (Context) getActivity();
                    AboutDialog about = new AboutDialog(contextLocal);
                    about.setTitle(getResources().getText(R.string.about_app));
                    about.setOnDismissListener(new Dialog.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mAboutOpen = false;
                        }
                    });
                    about.show();
                    mAboutOpen = true;
                    return true;
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(ABOUT_OPEN, mAboutOpen);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mAboutOpen = savedInstanceState.getBoolean(ABOUT_OPEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setPreferences();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preferenceScreen == null || preference == null) {
            return false;
        }

        String key = preference.getKey();

        if (key != null) {
            if (key.equalsIgnoreCase(getResources().getString(R.string.category_theme))) {
                Intent settingsIntent = new Intent(context, ZoromaticWidgetsPreferenceActivity.class);
                settingsIntent.setAction(key);
                startActivityForResult(settingsIntent, REQUEST_THEME);
            } else if (key.equalsIgnoreCase(getResources().getString(R.string.category_foreground))) {
                Intent settingsIntent = new Intent(context, ZoromaticWidgetsPreferenceActivity.class);
                settingsIntent.setAction(key);
                startActivity(settingsIntent);
            } else if (key.equalsIgnoreCase(getResources().getString(R.string.category_notification))) {
                Intent settingsIntent = new Intent(context, ZoromaticWidgetsPreferenceActivity.class);
                settingsIntent.setAction(key);
                startActivity(settingsIntent);
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_THEME:
                //if (resultCode == RESULT_OK){
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);
                //}
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Context context = (Context) getActivity();

        if (context != null) {
            if (key.equals(Preferences.PREF_MAIN_THEME)) {
                ListPreference mainTheme = (ListPreference) findPreference(Preferences.PREF_MAIN_THEME);

                if (mainTheme != null) {
                    String value = mainTheme.getValue();

                    if (value.equals("") || mainTheme.findIndexOfValue(value) < 0) {
                        value = "dark";
                    }

                    Preferences.setMainTheme(context, value);

                    mainTheme.setValueIndex(mainTheme.findIndexOfValue(value));
                    mainTheme.setSummary(mainTheme.getEntries()[mainTheme.findIndexOfValue(value)]);

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    getActivity().startActivity(intent);
                }
            }

            if (key.equals(Preferences.PREF_MAIN_COLOR_SCHEME)) {
                ListPreference mainColorScheme = (ListPreference) findPreference(Preferences.PREF_MAIN_COLOR_SCHEME);

                if (mainColorScheme != null) {
                    Preferences.setMainColorScheme(context, mainColorScheme.findIndexOfValue(mainColorScheme.getValue()));
                    mainColorScheme.setSummary(mainColorScheme.getEntries()[Preferences.getMainColorScheme(context)]);

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    getActivity().startActivity(intent);
                }
            }

    		if (key.equals(Preferences.PREF_BATTERY_ICONS)) {
                ListPreference batteryIcons = (ListPreference) findPreference(Preferences.PREF_BATTERY_ICONS);

                if (batteryIcons != null) {
                    Preferences.setBatteryIcons(context, batteryIcons.findIndexOfValue(batteryIcons.getValue()));
                    batteryIcons.setSummary(batteryIcons.getEntries()[Preferences.getBatteryIcons(context)]);

                    Intent startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.BATTERY_NOTIFICATION);

                    context.startService(startIntent);
                }
            }

            if (key.equals(Preferences.PREF_SOUND_OPTIONS)) {
                ListPreference soundToggle = (ListPreference) findPreference(Preferences.PREF_SOUND_OPTIONS);

                if (soundToggle != null) {
                    Preferences.setSoundOptions(context, soundToggle.findIndexOfValue(soundToggle.getValue()));
                    soundToggle.setSummary(soundToggle.getEntries()[Preferences.getSoundOptions(context)]);
                }
            }

            if (key.equals(Preferences.PREF_BRIGHTNESS_OPTIONS)) {
                ListPreference brightnessToggle = (ListPreference) findPreference(Preferences.PREF_BRIGHTNESS_OPTIONS);

                if (brightnessToggle != null) {
                    Preferences.setBrightnessOptions(context, brightnessToggle.findIndexOfValue(brightnessToggle.getValue()));
                    brightnessToggle.setSummary(brightnessToggle.getEntries()[Preferences.getBrightnessOptions(context)]);

                    Intent startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.BRIGHTNESS_CHANGED);

                    context.startService(startIntent);
                }
            }

            if (key.equals(Preferences.PREF_LANGUAGE_OPTIONS)) {
                ListPreference language = (ListPreference) findPreference(Preferences.PREF_LANGUAGE_OPTIONS);

                if (language != null) {
                    if (!language.getValue().equals("") && language.findIndexOfValue(language.getValue()) >= 0) {
                        Preferences.setLanguageOptions(context, language.getValue());
                    } else {
                        Preferences.setLanguageOptions(context, "en");
                    }

                    // Change locale settings in the application
                    Resources res = context.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.locale = new Locale(language.getValue().toLowerCase());
                    res.updateConfiguration(conf, dm);

                    Intent startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.UPDATE_WIDGETS);

                    context.startService(startIntent);

                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    getActivity().startActivity(intent);
                }
            }

            if (key.equals(Preferences.PREF_FOREGROUND_SERVICE_KEY)) {
                CheckBoxPreference foregroundService = (CheckBoxPreference) findPreference(Preferences.PREF_FOREGROUND_SERVICE_KEY);

                if (foregroundService != null) {
                    Preferences.setForegroundService(context, foregroundService.isChecked());

                    Intent startIntent = new Intent(context, WidgetUpdateService.class);
                    startIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, Intent.ACTION_CONFIGURATION_CHANGED);

                    context.startService(startIntent);
                }
            }

            if (key.equals(Preferences.PREF_FOREGROUND_SERVICE_DONT_SHOW_KEY)) {
                CheckBoxPreference dontShow = (CheckBoxPreference) findPreference(Preferences.PREF_FOREGROUND_SERVICE_DONT_SHOW_KEY);

                if (dontShow != null) {
                    Preferences.setForegroundServiceDontShowKey(context, dontShow.isChecked());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen == null) {
            return;
        }

        // Set up a listener whenever a key changes
        preferenceScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen == null) {
            return;
        }

        // Unregister the listener whenever a key changes
        preferenceScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}