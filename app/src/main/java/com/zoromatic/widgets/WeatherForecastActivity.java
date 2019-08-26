package com.zoromatic.widgets;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint({"SimpleDateFormat", "RtlHardcoded"})
public class WeatherForecastActivity extends ThemeAppCompatActivity {

    private static String LOG_TAG = "WeatherForecastActivity";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final int ACTIVITY_SETTINGS = 0;
    private BroadcastReceiver mReceiver;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLeftDrawerList;

    public static final String DRAWEROPEN = "draweropen";
    private boolean mDrawerOpen = false;

    private ViewPager mViewPager;
    ForecastFragmentPagerAdapter mFragmentPagerAdapter;
    TabLayout mSlidingTabLayout;

    //private List<ForecastPagerItem> mTabs = new ArrayList<>();
    private int mCurrentItem = 0;
    private static final String KEY_CURRENT_ITEM = "key_current_item";

    private ProgressDialogFragment mProgressFragment = null;

    static DataProviderTask dataProviderTask;
    public WeakReference<WeatherForecastActivity> mWeatherForecastActivity;
    private MenuItem mRefreshItem = null;
    Animation mRotation = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Read the appWidgetId to configure from the incoming intent
        mAppWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);

        mWeatherForecastActivity = new WeakReference<>(this);

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

        setContentView(R.layout.weatherforecast);

        initView();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initDrawer();

        setFragments();

        mRotation = AnimationUtils.loadAnimation(this, R.anim.animate_menu);

        // Show the ProgressDialogFragment on this thread
        mProgressFragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", (String) getResources().getText(R.string.working));
        args.putString("message", (String) getResources().getText(R.string.retrieving));
        mProgressFragment.setArguments(args);
        mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

        // Start a new thread that will download all the data
        dataProviderTask = new DataProviderTask();
        dataProviderTask.setActivity(mWeatherForecastActivity);
        dataProviderTask.execute();
    }

    @SuppressLint("NewApi")
    public void refreshData() {
        mDrawerLayout.closeDrawers();
        mDrawerOpen = false;

        float lat = Preferences.getLocationLat(getApplicationContext(), mAppWidgetId);
        float lon = Preferences.getLocationLon(getApplicationContext(), mAppWidgetId);
        long id = Preferences.getLocationId(getApplicationContext(), mAppWidgetId);

        if ((id == -1) && (lat == -222 || lon == -222 || Float.isNaN(lat) || Float.isNaN(lon))) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.nolocationdefined), Toast.LENGTH_LONG).show();
        } else {
            Intent refreshIntent = new Intent(getApplicationContext(), WidgetUpdateService.class);
            refreshIntent.putExtra(WidgetIntentDefinitions.INTENT_EXTRA, WidgetIntentDefinitions.WEATHER_UPDATE);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            getApplicationContext().startService(refreshIntent);

            if (mRefreshItem != null && mRotation != null) {
                ImageView imageRefresh = (ImageView) mRefreshItem.getActionView();

                if (imageRefresh != null) {
                    imageRefresh.startAnimation(mRotation);
                }
            }
        }
    }

    public void openSettings() {
        mDrawerLayout.closeDrawers();
        mDrawerOpen = false;

        Intent settingsIntent = new Intent(getApplicationContext(), DigitalClockAppWidgetPreferenceActivity.class);

        settingsIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        startActivityForResult(settingsIntent, ACTIVITY_SETTINGS);
    }

    private void initView() {
        String theme = Preferences.getMainTheme(this);

        mLeftDrawerList = findViewById(R.id.left_drawer);
        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawerLayout);

        List<RowItem> rowItems = new ArrayList<>();

        RowItem item = new RowItem(theme.compareToIgnoreCase("light") == 0 ? R.drawable.ic_refresh_black_48dp : R.drawable.ic_refresh_white_48dp,
                (String) getResources().getText(R.string.refresh), false);
        rowItems.add(item);
        item = new RowItem(theme.compareToIgnoreCase("light") == 0 ? R.drawable.ic_settings_black_48dp : R.drawable.ic_settings_white_48dp,
                (String) getResources().getText(R.string.settings), false);
        rowItems.add(item);

        ItemAdapter adapter = new ItemAdapter(this, rowItems);
        mLeftDrawerList.setAdapter(adapter);

        mLeftDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (theme.compareToIgnoreCase("light") == 0) {
            mLeftDrawerList.setBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            mLeftDrawerList.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
    }

    private void initDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerOpen = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerOpen = true;
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    // The click listener for ListView in the navigation drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mLeftDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawers();
        mDrawerOpen = false;

        switch (position) {
            case 0: // Refresh
                refreshData();
                break;
            case 1: // Settings
                openSettings();

                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(DRAWEROPEN, mDrawerOpen);

        if (mViewPager != null) {
            savedInstanceState.putInt(KEY_CURRENT_ITEM, mViewPager.getCurrentItem());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        mDrawerOpen = savedInstanceState.getBoolean(DRAWEROPEN);
        mCurrentItem = savedInstanceState.getInt(KEY_CURRENT_ITEM);

        if (mDrawerOpen) {
            if (mDrawerLayout != null) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                mDrawerOpen = true;
                mDrawerToggle.syncState();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weatherforecastmenu, menu);

        mRefreshItem = menu.findItem(R.id.refresh);

        if (mRefreshItem != null) {
            final Menu menuFinal = menu;
            ImageView imageRefresh = (ImageView) mRefreshItem.getActionView();

            if (imageRefresh != null) {
                TypedValue outValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.iconRefresh,
                        outValue,
                        true);
                int refreshIcon = outValue.resourceId;
                imageRefresh.setImageResource(refreshIcon);

                imageRefresh.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menuFinal.performIdentifierAction(mRefreshItem.getItemId(), 0);
                    }
                });
            }
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.refresh:
                refreshData();

                return true;
            case R.id.settings:
                openSettings();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ACTIVITY_SETTINGS) {
            setFragments();

            // Show the ProgressDialogFragment on this thread
            if (mProgressFragment != null) {
                mProgressFragment.dismiss();
            }
            mProgressFragment = new ProgressDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", (String) getResources().getText(R.string.working));
            args.putString("message", (String) getResources().getText(R.string.retrieving));
            mProgressFragment.setArguments(args);
            mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

            // Start a new thread that will download all the data
            dataProviderTask = new DataProviderTask();
            dataProviderTask.setActivity(mWeatherForecastActivity);
            dataProviderTask.execute();
        }
    }

    public void loadData() {
        try {
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.tabTextColor, outValue, true);
            int textColor = outValue.resourceId;
            int colorIndicator = getResources().getColor(textColor);

            int locationType = Preferences.getLocationType(getApplicationContext(), mAppWidgetId);

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CURRENT) {
                mFragmentPagerAdapter.addPagerItem("Current [" + Preferences.getLocation(getApplicationContext(), mAppWidgetId) + "]", colorIndicator, mAppWidgetId, -1);
            }

            long locIdTemp = -1;

            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CUSTOM) {
                locIdTemp = Preferences.getLocationId(getApplicationContext(), mAppWidgetId);
            }

            SQLiteDbAdapter dbHelper = new SQLiteDbAdapter(getApplicationContext());
            dbHelper.open();
            Cursor locationsCursor = dbHelper.fetchAllLocations();

            if (locationsCursor != null && locationsCursor.getCount() > 0) {
                // insert default location
                locationsCursor.moveToFirst();

                do {
                    String title = locationsCursor.getString(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_NAME));
                    long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                    if (locIdTemp >= 0 && locId == locIdTemp) {
                        mFragmentPagerAdapter.addPagerItem(title, colorIndicator, mAppWidgetId, locId);
                    }

                    locationsCursor.moveToNext();
                }
                while (!locationsCursor.isAfterLast());

                // insert other locations
                locationsCursor.moveToFirst();

                do {
                    String title = locationsCursor.getString(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_NAME));
                    long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                    if (locIdTemp < 0 || locId != locIdTemp) {
                        mFragmentPagerAdapter.addPagerItem(title, colorIndicator, mAppWidgetId, locId);
                    }

                    locationsCursor.moveToNext();
                }
                while (!locationsCursor.isAfterLast());
            }

            dbHelper.close();

            if (mViewPager != null && mViewPager.getChildCount() > 0) {
                mViewPager.setCurrentItem(Math.min(mCurrentItem, mFragmentPagerAdapter.getCount() - 1));
            }

        } catch (Exception e) {
            Log.w(LOG_TAG, "Exception: ", e);
        }

    }

    public void setFragments() {
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary,
                outValue,
                true);
        int primaryColor = getResources().getColor(outValue.resourceId);

        getTheme().resolveAttribute(R.attr.colorPrimaryDark,
                outValue,
                true);
        int primaryColorDark = getResources().getColor(outValue.resourceId);

        getTheme().resolveAttribute(R.attr.tabTextColor,
                outValue,
                true);
        int tabTextColor = getResources().getColor(outValue.resourceId);

        mSlidingTabLayout = findViewById(R.id.sliding_tabs);
        mViewPager = findViewById(R.id.viewpager);
        mFragmentPagerAdapter = new ForecastFragmentPagerAdapter(getSupportFragmentManager());
        mFragmentPagerAdapter.resetPagerItems();

        if (mViewPager != null) {
            mViewPager.setAdapter(mFragmentPagerAdapter);

            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                    .getDisplayMetrics());
            mViewPager.setPageMargin(pageMargin);
        }

        if (mSlidingTabLayout != null) {
            mSlidingTabLayout.setBackgroundColor(primaryColor);
            mSlidingTabLayout.setSelectedTabIndicatorColor(tabTextColor);

            int colorScheme = Preferences.getMainColorScheme(this);

            switch (colorScheme) {
                case 0: // black
                    mSlidingTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.sysWhite), tabTextColor);
                    break;
                case 1: // white
                    mSlidingTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.sysBlack), tabTextColor);
                    break;
                default:
                    mSlidingTabLayout.setTabTextColors(primaryColorDark, tabTextColor);
                    break;
            }

            mSlidingTabLayout.setupWithViewPager(mViewPager);
        }
    }

    private static class DataProviderTask extends AsyncTask<Void, Void, Void> {

        WeakReference<WeatherForecastActivity> mWeatherForecastActivity = null;

        void setActivity(WeakReference<WeatherForecastActivity> activity) {
            mWeatherForecastActivity = activity;
        }

        @SuppressWarnings("unused")
        WeakReference<WeatherForecastActivity> getActivity() {
            return mWeatherForecastActivity;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(LOG_TAG, "WeatherForecastActivity - Background thread starting");

            mWeatherForecastActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWeatherForecastActivity.get().loadData();
                    mWeatherForecastActivity.get().readCachedData(mWeatherForecastActivity.get());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //mWeatherForecastActivity.get().setFragments();

            if (mWeatherForecastActivity.get().mProgressFragment != null) {
                mWeatherForecastActivity.get().mProgressFragment.dismiss();
            }
        }
    }

    @SuppressLint("NewApi")
    void readCachedData(Context context) {
        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            if (mFragmentPagerAdapter != null) {
                for (int i = 0; i < mFragmentPagerAdapter.getCount(); i++) {
                    WeatherContentFragment fragment = (WeatherContentFragment) mFragmentPagerAdapter.getFragment(i);

                    if (fragment != null && fragment.getView() != null) {
                        fragment.readCachedData(context, mAppWidgetId);

                        SwipeRefreshLayout swipeLayoutFragment = fragment.getSwipeLayout();
                        if (swipeLayoutFragment != null) {
                            swipeLayoutFragment.setRefreshing(false);
                        }
                    }
                }
            }

            if (mRefreshItem != null) {
                ImageView imageRefresh = (ImageView) mRefreshItem.getActionView();

                if (imageRefresh != null) {
                    imageRefresh.clearAnimation();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(WidgetIntentDefinitions.UPDATE_FORECAST);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && appWidgetId == mAppWidgetId) {
                    readCachedData(context);
                }
            }
        };

        registerReceiver(mReceiver, intentFilter);
        setTitle(getResources().getString(R.string.weatherforecast));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mReceiver);

        if (mViewPager != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
            mCurrentItem = mViewPager.getCurrentItem();
        }
    }

    @Override
    public void onBackPressed() {
        if (dataProviderTask != null) {
            dataProviderTask.cancel(true);
        }

        if (!mDrawerOpen) {
            super.onBackPressed();
            finish();
        } else {
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawers();
            }
            mDrawerOpen = false;
        }
    }

    static class ForecastPagerItem {
        private CharSequence mTitle;
        private final int mIndicatorColor;
        private int mAppWidgetId;
        private long mLocationId;

        private WeatherContentFragment mFragment;

        ForecastPagerItem(CharSequence title, int indicatorColor, int appWidgetId, long locId) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mAppWidgetId = appWidgetId;
            mLocationId = locId;
        }

        Fragment createFragment() {
            Fragment fragment = WeatherContentFragment.newInstance(mTitle, mIndicatorColor, mAppWidgetId, mLocationId);
            ((WeatherContentFragment) fragment).setTitle((String) mTitle);
            mFragment = (WeatherContentFragment) fragment;

            return fragment;
        }

        CharSequence getTitle() {
            return mTitle;
        }

        void setTitle(CharSequence title) {
            mTitle = title;
        }

        public WeatherContentFragment getFragment() {
            return mFragment;
        }

        public void setFragment(WeatherContentFragment mFragment) {
            this.mFragment = mFragment;
        }
    }

    class ForecastFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<ForecastPagerItem> mPagerItems = new ArrayList<>();

        ForecastFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mPagerItems.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mPagerItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerItems.get(position).getTitle();
        }

        Fragment getFragment(int position) {
            return mPagerItems.get(position).getFragment();
        }

        void resetPagerItems() {
            for (ForecastPagerItem item : mPagerItems) {
                item.setFragment(null);
            }

            mPagerItems.clear();
        }

        void addPagerItem(CharSequence title, int colorIndicator, int appWidgetId, long locId) {
            mPagerItems.add(new ForecastPagerItem(title, colorIndicator, appWidgetId, locId));
            notifyDataSetChanged();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WeatherContentFragment fragment = (WeatherContentFragment) super.instantiateItem(container, position);
            mPagerItems.get(position).setFragment(fragment);

            return fragment;
        }
    }
}
