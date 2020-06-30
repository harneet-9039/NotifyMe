package app.com.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.common.GlobalMethods;
import app.com.common.Singleton;
import app.com.models.Notice;
import app.com.notifyme.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> implements Filterable {
    private LayoutInflater inflater;
    private Context c;
    private ArrayList<Notice> noticeModelArrayList;
    private ArrayList<Notice> noticeModelArrayListFilter;
    private ProgressDialog progressDialog;
    private OnItemClickListener onItemClickListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private View view;

    public void swap(List list){
        if (noticeModelArrayListFilter != null) {
            noticeModelArrayListFilter.clear();
            noticeModelArrayListFilter.addAll(list);
        }
        else {
            noticeModelArrayListFilter.addAll(list);
        }
        notifyDataSetChanged();
    }

    public NoticeAdapter(Context ctx, ArrayList<Notice> noticeModelArrayList, OnItemClickListener onItemClickListener, View mainView){
        this.c = ctx;
        this.view=mainView;
        inflater = LayoutInflater.from(ctx);
        this.noticeModelArrayList = noticeModelArrayList;
        this.noticeModelArrayListFilter = new ArrayList<>();
        this.noticeModelArrayListFilter.addAll(noticeModelArrayList);
        this.onItemClickListener = onItemClickListener;
        pref = ctx.getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();

    }

    @Override
    public NoticeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder;
        if(c.getClass().getSimpleName().equals("ViewNotice")){
            View view = inflater.inflate(R.layout.single_notice_view_profile, parent, false);
            holder = new MyViewHolder(view, onItemClickListener);
        }
        else {
            View view = inflater.inflate(R.layout.singlenotice_view, parent, false);
            holder = new MyViewHolder(view, onItemClickListener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.MyViewHolder holder, final int position) {
        if(noticeModelArrayListFilter.get(position).getImages().equals("null")){
            Glide.with(c)
                    .load(R.drawable.banner)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .into(holder.bannerImage);
        }
        else {
            Glide.with(c)
                    .load(noticeModelArrayListFilter.get(position).getImages())
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    holder.bannerImage.setImageResource(R.drawable.banner);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d("HAR", "loaded");
                                    return false;
                                }
                            })
                    .into(holder.bannerImage);
        }

        holder.name.setText(noticeModelArrayListFilter.get(position).getName());
        holder.name.setTypeface(null,Typeface.BOLD);
        if(noticeModelArrayListFilter.get(position).getPriority().equals("1")) {
            holder.priority.setText("High");
            holder.priority.setTextColor(c.getResources().getColor(R.color.highpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        else if(noticeModelArrayListFilter.get(position).getPriority().equals("2")){
            holder.priority.setText("Medium");
            holder.priority.setTextColor(c.getResources().getColor(R.color.mediumpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        else{
            holder.priority.setText("Low");
            holder.priority.setTextColor(c.getResources().getColor(R.color.lowpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        holder.title.setText(noticeModelArrayListFilter.get(position).getTitle());
        holder.title.setTextColor(c.getResources().getColor(R.color.colorAccent));
        holder.desc.setText(noticeModelArrayListFilter.get(position).getDescription());
        String date = noticeModelArrayListFilter.get(position).getTimestamp();
        Log.d("HAR",date);
        String updatedDate = date.substring(0,date.indexOf("T"));
        String[] parts = updatedDate.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parts[2]+"-"+parts[1]+"-"+parts[0]);
        holder.date.setText(stringBuilder);
        if(noticeModelArrayListFilter.get(position).getIsCoordinator().equals("0")||
                noticeModelArrayListFilter.get(position).getIsCoordinator().equals("1")){
            if(!(noticeModelArrayListFilter.get(position).getEventName().equals(""))) {

                holder.desgination.setText("Coordinator (" + noticeModelArrayListFilter.get(position).getEventName() + ")");
            }
            else{
                holder.desgination.setText("Coordinator");
            }
        }
        else{
            holder.desgination.setText(noticeModelArrayListFilter.get(position).getIsCoordinator());
        }

        if(c.getClass().getSimpleName().equals("ViewNotice")){
            if(noticeModelArrayListFilter.get(position).getScope().equals("1")){
                holder.scope.setText("Notice for: public");
            }
            else if(noticeModelArrayListFilter.get(position).getScope().equals("2")){
                holder.scope.setText("Notice for: " + noticeModelArrayListFilter.get(position).getScopeDepartment()+" department");
            }
            else if(noticeModelArrayListFilter.get(position).getScope().equals("3")){
                holder.scope.setText("Notice for: " + noticeModelArrayListFilter.get(position).getScopeDepartment()+" department, "+
                        noticeModelArrayListFilter.get(position).getScopeCourse()+" course");
            }
            else if(noticeModelArrayListFilter.get(position).getScope().equals("4")){
                holder.scope.setText("Notice for: " + noticeModelArrayListFilter.get(position).getScopeDepartment()+" department, "+
                        noticeModelArrayListFilter.get(position).getScopeCourse()+" course, "+
                        noticeModelArrayListFilter.get(position).getScopeYear()+" year");
            }
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    progressDialog = new ProgressDialog(c);
                    progressDialog.setMessage("Deleting notice, Please wait...");
                    progressDialog.show();
                    String URL = GlobalMethods.getURL()+"deleteNotice";
                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("HAR",response);

                            noticeModelArrayList.clear();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equals("true")) {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, "Notice deleted successfully",
                                            Snackbar.LENGTH_LONG)
                                           .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Snackbar.make(view, "Error parsing data from server",
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("HAR",error.toString());
                            progressDialog.dismiss();
                            Snackbar.make(view, "Internal server error",
                                    Snackbar.LENGTH_LONG)
                                    .show();



                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<>();
                            // Log.d("HAR",String.valueOf(DepartmentID));
                            parameters.put("notice_id",noticeModelArrayListFilter.get(position).getId());
                            return parameters;
                        }
                    };
                    Singleton.getInstance(c).addToRequestQueue(jsonObjectRequest);
                }
            });
        }
      /* */

    }

    @Override
    public int getItemCount() {
        return noticeModelArrayListFilter.size();
    }

    public interface OnItemClickListener{
        void onItemClick(ArrayList<Notice> notice,View v, int position);

    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    noticeModelArrayListFilter = noticeModelArrayList;
                } else {

                    ArrayList<Notice> filteredList = new ArrayList<>();

                    for (Notice androidVersion : noticeModelArrayList) {

                        if (androidVersion.getTitle().toLowerCase().contains(charString) || androidVersion.getName().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    noticeModelArrayListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noticeModelArrayListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                noticeModelArrayListFilter = (ArrayList<Notice>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

            public ArrayList<Notice> returnToActivity(){
        if(noticeModelArrayListFilter.size()>=1) {
            return noticeModelArrayListFilter;
        }
        return noticeModelArrayList;
        }
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, desgination, priority, title, desc, date, deleteButton, scope;
        ImageView bannerImage;
        OnItemClickListener onItemClickListener;
        public MyViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            //contact = itemView.findViewById(R.id.contact);
            name = itemView.findViewById(R.id.name);
            //email = itemView.findViewById(R.id.email);
            desgination = itemView.findViewById(R.id.designation);
            bannerImage = itemView.findViewById(R.id.bannerimg);
            priority = itemView.findViewById(R.id.priority);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            date = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);
            if(c.getClass().getSimpleName().equals("ViewNotice")) {
                deleteButton = itemView.findViewById(R.id.delete);
                scope = itemView.findViewById(R.id.scope);
            }
            this.onItemClickListener = onItemClickListener;

        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(noticeModelArrayListFilter,view,getAdapterPosition());


        }
    }
}
