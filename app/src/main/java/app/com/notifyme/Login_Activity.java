package app.com.notifyme;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

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

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        InitializeUIComponent();


    }

    private void InitializeUIComponent()
    {
        Register_Link = findViewById(R.id.Register_Link);
        LoginUser = findViewById(R.id.Login);
        UserName = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging you...");

        LoginUser.setOnClickListener(this);
        Register_Link.setOnClickListener(this);
    }

    private void LoginUser()
    {
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

                        //JSONObject jsonObject1 = data.getJSONObject(0);
                        if(data.toString().equals("345")){
                            Toast.makeText(Login_Activity.this,"Internal Server Error",Toast.LENGTH_LONG);
                        }
                        else if(data.toString().equals("400")){
                            Toast.makeText(Login_Activity.this,"Username and password do not match",Toast.LENGTH_LONG);
                        }
                        else if(data.toString().equals("401")){
                            Toast.makeText(Login_Activity.this,"Register First",Toast.LENGTH_LONG);
                        }
                        else if(data.toString().equals("402")){
                            Toast.makeText(Login_Activity.this,"Account Not Activated",Toast.LENGTH_LONG);
                        }
                        else if(data.toString().equals("100")){
                            Toast.makeText(Login_Activity.this,"Login Success",Toast.LENGTH_LONG);
                        }

                    progressDialog.dismiss();
                }
                catch (JSONException e){
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HAR",error.toString());
                progressDialog.dismiss();


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
        }
        else if(view.getId() == R.id.Login)
        {
            LoginUser();
        }
    }
}
