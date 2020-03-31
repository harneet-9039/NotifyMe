package app.com.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import app.com.adapters.NotificationAdapter;
import app.com.models.NotificationModel;
import app.com.notifyme.R;

public class NotificationFragment extends DialogFragment {
        public static final String TAG = "example_dialog";
        private static Context ctx;
        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Toolbar toolbar;
        private AppBarLayout appBarLayout;
        private ListView listView;

        public static app.com.fragments.NotificationFragment display(FragmentManager fragmentManager, Context ctx) {
            app.com.fragments.NotificationFragment notificationDialog = new app.com.fragments.NotificationFragment();
            NotificationFragment.ctx = ctx;
            notificationDialog.show(fragmentManager, TAG);
            return notificationDialog;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        }

        @Override
        public void onStart() throws NullPointerException {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
                dialog.setTitle("Notifications");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            getDialog().setTitle("Notifications");
            View view = inflater.inflate(R.layout.notification_layout, container, false);
            toolbar = view.findViewById(R.id.viewtoolbar);
            appBarLayout = view.findViewById(R.id.appbar);
            listView = view.findViewById(R.id.notificationlist);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationFragment.this.dismiss();
                }
            });
            toolbar.setTitle("Notifications");
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

            pref = ctx.getSharedPreferences("UserVals", 0); // 0 - for private mode
            editor = pref.edit();
            Gson record = new Gson();
            String jsonText = pref.getString("notificationdata", null);
            if(jsonText!=null) {
                Type type = new TypeToken<List<NotificationModel>>() {
                }.getType();
                List<NotificationModel> notificationList = record.fromJson(jsonText, type);
                NotificationAdapter notificationAdapter = new NotificationAdapter(ctx, notificationList);
                listView.setAdapter(notificationAdapter);
            }

        }
}
