package app.com.notifyme;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;


import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.com.adapters.NoticeAdapter;
import app.com.common.CheckConnection;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.fragments.FilterFragment;
import app.com.fragments.NoticeExtendedFragment;
import app.com.fragments.SortFragment;
import app.com.models.Notice;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeDashboard extends AppCompatActivity implements NoticeAdapter.OnItemClickListener, SortFragment.ItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private List<Notice> noticeListData;
    private NoticeAdapter noticeAdapter;
    private RecyclerView noticeView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager SLayout;
    private View v;
    private ArrayList<Notice> noticeModelArrayList;
    private TextView writeheretext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean networkStatus = CheckConnection.getInstance(this).getNetworkStatus();
        if(networkStatus==true) {
            setContentView(R.layout.activity_notice_dashboard);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            getSupportActionBar().setTitle("Notify Me");
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                    R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            SearchView simpleSearchView = findViewById(R.id.searchView);
            UpdateSearchView(simpleSearchView);
            noticeModelArrayList = new ArrayList<>();
            writeheretext = findViewById(R.id.writeheretext);
            writeheretext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createNotice =new Intent(NoticeDashboard.this,createNotice.class);
                    startActivity(createNotice);
                }
            });
            progressDialog = new ProgressDialog(this);

            findViewById(R.id.filterbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                }
            });
            findViewById(R.id.sortbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SortFragment addPhotoBottomDialogFragment =
                            SortFragment.newInstance();
                    addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                            SortFragment.TAG);

                }
            });

            swipeRefreshLayout = findViewById(R.id.swiperefresh_items);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d("HAR", "hello");
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


            PopulateData();
        }
        else{
            setContentView(R.layout.nointernet_view);
        }

    }


    private void PopulateData(){
        noticeView = findViewById(R.id.recyclerView);
        noticeView.setHasFixedSize(true);
        SLayout = new LinearLayoutManager(this);
        noticeView.setLayoutManager(SLayout);
        noticeListData = new ArrayList<>();
        LoadData();
    }

    private void LoadData() {
        Log.d("HAR","sys");
        final String URL = GlobalMethods.getURL()+"fetchNotice";
        progressDialog.show();
        v =findViewById(R.id.root);

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
                            Snackbar.make(v, "No Notices found for you",
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

                                Notice noticeModel = new Notice();
                                ArrayList<String> attachmentList = new ArrayList<>();
                                JSONObject dataobj = dataArray.getJSONObject(i);
                                noticeModel.setName("Posted by: " + dataobj.getString("name"));
                                noticeModel.setEmailID(dataobj.getString("email_id"));
                                noticeModel.setContact(dataobj.getString("contact"));
                                noticeModel.setId(dataobj.getString("Notice_id"));
                                noticeModel.setImages(dataobj.getString("Images").split(",")[0]);
                                noticeModel.setPriority(dataobj.getString("priority"));
                                noticeModel.setTitle(dataobj.getString("title"));
                                noticeModel.setDescription(dataobj.getString("description"));
                                noticeModel.setIsCoordinator(dataobj.getString("isCoordinator"));
                                noticeModel.setDepartment(dataobj.getString("department"));
                                noticeModel.setCourse(dataobj.getString("course"));
                                String[] attachments = dataobj.getString("Attachments").split(",");
                                for(String path : attachments){
                                    attachmentList.add(path);
                                }
                                noticeModel.setAttachments(attachmentList);
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                String dateOfCreation = dataobj.getString("date_time");
                                try {

                                    Date date = formatter.parse(dateOfCreation);
                                    noticeModel.setTimestamp(formatter.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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




                noticeAdapter = new NoticeAdapter(NoticeDashboard.this,noticeModelArrayList,NoticeDashboard.this);
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
                parameters.put("scope", "1");
                parameters.put("dept_id", "2");
                parameters.put("year", "2");
                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void UpdateSearchView(SearchView searchView){
        searchView.setActivated(true);
        searchView.setQueryHint("Search Notice..");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.setFocusable(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noticeAdapter.getFilter().filter(newText);
                //noticeModelArrayList = noticeAdapter.returnToActivity()
                //Collections.copy(noticeModelArrayList,noticeAdapter.returnToActivity());

                    //noticeModelArrayList = noticeAdapter.returnToActivity();
                return true;
            }
        });
    }

    private void openDialog() {
        FilterFragment.display(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_dashboard, menu);
        return true;
    }

   @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onItemClick(ArrayList<Notice> list, View v, int position) {
        Notice rowRecord = list.get(position);
        TextView title = v.findViewById(R.id.title);
        TextView desc = v.findViewById(R.id.desc);
        TextView priority = v.findViewById(R.id.priority);
        TextView name = v.findViewById(R.id.name);
        TextView designation = v.findViewById(R.id.designation);
        TextView date = v.findViewById(R.id.date);
        NoticeExtendedFragment.display(getSupportFragmentManager(),rowRecord, NoticeDashboard.this);
        title.setTextColor(Color.DKGRAY);
        title.setTypeface(null, Typeface.NORMAL);
        desc.setTextColor(Color.DKGRAY);
        desc.setTypeface(null, Typeface.NORMAL);
        priority.setTextColor(Color.DKGRAY);
        priority.setTypeface(null, Typeface.NORMAL);
        name.setTextColor(Color.DKGRAY);
        name.setTypeface(null, Typeface.NORMAL);
        designation.setTextColor(Color.DKGRAY);
        designation.setTypeface(null, Typeface.NORMAL);
        date.setTextColor(Color.DKGRAY);
        date.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public void onPriorityClick() {
        ArrayList<Notice> tempNoticeArray = new ArrayList<>();
        tempNoticeArray.addAll(noticeModelArrayList);
        Collections.sort(tempNoticeArray,Notice.priorityComparator);
        noticeAdapter.swap(tempNoticeArray);
    }

    @Override
    public void onDateClick() {
        ArrayList<Notice> tempNoticeArray = new ArrayList<>();
        tempNoticeArray.addAll(noticeModelArrayList);
        Collections.sort(tempNoticeArray,Notice.dateCompartor);
        noticeAdapter.swap(tempNoticeArray);
    }
}