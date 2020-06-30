package app.com.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import app.com.models.RequestStatusModel;
import app.com.models.RequestViewInnerModel;
import app.com.models.ViewRequestModel;
import app.com.notifyme.R;

public class ViewRequestAdapter extends ArrayAdapter<ViewRequestModel> {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private Context ctx;
    private List<ViewRequestModel> viewrequest;
    private List<RequestStatusModel> vwreq;
    private View v;
    ListView listView;

    public ViewRequestAdapter(Context context, List<ViewRequestModel> requestList, View view){
        super(context, 0, requestList);
        viewrequest = new ArrayList<ViewRequestModel>();
        this.ctx = context;
        viewrequest.addAll(requestList);
        this.v=view;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewRequestModel request=getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_request, parent, false);
        }
        TextView title = convertView.findViewById(R.id.requestid);
        TextView validity = convertView.findViewById(R.id.validitytext);
        TextView dateView = convertView.findViewById(R.id.date);
        TextView facultyname=convertView.findViewById(R.id.facultyid);
        final ListView listView = convertView.findViewById(R.id.innerview);
        title.setText("Request ID: "+request.getRequestId().substring(0,request.getRequestId().indexOf("-")));
        title.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
        title.setTypeface(null, Typeface.BOLD);
        String date = request.getDate();
        String updatedDate = date.substring(0,date.indexOf("T"));
        String[] parts = updatedDate.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parts[2]+"-"+parts[1]+"-"+parts[0]);
        dateView.setText("posted on: "+stringBuilder);
        dateView.setTypeface(null,Typeface.BOLD);
        validity.setText(request.getValidity()+" days");
        validity.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
        validity.setTypeface(null,Typeface.BOLD);
        facultyname.setText(request.getFacultyName());
        facultyname.setTextColor(ctx.getResources().getColor(R.color.colorAccent));
        facultyname.setTypeface(null,Typeface.BOLD);

        try {
            ArrayList<RequestViewInnerModel> innerModelArrayList = new ArrayList<>();
            for (int i = 0; i < request.getNames().size(); i++) {
                RequestViewInnerModel requestViewInnerModel = new RequestViewInnerModel();
                requestViewInnerModel.setName(request.getNames().get(i));
                requestViewInnerModel.setRegid(request.getRegistrationID().get(i));
                requestViewInnerModel.setStatus(request.getStatus().get(i));
                innerModelArrayList.add(requestViewInnerModel);

            }
            ViewRequestInnerAdapter viewRequestInnerAdapter = new ViewRequestInnerAdapter(ctx, innerModelArrayList,v,request.getRequestId());
            listView.setAdapter(viewRequestInnerAdapter);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return convertView;
    }
}
