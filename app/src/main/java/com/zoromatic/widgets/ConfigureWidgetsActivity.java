package com.zoromatic.widgets;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ConfigureWidgetsActivity extends ThemeActionBarActivity {

	private static String LOG_TAG = "ConfigureWidgetsActivity";
	private Toolbar toolbar;
	ListView mListView;
	List<WidgetRowItem> widgetRowItems;
	private static final int WIDGET_SETTINGS = 0;

	private ProgressDialogFragment mProgressFragment = null;
	public boolean mActivityDelete = false;
	
	static DataProviderTask mDataProviderTask;
	static ConfigureWidgetsActivity mConfigureWidgetsActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
	
		mConfigureWidgetsActivity = this;

		setContentView(R.layout.configurewidget);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		}

		TypedValue outValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.colorPrimary,
				outValue,
				true);
		int primaryColor = outValue.resourceId;

		setStatusBarColor(findViewById(R.id.statusBarBackground), 
				getResources().getColor(primaryColor));
		
		// Show the ProgressDialog on this thread
		mProgressFragment = new ProgressDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", (String) getResources().getText(R.string.working));
		args.putString("message", (String) getResources().getText(R.string.retrieving));
		mProgressFragment.setArguments(args);
		mProgressFragment.show(getSupportFragmentManager(), "tagProgress");
		
		mListView = (ListView)findViewById(R.id.listWidgets);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				WidgetRowItem widgetRowItem = widgetRowItems.get(position);
				String className = widgetRowItem.getClassName();
				int appWidgetId = widgetRowItem.getAppWidgetId();

				if (className.equals(PowerAppWidgetProvider.class.getSimpleName())) {

					Intent intent = new Intent(getDialogContext(), PowerAppWidgetPreferenceActivity.class);

					if (intent != null) {
						intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);			
						startActivityForResult(intent, WIDGET_SETTINGS);
					}
				} else if (className.equals(DigitalClockAppWidgetProvider.class.getSimpleName())) {
					Intent intent = new Intent(getDialogContext(), DigitalClockAppWidgetPreferenceActivity.class);

					if (intent != null) {
						intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);			
						startActivityForResult(intent, WIDGET_SETTINGS);
					}
				}
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//return false;
				mActivityDelete = true;
				((WidgetItemAdapter)mListView.getAdapter()).setActivityDelete(mActivityDelete);
		        setListViewItems();
		        
		        mListView.setItemChecked(position, true);
		        
		        View tempView = getViewByPosition(position, mListView);
        		
        		if (tempView != null) {
	        		CheckBox checkBox = (CheckBox) tempView.findViewById(R.id.checkBoxSelect);
	        		
	        		if (checkBox != null) {
	        			checkBox.setChecked(true);
	        		}	        			 
				}
		        
		        return true;
			}
		});

		registerForContextMenu(mListView);

		// Start a new thread that will download all the data
		mDataProviderTask = new DataProviderTask();
		mDataProviderTask.setActivity(mConfigureWidgetsActivity);
		mDataProviderTask.execute(); 

		//fillData();
	}

	@SuppressLint("InlinedApi")
	public void setStatusBarColor(View statusBar,int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = getWindow();
			w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
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
	
	private class DataProviderTask extends AsyncTask<Void, Void, Void> {

		ConfigureWidgetsActivity configureWidgetsActivity = null;

		void setActivity(ConfigureWidgetsActivity activity) {
			configureWidgetsActivity = activity;
		}

		@SuppressWarnings("unused")
		ConfigureWidgetsActivity getActivity() {
			return configureWidgetsActivity;
		}

		@Override
		protected void onPreExecute() {

		}

		protected Void doInBackground(Void... params) {
			Log.i(LOG_TAG, "ConfigureWidgetsActivity - Background thread starting");

			configureWidgetsActivity.fillData();

			return null;
		}

		protected void onPostExecute(Void result) {

			if (configureWidgetsActivity.mProgressFragment != null) {
				configureWidgetsActivity.mProgressFragment.dismiss(); 
			}   
			
			configureWidgetsActivity.mActivityDelete = false;
			configureWidgetsActivity.setListViewItems();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == WIDGET_SETTINGS) {
			// Show the ProgressDialog on this thread
			mProgressFragment = new ProgressDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", (String) getResources().getText(R.string.working));
			args.putString("message", (String) getResources().getText(R.string.retrieving));
			mProgressFragment.setArguments(args);
			mProgressFragment.show(getSupportFragmentManager(), "tagProgress");

			// Start a new thread that will download all the data
			mDataProviderTask = new DataProviderTask();
			mDataProviderTask.setActivity(mConfigureWidgetsActivity);
			mDataProviderTask.execute();        	
		} 
	}

	private void fillData() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				widgetRowItems = new ArrayList<WidgetRowItem>();
				WidgetRowItem item = null;
		
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getDialogContext());
		
				ComponentName clockWidget = new ComponentName(getDialogContext(),
						DigitalClockAppWidgetProvider.class);
		
				if (clockWidget != null) {
					int[] appWidgetIds = appWidgetManager.getAppWidgetIds(clockWidget);
		
					if (appWidgetIds != null && appWidgetIds.length > 0) {
						for (int appWidgetId : appWidgetIds) {
							item = new WidgetRowItem(appWidgetId, DigitalClockAppWidgetProvider.class.getSimpleName());
							widgetRowItems.add(item);					
						}
					}
				}	
		
				ComponentName powerWidget = new ComponentName(getDialogContext(), PowerAppWidgetProvider.class);	
		
				if (powerWidget != null) {
					int[] appWidgetIds = appWidgetManager.getAppWidgetIds(powerWidget);
		
					if (appWidgetIds != null && appWidgetIds.length > 0) {
						for (int appWidgetId : appWidgetIds) {
							item = new WidgetRowItem(appWidgetId, PowerAppWidgetProvider.class.getSimpleName());
							widgetRowItems.add(item);
						}
					}
				}

		
				WidgetItemAdapter adapter = new WidgetItemAdapter(getDialogContext(), widgetRowItems, mActivityDelete);
				mListView.setAdapter(adapter);
			}
		});		
	}

	public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
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
		        	for (int i=0; i<mListView.getCount(); i++) {
		        		View view = getViewByPosition(i, mListView);
		        		
		        		if (view != null) {
		        			setListViewItem(view, i);
		        		}
		        	}
		        }
			}
    	});
    }
    
    private void setListViewItem(View view, int position) {
    	if (view == null)
    		return;
    	
    	WidgetRowItem widgetRowItem = (WidgetRowItem) mListView.getAdapter().getItem(position);
        
        String className = widgetRowItem.getClassName();
    	
    	ImageView image = (ImageView) view.findViewById(R.id.iconWidget);
		if (image != null) {
			image.setVisibility(mActivityDelete?View.GONE:View.VISIBLE);	
			
			final Resources res = getDialogContext().getResources();
		    final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);

		    final LetterTileProvider tileProvider = new LetterTileProvider(getDialogContext());
		    final Bitmap letterTile = tileProvider.getLetterTile(className.substring(0, 1), className.substring(0, 1), tileSize, tileSize);
		    
		    image.setImageBitmap(letterTile);								 		    
		}
		
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxSelect);
		if (checkBox != null) {
			checkBox.setVisibility(mActivityDelete?View.VISIBLE:View.GONE);
			
			if (!mActivityDelete)
				checkBox.setChecked(false);
		}
    }
    
    @Override
    public void onBackPressed() {
    	if (mActivityDelete) {
    		mActivityDelete = false;
    		((WidgetItemAdapter)mListView.getAdapter()).setActivityDelete(mActivityDelete);
    		setListViewItems();
    	}
    	else {
	    	super.onBackPressed();
    	}              
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.delete).setVisible(mActivityDelete);
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.widgetsmenu, menu);
	    return true;        
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
	        onBackPressed();
			return true;
		case R.id.delete:
            deleteWidget();
            return true;
	    default:
	    	return super.onOptionsItemSelected(item);	         
		}
	}

	private void deleteWidget() {
		boolean bChecked = false;
		if (mListView != null && mListView.getCount() > 0) {
			for (int i=mListView.getCount()-1; i>=0; i--) {
        		View tempView = getViewByPosition(i, mListView);
        		
        		if (tempView != null) {
	        		CheckBox checkBox = (CheckBox) tempView.findViewById(R.id.checkBoxSelect);
	        		
	        		if (checkBox != null && checkBox.isChecked()) {
	        			bChecked = true;
	        			break;
	        		}
        		}
        	}					
		}
		
		if (bChecked) {
			AlertDialogFragment	disabledLocationsFragment = new AlertDialogFragment();
    		Bundle args = new Bundle();
	        args.putString("title", getResources().getString(R.string.configurewidgets));
	        args.putString("message", getResources().getString(R.string.deletewidgetwarning));
	        args.putBoolean("deletewidgets", true);
	        disabledLocationsFragment.setArguments(args);
	        disabledLocationsFragment.show(getSupportFragmentManager(), "tagAlert");
		}
	}
	
	private void deleteWidgets() {
		if (mListView != null && mListView.getCount() > 0) {
			for (int i=mListView.getCount()-1; i>=0; i--) {
        		View tempView = getViewByPosition(i, mListView);
        		
        		if (tempView != null) {
	        		CheckBox checkBox = (CheckBox) tempView.findViewById(R.id.checkBoxSelect);
	        		
	        		if (checkBox != null && checkBox.isChecked()) {
	        			WidgetRowItem widgetRowItem = widgetRowItems.get(i);
	    				int appWidgetId = widgetRowItem.getAppWidgetId();
	    				
	    				AppWidgetHost host = new AppWidgetHost(this, 0);
						host.deleteAppWidgetId(appWidgetId);
						widgetRowItems.remove(i);	    				
	        		}
        		}
        	}
			
			mDataProviderTask = new DataProviderTask();
            mDataProviderTask.setActivity(mConfigureWidgetsActivity);
            mDataProviderTask.execute();
		}
	}
	
	DialogInterface.OnClickListener dialogDeleteWidgetClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                deleteWidgets();
                break;

            default:
                break;
            }
        }
    };
    
    DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
				
		}
	};
	
	 DialogInterface.OnKeyListener dialogKeyListener = new DialogInterface.OnKeyListener() {
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				dialog.dismiss();			
            }
			
			return true;
		}
	};
	
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
}
