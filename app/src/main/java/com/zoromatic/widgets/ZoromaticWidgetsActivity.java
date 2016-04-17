package com.zoromatic.widgets;

import com.zoromatic.widgets.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class ZoromaticWidgetsActivity extends ThemeActivity {

    static final String LOG_TAG = "ZoromaticWidgetsActivity";
    private static final int PERMISSIONS_REQUEST = 0;

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permissions and open request if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    //Toast.makeText( getApplicationContext(), getResources().getString( R.string.permission_needed ), Toast.LENGTH_LONG ).show();

                    new Thread() {
                        @Override
                        public void run() {
                            ZoromaticWidgetsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ExplanationDialogFragment question = new ExplanationDialogFragment();
                                    question.show(getSupportFragmentManager(), "ExplanationDialogFragment");
                                }
                            });
                        }
                    }.start();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST);

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
        setContentView(R.layout.main);

        Intent settingsIntent = new Intent(getApplicationContext(), ZoromaticWidgetsPreferenceActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, ZoromaticWidgetsPreferenceFragment.class.getName());
            settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        }
        startActivity(settingsIntent);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) // phone
                {
                    // permission granted
                    initializeActivity();
                } else {
                    // permission denied
                    finish();
                }

                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static class ExplanationDialogFragment extends DialogFragment {
        Context mContext;

        public ExplanationDialogFragment() {
            mContext = getActivity();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mContext = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.permission_needed))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    PERMISSIONS_REQUEST);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            ((ZoromaticWidgetsActivity) mContext).finish();
                        }
                    });

            return builder.create();
        }
    }
}