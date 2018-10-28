package com.zoromatic.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String message = "BootReceiver onReceive";
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            BootJobIntentService.enqueueWork(context, new Intent());
        }*/
    }
}
