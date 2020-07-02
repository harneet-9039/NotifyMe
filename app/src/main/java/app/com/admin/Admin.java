package app.com.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import app.com.common.GlobalMethods;
import app.com.notifyme.R;

public class Admin extends AppCompatActivity implements View.OnClickListener{
    private CardView viewnotice,currentreq;
    private TextView seeprofile;
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // initialize cardview
        //seeprofile= findViewById(R.id.seeprofile_link);


        viewnotice= findViewById(R.id.view_notice);
        currentreq= findViewById(R.id.current_req);

      //  notification=(ImageView)findViewById(R.id.notification);;
        viewnotice= findViewById(R.id.view_notice);
        currentreq= findViewById(R.id.current_req);

        //onClickListener

      //  notification.setOnClickListener(this);

        viewnotice.setOnClickListener(this);
        currentreq.setOnClickListener(this);
    }


    public void onClick(View v) {
        if(v.getId()==R.id.view_notice)
        {
            Intent in=new Intent(Admin.this, Admin_ViewNotice.class);
            startActivity(in);
        }

        else if(v.getId()==R.id.current_req)
        {
            Intent in=new Intent(Admin.this, Admin_ViewRequest.class);
            startActivity(in);
        }

        else if(v.getId()==R.id.menu)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GlobalMethods.logout(Admin.this, view);
                    finish();
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
