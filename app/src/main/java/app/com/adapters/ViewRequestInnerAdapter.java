package app.com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.models.RequestViewInnerModel;
import app.com.notifyme.R;

public class ViewRequestInnerAdapter extends ArrayAdapter<RequestViewInnerModel> {
    private Context ctx;
    private View view;
    private String RequestId;
    private int flag=0;
    private List<RequestViewInnerModel> requestStatusModelList;


    public ViewRequestInnerAdapter (Context context, List<RequestViewInnerModel> requestList, View v,String RequestId) {
        super(context, 0, requestList);
        requestStatusModelList = new ArrayList<>();
        this.ctx = context;
        requestStatusModelList.addAll(requestList);
        this.RequestId=RequestId;
        this.view=v;
    }
    public int getCount() {
        return requestStatusModelList.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final RequestViewInnerModel request = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_student_request_view, parent, false);
        }
        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.name);
        final TextView regID = convertView.findViewById(R.id.regid);
        final TextView status=convertView.findViewById(R.id.status);
        if(request.getStatus().equals("2")){
            name.setText("Invalid ID");
        }
        else if(request.getStatus().equals("0") && request.getName().equals("false")) {
            name.setText("Invalid ID");
        }
        else{
            name.setText(request.getName());
        }
        regID.setText("("+request.getRegid()+")");


         final Button Accept=convertView.findViewById(R.id.acceptbtn);
         final Button Reject=convertView.findViewById(R.id.rejectbtn);

        if (request.getStatus().equals("1")) {
            Accept.setVisibility(View.GONE);
            Reject.setVisibility(View.GONE);
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
            status.setText("Approved");
        } else if (request.getStatus().equals("-1")) {
            Accept.setVisibility(View.GONE);
            Reject.setVisibility(View.GONE);
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.highpriority));
            status.setText("Rejected");
        } else if (request.getStatus().equals("2")) {
            Accept.setVisibility(View.GONE);
            Reject.setVisibility(View.GONE);
            status.setText("No status");
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.black));
        }
        else{
            status.setVisibility(View.GONE);
            Accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable R = new Runnable() {
                        @Override
                        public void run() {
                            flag=1;
                            String URL= GlobalMethods.getURL()+"actionRequest";
                            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("Har","Response");
                                        final JSONObject jsonObject = new JSONObject(response);
                                        final int data = (int) jsonObject.get("code");
                                        status.setVisibility(View.VISIBLE);
                                        if (data == 200&&(flag==1||flag==2)) {
                                            Accept.setVisibility(View.GONE);
                                            Reject.setVisibility(View.GONE);
                                            status.setTypeface(null, Typeface.BOLD);
                                            status.setTextColor(Color.parseColor("#00A5FF"));
                                            status.setText("Accepted");
                                            Snackbar.make(view, "Status change successfully", Snackbar.LENGTH_INDEFINITE).show();
                                        }
                                        else if(data==400&&(flag==1||flag==2)){
                                            Accept.setVisibility(View.GONE);
                                            Reject.setVisibility(View.GONE);
                                            status.setText("Invalid Id");
                                            status.setTypeface(null, Typeface.BOLD);
                                            status.setTextColor(Color.parseColor("#000000"));
                                            Snackbar.make(view, "Status change failed", Snackbar.LENGTH_INDEFINITE).show();
                                        }

                                    }

                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        Snackbar.make(view, "Exception Occured",
                                                Snackbar.LENGTH_LONG)
                                                .show();
                                    }

                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("HAR",error.toString());
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> parameter=new HashMap<>();
                                    parameter.put("request_id",RequestId);
                                    parameter.put("student_id",request.getRegid());
                                    parameter.put("flag",String.valueOf(flag));
                                    return parameter;
                                }
                            };
                            Singleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
                        }
                    };
                    Thread pthread = new Thread(R);
                    pthread.start();
                    Accept.setEnabled(false);
                    Reject.setEnabled(false);
                    Accept.setBackground(ctx.getResources().getDrawable(app.com.notifyme.R.drawable.requestbuttondrawable));
                    Reject.setBackground(ctx.getResources().getDrawable(app.com.notifyme.R.drawable.rejectbuttondrawlable));
                }

            });

            Reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable R = new Runnable() {
                        @Override
                        public void run() {
                            flag=2;
                            String URL= GlobalMethods.getURL()+"actionRequest";

                            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("Har","Response");
                                        final JSONObject jsonObject = new JSONObject(response);
                                        final int data = (int) jsonObject.get("code");
                                        Log.d("data",String.valueOf(data));
                                        status.setVisibility(View.VISIBLE);
                                        if(data==300) {
                                            Accept.setVisibility(View.GONE);
                                            Reject.setVisibility(View.GONE);
                                            status.setTypeface(null, Typeface.BOLD);
                                            status.setTextColor(Color.parseColor("#CD5C5C"));
                                            status.setText("Rejected");
                                            Snackbar.make(view, "Status change successfully", Snackbar.LENGTH_INDEFINITE).show();
                                        }
                                        else if(data==400){
                                            Accept.setVisibility(View.GONE);
                                            Reject.setVisibility(View.GONE);
                                            status.setText("Invalid Id");
                                            status.setTypeface(null, Typeface.BOLD);
                                            status.setTextColor(Color.parseColor("#000000"));
                                            Snackbar.make(view, "Status change failed", Snackbar.LENGTH_INDEFINITE).show();
                                        }

                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        Snackbar.make(view, "Exception Occured",
                                                Snackbar.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("HAR",error.toString());
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> parameter=new HashMap<>();
                                    parameter.put("request_id",RequestId);
                                    parameter.put("student_id",request.getRegid());
                                    parameter.put("flag",String.valueOf(flag));
                                    return parameter;
                                }
                            };
                            Singleton.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
                        }
                    };
                    Thread pthread = new Thread(R);
                    pthread.start();
                    Accept.setEnabled(false);
                    Reject.setEnabled(false);
                    Accept.setBackground(ctx.getResources().getDrawable(app.com.notifyme.R.drawable.requestbuttondrawable));
                    Reject.setBackground(ctx.getResources().getDrawable(app.com.notifyme.R.drawable.rejectbuttondrawlable));
                }
            });
        }
        // Return the completed view to render on screen
        return convertView;
    }
}