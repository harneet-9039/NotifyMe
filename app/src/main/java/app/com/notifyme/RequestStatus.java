package app.com.notifyme;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.adapters.RequestStatusAdapter;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.fragments.NotificationFragment;
import app.com.models.RequestStatusModel;

public class RequestStatus extends AppCompatActivity {
    private static final String TAG = "REQUEST_ACTIVITY";
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView navbarusername, navbaryearcourse, navbardesignation, plusButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private DrawerLayout drawer;
    private View v;
    private LayerDrawable icon;
    private ArrayList<RequestStatusModel> requestStatusModelArrayList;
    private RequestStatusAdapter requestStatusAdapter;
    private ListView listView;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("HAR","aya");
            Log.d("HAR",String.valueOf(pref.getInt("notificationStatusCount", -1)));
            if (pref.getInt("notificationStatusCount", -1) != -1) {
                GlobalMethods.setCountForNotifcation(icon, String.valueOf(pref.getInt("notificationStatusCount", -1)), RequestStatus.this);
            } else {
                GlobalMethods.setCountForNotifcation(icon, "0", RequestStatus.this);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_status);
        CommonWorkOfMenuItems();
        listView = findViewById(R.id.mainlist);
        requestStatusModelArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        LoadData();
    }

    private void LoadData() {

        String URL = GlobalMethods.getURL()+"viewRequestStatus";
        progressDialog.show();
        progressDialog.setMessage("Fetching your requests...");
        v =findViewById(R.id.mainlayoutcons);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HAR",response);

                requestStatusModelArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")==true) {

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if(dataArray.length()<1){
                            Snackbar.make(v, "No requests for you",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener(){

                                        @Override
                                        public void onClick(View view) {
                                            LoadData();
                                        }
                                    }).show();
                        }
                        else {

                            for (int i = 0; i < dataArray.length(); i++) {

                                RequestStatusModel noticeModel = new RequestStatusModel();
                                ArrayList<String> attachmentList = new ArrayList<>();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                noticeModel.setRequestID(dataobj.getString("request_id"));
                                noticeModel.setDate(dataobj.getString("date"));
                                noticeModel.setValidity(String.valueOf(dataobj.getInt("duration")));
                                String[] names = dataobj.getString("Student_name").split(",");
                                ArrayList<String> studentnameList = new ArrayList<>();
                                for(String name: names){
                                    studentnameList.add(name);
                                }
                                String[] regid = dataobj.getString("Student_id").split(",");
                                ArrayList<String> studentIDList = new ArrayList<>();
                                for(String name: regid){
                                    studentIDList.add(name);
                                }
                                String[] status = dataobj.getString("status").split(",");
                                ArrayList<String> statusList = new ArrayList<>();
                                for(String name: status){
                                    statusList.add(name);
                                }
                                noticeModel.setNames(studentnameList);
                                noticeModel.setRegistrationID(studentIDList);
                                noticeModel.setStatus(statusList);
                                requestStatusModelArrayList.add(noticeModel);

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(v, "Exception Occured",
                            Snackbar.LENGTH_LONG)
                            .show();
                }



                Collections.sort(requestStatusModelArrayList,RequestStatusModel.priorityComparator);
                requestStatusAdapter = new RequestStatusAdapter(RequestStatus.this,requestStatusModelArrayList,v);
                listView.setAdapter(requestStatusAdapter);
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HAR",error.toString());
                Snackbar.make(v, "Error Response from Server",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                LoadData();
                            }
                        }).show();
                progressDialog.dismiss();


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                // Log.d("HAR",String.valueOf(DepartmentID));
                if(pref.getString("fregistrationNumber","")!="") {
                    parameters.put("faculty_id",pref.getString("fregistrationNumber",""));
                }
                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void CommonWorkOfMenuItems() {

        Toolbar toolbar = findViewById(R.id.requesttoolbarstatus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<small>View request status</small>"));
        drawer = findViewById(R.id.drawer_layout_request);
        navigationView = findViewById(R.id.nav_view_request);

        View viewNav = navigationView.getHeaderView(0);
        navbarusername = viewNav.findViewById(R.id.userName);
        navbaryearcourse = viewNav.findViewById(R.id.courseyear);
        navbardesignation = viewNav.findViewById(R.id.designation);
        pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();

        if(pref.getString("name","")!="" ){
            navbarusername.setText(pref.getString("name",""));
        }
        else if(pref.getString("fname","")!=""){
            navbarusername.setText(pref.getString("fname",""));
        }

        if(pref.getString("fregistrationNumber","")==""){
            navbaryearcourse.setText(pref.getString("departmentname","")+", "+
                    pref.getString("coursename","")+", "+ pref.getInt("year", 0) +" year");
        }
        else{
            navbardesignation.setVisibility(View.VISIBLE);
            navbardesignation.setText(pref.getString("fdesignation","")+",");
            navbaryearcourse.setText(pref.getString("fdepartmentname","")+" department");
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dash, R.id.nav_notice, R.id.nav_profile, R.id.nav_access,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(RequestStatus.this);
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlobalMethods.logout(RequestStatus.this,v);
                                finish();
                                dialogInterface.cancel();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    case R.id.nav_access:
                        startActivity(new Intent(RequestStatus.this,RequestAccessActivity.class));
                        return true;
                    case R.id.nav_request_status:
                        startActivity(new Intent(RequestStatus.this,RequestStatus.class));
                        return true;
                    case R.id.nav_dash:
                        startActivity(new Intent(RequestStatus.this,NoticeDashboard.class));
                        return true;
                    default:return false;
                }
            }
        });
        navigationView.getMenu().getItem(4).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_dashboard, menu);
        MenuItem menuItem = menu.findItem(R.id.ic_group);
        icon = (LayerDrawable) menuItem.getIcon();
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                NotificationFragment.display(getSupportFragmentManager(), RequestStatus.this);
                GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
                editor.putInt("notificationStatusCount",0);
                return true;
            }
        });
        GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
        return true;

    }

    @Override
    public void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

    @Override
    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

    @Override
    public void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }


    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }


}
