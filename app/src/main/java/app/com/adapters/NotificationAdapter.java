package app.com.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.models.NotificationModel;
import app.com.notifyme.R;

public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

        private static LayoutInflater inflater = null;
        private Activity activity;
        private List<NotificationModel> notificationModelList;

        public NotificationAdapter (Context context, List<NotificationModel> notificationList) {
            super(context, 0, notificationList);
            notificationModelList = new ArrayList<>();
            notificationModelList.addAll(notificationList);
        }

        public int getCount() {
            return notificationModelList.size();
        }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NotificationModel notification = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_notification_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.title_extended);
        TextView date = convertView.findViewById(R.id.date_extended);
        // Populate the data into the template view using the data object
        title.setText(notification.getTitle());
        date.setText("posted on: "+notification.getDate());
        // Return the completed view to render on screen
        return convertView;
    }

}
