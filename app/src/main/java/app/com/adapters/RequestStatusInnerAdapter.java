package app.com.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.models.RequestViewInnerModel;
import app.com.notifyme.R;

public class RequestStatusInnerAdapter  extends ArrayAdapter<RequestViewInnerModel> {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private Context ctx;
    private List<RequestViewInnerModel> requestStatusModelList;

    public RequestStatusInnerAdapter (Context context, List<RequestViewInnerModel> requestList) {
        super(context, 0, requestList);
        requestStatusModelList = new ArrayList<>();
        this.ctx = context;
        requestStatusModelList.addAll(requestList);
    }

    public int getCount() {
        return requestStatusModelList.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RequestViewInnerModel request = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_listview_multiple_in_one_reques, parent, false);
        }
        // Lookup view for data population
        TextView name = convertView.findViewById(R.id.name);
        TextView regID = convertView.findViewById(R.id.regid);
        TextView status = convertView.findViewById(R.id.status);
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
        if(request.getStatus().equals("0")){
            status.setTextColor(ctx.getResources().getColor(R.color.lowpriority));
            status.setTypeface(null, Typeface.BOLD);
            status.setText("Pending");
        }
        else if(request.getStatus().equals("1")){
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
            status.setText("Approved");
        }
        else if(request.getStatus().equals("-1")){
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.highpriority));
            status.setText("Rejected");
        }
        else{
            status.setText("No status");
            status.setTypeface(null, Typeface.BOLD);
            status.setTextColor(ctx.getResources().getColor(R.color.black));

        }



        // Return the completed view to render on screen
        return convertView;
    }

}
