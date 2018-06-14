package com.zoromatic.widgets;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WidgetUpdateJobService extends JobService {
    @Override
    public boolean onStartJob(final JobParameters params) {

        HandlerThread handlerThread = new HandlerThread("SomeOtherThread");

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // do some stuff, or not
                jobFinished(params, true); // see this, we are saying we just finished the job
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        return false;
    }
}
