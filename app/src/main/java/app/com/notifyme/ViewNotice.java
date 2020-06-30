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
import java.util.HashMap;
import java.util.List;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.com.adapters.NoticeAdapter;
import app.com.adapters.RequestStatusAdapter;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.fragments.NoticeExtendedFragment;
import app.com.fragments.NotificationFragment;
import app.com.models.Notice;

public class ViewNotice extends AppCompatActivity implements NoticeAdapter.OnItemClickListener {
    SwipeRefreshLayout swipeRefreshLayout;
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView navbarusername, navbaryearcourse, navbardesignation, plusButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private DrawerLayout drawer;
    private List<Notice> noticeListData;
    private NoticeAdapter noticeAdapter;
    private RecyclerView noticeView;
    private RecyclerView.LayoutManager SLayout;
    private View v;
    private LayerDrawable icon;
    private ArrayList<Notice> noticeModelArrayList;
    private RequestStatusAdapter requestStatusAdapter;
    private ListView listView;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pref.getInt("notificationStatusCount", -1) != -1) {
                GlobalMethods.setCountForNotifcation(icon, String.valueOf(pref.getInt("notificationStatusCount", -1)), ViewNotice.this);
            } else {
                GlobalMethods.setCountForNotifcation(icon, "0", ViewNotice.this);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);
        CommonWorkOfMenuItems();
        noticeView = findViewById(R.id.recyclerView);
        noticeView.setHasFixedSize(true);
        SLayout = new LinearLayoutManager(this);
        noticeView.setLayoutManager(SLayout);
        noticeListData = new ArrayList<>();
        noticeModelArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        swipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        LoadData();
    }


    private void CommonWorkOfMenuItems() {

        Toolbar toolbar = findViewById(R.id.requesttoolbarviewnotice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<small>Your posted notices</small>"));
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

        if(pref.getInt("isCoordinator",-1)!=2){
            hideItem();
        }
        if(pref.getInt("isCoordinator",-1)==0){
            hideMore();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewNotice.this);
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlobalMethods.logout(ViewNotice.this,v);
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
                        startActivity(new Intent(ViewNotice.this,RequestAccessActivity.class));
                        return true;
                    case R.id.nav_request_status:
                        startActivity(new Intent(ViewNotice.this,RequestStatus.class));
                        return true;
                    case R.id.nav_dash:
                        startActivity(new Intent(ViewNotice.this,NoticeDashboard.class));
                        return true;
                    case R.id.nav_notice:
                        startActivity(new Intent(ViewNotice.this,ViewNotice.class));
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(ViewNotice.this,UserProfile.class));
                        return true;
                    default:return false;
                }
            }
        });
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

    private void hideMore() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_notice).setVisible(false);

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

    private void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_access).setVisible(false);
        nav_Menu.findItem(R.id.nav_request_status).setVisible(false);

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
                NotificationFragment.display(getSupportFragmentManager(), ViewNotice.this);
                GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
                editor.putInt("notificationStatusCount",0);
                return true;
            }
        });
        GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
        return true;

    }


    private void LoadData() {
        String URL = GlobalMethods.getURL()+"myNotices";
        progressDialog.show();
        v =findViewById(R.id.mainlayoutcons);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HAR",response);

                noticeModelArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("true")) {

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if(dataArray.length()<1){
                            Snackbar.make(v, "You've not created any notice",
                                    Snackbar.LENGTH_LONG)
                                   .show();
                        }
                        else {

                            for (int i = 0; i < dataArray.length(); i++) {

                                Notice noticeModel = new Notice();
                                ArrayList<String> attachmentList = new ArrayList<>();
                                JSONObject dataobj = dataArray.getJSONObject(i);
                                noticeModel.setId(dataobj.getString("Notice_id"));
                                noticeModel.setName("Posted by: " + dataobj.getString("name"));
                                noticeModel.setEmailID(dataobj.getString("email_id"));
                                noticeModel.setContact(dataobj.getString("contact"));
                                    noticeModel.setImages(dataobj.getString("Images").split(",")[0]);
                                noticeModel.setPriority(String.valueOf(dataobj.getInt("priority")));
                                noticeModel.setTitle(dataobj.getString("title"));
                                noticeModel.setDescription(dataobj.getString("description"));
                                noticeModel.setIsCoordinator(dataobj.getString("isCoordinator"));
                                noticeModel.setDepartment(dataobj.getString("mydepartment"));
                                noticeModel.setCourse(dataobj.getString("mycourse"));
                                noticeModel.setScope(dataobj.getString("scope"));
                                noticeModel.setScopeDepartment(dataobj.getString("scope_department"));
                                noticeModel.setScopeYear(dataobj.getString("scope_year"));
                                noticeModel.setScopeCourse(dataobj.getString("scope_course"));
                                noticeModel.setEventName("");
                                if(!dataobj.getString("Attachments").equals("null")) {
                                    String[] attachments = dataobj.getString("Attachments").split(",");
                                    for (String path : attachments) {
                                        attachmentList.add(path);
                                    }
                                    noticeModel.setAttachments(attachmentList);
                                }
                                String dateOfCreation = dataobj.getString("date_time");
                                noticeModel.setTimestamp(dateOfCreation);
                                noticeModelArrayList.add(noticeModel);

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(v, "Exception Occured",
                            Snackbar.LENGTH_LONG)
                            .show();
                }




                noticeAdapter = new NoticeAdapter(ViewNotice.this,noticeModelArrayList,ViewNotice.this, v);
                noticeView.setAdapter(noticeAdapter);
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
                else{
                    parameters.put("student_id",pref.getString("registrationNumber",""));
                }
                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onItemClick(ArrayList<Notice> notice, View v, int position) {
        Notice rowRecord = notice.get(position);
        NoticeExtendedFragment.display(getSupportFragmentManager(),rowRecord, ViewNotice.this);
    }

}
