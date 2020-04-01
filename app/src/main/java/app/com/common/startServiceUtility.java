package app.com.common;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import app.com.services.TestJobService;

public class startServiceUtility {
    // schedule the start of the service every 10 - 30 seconds
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        //builder.setMinimumLatency(1 * 5000); // wait at least
        //builder.setOverrideDeadline(10 * 1000); // maximum delay
        //builder.setPeriodic(5000);
               builder.setPersisted(true);
                builder.setBackoffCriteria(6000, JobInfo.BACKOFF_POLICY_LINEAR);
                builder.setMinimumLatency(1000 * 4);
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        int result = jobScheduler.schedule(builder.build());
        if(result==JobScheduler.RESULT_SUCCESS){
            Log.d("HAR","success job");
        }
    }
}
