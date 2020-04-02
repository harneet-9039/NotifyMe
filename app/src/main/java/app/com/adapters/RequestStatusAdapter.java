package app.com.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.models.RequestStatusModel;
import app.com.models.RequestViewInnerModel;
import app.com.notifyme.R;

public class RequestStatusAdapter  extends ArrayAdapter<RequestStatusModel> {

    private static LayoutInflater inflater = null;
    private Context ctx;
    private List<RequestStatusModel> requestStatusModelList;
    private View view;

    public RequestStatusAdapter (Context context, List<RequestStatusModel> requestList, View v) {
        super(context, 0, requestList);
        requestStatusModelList = new ArrayList<>();
        this.ctx=context;
        this.view=v;
        requestStatusModelList.addAll(requestList);
    }

    public int getCount() {
        return requestStatusModelList.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final RequestStatusModel request = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_status_view, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.requestid);
        TextView validity = convertView.findViewById(R.id.validity);
        TextView dateView = convertView.findViewById(R.id.date);
        final ListView listView = convertView.findViewById(R.id.innerview);
        // Populate the data into the template view using the data object

        title.setText("Request ID: "+request.getRequestID().substring(0,request.getRequestID().indexOf("-")));
        title.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
        title.setTypeface(null, Typeface.BOLD);
        String date = request.getDate();
        String updatedDate = date.substring(0,date.indexOf("T"));
        String[] parts = updatedDate.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parts[2]+"-"+parts[1]+"-"+parts[0]);
        dateView.setText("posted on: "+stringBuilder);
        dateView.setTypeface(null,Typeface.BOLD);
        if(request.getValidity().equals("1")){
            validity.setText(request.getValidity()+" day");
            validity.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
            validity.setTypeface(null,Typeface.BOLD);
        }else {
            validity.setText(request.getValidity() + " days");
            validity.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
            validity.setTypeface(null,Typeface.BOLD);
        }
        try {

            ArrayList<RequestViewInnerModel> innerModelArrayList = new ArrayList<>();
            for (int i = 0; i < request.getNames().size(); i++) {
                RequestViewInnerModel requestViewInnerModel = new RequestViewInnerModel();
                requestViewInnerModel.setName(request.getNames().get(i));
                requestViewInnerModel.setRegid(request.getRegistrationID().get(i));
                requestViewInnerModel.setStatus(request.getStatus().get(i));
                innerModelArrayList.add(requestViewInnerModel);
            }
            RequestStatusInnerAdapter requestStatusInnerAdapter = new RequestStatusInnerAdapter(ctx, innerModelArrayList);
            listView.setAdapter(requestStatusInnerAdapter);
        }
        catch(Exception e){
           e.printStackTrace();
        }
        // Return the completed view to render on screen
        return convertView;
    }



}

