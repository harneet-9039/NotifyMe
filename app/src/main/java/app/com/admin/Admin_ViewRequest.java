package app.com.admin;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

import app.com.adapters.ViewRequestAdapter;
import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.models.ViewRequestModel;
import app.com.notifyme.R;

public class Admin_ViewRequest extends AppCompatActivity {
    private ArrayList<String> requests=new ArrayList<>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Button Accept,Reject;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private View view;
    private ArrayList<ViewRequestModel> viewRequestModelArrayList;
    private ViewRequestAdapter viewRequestAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        listView = findViewById(R.id.list);
        Accept=findViewById(R.id.acceptbtn);
        Reject=findViewById(R.id.rejectbtn);
        CommonWorkOfMenuItems();
        viewRequestModelArrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notify Me");
        LoadData();
    }


    private void LoadData() {

        String URL = GlobalMethods.getURL()+"viewRequests";
        progressDialog.show();
        progressDialog.setMessage("Fetching your requests...");
        view =findViewById(R.id.mainreq);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HAR", response);

                viewRequestModelArrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int data=(int)jsonObject.get("code");
                    if (jsonObject.getBoolean("status") == true) {

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() < 1) {
                            Snackbar.make(view, "No requests for you",
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            LoadData();
                                        }
                                    }).show();
                        } else {

                            for (int i = 0; i < dataArray.length(); i++) {

                                ViewRequestModel noticeModel = new ViewRequestModel();
                                ArrayList<String> attachmentList = new ArrayList<>();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                noticeModel.setRequestId(dataobj.getString("request_id"));
                                noticeModel.setDate(dataobj.getString("date"));
                                noticeModel.setFacultyName(dataobj.getString("faculty_name"));
                                noticeModel.setValidity(String.valueOf(dataobj.getInt("duration")));
                               String[] names = dataobj.getString("Student_name").split(",");
                                ArrayList<String> studentnameList = new ArrayList<>();
                                for (String name : names) {
                                    studentnameList.add(name);
                                }
                                String[] regid = dataobj.getString("Student_id").split(",");
                                ArrayList<String> studentIDList = new ArrayList<>();
                                for (String name : regid) {
                                    studentIDList.add(name);
                                }
                                String[] status = dataobj.getString("status").split(",");
                                ArrayList<String> statusList = new ArrayList<>();
                                for (String name : status) {
                                    statusList.add(name);
                                }
                                noticeModel.setNames(studentnameList);
                                noticeModel.setRegistrationID(studentIDList);
                                noticeModel.setStatus(statusList);
                              viewRequestModelArrayList.add(noticeModel);

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Exception Occured",
                            Snackbar.LENGTH_LONG)
                            .show();
                }



                Collections.sort(viewRequestModelArrayList, ViewRequestModel.priorityComparator);


                viewRequestAdapter = new ViewRequestAdapter(Admin_ViewRequest.this, viewRequestModelArrayList, view);
                listView.setAdapter(viewRequestAdapter);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("HAR",error.toString());
                    Snackbar.make(view, "Error Response from Server",
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
               /* if(pref.getString("fregistrationNumber","")!="") {
                    parameters.put("faculty_id",pref.getString("fregistrationNumber",""));
                }*/
               //if(pref.getString("actionRequest","")!="")

                return parameters;

            }
            };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        }


    private void CommonWorkOfMenuItems() {

        pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();
        //androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.requesttoolbarstatus);
       // setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(Html.fromHtml("<small>View request status</small>"));
    }
}