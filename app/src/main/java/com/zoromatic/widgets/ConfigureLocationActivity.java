/*
 * Copyright (C) 2009 Jeff Sharkey, http://jsharkey.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zoromatic.widgets;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressWarnings({"unused", "deprecation"})
public class ConfigureLocationActivity extends ThemeAppCompatActivity {

    public static final String LOG_TAG = "ConfigureLocation";

    public static int LOCATION_TYPE_CUSTOM = 0;
    public static int LOCATION_TYPE_CURRENT = 1;

    private ImageButton mButtonSearch;
    private EditText mEditLocation;
    private RadioButton mRadioCurrent;
    private RadioButton mRadioManual;
    private CheckBox mCheckCurrent;
    private Button mButtonMap;

    private double mLat = Double.NaN;
    private double mLon = Double.NaN;
    private String mLocation = "";
    private long mLocationID = -1;
    private int mLocationType = 0;

    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String LOC = "loc";
    public static final String ID = "id";
    public static final String LOCTYPE = "loctype";

    public static final String LAST_UPDATED = "lastUpdated";
    public static final String CONFIGURED = "configured";
    public static final int CONFIGURED_TRUE = 1;
    public static int mSelectedGeocode = 0;
    public static boolean mStateSaved = false;

    Bundle mSavedState = null;
    public boolean mActivityDelete = false;

    public static final String AUTHORITY = "com.zoromatic.widgets";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/appwidgets");
    public static String FIND_CITIES_URL = "http://api.openweathermap.org/data/2.5/find?q=%s&type=like&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";
    public static String GET_CITY_URL = "http://api.openweathermap.org/data/2.5/find?lat=%f&lon=%f&cnt=1&lang=%s&APPID=364a27c67e53df61c49db6e5bdf26aa5";

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    static final String APPWIDGETID = "AppWidgetId";
    private static final int ZOOM_LEVEL = 15;

    private ProgressDialogFragment mProgressFragment = null;

    static HttpTask mHttpTask;
    static DataProviderTask mDataProviderTask;
    static WeakReference<ConfigureLocationActivity> mWeakConfigureLocationActivity;
    ConfigureLocationActivity mConfigureLocationActivity;

    static CharSequence[] items = null;
    static JSONArray list = null;
    String parseString = null;

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_LOCATION = 2;
    private static final int ACTIVITY_PERMISSION = 3;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int UPDATE_ID = Menu.FIRST + 2;
    private static final int DEFAULT_ID = Menu.FIRST + 3;

    private SQLiteDbAdapter mDbHelper;
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedState = savedInstanceState;

        // Read the appWidgetId to configure from the incoming intent
        setAppWidgetId(getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId()));
        setConfigureResult(Activity.RESULT_CANCELED);
        if (getAppWidgetId() == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mWeakConfigureLocationActivity = new WeakReference<>(this);
        mConfigureLocationActivity = mWeakConfigureLocationActivity.get();

        mDbHelper = new SQLiteDbAdapter(this);

        displayActivity();

        if (mHttpTask != null) {
            mHttpTask.setActivity(mWeakConfigureLocationActivity);
        }
    }

    private void displayActivity() {
        setContentView(R.layout.configurelocation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        }

        mListView = findViewById(R.id.listLocations);

        //create location in database from old preferences
        if (Preferences.getLocationType(getDialogContext(), mAppWidgetId) == LOCATION_TYPE_CUSTOM) {
            mDbHelper.open();
            Cursor locationsCursor = mDbHelper.fetchAllLocations();

            float latPref = Preferences.getLocationLat(getDialogContext(), mAppWidgetId);
            float lonPref = Preferences.getLocationLon(getDialogContext(), mAppWidgetId);
            String locationPref = Preferences.getLocation(getDialogContext(), mAppWidgetId);
            long locationIDPref = Preferences.getLocationId(getDialogContext(), mAppWidgetId);
            boolean bFound = false;

            if (locationsCursor != null && locationsCursor.getCount() > 0) {
                locationsCursor.moveToFirst();

                do {
                    long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                    if (locId != -1 && locationIDPref == locId) {
                        bFound = true;
                    }

                    locationsCursor.moveToNext();

                }
                while (!locationsCursor.isAfterLast());
            }

            if (!bFound && locationIDPref >= 0) {
                mDbHelper.createLocation(locationIDPref, latPref, lonPref, locationPref);
            }

            mDbHelper.close();
        }

        // Show the ProgressDialogFragment on this thread
        mProgressFragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", (String) getResources().getText(R.string.working));
        args.putString("message", (String) getResources().getText(R.string.retrieving));
        mProgressFragment.setArguments(args);
        mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                makeDefault(id);
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                //return false;
                mActivityDelete = true;
                setListViewItems();

                View tempView = getViewByPosition(position, mListView);

                if (tempView != null) {
                    CheckBox checkBox = tempView.findViewById(R.id.checkBoxSelect);

                    if (checkBox != null) {
                        checkBox.setChecked(true);
                    }
                }

                return true;
            }
        });

        registerForContextMenu(mListView);

        mCheckCurrent = findViewById(R.id.checkCurrent);

        mCheckCurrent.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked && getLocationType() != LOCATION_TYPE_CURRENT) {
                    checkCurrentLocationInfo();
                } else if (!isChecked && getLocationType() != LOCATION_TYPE_CUSTOM) {
                    setLocationType(LOCATION_TYPE_CUSTOM);
                    getCheckCurrent().setText(getResources().getText(R.string.locationcurrent));

                    if (mListView != null && mListView.getCount() > 0) {
                        CustomSimpleCursorAdapter cursorAdapter = (CustomSimpleCursorAdapter) mListView.getAdapter();
                        makeDefault(cursorAdapter.getItemId(0)); // make first default
                    }
                }
            }
        });

        double latPref = Preferences.getLocationLat(this, getAppWidgetId());
        double lonPref = Preferences.getLocationLon(this, getAppWidgetId());
        String locationPref = Preferences.getLocation(this, getAppWidgetId());
        long locationIDPref = Preferences.getLocationId(this, getAppWidgetId());
        int locationTypePref = Preferences.getLocationType(this, getAppWidgetId());

        // If restoring, read location and units from bundle
        if (mSavedState != null) {
            setLat(mSavedState.getDouble(LAT));
            setLon(mSavedState.getDouble(LON));
            setLoc(mSavedState.getString(LOC));
            setLocationID(mSavedState.getLong(ID));
            setLocationType(mSavedState.getInt(LOCTYPE));
        } else {
            setLat(latPref);
            setLon(lonPref);
            setLoc(locationPref);
            setLocationID(locationIDPref);
            setLocationType(locationTypePref);

            items = null;
            list = null;
            parseString = null;
        }

        if (getLocationType() == LOCATION_TYPE_CURRENT) {
            mCheckCurrent.setChecked(true);
            CharSequence loc = getResources().getText(R.string.locationcurrent);
            loc = loc + " [" + locationPref + "]";
            mCheckCurrent.setText(loc);
        } else {
            mCheckCurrent.setChecked(false);
            mCheckCurrent.setText(getResources().getText(R.string.locationcurrent));
        }

        // Start a new thread that will download all the data
        mDataProviderTask = new DataProviderTask();
        mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
        mDataProviderTask.execute();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void setListViewItems() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                supportInvalidateOptionsMenu();

                if (mListView != null && mListView.getCount() > 0) {
                    for (int i = 0; i < mListView.getCount(); i++) {
                        View view = getViewByPosition(i, mListView);

                        if (view != null) {
                            setListViewItem(view);
                        }
                    }
                }
            }
        });
    }

    private void setListViewItem(View view) {
        if (view == null)
            return;

        TextView text = view.findViewById(R.id.label);
        String strLabel = "L";

        if (text != null) {
            strLabel = text.getText().toString();

            if (strLabel.length() > 0)
                strLabel = strLabel.subSequence(0, 1).toString();
            else
                strLabel = "L";
        }

        ImageView image = view.findViewById(R.id.iconWeather);

        if (image != null) {
            image.setVisibility(mActivityDelete ? View.GONE : View.VISIBLE);

            final Resources res = getDialogContext().getResources();
            final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

            final LetterTileProvider tileProvider = new LetterTileProvider(getDialogContext());
            final Bitmap letterTile = tileProvider.getLetterTile(strLabel, strLabel, tileSize, tileSize);

            image.setImageBitmap(letterTile);
        }

        CheckBox checkBox = view.findViewById(R.id.checkBoxSelect);

        if (checkBox != null) {
            checkBox.setVisibility(mActivityDelete ? View.VISIBLE : View.GONE);

            if (!mActivityDelete)
                checkBox.setChecked(false);
        }
    }

    private class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
        CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to) {
            super(context, layout, c, from, to);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if (view != null) {
                setListViewItem(view);
            }

            return view;
        }
    }

    private static class DataProviderTask extends AsyncTask<Void, Void, Void> {

        WeakReference<ConfigureLocationActivity> mConfigureLocationActivity = null;

        void setActivity(WeakReference<ConfigureLocationActivity> activity) {
            mConfigureLocationActivity = activity;
        }

        WeakReference<ConfigureLocationActivity> getActivity() {
            return mConfigureLocationActivity;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(LOG_TAG, "ConfigureLocationActivity - Background thread starting");

            mConfigureLocationActivity.get().fillData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mConfigureLocationActivity.get().mProgressFragment != null) {
                mConfigureLocationActivity.get().mProgressFragment.dismiss();
            }

            mConfigureLocationActivity.get().mActivityDelete = false;
            mConfigureLocationActivity.get().setListViewItems();
        }
    }

    private void fillData() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDbHelper == null)
                    mDbHelper = new SQLiteDbAdapter(getDialogContext());

                mDbHelper.open();

                // Get all of the rows from the database and create the item list
                final Cursor locationsCursor = mDbHelper.fetchAllLocations();
                startManagingCursor(locationsCursor);

                if (Preferences.getLocationType(getDialogContext(), getAppWidgetId()) == LOCATION_TYPE_CUSTOM && locationsCursor != null && locationsCursor.getCount() == 1) {
                    // if only one, make default
                    locationsCursor.moveToFirst();

                    double lat = locationsCursor.getDouble(locationsCursor.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LATITUDE));
                    double lon = locationsCursor.getDouble(locationsCursor.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LONGITUDE));
                    String loc = locationsCursor.getString(locationsCursor.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_NAME));
                    long locId = locationsCursor.getLong(locationsCursor.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LOCATION_ID));

                    setLat(lat);
                    setLon(lon);
                    setLoc(loc);
                    setLocationID(locId);
                    setLocationType(LOCATION_TYPE_CUSTOM);

                    Preferences.setLocationLat(getDialogContext(), getAppWidgetId(), (float) getLat());
                    Preferences.setLocationLon(getDialogContext(), getAppWidgetId(), (float) getLon());
                    Preferences.setLocation(getDialogContext(), getAppWidgetId(), getLoc());

                    Preferences.setLocationId(getDialogContext(), getAppWidgetId(), getLocationID());
                    Preferences.setLocationType(getDialogContext(), getAppWidgetId(), getLocationType());
                } else if (Preferences.getLocationType(getDialogContext(), getAppWidgetId()) == LOCATION_TYPE_CUSTOM && locationsCursor != null && locationsCursor.getCount() == 0) {
                    setLat(Double.NaN);
                    setLon(Double.NaN);
                    setLoc("");
                    setLocationID(-1);
                    setLocationType(LOCATION_TYPE_CUSTOM);

                    Preferences.setLocationLat(getDialogContext(), getAppWidgetId(), (float) getLat());
                    Preferences.setLocationLon(getDialogContext(), getAppWidgetId(), (float) getLon());
                    Preferences.setLocation(getDialogContext(), getAppWidgetId(), getLoc());

                    Preferences.setLocationId(getDialogContext(), getAppWidgetId(), getLocationID());
                    Preferences.setLocationType(getDialogContext(), getAppWidgetId(), getLocationType());
                }

                // Create an array to specify the fields we want to display in the list (only TITLE)
                final String[] from = new String[]{SQLiteDbAdapter.KEY_NAME};

                // and an array of the fields we want to bind those fields to (in this case just text1)
                final int[] to = new int[]{R.id.label};

                // Now create a simple cursor adapter and set it to display
                CustomSimpleCursorAdapter notes =
                        new CustomSimpleCursorAdapter(getDialogContext(), R.layout.locations_row, locationsCursor, from, to);

                notes.setViewBinder(new CustomSimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == R.id.label) {
                            int getIndex = cursor.getColumnIndex(SQLiteDbAdapter.KEY_NAME);
                            String name = cursor.getString(getIndex);

                            TextView textView = (TextView) view;
                            textView.setText(name);

                            getIndex = cursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID);
                            long locId = cursor.getLong(getIndex);

                            int locationType = Preferences.getLocationType(getDialogContext(), mAppWidgetId);
                            long locIdTemp = -1;

                            if (locationType == ConfigureLocationActivity.LOCATION_TYPE_CUSTOM) {
                                locIdTemp = Preferences.getLocationId(getDialogContext(), mAppWidgetId);
                            }

                            if (locIdTemp >= 0 && locIdTemp == locId) {
                                name += " [" + getResources().getText(R.string.defaultsummary) + "]";
                            }

                            int lnLoc = name.length();
                            SpannableString spStrLoc = new SpannableString(name);

                            if (locIdTemp >= 0 && locIdTemp == locId) {
                                spStrLoc.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, lnLoc, 0);
                            } else {
                                spStrLoc.setSpan(new StyleSpan(Typeface.NORMAL), 0, lnLoc, 0);
                            }

                            textView.setText(spStrLoc);

                            return true;
                        }
                        return false;
                    }
                });

                mListView.setAdapter(notes);
                stopManagingCursor(locationsCursor);
                mDbHelper.close();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mActivityDelete) {
            mActivityDelete = false;
            setListViewItems();
        } else {
            if (Double.isNaN(getLat()) || Double.isNaN(getLon()) || getLoc().equals("")) {
                setResult(RESULT_CANCELED);
            } else {
                Preferences.setLocationLat(this, getAppWidgetId(), (float) getLat());
                Preferences.setLocationLon(this, getAppWidgetId(), (float) getLon());
                Preferences.setLocation(this, getAppWidgetId(), getLoc());

                Preferences.setLocationId(this, getAppWidgetId(), getLocationID());
                Preferences.setLocationType(this, getAppWidgetId(), getLocationType());

                setResult(RESULT_OK);
            }

            showProgressDialog(false);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.insert).setVisible(!mActivityDelete);
        menu.findItem(R.id.delete).setVisible(mActivityDelete);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locationmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //finish();
                return true;
            case R.id.insert:
                addLocation();
                return true;
            case R.id.delete:
                deleteLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo infoDelete = (AdapterContextMenuInfo) item.getMenuInfo();

                boolean moveDefault = false;
                long locId = -1;

                mDbHelper.open();
                Cursor location = mDbHelper.fetchLocation(infoDelete.id);

                // check if this one was default and move default
                if (location != null && location.getCount() > 0) {
                    location.moveToFirst();

                    locId = location.getLong(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LOCATION_ID));
                    long locIdTemp = Preferences.getLocationId(getDialogContext(), mAppWidgetId);

                    if (locId != -1 && locId == locIdTemp && Preferences.getLocationType(getDialogContext(), mAppWidgetId) == LOCATION_TYPE_CUSTOM) {
                        moveDefault = true;
                    }
                }

                mDbHelper.close();

                mDbHelper.open();
                mDbHelper.deleteLocation(infoDelete.id);
                mDbHelper.close();

                // delete cache file
                File parentDirectory = new File(this.getFilesDir().getAbsolutePath());

                if (parentDirectory.exists()) {
                    File cacheFile;

                    if (locId >= 0) {
                        cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);

                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }

                        cacheFile = new File(parentDirectory, "forecast_cache_loc_" + locId);

                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                    }
                }

                if (moveDefault) {
                    CustomSimpleCursorAdapter cursorAdapter = (CustomSimpleCursorAdapter) mListView.getAdapter();
                    makeDefault(cursorAdapter.getItemId(0)); // make first default
                }

                // Show the ProgressDialogFragment on this thread
                mProgressFragment = new ProgressDialogFragment();
                Bundle args = new Bundle();
                args.putString("title", (String) getResources().getText(R.string.working));
                args.putString("message", (String) getResources().getText(R.string.retrieving));
                mProgressFragment.setArguments(args);
                mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

                // Start a new thread that will download all the data
                mDataProviderTask = new DataProviderTask();
                mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
                mDataProviderTask.execute();

                return true;
            case DEFAULT_ID:
                AdapterContextMenuInfo infoUpdate = (AdapterContextMenuInfo) item.getMenuInfo();
                makeDefault(infoUpdate.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void addLocation() {
        AlertDialogFragment addLocationFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", getResources().getString(R.string.new_location));
        args.putString("message", getResources().getString(R.string.enter_location));
        args.putBoolean("editbox", true);
        addLocationFragment.setArguments(args);
        addLocationFragment.show(getSupportFragmentManager(), "tagAlert");
    }

    private void deleteLocation() {
        if (mListView != null && mListView.getCount() > 0) {
            CustomSimpleCursorAdapter cursorAdapter = (CustomSimpleCursorAdapter) mListView.getAdapter();

            for (int i = mListView.getCount() - 1; i >= 0; i--) {
                View tempView = getViewByPosition(i, mListView);

                if (tempView != null) {
                    CheckBox checkBox = tempView.findViewById(R.id.checkBoxSelect);

                    if (checkBox != null && checkBox.isChecked()) {
                        int id = (int) cursorAdapter.getItemId(i);
                        boolean moveDefault = false;
                        long locId = -1;

                        mDbHelper.open();
                        Cursor location = mDbHelper.fetchLocation(id);

                        // check if this one was default and move default
                        if (location != null && location.getCount() > 0) {
                            location.moveToFirst();

                            locId = location.getLong(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LOCATION_ID));
                            long locIdTemp = Preferences.getLocationId(getDialogContext(), mAppWidgetId);

                            if (locId != -1 && locId == locIdTemp && Preferences.getLocationType(getDialogContext(), mAppWidgetId) == LOCATION_TYPE_CUSTOM) {
                                moveDefault = true;
                            }
                        }

                        mDbHelper.close();

                        mDbHelper.open();
                        mDbHelper.deleteLocation(id);
                        mDbHelper.close();

                        // delete cache file
                        File parentDirectory = new File(this.getFilesDir().getAbsolutePath());

                        if (parentDirectory.exists()) {
                            File cacheFile = null;

                            if (locId >= 0) {
                                cacheFile = new File(parentDirectory, "weather_cache_loc_" + locId);

                                if (cacheFile.exists()) {
                                    cacheFile.delete();
                                }

                                cacheFile = new File(parentDirectory, "forecast_cache_loc_" + locId);

                                if (cacheFile.exists()) {
                                    cacheFile.delete();
                                }
                            }
                        }

                        if (moveDefault) {
                            makeDefault(cursorAdapter.getItemId(0)); // make first default
                        }
                    }
                }
            }

            mDataProviderTask = new DataProviderTask();
            mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
            mDataProviderTask.execute();
        }
    }

    void makeDefault(long id) {
        mDbHelper.open();
        Cursor location = mDbHelper.fetchLocation(id);
        startManagingCursor(location);

        if (location != null && location.getCount() > 0) {

            location.moveToFirst();

            double lat = location.getDouble(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LATITUDE));
            double lon = location.getDouble(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LONGITUDE));
            String loc = location.getString(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_NAME));
            long locId = location.getLong(location.getColumnIndexOrThrow(SQLiteDbAdapter.KEY_LOCATION_ID));

            setLat(lat);
            setLon(lon);
            setLoc(loc);
            setLocationID(locId);
            setLocationType(LOCATION_TYPE_CUSTOM);
            getCheckCurrent().setChecked(false);
            getCheckCurrent().setText(getResources().getText(R.string.locationcurrent));

            Preferences.setLocationLat(this, getAppWidgetId(), (float) getLat());
            Preferences.setLocationLon(this, getAppWidgetId(), (float) getLon());
            Preferences.setLocation(this, getAppWidgetId(), getLoc());

            Preferences.setLocationId(this, getAppWidgetId(), getLocationID());
            Preferences.setLocationType(this, getAppWidgetId(), getLocationType());
        }

        stopManagingCursor(location);
        mDbHelper.close();

        //fillData();

        // Show the ProgressDialogFragment on this thread
        mProgressFragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", (String) getResources().getText(R.string.working));
        args.putString("message", (String) getResources().getText(R.string.retrieving));
        mProgressFragment.setArguments(args);
        mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

        // Start a new thread that will download all the data
        mDataProviderTask = new DataProviderTask();
        mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
        mDataProviderTask.execute();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(LAT, getLat());
        outState.putDouble(LON, getLon());
        outState.putString(LOC, getLoc());
        outState.putLong(ID, getLocationID());
        outState.putInt(LOCTYPE, getLocationType());
        outState.putInt(APPWIDGETID, mAppWidgetId);

        mSavedState = outState;

        mStateSaved = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);

        setLat(outState.getDouble(LAT));
        setLon(outState.getDouble(LON));
        setLoc(outState.getString(LOC));
        setLocationID(outState.getLong(ID));
        setLocationType(outState.getInt(LOCTYPE));
        mAppWidgetId = outState.getInt(APPWIDGETID);

        mSavedState = outState;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        mStateSaved = false;
    }

    DialogInterface.OnClickListener dialogLocationDisabledClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startLocationsActivity();
                    break;

                default:
                    showProgressDialog(false);
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dialogAddLocationClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,
                            int whichButton) {

            EditText input = ((AlertDialog) dialog).findViewById(R.id.text_id);

            if (input != null) {
                Editable value = input.getText();

                if (value != null && value.length() > 0) {
                    HttpTaskInfo info = new HttpTaskInfo();
                    info.cityName = value.toString();
                    info.latitude = Double.NaN;
                    info.longitude = Double.NaN;
                    info.appWidgetId = getAppWidgetId();

                    parseString = null;
                    list = null;
                    items = null;

                    mHttpTask = new HttpTask();
                    mHttpTask.setActivity(mWeakConfigureLocationActivity);
                    showProgressDialog(true);

                    try {
                        mHttpTask.execute(info);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            showProgressDialog(false);
        }
    };

    DialogInterface.OnKeyListener dialogKeyListener = new DialogInterface.OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                showProgressDialog(false);
            }

            return true;
        }
    };

    DialogInterface.OnClickListener dialogSelectLocationClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (!parseCustomLocation(item)) {
                Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
    };

    private void startLocationsActivity() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, ACTIVITY_LOCATION);
    }

    private void getLocation() {
        LocationProvider.LocationResult locationResult = new LocationProvider.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                showProgressDialog(false);

                if (location != null) {
                    setLat(location.getLatitude());
                    setLon(location.getLongitude());

                    // determine city name
                    startGeolocation(location);
                } else {
                    setLat(Double.NaN);
                    setLon(Double.NaN);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };

        LocationProvider loc = new LocationProvider();
        loc.getLocation(this, locationResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CREATE || requestCode == ACTIVITY_EDIT) {
            //fillData();

            // Show the ProgressDialogFragment on this thread
            mProgressFragment = new ProgressDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", (String) getResources().getText(R.string.working));
            args.putString("message", (String) getResources().getText(R.string.retrieving));
            mProgressFragment.setArguments(args);
            mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

            // Start a new thread that will download all the data
            mDataProviderTask = new DataProviderTask();
            mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
            mDataProviderTask.execute();

        } else {
            if (requestCode == ACTIVITY_LOCATION) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers;

                if (locationManager != null) {
                    providers = locationManager.getProviders(new Criteria(), true);

                    if (!providers.isEmpty()) {
                        getLocation();
                    } else {
                        showProgressDialog(false);
                    }
                } else {
                    showProgressDialog(false);
                }
            } else {
                if (requestCode == ACTIVITY_PERMISSION) {
                    if (resultCode == RESULT_OK) {
                        mCheckCurrent.setChecked(false);
                        mCheckCurrent.setText(getResources().getText(R.string.locationcurrent));

                        showProgressDialog(true);

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        List<String> providers;

                        if (locationManager != null) {
                            providers = locationManager.getProviders(new Criteria(), true);

                            if (!providers.isEmpty()) {
                                getLocation();
                            } else {
                                showLocationDisabledAlertDialog();
                            }
                        } else {
                            showLocationDisabledAlertDialog();
                        }
                    } else {
                        mCheckCurrent.setChecked(false);
                        mCheckCurrent.setText(getResources().getText(R.string.locationcurrent));
                    }
                }
            }

        }
    }

    public void checkCurrentLocationInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Intent permissionsIntent = new Intent(this, SetPermissionsActivity.class);
                permissionsIntent.putExtra(SetPermissionsActivity.PERMISSIONS_TYPE, SetPermissionsActivity.PERMISSIONS_REQUEST_LOCATION);
                startActivityForResult(permissionsIntent, ACTIVITY_PERMISSION);
            } else {
                showCurrentLocationInfo();
            }
        } else {
            showCurrentLocationInfo();
        }
    }

    public void showCurrentLocationInfo() {
        mCheckCurrent.setChecked(false);
        mCheckCurrent.setText(getResources().getText(R.string.locationcurrent));

        showProgressDialog(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers;

        if (locationManager != null) {
            providers = locationManager.getProviders(new Criteria(), true);

            if (!providers.isEmpty()) {
                getLocation();
            } else {
                showLocationDisabledAlertDialog();
            }
        } else {
            showLocationDisabledAlertDialog();
        }
    }

    public void showProgressDialog(boolean show) {
        if (show) {
            try {
                if (!mStateSaved) {
                    ProgressDialogFragment progressFragment = new ProgressDialogFragment();
                    progressFragment.setTask(mHttpTask);
                    Bundle args = new Bundle();
                    args.putString("title", getResources().getString(R.string.searchinglocations));
                    args.putString("message", getResources().getString(R.string.searching));
                    progressFragment.setArguments(args);
                    progressFragment.show(getSupportFragmentManager(), "tagProgress");
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ProgressDialogFragment progressFragment = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag("tagProgress");
                if (progressFragment != null) {
                    progressFragment.dismiss();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void showLocationDisabledAlertDialog() {
        try {
            if (!mStateSaved) {
                showProgressDialog(false);

                AlertDialogFragment disabledLocationsFragment = new AlertDialogFragment();
                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.locationsettings));
                args.putString("message", getResources().getString(R.string.locationssettingsdisabled));
                args.putBoolean("locationdisabled", true);
                disabledLocationsFragment.setArguments(args);
                disabledLocationsFragment.show(getSupportFragmentManager(), "tagAlert");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void showSelectLocationAlertDialog(CharSequence[] items) {
        try {
            if (!mStateSaved) {
                showProgressDialog(false);

                AlertDialogFragment selectLocationFragment = new AlertDialogFragment();
                Bundle args = new Bundle();
                args.putString("title", getResources().getString(R.string.selectlocation));
                args.putCharSequenceArray("items", items);
                selectLocationFragment.setArguments(args);
                selectLocationFragment.show(getSupportFragmentManager(), "tagChoice");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    private void startGeolocation(Location location) {
        HttpTaskInfo info = new HttpTaskInfo();

        info.cityName = "";
        info.latitude = getLat();
        info.longitude = getLon();
        info.appWidgetId = getAppWidgetId();

        parseString = null;
        list = null;
        items = null;

        mHttpTask = new HttpTask();
        mHttpTask.setActivity(mWeakConfigureLocationActivity);
        showProgressDialog(true);

        try {
            mHttpTask.execute(info);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean parseCustomLocations(int appWidgetId, String parseString) {
        Log.d(LOG_TAG, "ConfigureLocationActivity parseCityData appWidgetId: " + appWidgetId);

        showProgressDialog(false);

        if (parseString.equals("") || parseString.contains("<html>") || parseString.contains("failed to connect")) {
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        }

        JSONTokener parser = new JSONTokener(parseString);
        try {
            JSONObject query = (JSONObject) parser.nextValue();

            list = query.getJSONArray("list");

            if (list == null || list.length() == 0) {
                Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
                return false;
            }

            ArrayList<String> locations = new ArrayList<>();

            for (int i = 0; i < list.length(); i++) {
                JSONObject cityJSON = list.getJSONObject(i);

                int cityId = cityJSON.getInt("id");
                String name = cityJSON.getString("name");
                String country = "";

                JSONObject sys = null;
                try {
                    sys = cityJSON.getJSONObject("sys");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (sys != null)
                        country = sys.getString("country");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (name == null || country == null)
                    continue;

                locations.add(name + ", " + country);
            }

            items = new CharSequence[locations.size()];

            for (int i = 0; i < locations.size(); i++) {
                items[i] = locations.get(i);
            }

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private boolean parseCurrentLocation(int appWidgetId, String parseString) {
        Log.d(LOG_TAG, "ConfigureLocationActivity parseCityData appWidgetId: " + appWidgetId);

        showProgressDialog(false);

        if (TextUtils.isEmpty(parseString) || parseString.contains("<html>") || parseString.contains("failed to connect")) {
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        }

        parseString = parseString.trim();

        if (parseString.endsWith("\n"))
            parseString = parseString.substring(0, parseString.length() - 1);

        String start = parseString.substring(0, 1);
        String end = parseString.substring(parseString.length() - 1, parseString.length());

        if (!(start.equalsIgnoreCase("{") && end.equalsIgnoreCase("}"))
                && !(start.equalsIgnoreCase("[") && end.equalsIgnoreCase("]")))
            return false;

        JSONTokener parser = new JSONTokener(parseString);
        try {
            JSONObject query = (JSONObject) parser.nextValue();
            JSONObject weatherJSON;

            if (query.has("list")) {

                JSONArray list = query.getJSONArray("list");

                if (list.length() == 0) {
                    return false;
                }

                weatherJSON = list.getJSONObject(0);
            } else {
                weatherJSON = query;
            }

            int cityId = weatherJSON.getInt("id");
            String location = weatherJSON.getString("name");

            double lat = -222;
            double lon = -222;

            JSONObject coord = null;
            try {
                coord = weatherJSON.getJSONObject("coord");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (coord != null) {
                    lat = coord.getDouble("lat");
                    lon = coord.getDouble("lon");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setLat(lat);
            setLon(lon);
            setLocationID(-1);
            setLoc(location);
            setLocationType(LOCATION_TYPE_CURRENT);

            Preferences.setLocationLat(this, getAppWidgetId(), (float) getLat());
            Preferences.setLocationLon(this, getAppWidgetId(), (float) getLon());
            Preferences.setLocation(this, getAppWidgetId(), getLoc());

            getCheckCurrent().setChecked(true);
            CharSequence loc = getResources().getText(R.string.locationcurrent);
            loc = loc + " [" + Preferences.getLocation(this, getAppWidgetId()) + "]";
            getCheckCurrent().setText(loc);

            Preferences.setLocationId(this, getAppWidgetId(), getLocationID());
            Preferences.setLocationType(this, getAppWidgetId(), getLocationType());

            // Show the ProgressDialogFragment on this thread
            mProgressFragment = new ProgressDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", (String) getResources().getText(R.string.working));
            args.putString("message", (String) getResources().getText(R.string.retrieving));
            mProgressFragment.setArguments(args);
            mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

            // Start a new thread that will download all the data
            mDataProviderTask = new DataProviderTask();
            mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
            mDataProviderTask.execute();

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private boolean parseCustomLocation(int item) {
        int cityId;
        String name;
        double lat;
        double lon;

        if (list == null || list.length() < item)
            return false;

        try {
            JSONObject cityJSON = list.getJSONObject(item);

            cityId = cityJSON.getInt("id");
            name = cityJSON.getString("name");

            JSONObject coord;

            try {
                coord = cityJSON.getJSONObject("coord");
            } catch (JSONException e) {
                return false;
            }
            try {
                lat = coord.getDouble("lat");
                lon = coord.getDouble("lon");
            } catch (JSONException e) {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }

        mDbHelper.open();
        Cursor locationsCursor = mDbHelper.fetchAllLocations();

        boolean bFound = false;

        if (locationsCursor != null && locationsCursor.getCount() > 0) {
            locationsCursor.moveToFirst();

            do {
                long locId = locationsCursor.getLong(locationsCursor.getColumnIndex(SQLiteDbAdapter.KEY_LOCATION_ID));

                if (locId != -1 && cityId == locId) {
                    bFound = true;
                }

                locationsCursor.moveToNext();

            }
            while (!locationsCursor.isAfterLast());
        }

        if (!bFound && cityId >= 0) {
            long id = mDbHelper.createLocation(cityId, lat, lon, name);
        }

        mDbHelper.close();

        // Show the ProgressDialogFragment on this thread
        mProgressFragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", (String) getResources().getText(R.string.working));
        args.putString("message", (String) getResources().getText(R.string.retrieving));
        mProgressFragment.setArguments(args);
        mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

        // Start a new thread that will download all the data
        mDataProviderTask = new DataProviderTask();
        mDataProviderTask.setActivity(mWeakConfigureLocationActivity);
        mDataProviderTask.execute();

        return true;
    }

    void selectLocation(boolean current) {
        if (current) {
            if ((!parseCurrentLocation(getAppWidgetId(), parseString)) || (items != null && items.length <= 0)) {
                Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
            }
        } else {
            if ((!parseCustomLocations(getAppWidgetId(), parseString)) || (items != null && items.length <= 0)) {
                Toast.makeText(getDialogContext(), getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
                return;
            }

            showSelectLocationAlertDialog(items);
        }
    }

    /**
     * Custom location
     */

    public static class HttpTask extends AsyncTask<HttpTaskInfo, Void, OpenQuery> {

        WeakReference<ConfigureLocationActivity> mLocationActivity = null;
        ProgressDialogFragment mProgressFragment;
        int mProgress = 0;

        void setProgressFragment(ProgressDialogFragment fragment) {
            mProgressFragment = fragment;
        }

        void setActivity(WeakReference<ConfigureLocationActivity> activity) {
            mLocationActivity = activity;
        }

        WeakReference<ConfigureLocationActivity> getActivity() {
            return mLocationActivity;
        }

        @Override
        protected void onPreExecute() {

        }

        public OpenQuery doInBackground(HttpTaskInfo... info) {

            String cityName = info[0].cityName;
            int appWidgetId = info[0].appWidgetId;
            double latitude = info[0].latitude;
            double longitude = info[0].longitude;

            String lang = Preferences.getLanguageOptions(mLocationActivity.get().getDialogContext());

            if (lang.equals("")) {
                String langDef = Locale.getDefault().getLanguage();

                if (!langDef.equals(""))
                    lang = langDef;
                else
                    lang = "en";
            }

            OpenQuery openResult = null;

            try {
                Reader responseReader;
                HttpClient client = new DefaultHttpClient();
                HttpGet request;

                if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
                    request = new HttpGet(String.format(WidgetIntentDefinitions.WEATHER_SERVICE_COORD_URL, latitude, longitude, lang));

                } else if (!TextUtils.isEmpty(cityName)) {
                    cityName = cityName.replaceAll(" ", "%20");
                    request = new HttpGet(String.format(FIND_CITIES_URL, cityName, lang));
                } else {
                    return null;
                }

                HttpResponse response = client.execute(request);

                StatusLine status = response.getStatusLine();
                Log.d(LOG_TAG, "Request returned status " + status);

                HttpEntity entity = response.getEntity();
                responseReader = new InputStreamReader(entity.getContent());

                char[] buf = new char[1024];
                StringBuilder result = new StringBuilder();
                int read = responseReader.read(buf);

                while (read >= 0) {
                    result.append(buf, 0, read);
                    read = responseReader.read(buf);
                }

                openResult = new OpenQuery(result.toString(), TextUtils.isEmpty(cityName));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return openResult;
        }

        @Override
        protected void onPostExecute(OpenQuery found) {

            if (mLocationActivity == null || mLocationActivity.get().isFinishing()) {
                if (mProgressFragment != null)
                    mProgressFragment.taskFinished();

                return;
            }

            mLocationActivity.get().showProgressDialog(false);

            if (found != null) {
                mLocationActivity.get().parseString = found.httpResult;
                mLocationActivity.get().selectLocation(found.current);
            } else {
                mLocationActivity.get().setLat(Double.NaN);
                mLocationActivity.get().setLon(Double.NaN);
                mLocationActivity.get().setLocationID(-1);
                //mLocation = "";

                mLocationActivity.get().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mLocationActivity.get().getDialogContext(), mLocationActivity.get().getResources().getText(R.string.locationnotfound), Toast.LENGTH_LONG).show();
                    }
                });
            }

            if (mProgressFragment != null)
                mProgressFragment.taskFinished();
        }
    }

    class HttpTaskInfo {
        String cityName = "";
        double latitude = Double.NaN;
        double longitude = Double.NaN;
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    private static class OpenQuery {
        String httpResult;
        boolean current;

        OpenQuery(String query, boolean emptyCity) {
            httpResult = query;
            current = emptyCity;
        }
    }

    public void setConfigureResult(int resultCode) {
        final Intent data = new Intent();
        data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
        setResult(resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.setTitle(getResources().getString(R.string.locationsettings));
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double mLat) {
        this.mLat = mLat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double mLon) {
        this.mLon = mLon;
    }

    public void setLoc(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getLoc() {
        return mLocation;
    }

    public long getLocationID() {
        return mLocationID;
    }

    public void setLocationID(long mLocationID) {
        this.mLocationID = mLocationID;
    }

    public int getLocationType() {
        return mLocationType;
    }

    public void setLocationType(int mLocationType) {
        this.mLocationType = mLocationType;
    }

    public EditText getEditLocation() {
        return mEditLocation;
    }

    public void setEditLocation(EditText mEditLocation) {
        this.mEditLocation = mEditLocation;
    }

    public int getAppWidgetId() {
        return mAppWidgetId;
    }

    public void setAppWidgetId(int mAppWidgetId) {
        this.mAppWidgetId = mAppWidgetId;
    }

    /**
     * @return the mCheckCurrent
     */
    private CheckBox getCheckCurrent() {
        return mCheckCurrent;
    }

    /**
     * @param mCheckCurrent the mCheckCurrent to set
     */
    private void setCheckCurrent(CheckBox mCheckCurrent) {
        this.mCheckCurrent = mCheckCurrent;
    }
}
	