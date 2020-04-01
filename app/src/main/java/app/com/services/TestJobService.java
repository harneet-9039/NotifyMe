package app.com.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.common.startServiceUtility;
import app.com.models.NotificationModel;

public class TestJobService extends JobService {
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    boolean isWorking = false;
    boolean jobCancelled = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("HAR", "Job started!");
        isWorking = true;
        callAPIThread(params);
        startServiceUtility.scheduleJob(getApplicationContext()); // reschedule the job
        return isWorking;
    }

    private void callAPI(final JobParameters parameters){
        SharedPreferences sharedPreferences = getSharedPreferences("UserVals",
                0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        pref = getSharedPreferences("UserVals", 0);
        String URL_FINAL = GlobalMethods.getURL()+"fetchNotification";
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL_FINAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HAR",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = (int) jsonObject.get("code");
                    if(code==200){
                        ArrayList<NotificationModel> notificationModels = new ArrayList<>();
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        int statusCount=0;
                        if(dataArray.length()>0){
                            for(int i=0;i<dataArray.length();i++){
                                NotificationModel notificationModel = new NotificationModel();
                                JSONObject dataobj = dataArray.getJSONObject(i);
                                notificationModel.setTitle(dataobj.getString("title"));
                                notificationModel.setStatus(dataobj.getInt("status"));
                                if(dataobj.getInt("status")==0){
                                    statusCount++;
                                }
                                String updatedDate = dataobj.getString("date").substring(0,dataobj.getString("date").indexOf("T"));
                                String[] parts = updatedDate.split("-");
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(parts[2]+"-"+parts[1]+"-"+parts[0]);
                                notificationModel.setDate(String.valueOf(stringBuilder));
                                notificationModels.add(notificationModel);
                            }
                            editor.putInt("notificationStatusCount",statusCount);
                            Gson gson = new Gson();
                            editor.putString("notificationlist",gson.toJson(notificationModels));
                            editor.commit();
                            sendBroadcast(parameters);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                if(pref.getString("registrationNumber","")!="") {
                    parameters.put("studentID", pref.getString("registrationNumber", ""));
                }
                else{
                    parameters.put("facultyID", pref.getString("fregistrationNumber", ""));
                }

                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void sendBroadcast(JobParameters parameters) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent("notification");
        localBroadcastManager.sendBroadcast(intent);
        Log.d("HAR", "Job finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(parameters, needsReschedule);
    }


    private void callAPIThread(final JobParameters parameters) {
        new Thread(new Runnable() {
            public void run() {
                callAPI(parameters);
            }
        }).start();
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("HAR", "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(params, needsReschedule);
        return needsReschedule;
    }
}
