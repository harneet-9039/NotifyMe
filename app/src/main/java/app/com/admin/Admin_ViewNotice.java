package app.com.admin;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.adapters.NoticeAdapter;
import app.com.common.CheckConnection;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.fragments.FilterFragment;
import app.com.fragments.NoticeExtendedFragment;
import app.com.fragments.SortFragment;
import app.com.models.Notice;
import app.com.notifyme.R;

public class Admin_ViewNotice extends AppCompatActivity implements NoticeAdapter.OnItemClickListener, SortFragment.ItemClickListener, FilterFragment.ItemClickListener{

    private AppBarConfiguration mAppBarConfiguration;
    private List<Notice> noticeListData;
    private NoticeAdapter noticeAdapter;
    private RecyclerView noticeView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager SLayout;
    private View v;
    private ArrayList<Notice> noticeModelArrayList;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private void PopulateData(){
        noticeView = findViewById(R.id.recyclerView);
        noticeView.setHasFixedSize(true);
        SLayout = new LinearLayoutManager(this);
        noticeView.setLayoutManager(SLayout);
        noticeListData = new ArrayList<>();
        LoadData();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_view_notice);
        boolean networkStatus = CheckConnection.getInstance(this).getNetworkStatus();
        if(networkStatus==true) {
            setContentView(R.layout.activity_view_notice);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
            editor = pref.edit();
            //DrawerLayout drawer = findViewById(R.id.drawer_layout);

           getSupportActionBar().setTitle("Notify Me");
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.

            SearchView simpleSearchView = findViewById(R.id.searchView);
            UpdateSearchView(simpleSearchView);

            noticeModelArrayList = new ArrayList<>();
            /*writeheretext = findViewById(R.id.writeheretext);
            writeheretext.setOnClickListener(new View.OnClickListener() {
                @Override
               public void onClick(View view) {
                    Intent createNotice =new Intent(ViewNotice.this,ViewNotice.class);
                    startActivity(createNotice);
                }
            });*/
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

                                noticeModel.setEventName(dataobj.getString("eventName"));

                                String[] attachments = dataobj.getString("Attachments").split(",");
                                for(String path : attachments){
                                    attachmentList.add(path);
                                }
                                noticeModel.setAttachments(attachmentList);
                               /* SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                String dateOfCreation = dataobj.getString("date_time");
                                try {

                                    Date date = formatter.parse(dateOfCreation);
                                    noticeModel.setTimestamp(formatter.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/
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
                noticeAdapter = new NoticeAdapter(Admin_ViewNotice.this,noticeModelArrayList, Admin_ViewNotice.this,v);
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
                if(noticeModelArrayList!=null){
                    ArrayList<Notice>temp = noticeAdapter.returnToActivity();
                    noticeModelArrayList.clear();
                    noticeModelArrayList.addAll(temp);
                }
                else{
                    noticeModelArrayList.addAll(noticeAdapter.returnToActivity());
                }
                return true;
            }
        });
    }

    private void openDialog() {
        //FilterFragment.display(getSupportFragmentManager());
        FilterFragment addPhotoBottomDialogFragment =
                FilterFragment.newInstance(this, noticeModelArrayList, noticeAdapter);
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                FilterFragment.TAG);
    }

    /*@Override
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
*/
    @Override
    public void onItemClick(ArrayList<Notice> list, View v, int position) {
        Notice rowRecord = list.get(position);
        /*TextView title = v.findViewById(R.id.title);
        TextView desc = v.findViewById(R.id.desc);
        TextView priority = v.findViewById(R.id.priority);
        TextView name = v.findViewById(R.id.name);
        TextView designation = v.findViewById(R.id.designation);
        TextView date = v.findViewById(R.id.date);*/
        NoticeExtendedFragment.display(getSupportFragmentManager(),rowRecord, Admin_ViewNotice.this);
       /* title.setTextColor(Color.DKGRAY);
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
        date.setTypeface(null, Typeface.NORMAL);*/
    }


    public void onPriorityClick() {
        ArrayList<Notice> tempNoticeArray = new ArrayList<>();
        tempNoticeArray.addAll(noticeModelArrayList);
        Collections.sort(tempNoticeArray,Notice.priorityComparator);
        noticeAdapter.swap(tempNoticeArray);
    }

    public void onDateClick() {
        ArrayList<Notice> tempNoticeArray = new ArrayList<>();
        tempNoticeArray.addAll(noticeModelArrayList);
        Collections.sort(tempNoticeArray,Notice.dateCompartor);
        noticeAdapter.swap(tempNoticeArray);
    }
}
