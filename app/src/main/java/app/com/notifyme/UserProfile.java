package app.com.notifyme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import app.com.common.GlobalMethods;
import app.com.fragments.NotificationFragment;

public class UserProfile extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View v;
    private LayerDrawable icon;
    private TextView navbarusername, navbaryearcourse, navbardesignation, plusButton;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("HAR","aya");
            Log.d("HAR",String.valueOf(pref.getInt("notificationStatusCount", -1)));
            if (pref.getInt("notificationStatusCount", -1) != -1) {
                GlobalMethods.setCountForNotifcation(icon, String.valueOf(pref.getInt("notificationStatusCount", -1)), UserProfile.this);
            } else {
                GlobalMethods.setCountForNotifcation(icon, "0", UserProfile.this);
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        CommonWorkOfMenuItems();
    }

    private void CommonWorkOfMenuItems() {

        Toolbar toolbar = findViewById(R.id.requesttoolbaruserprofile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        drawer = findViewById(R.id.drawer_layout_request);
        navigationView = findViewById(R.id.nav_view_request);

        View viewNav = navigationView.getHeaderView(0);
        navbarusername = viewNav.findViewById(R.id.userName);
        navbaryearcourse = viewNav.findViewById(R.id.courseyear);
        navbardesignation = viewNav.findViewById(R.id.designation);
        pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();

        if(pref.getString("name","")!="" ){
            navbarusername.setText(pref.getString("name",""));
        }
        else if(pref.getString("fname","")!=""){
            navbarusername.setText(pref.getString("fname",""));
        }

        if(pref.getString("fregistrationNumber","")==""){
            navbaryearcourse.setText(pref.getString("departmentname","")+", "+
                    pref.getString("coursename","")+", "+ pref.getInt("year", 0) +" year");
        }
        else{
            navbardesignation.setVisibility(View.VISIBLE);
            navbardesignation.setText(pref.getString("fdesignation","")+",");
            navbaryearcourse.setText(pref.getString("fdepartmentname","")+" department");
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dash, R.id.nav_notice, R.id.nav_profile, R.id.nav_access,
                R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        if(pref.getInt("isCoordinator",-1)!=2){
            hideItem();
        }
        if(pref.getInt("isCoordinator",-1)==0){
            hideMore();
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlobalMethods.logout(UserProfile.this,v);
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
                        return true;
                    case R.id.nav_access:
                        startActivity(new Intent(UserProfile.this,RequestAccessActivity.class));
                        return true;
                    case R.id.nav_request_status:
                        startActivity(new Intent(UserProfile.this,RequestStatus.class));
                        return true;
                    case R.id.nav_dash:
                        startActivity(new Intent(UserProfile.this,NoticeDashboard.class));
                        return true;
                    case R.id.nav_notice:
                        startActivity(new Intent(UserProfile.this,ViewNotice.class));
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(UserProfile.this,UserProfile.class));
                        return true;
                    default:return false;
                }
            }
        });
        navigationView.getMenu().getItem(2).setChecked(true);
    }

    private void hideMore() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_notice).setVisible(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_dashboard, menu);
        MenuItem menuItem = menu.findItem(R.id.ic_group);
        icon = (LayerDrawable) menuItem.getIcon();
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                NotificationFragment.display(getSupportFragmentManager(), UserProfile.this);
                GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
                editor.putInt("notificationStatusCount",0);
                return true;
            }
        });
        GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
        return true;

    }

    @Override
    public void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

    private void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_access).setVisible(false);
        nav_Menu.findItem(R.id.nav_request_status).setVisible(false);

    }

    @Override
    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

    @Override
    public void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }


    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("notification")
        );
    }

}
