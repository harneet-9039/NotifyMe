package app.com.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import app.com.adapters.NotificationAdapter;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.models.NotificationModel;
import app.com.notifyme.R;

public class NotificationFragment extends DialogFragment {
        public static final String TAG = "example_dialog";
        private static Context ctx;
        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Toolbar toolbar;
        private AppBarLayout appBarLayout;
        private ListView listView;
    private ProgressDialog pDialog;

        public static app.com.fragments.NotificationFragment display(FragmentManager fragmentManager, Context ctx) {
            app.com.fragments.NotificationFragment notificationDialog = new app.com.fragments.NotificationFragment();
            NotificationFragment.ctx = ctx;
            notificationDialog.show(fragmentManager, TAG);

            return notificationDialog;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        }

        @Override
        public void onStart() throws NullPointerException {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
                dialog.setTitle("Notifications");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            getDialog().setTitle("Notifications");
            View view = inflater.inflate(R.layout.notification_layout, container, false);
            toolbar = view.findViewById(R.id.viewtoolbar);
            appBarLayout = view.findViewById(R.id.appbar);
            listView = view.findViewById(R.id.notificationlist);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationFragment.this.dismiss();
                }
            });
            toolbar.setTitle("Notifications");
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

            pref = ctx.getSharedPreferences("UserVals", 0); // 0 - for private mode
            editor = pref.edit();
            String URL_FINAL = GlobalMethods.getURL()+"updateNotification";
            pDialog = new ProgressDialog(getContext());
            pDialog.show();
            pDialog.setMessage("Fetching your personalized notifications...");
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL_FINAL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("HAR",response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = (int) jsonObject.get("code");
                        if(code==200){
                                Gson gson = new Gson();
                                String jsonText = pref.getString("notificationlist", null);
                                if(jsonText!=null) {
                                    Type type = new TypeToken<List<NotificationModel>>() {
                                    }.getType();
                                    List<NotificationModel> notificationList = gson.fromJson(jsonText, type);
                                    NotificationAdapter notificationAdapter = new NotificationAdapter(ctx, notificationList);
                                    listView.setAdapter(notificationAdapter);
                                    pDialog.dismiss();
                                }

                            }
                        else{
                            pDialog.dismiss();
                        }
                        }

                     catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
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
            Singleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);


        }
}
