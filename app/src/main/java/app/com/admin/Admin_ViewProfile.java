package app.com.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.notifyme.R;

public class Admin_ViewProfile extends AppCompatActivity {
    private ImageView Pic;
    private String Username,Email,Mobile,Reg_No,Department,Course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

    }
}
