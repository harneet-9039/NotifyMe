package app.com.notifyme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.*;

public class Login_Activity extends AppCompatActivity {
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt=(TextView) findViewById(R.id.Register_Link);

        txt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent in=new Intent(Login_Activity.this, Register_Activity.class);
                startActivity(in);
            }
        });

    }
}
