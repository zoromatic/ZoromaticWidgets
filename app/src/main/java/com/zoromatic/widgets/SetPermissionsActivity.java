package com.zoromatic.widgets;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SetPermissionsActivity extends AppCompatActivity {
    public static final String PERMISSIONS_TYPE = "PERMISSIONS_TYPE";
    public static final int PERMISSIONS_REQUEST_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private int mPermissionType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_permissions);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mPermissionType = extras.getInt(PERMISSIONS_TYPE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check permissions and open request if not granted
            if (mPermissionType == PERMISSIONS_REQUEST_LOCATION) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        new Thread() {
                            @Override
                            public void run() {
                                SetPermissionsActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ExplanationDialogFragment question = new ExplanationDialogFragment();
                                        question.setPermissionType(mPermissionType);
                                        question.show(getSupportFragmentManager(), "ExplanationDialogFragment");
                                    }
                                });
                            }
                        }.start();
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_LOCATION);

                        // PERMISSIONS_REQUEST_LOCATION is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    initializeActivity();
                }
            } else {
                if (mPermissionType == PERMISSIONS_REQUEST_CAMERA) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            new Thread() {
                                @Override
                                public void run() {
                                    SetPermissionsActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ExplanationDialogFragment question = new ExplanationDialogFragment();
                                            question.setPermissionType(mPermissionType);
                                            question.show(getSupportFragmentManager(), "ExplanationDialogFragment");
                                        }
                                    });
                                }
                            }.start();
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CAMERA},
                                    PERMISSIONS_REQUEST_CAMERA);

                            // PERMISSIONS_REQUEST_CAMERA is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        initializeActivity();
                    }
                }
            }
        } else {
            initializeActivity();
        }
    }

    private void initializeActivity() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    initializeActivity();
                } else {
                    // permission denied
                    setResult(RESULT_CANCELED);
                    finish();
                }

                break;
            }
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    initializeActivity();
                } else {
                    // permission denied
                    setResult(RESULT_CANCELED);
                    finish();
                }

                break;
            }
            default: {
                setResult(RESULT_CANCELED);
                finish();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public int getPermissionType() {
        return mPermissionType;
    }

    public void setPermissionType(int mPermissionGroup) {
        this.mPermissionType = mPermissionGroup;
    }


    public static class ExplanationDialogFragment extends DialogFragment {
        Context mContext;
        private int mPermissionType = -1;

        public ExplanationDialogFragment() {
            mContext = getActivity();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mContext = getActivity();
            String theme = Preferences.getMainTheme(getContext());

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
                    theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle);

            if (mPermissionType == PERMISSIONS_REQUEST_LOCATION) {
                builder
                        .setTitle(mContext.getResources().getString(R.string.permission_needed_location))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                ((SetPermissionsActivity) mContext).finish();
                            }
                        });
            } else {
                if (mPermissionType == PERMISSIONS_REQUEST_CAMERA) {
                    builder
                            .setTitle(mContext.getResources().getString(R.string.permission_needed_camera))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            PERMISSIONS_REQUEST_CAMERA);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    ((SetPermissionsActivity) mContext).finish();
                                }
                            });
                }
            }

            return builder.create();
        }

        public int getPermissionType() {
            return mPermissionType;
        }

        public void setPermissionType(int mPermissionType) {
            this.mPermissionType = mPermissionType;
        }
    }
}
