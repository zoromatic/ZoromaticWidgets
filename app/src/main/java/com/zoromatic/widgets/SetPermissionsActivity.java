package com.zoromatic.widgets;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class SetPermissionsActivity extends AppCompatActivity
{
	private static final int PERMISSIONS_REQUEST   = 0;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_set_permissions );

		if (  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
			// Check permissions and open request if not granted
			if ( ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_SETTINGS ) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
				// Should we show an explanation?
				if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.WRITE_SETTINGS ) ||
						ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_COARSE_LOCATION ) ||
						ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) ) {
					// Show an explanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.
					//Toast.makeText( getApplicationContext(), getResources().getString( R.string.permission_needed ), Toast.LENGTH_LONG ).show();

					new Thread()
					{
						@Override
						public void run()
						{
							SetPermissionsActivity.this.runOnUiThread( new Runnable()
							{
								@Override
								public void run()
								{
									ExplanationDialogFragment question = new ExplanationDialogFragment();
									question.show( getSupportFragmentManager(), "ExplanationDialogFragment" );
								}
							} );
						}
					}.start();
				} else {
					// No explanation needed, we can request the permission.
					ActivityCompat.requestPermissions( this,
					                                   new String[]{ Manifest.permission.WRITE_SETTINGS, Manifest.permission.ACCESS_COARSE_LOCATION,
							                                   Manifest.permission.ACCESS_FINE_LOCATION },
					                                   PERMISSIONS_REQUEST );

					// PERMISSIONS_REQUEST is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			} else {
				initializeActivity();
			}
		} else {
			initializeActivity();
		}
	}

	private void initializeActivity() {
		setResult( RESULT_OK );
		finish();
	}

	@Override
	public void onRequestPermissionsResult( int requestCode, @NonNull String permissions[], @NonNull int[] grantResults ) {
		switch ( requestCode ) {
			case PERMISSIONS_REQUEST:
			{
				// If request is cancelled, the result arrays are empty.
				if ( grantResults.length > 2
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
						&& grantResults[2] == PackageManager.PERMISSION_GRANTED ) {
					// permission granted
					initializeActivity();
				} else {
					// permission denied
					setResult( RESULT_CANCELED );
					finish();
				}

				break;
			}
			default:
			{
				setResult( RESULT_CANCELED );
				finish();
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}


	public static class ExplanationDialogFragment extends DialogFragment
	{
		Context mContext;

		public ExplanationDialogFragment()
		{
			mContext = getActivity();
		}

		@NonNull
		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState )
		{
			mContext = getActivity();

			AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
			builder
					.setTitle( mContext.getResources().getString( R.string.permission_needed ) )
					.setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener()
					{
						public void onClick( DialogInterface dialog, int id )
						{
							dialog.dismiss();

							ActivityCompat.requestPermissions( getActivity(),
							                                   new String[]{ Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
									                                   Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO,
									                                   Manifest.permission.MODIFY_AUDIO_SETTINGS },
							                                   PERMISSIONS_REQUEST );
						}
					} )
					.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener()
					{
						public void onClick( DialogInterface dialog, int id )
						{
							dialog.dismiss();
							( ( SetPermissionsActivity ) mContext ).finish();
						}
					} );

			return builder.create();
		}
	}
}
