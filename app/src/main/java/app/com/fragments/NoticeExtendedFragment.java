package app.com.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import app.com.models.Notice;
import app.com.notifyme.R;

public class NoticeExtendedFragment extends DialogFragment {
public static final String TAG = "example_dialog";

private Toolbar toolbar;
private TextView name, designation, course, date, contact, desc;
private ImageView banner;
private static Context ctx;
private static Notice record;

public static NoticeExtendedFragment display(FragmentManager fragmentManager, Notice record, Context c) {
        NoticeExtendedFragment filterDialog = new NoticeExtendedFragment();
        NoticeExtendedFragment.record = record;
        NoticeExtendedFragment.ctx = c;
        filterDialog.show(fragmentManager, TAG);
        return filterDialog;
        }

@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        }

@Override
public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.notice_extended_view, container, false);
        toolbar = view.findViewById(R.id.viewtoolbar);
        name = view.findViewById(R.id.name_extended);
        designation = view.findViewById(R.id.designation_extended);
        course = view.findViewById(R.id.course_extended);
        contact = view.findViewById(R.id.contact_extended);
        date = view.findViewById(R.id.date_extended);
        banner = view.findViewById(R.id.bannerimg_extended);
        desc = view.findViewById(R.id.desc_extended);
        return view;
        }

@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        NoticeExtendedFragment.this.dismiss();
        }
        });
        toolbar.setTitle(record.getTitle());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        name.setText(record.getName());
        designation.setText(record.getIsCoordinator());
        course.setText(record.getCourse());
        contact.setText(record.getContact());
        date.setText(record.getTimestamp());
        desc.setText(record.getDescription());
    if(record.getImages().equals("null")){
        Glide.with(ctx)
                .load(R.drawable.banner)
                .into(banner);
    }

    else {
        Glide.with(ctx)
                .load(record.getImages())
                .into(banner);
    }

}
        }

