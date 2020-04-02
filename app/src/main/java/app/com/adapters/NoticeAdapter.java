package app.com.adapters;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import app.com.models.Notice;
import app.com.notifyme.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> implements Filterable {
    private LayoutInflater inflater;
    private Context c;
    private ArrayList<Notice> noticeModelArrayList;
    private ArrayList<Notice> noticeModelArrayListFilter;
    private OnItemClickListener onItemClickListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
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

    public NoticeAdapter(Context ctx, ArrayList<Notice> noticeModelArrayList, OnItemClickListener onItemClickListener){
        c = ctx;
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

        View view = inflater.inflate(R.layout.singlenotice_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view,onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.MyViewHolder holder, int position) {
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
        }
        else{
            holder.desgination.setText(noticeModelArrayListFilter.get(position).getIsCoordinator());
        }

        if(pref.getString("seennotices","")!=""){

          /*  holder.title.setTextColor(Color.DKGRAY);
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.desc.setTextColor(Color.DKGRAY);
            holder.desc.setTypeface(null, Typeface.NORMAL);
            holder.priority.setTextColor(Color.DKGRAY);
            holder.priority.setTypeface(null, Typeface.NORMAL);
            holder.name.setTextColor(Color.DKGRAY);
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.desgination.setTextColor(Color.DKGRAY);
            holder.desgination.setTypeface(null, Typeface.NORMAL);
            holder.date.setTextColor(Color.DKGRAY);
            holder.date.setTypeface(null, Typeface.NORMAL);
            */
        }
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
        onItemClickListener.onItemClick(noticeModelArrayListFilter,view,getAdapterPosition());

        }
    }
}
