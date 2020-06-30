package app.com.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import app.com.notifyme.R;

public class Admin extends AppCompatActivity implements View.OnClickListener{
    private CardView viewnotice,approvedreq,currentreq,previousreq,rejectedreq;
    private TextView seeprofile;
    private ImageView menu,notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // initialize cardview
        seeprofile=(TextView)findViewById(R.id.seeprofile_link);

        menu=(ImageView)findViewById(R.id.menu);
        notification=(ImageView)findViewById(R.id.notification);

        viewnotice=(CardView)findViewById(R.id.view_notice);
        approvedreq=(CardView)findViewById(R.id.approved_req);
        currentreq=(CardView)findViewById(R.id.current_req);
        previousreq=(CardView)findViewById(R.id.previous_req);
        rejectedreq=(CardView)findViewById(R.id.rejected_req);

        //onClickListener
        seeprofile.setOnClickListener(this);

        menu.setOnClickListener(this);
        notification.setOnClickListener(this);

        viewnotice.setOnClickListener(this);
        approvedreq.setOnClickListener(this);
        currentreq.setOnClickListener(this);
        previousreq.setOnClickListener(this);
        rejectedreq.setOnClickListener(this);
    }


    public void onClick(View v) {
        if(v.getId()==R.id.seeprofile_link)
        {
            Intent in=new Intent(Admin.this, Admin_ViewProfile.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.view_notice)
        {
            Intent in=new Intent(Admin.this, Admin_ViewNotice.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.approved_req)
        {
            Intent in=new Intent(Admin.this, Admin_ApprovedRequest.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.previous_req)
        {
            Intent in=new Intent(Admin.this, Admin_PreviousRequest.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.current_req)
        {
            Intent in=new Intent(Admin.this, Admin_ViewRequest.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.rejected_req)
        {
            Intent in=new Intent(Admin.this, Admin_RejectedRequest.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.menu)
        {
            Intent in=new Intent(Admin.this, Admin_Menu.class);
            startActivity(in);
        }
        else if(v.getId()==R.id.notification)
        {
            Intent in=new Intent(Admin.this, Admin_Notification.class);
            startActivity(in);
        }
    }
}
