package app.com.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.com.NotificationDrawable;
import app.com.notifyme.LoginActivity;
import app.com.notifyme.R;

public class GlobalMethods {
    static String URL = "https://app--notifyme.herokuapp.com/";
    //static String URL = "http://192.168.43.137:3000/";
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;



    public static String GetSubString(String res)
    {
        return res.substring(13,16);

    }
    public static void print(Context context, String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static String getURL()
    {
        return URL;
    }

    public static void setCountForNotifcation(LayerDrawable icon, String count, Context ctx){
        NotificationDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof NotificationDrawable) {
            badge = (NotificationDrawable) reuse;
        } else {
            badge = new NotificationDrawable(ctx);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    public static void logout(final Context ctx, final View v){
        ProgressDialog progressDialog = new ProgressDialog(ctx);
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("UserVals",
                0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        pref = ctx.getSharedPreferences("UserVals", 0); // 0 - for private mode
        progressDialog.show();
        String URL_FINAL = getURL()+"logout";
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL_FINAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HAR",response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = (int) jsonObject.get("code");
                    if(code==200){
                        editor.clear();
                        editor.apply();
                        ctx.startActivity(new Intent(ctx,LoginActivity.class));
                    }
                    else{
                        Snackbar.make(v, "Logout not successful",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(v, "Exception Occured",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                if(pref.getString("registrationNumber","")!="") {
                    parameters.put("regID", pref.getString("registrationNumber", ""));
                }
                else{
                    parameters.put("regID", pref.getString("fregistrationNumber", ""));
                }
                parameters.put("token",pref.getString("token",""));

                return parameters;
            }
        };
        Singleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }


    }
