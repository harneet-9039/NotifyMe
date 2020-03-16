package app.com.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import app.com.models.Notice;
import app.com.notifyme.R;

public class NoticeExtendedAdapter {
    /*private LayoutInflater inflater;
    private Context c;
    private ArrayList<Notice> noticeModelArrayList;

    public NoticeExtendedAdapter(Context ctx, ArrayList<Notice> noticeModelArrayList){
        c = ctx;
        inflater = LayoutInflater.from(ctx);
        this.noticeModelArrayList = noticeModelArrayList;
    }

    @Override
    public NoticeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.singlenotice_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.MyViewHolder holder, int position) {
        if(noticeModelArrayList.get(position).getImages().equals("null")){
            Glide.with(c)
                    .load(R.drawable.banner)
                    .into(holder.bannerImage);
        }
        else {
            Glide.with(c)
                    .load(noticeModelArrayList.get(position).getImages())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.bannerImage.setImageResource(R.drawable.banner);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d("HAR", "loaded");
                            return false;
                        }
                    })
                    .into(holder.bannerImage);
        }

        holder.name.setText(noticeModelArrayList.get(position).getName());
        holder.name.setTypeface(null,Typeface.BOLD);
        if(noticeModelArrayList.get(position).getPriority().equals("1")) {
            holder.priority.setText("High");
            holder.priority.setTextColor(c.getResources().getColor(R.color.highpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        else if(noticeModelArrayList.get(position).getPriority().equals("2")){
            holder.priority.setText("Medium");
            holder.priority.setTextColor(c.getResources().getColor(R.color.mediumpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        else{
            holder.priority.setText("Low");
            holder.priority.setTextColor(c.getResources().getColor(R.color.lowpriority));
            holder.priority.setTypeface(null, Typeface.BOLD);
        }
        holder.title.setText(noticeModelArrayList.get(position).getTitle());
        holder.desc.setText(noticeModelArrayList.get(position).getDescription());
        holder.date.setText(noticeModelArrayList.get(position).getTimestamp());
        if(noticeModelArrayList.get(position).getIsCoordinator().equals("0")||
                noticeModelArrayList.get(position).getIsCoordinator().equals("1")){
            holder.desgination.setText("Student Coordinator");
        }
        else{
            holder.desgination.setText(noticeModelArrayList.get(position).getIsCoordinator());
        }
    }

    @Override
    public int getItemCount() {
        return noticeModelArrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, desgination, priority, title, desc, date;
        ImageView bannerImage;

        public MyViewHolder(View itemView) {
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

        }

    }*/
}
