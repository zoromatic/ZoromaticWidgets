package com.zoromatic.widgets;

import com.zoromatic.widgets.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZoromaticWidgetsActivity extends ThemeActivity {

	static final String LOG_TAG = "ZoromaticWidgetsActivity";
	    
    @SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              
        
        setContentView(R.layout.main);      
        
        Intent settingsIntent = new Intent(getApplicationContext(), ZoromaticWidgetsPreferenceActivity.class);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	settingsIntent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT, ZoromaticWidgetsPreferenceFragment.class.getName() );
        	settingsIntent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        }
		startActivity(settingsIntent);
		
		finish();
    }                          
}