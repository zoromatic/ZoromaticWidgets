package com.zoromatic.widgets;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.alertdialogpro.AlertDialogPro;
import com.zoromatic.widgets.R;

public class BrightnessActivity extends ThemeActivity {

	private static String LOG_TAG = "BrightnessActivity";

	private int      brightnessValue;
	private boolean  isAuto;
	private SeekBar  seekBarBrightness;
	private CheckBox checkAuto;
	//private Button buttonSave;
	//private Button buttonCancel;

	public int mOldBrightness = MAXIMUM_BACKLIGHT;
	public int mOldAutomatic  = 0;

	private static       int mScreenBrightnessDim = 20 + 10;
	private static final int MAXIMUM_BACKLIGHT    = 255;
	private static final int SEEK_BAR_RANGE       = MAXIMUM_BACKLIGHT - mScreenBrightnessDim;

	static DataProviderTask   mDataProviderTask;
	static BrightnessActivity mBrightnessActivity;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		mBrightnessActivity = this;

		getWindow().setBackgroundDrawable( new ColorDrawable( 0 ) );
		displayDialog();
	}

	private void displayDialog()
	{
		LayoutInflater li             = LayoutInflater.from( getDialogContext() );
		View           brightnessView = li.inflate( R.layout.brightnessdialog, null );

		String theme = Preferences.getMainTheme( getDialogContext() );

		AlertDialogPro.Builder alertDialogBuilder = new AlertDialogPro.Builder( getDialogContext(),
		                                                                        theme.compareToIgnoreCase( "light" ) == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material );

		alertDialogBuilder.setView( brightnessView );

		seekBarBrightness = ( SeekBar ) brightnessView.findViewById( R.id.seekBarBrightness );

		seekBarBrightness
				.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
				{
					@Override
					public void onStopTrackingTouch( SeekBar seekBar )
					{
						//saveBrightness(getContentResolver(), brightnessValue);
						mDataProviderTask = new DataProviderTask();
						mDataProviderTask.setActivity(mBrightnessActivity);
						mDataProviderTask.execute(); 
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						setBrightnessValue(progress + mScreenBrightnessDim);
						setBrightness(getBrightnessValue());
					}
				});

		seekBarBrightness.setMax(SEEK_BAR_RANGE);

		try {
			mOldBrightness = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException snfe) {
			mOldBrightness = MAXIMUM_BACKLIGHT;
		}
		
		setBrightnessValue(mOldBrightness);

		seekBarBrightness.setProgress(mOldBrightness - mScreenBrightnessDim);

		checkAuto = (CheckBox) brightnessView.findViewById(R.id.checkBoxBrightness);

		checkAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					startAutoBrightness();

					seekBarBrightness.setVisibility(View.GONE);
				} else {
					stopAutoBrightness();

					seekBarBrightness.setVisibility(View.VISIBLE);
					setBrightness(seekBarBrightness.getProgress()
							+ mScreenBrightnessDim);
				}
			}
		});

		try {
			mOldAutomatic = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException snfe) {
			mOldAutomatic = 0;
		}

		checkAuto.setChecked(mOldAutomatic != 0);

		isAuto = isAutoBrightness(getContentResolver());

		if (isAuto) {
			// stopAutoBrightness();

			seekBarBrightness.setVisibility(View.GONE);
		}
		
		alertDialogBuilder
		.setTitle(getResources().getString(R.string.brightnessdesc))
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateWidget();
				
				dialog.dismiss();
				finish();
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
			}
		})
		.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                	onBackPressed();
                }
                
                return true;
            }
        });
		
		AlertDialogPro dialog = alertDialogBuilder.show();
		dialog.setCanceledOnTouchOutside(false);
        
        if (dialog != null) {
        	final Resources res = getResources();
        	TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark,
                    outValue,
                    true);
            int primaryColor = outValue.resourceId;
            
        	int color = res.getColor(primaryColor);
        	
    		// Title
            final int titleId = res.getIdentifier("alertTitle", "id", "android");
            final View titleView = dialog.findViewById(titleId);
            if (titleView != null) {
                ((TextView) titleView).setTextColor(color);
            }

            // Title divider
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = dialog.findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(color);
            }
    	}
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
	
	private class DataProviderTask extends AsyncTask<Void, Void, Void> {

		BrightnessActivity brightnessActivity = null;

		void setActivity(BrightnessActivity activity) {
			brightnessActivity = activity;
		}

		@SuppressWarnings("unused")
		BrightnessActivity getActivity() {
			return brightnessActivity;
		}

		@Override
		protected void onPreExecute() {

		}

		protected Void doInBackground(Void... params) {
			Log.i(LOG_TAG, "BrightnessActivity - Background thread starting");

			brightnessActivity.saveBrightness(getContentResolver(), brightnessActivity.getBrightnessValue());

			return null;
		}

		protected void onPostExecute(Void result) {
			
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (mOldAutomatic == 0) {
			stopAutoBrightness();
		} else {
			startAutoBrightness();
		}

		saveBrightness(getContentResolver(), mOldBrightness);
		
		updateWidget();
		
		setResult(RESULT_CANCELED);
		
		finish();		
	}
	
	void updateWidget() {
		Intent startIntent = new Intent(this, WidgetUpdateService.class);
        startIntent.putExtra(WidgetInfoReceiver.INTENT_EXTRA, WidgetUpdateService.UPDATE_SINGLE_BRIGHTNESS_WIDGET);

        startService(startIntent);
	}

	public boolean isAutoBrightness(ContentResolver aContentResolver) {
		boolean autoBrightness = false;
		try {
			autoBrightness = Settings.System.getInt(aContentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return autoBrightness;
	}

	public void setBrightness(int bright) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(bright) * (1f / 255f);
		getWindow().setAttributes(lp);
	}

	public void startAutoBrightness() {
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}

	public void stopAutoBrightness() {
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	public void saveBrightness(ContentResolver resolver, int bright) {
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness",
				bright);
		resolver.notifyChange(uri, null);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        this.setTitle(getResources().getString(R.string.brightnessdesc));
    }

	private int getBrightnessValue() {
		return brightnessValue;
	}

	private void setBrightnessValue(int brightnessValue) {
		this.brightnessValue = brightnessValue;
	}
}