package com.zoromatic.widgets;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BatteryInfoActivity extends ThemeActionBarActivity {
	
	private static final int ACTIVITY_SETTINGS=0;
	//private static ListView listViewInfo;
	//private List<Map<String, String>> batteryInfoList = null;
	List<RowItem> rowItems;
	SimpleAdapter simpleAdapter; 
	private BroadcastReceiver mReceiver;
	private Toolbar mToolbar;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);               
        
        setContentView(R.layout.batteryinfo);
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(mToolbar);
	    
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
	    
        //batteryInfoList = new ArrayList<Map<String,String>>();
//        listViewInfo = (ListView) findViewById(R.id.list);
//        
//        listViewInfo.setOnItemClickListener(new OnItemClickListener() {
//        	@Override
//        	public void onItemClick(AdapterView<?> parent, View view,
//        			int position, long id) {
//
//        		if (position == 5) { // start Android system battery info screen
//        			Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
//        			ResolveInfo resolveInfo = getPackageManager().resolveActivity(
//        					powerUsageIntent, PackageManager.MATCH_DEFAULT_ONLY);
//
//        			if (resolveInfo != null) {
//        				startActivityForResult(powerUsageIntent, ACTIVITY_SETTINGS);
//        			}
//        		}
//        		
//        		if (position == 6) { // refresh
//        			initList();
//        		}
//        	}			
//        }); 
        
        initList();		                       
    }
	
	private void initList() {

		//if (batteryInfoList == null)
		//	return;		
        
        Intent batteryIntent = getApplicationContext().registerReceiver(
				null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
		int rawlevel = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
				-1);
		int status = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_STATUS,
				BatteryManager.BATTERY_STATUS_UNKNOWN);
		
		int health = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_HEALTH, -1);
		
		int plugged = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_PLUGGED, -1);
		
		String technology = batteryIntent.getStringExtra(
				BatteryManager.EXTRA_TECHNOLOGY);
		
		int temp = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_TEMPERATURE, -1);
		
		int voltage = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_VOLTAGE, -1);
		
		String statusDesc = "";
		String healthDesc = "", pluggedDesc = "";
		int level = -1;
		int statusIcon = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_ICON_SMALL, -1);
		int percentIcon = batteryIntent.getIntExtra(
				BatteryManager.EXTRA_ICON_SMALL, -1);
		
		if (rawlevel >= 0 && scale > 0) {
			level = (rawlevel * 100) / scale;
		}
				
		switch (status) {
		
		case (BatteryManager.BATTERY_STATUS_UNKNOWN):
			statusDesc = (String) getResources().getText(R.string.batteryunknown);
			//statusIcon = R.drawable.battery_000;
			percentIcon = R.drawable.battery_font_000;
			break;
		case (BatteryManager.BATTERY_STATUS_FULL):
			statusDesc = (String) getResources().getText(R.string.batteryfull);
			//statusIcon = R.drawable.battery_full;
			percentIcon = R.drawable.battery_font_full;
			break;
		case (BatteryManager.BATTERY_STATUS_CHARGING):			
			statusDesc = (String) getResources().getText(R.string.batterycharging);
			//statusIcon = R.drawable.battery_charge_000 + level;
			percentIcon = R.drawable.battery_font_charge_000 + level;
			break;
		case (BatteryManager.BATTERY_STATUS_DISCHARGING):
		case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
			statusDesc = (String) getResources().getText(R.string.batterydisharging);
			//statusIcon = R.drawable.battery_000 + level;
			percentIcon = R.drawable.battery_font_000 + level;
			break;
		default:
			statusDesc = (String) getResources().getText(R.string.batteryunknown);		
			//statusIcon = R.drawable.battery_000;
			percentIcon = R.drawable.battery_font_000;
		}
		
		switch (health) {
		case 7://BatteryManager.BATTERY_HEALTH_COLD:
			healthDesc = (String) getResources().getText(R.string.batterycold);
			break;
		case BatteryManager.BATTERY_HEALTH_DEAD:
			healthDesc = (String) getResources().getText(R.string.batterydead);
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			healthDesc = (String) getResources().getText(R.string.batterygood);
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			healthDesc = (String) getResources().getText(R.string.batteryoverheat);
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			healthDesc = (String) getResources().getText(R.string.batteryovervoltage);
			break;			
		case BatteryManager.BATTERY_HEALTH_UNKNOWN:
			healthDesc = (String) getResources().getText(R.string.batteryunknown);
			break;		
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
			healthDesc = (String) getResources().getText(R.string.batteryunspecified);
			break;
		default:
			break;
		}
		
		switch (plugged) {
		case 0:
			pluggedDesc = (String) getResources().getText(R.string.batterynotplugged);
			break;
		case BatteryManager.BATTERY_PLUGGED_AC:
			pluggedDesc = (String) getResources().getText(R.string.batteryac);
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:
			pluggedDesc = (String) getResources().getText(R.string.batteryusb);
			break;
		case 17: //BatteryManager.BATTERY_PLUGGED_WIRELESS:
			pluggedDesc = (String) getResources().getText(R.string.batterywireless);
			break;
		default:
			break;
		}
		
		Object mPowerProfile_ = null;
		double batteryCapacity = 0.f;

	    final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

	    try {
	        mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
	                .getConstructor(Context.class).newInstance(this);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 

	    try {
	        batteryCapacity = (Double) Class
	                .forName(POWER_PROFILE_CLASS)
	                .getMethod("getAveragePower", java.lang.String.class)
	                .invoke(mPowerProfile_, "battery.capacity");	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

		String theme = Preferences.getMainTheme(this);
		
		rowItems = new ArrayList<RowItem>();
		
		RowItem item = new RowItem(statusIcon, (String) getResources().getText(R.string.batterycurrentstatus) + " " + statusDesc, false);
        rowItems.add(item);
        
        item = new RowItem(percentIcon, (String) getResources().getText(R.string.batterylevel) + " " + level + "%", false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.temperature_black:R.drawable.temperature_white, (String) getResources().getText(R.string.batterytemperature) + " " + (float)temp/10.0 + "°C", false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.health_black:R.drawable.health_white, (String) getResources().getText(R.string.batteryhealth) + " " + healthDesc, false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.capacity_black:R.drawable.capacity_white, (String) getResources().getText(R.string.batterycapacity) + " " + batteryCapacity + " mAh", false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.voltage_black:R.drawable.voltage_white, (String) getResources().getText(R.string.batteryvoltage) + " " + (float)voltage/1000.0 + " V", false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.plug_black:R.drawable.plug_white, (String) getResources().getText(R.string.batterypluggedto) + " " + pluggedDesc, false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.ic_settings_black_48dp:R.drawable.ic_settings_white_48dp, (String) getResources().getText(R.string.batterytechnology) + " " + technology, false);
        rowItems.add(item);
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.consumption_black:R.drawable.consumption_white, (String) getResources().getText(R.string.batterysysteminfo), true);
        rowItems.add(item);
        
        final int info = rowItems.size()-1; 
        
        item = new RowItem(theme.compareToIgnoreCase("light") == 0?R.drawable.ic_refresh_black_48dp:R.drawable.ic_refresh_white_48dp, (String) getResources().getText(R.string.batteryrefresh), false);
        rowItems.add(item);
        
        final int refresh = rowItems.size()-1;
        
		/*batteryInfoList.clear();
		
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterycurrentstatus) + " " + statusDesc));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterylevel) + " " + level + "%"));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterytemperature) + " " + (float)temp/10.0 + "°C"));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batteryhealth) + " " + healthDesc));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batteryvoltage) + " " + (float)voltage/1000.0 + " V"));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterypluggedto) + " " + pluggedDesc));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterytechnology) + " " + technology));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batterysysteminfo)));
		batteryInfoList.add(createBatteryInfo("battery", (String) getResources().getText(R.string.batteryrefresh)));*/
		
		//simpleAdapter = new SimpleAdapter(this, batteryInfoList, android.R.layout.simple_list_item_1, new String[] {"battery"}, new int[] {android.R.id.text1});
		//listViewInfo.setAdapter(simpleAdapter);
		//simpleAdapter = new SimpleAdapter(this, batteryInfoList, R.layout.batteryinforow, new String[] {"battery"}, new int[] {R.id.batterylabel});
        ItemAdapter adapter = new ItemAdapter(this, rowItems);
		//setListAdapter(adapter);
		
		ListView list = (ListView) findViewById(R.id.battery_list);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
	        @Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {		
	        	if (position == info) { // start Android system battery info screen
	    			Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
	    			ResolveInfo resolveInfo = getPackageManager().resolveActivity(
	    					powerUsageIntent, PackageManager.MATCH_DEFAULT_ONLY);

	    			if (resolveInfo != null) {
	    				startActivityForResult(powerUsageIntent, ACTIVITY_SETTINGS);
	    			}
	    		}
	    		
	    		if (position == refresh) { // refresh
	    			initList();
	    		} 
			}
	    });
	}
	
	@SuppressLint("InlinedApi")
	public void setStatusBarColor(View statusBar,int color){
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
    

//	private HashMap<String, String> createBatteryInfo(String key, String name) {
//	    HashMap<String, String> info = new HashMap<String, String>();
//	    info.put(key, name);
//
//	    return info;
//	}
	
	@Override
    protected void onResume() {
        super.onResume();    
        
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();           	
            }			
        };
        this.registerReceiver(mReceiver, intentFilter);
        
        this.setTitle(getResources().getString(R.string.batteryinfo));
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.batteryinfomenu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.refresh:
	    	initList();
	    	return true;
	    case R.id.settings:
	    	Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
			ResolveInfo resolveInfo = getPackageManager().resolveActivity(
					powerUsageIntent, PackageManager.MATCH_DEFAULT_ONLY);
			
			if (resolveInfo != null) {
				startActivityForResult(powerUsageIntent, ACTIVITY_SETTINGS);
			}
	    	return true;
	    case android.R.id.home:
	        onBackPressed();
			//finish();
	        return true;
	    default:
	    	return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
             
    }

	/*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);        
    	
    	if (position == 7) { // start Android system battery info screen
			Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
			ResolveInfo resolveInfo = getPackageManager().resolveActivity(
					powerUsageIntent, PackageManager.MATCH_DEFAULT_ONLY);

			if (resolveInfo != null) {
				startActivityForResult(powerUsageIntent, ACTIVITY_SETTINGS);
			}
		}
		
		if (position == 8) { // refresh
			initList();
		}      
    }*/
}
