package app.com.notifyme;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.*;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.com.common.GlobalMethods;
import app.com.common.Singleton;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView Register_Link;
    private EditText UserName, Password;
    private Button LoginUser;
    private SharedPreferences.Editor editor;
    private View v;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        v = findViewById(R.id.main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();
        InitializeUIComponent();


    }

    private void InitializeUIComponent()
    {
        Register_Link = findViewById(R.id.Register_Link);
        LoginUser = findViewById(R.id.Login);
        UserName = findViewById(R.id.username);
        Password = findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging you...");

        LoginUser.setOnClickListener(this);
        Register_Link.setOnClickListener(this);
    }

    private void LoginUser()
    {
        final Intent homeintent=new Intent(Login_Activity.this,NoticeDashboard.class);
        final String URL = GlobalMethods.getURL()+"login";
        progressDialog.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //JSONArray response = null;
                Log.d("HAR",response.toString());
                try {
                    JSONObject j = new JSONObject(response);
                    String data = (String) j.get("code");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    JSONObject dataobj = dataArray.getJSONObject(0);

                        //JSONObject jsonObject1 = data.getJSONObject(0);
                        if(data.toString().equals("345")){
                            progressDialog.dismiss();
                            Snackbar.make(v, "Internal Server Error",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else if(data.toString().equals("400")){
                            progressDialog.dismiss();
                            Snackbar.make(v, "Username and password do not match",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else if(data.toString().equals("401")){
                            progressDialog.dismiss();
                            Snackbar.make(v, "Register to continue login",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else if(data.toString().equals("402")){
                            progressDialog.dismiss();
                            Snackbar.make(v, "Account not activated, please check your email",
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        else if(data.toString().equals("100")){
                            editor.putString("registrationNumber",dataobj.getString("Reg_id"));
                            editor.putString("name",dataobj.getString("name"));
                            editor.putInt("departmentid",dataobj.getInt("dept_id"));
                            editor.putInt("courseid",dataobj.getInt("course_id"));
                            editor.putString("departmentname",dataobj.getString("Dept_name"));
                            editor.putString("coursename",dataobj.getString("Course_branch"));
                            editor.putInt("year",dataobj.getInt("year"));
                            editor.putString("email",dataobj.getString("email_id"));
                            editor.putString("contact",dataobj.getString("contact"));
                            editor.putString("password",Password.getText().toString());
                            editor.putInt("isCoordinator",0);
                            editor.commit();
                            progressDialog.dismiss();
                            startActivity(homeintent);
                            finish();
                        }
                        else if(data.equals("200")){
                            editor.putString("registrationNumber",dataobj.getString("Reg_id"));
                            editor.putString("name",dataobj.getString("name"));
                            editor.putInt("departmentid",dataobj.getInt("dept_id"));
                            editor.putInt("courseid",dataobj.getInt("course_id"));
                            editor.putString("departmentname",dataobj.getString("Dept_name"));
                            editor.putString("coursename",dataobj.getString("Course_branch"));
                            editor.putInt("year",dataobj.getInt("year"));
                            editor.putString("email",dataobj.getString("email_id"));
                            editor.putString("contact",dataobj.getString("contact"));
                            editor.putString("password",Password.getText().toString());
                            editor.putInt("isCoordinator",1);
                            editor.commit();
                            progressDialog.dismiss();
                            startActivity(homeintent);
                            finish();
                        }
                        else if(data.equals("300")){
                            Gson gson = new Gson();
                            editor.putString("fregistrationNumber",dataobj.getString("Faculty_id"));
                            editor.putString("fname",dataobj.getString("Name"));
                            editor.putInt("fdepartmentid",dataobj.getInt("dept_id"));
                            editor.putString("fdepartmentname",dataobj.getString("Dept_name"));
                            editor.putString("femail",dataobj.getString("email_id"));
                            editor.putString("fcontact",dataobj.getString("contact"));
                            editor.putString("fdesignation",dataobj.getString("designation"));
                            String[] courses = dataobj.getString("course_name").split(",");
                            String courseJSONString = gson.toJson(courses);
                            editor.putString("fcourse",courseJSONString);
                            String[] year = dataobj.getString("year").split(",");
                            editor.putString("password",Password.getText().toString());
                            String yearJSONString = gson.toJson(year);
                            editor.putString("fyear",yearJSONString);
                            String[] courseName = dataobj.getString("course_name").split(",");
                            String courseNameJSONString = gson.toJson(courseName);
                            editor.putString("fcoursename",courseNameJSONString);
                            String[] courseDeptID = dataobj.getString("Course_Dept_id").split(",");
                            String courseDeptIDJSONString = gson.toJson(courseDeptID);
                            editor.putString("fcoursedeptID",courseDeptIDJSONString);
                            String[] courseDeptName = dataobj.getString("Course_Dept_name").split(",");
                            String courseDeptNameJSONString = gson.toJson(courseDeptName);
                            editor.putString("fcoursedeptName",courseDeptNameJSONString);
                            editor.putInt("isCoordinator",2);
                            editor.commit();
                            progressDialog.dismiss();
                            startActivity(homeintent);
                            finish();
                        }




                }
                catch (JSONException e){
                    progressDialog.dismiss();
                    Snackbar.make(v, "Unexpected response from server",
                            Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HAR",error.toString());
                progressDialog.dismiss();
                Snackbar.make(v, "Restart Server",
                        Snackbar.LENGTH_LONG)
                        .show();


            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
               // Log.d("HAR",String.valueOf(DepartmentID));
                parameters.put("reg_id", UserName.getText().toString());
                parameters.put("password", Password.getText().toString());
                return parameters;
            }
        };
        Singleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.Register_Link)
        {
            Intent in=new Intent(Login_Activity.this, Register_Activity.class);
            startActivity(in);
            finish();
        }
        else if(view.getId() == R.id.Login)
        {
            LoginUser();
        }
    }
}
