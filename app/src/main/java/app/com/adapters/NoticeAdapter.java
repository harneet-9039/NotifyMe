package app.com.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> implements Filterable {
    private LayoutInflater inflater;
    private Context c;
    private ArrayList<Notice> noticeModelArrayList;
    private ArrayList<Notice> noticeModelArrayListFilter;
    private OnItemClickListener onItemClickListener;


    public NoticeAdapter(Context ctx, ArrayList<Notice> noticeModelArrayList, OnItemClickListener onItemClickListener){
        c = ctx;
        inflater = LayoutInflater.from(ctx);
        this.noticeModelArrayList = noticeModelArrayList;
        noticeModelArrayListFilter = noticeModelArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public NoticeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.singlenotice_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view,onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.MyViewHolder holder, int position) {
        if(noticeModelArrayListFilter.get(position).getImages().equals("null")){
            Glide.with(c)
                    .load(R.drawable.banner)
                    .into(holder.bannerImage);
        }
        else {
            Glide.with(c)
                    .load(noticeModelArrayListFilter.get(position).getImages())
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
        holder.desc.setText(noticeModelArrayListFilter.get(position).getDescription());
        holder.date.setText(noticeModelArrayListFilter.get(position).getTimestamp());
        if(noticeModelArrayListFilter.get(position).getIsCoordinator().equals("0")||
                noticeModelArrayListFilter.get(position).getIsCoordinator().equals("1")){
            holder.desgination.setText("Student Coordinator");
        }
        else{
            holder.desgination.setText(noticeModelArrayListFilter.get(position).getIsCoordinator());
        }
    }

    @Override
    public int getItemCount() {
        return noticeModelArrayListFilter.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, desgination, priority, title, desc, date;
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
            this.onItemClickListener = onItemClickListener;

        }

        @Override
        public void onClick(View view) {
        onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
