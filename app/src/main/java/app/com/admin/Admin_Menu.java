package app.com.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import app.com.common.GlobalMethods;
import app.com.fragments.NotificationFragment;
import app.com.notifyme.NoticeDashboard;
import app.com.notifyme.R;
import app.com.notifyme.UserProfile;

public class Admin_Menu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView navbarusername, navbaryearcourse, navbardesignation;
    private NavigationView navigationView;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private View v;
    private LayerDrawable icon;

    private void hideItem()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_access).setVisible(false);
        nav_Menu.findItem(R.id.nav_request_status).setVisible(false);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        pref = getApplicationContext().getSharedPreferences("UserVals", 0); // 0 - for private mode
        editor = pref.edit();

        View viewNav = navigationView.getHeaderView(0);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dash, R.id.nav_profile, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Menu.this);
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlobalMethods.logout(Admin_Menu.this,v);
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
                   /* case R.id.nav_access:
                        startActivity(new Intent(.this, RequestAccessActivity.class));
                        return true;
                    case R.id.nav_request_status:
                        startActivity(new Intent(NoticeDashboard.this, RequestStatus.class));
                        return true;*/
                    case R.id.nav_dash:
                        startActivity(new Intent(Admin_Menu.this,NoticeDashboard.class));
                        return true;
                   /* case R.id.nav_notice:
                        startActivity(new Intent(Admin_Menu.this, ViewNotice.class));
                        return true;*/
                    case R.id.nav_profile:
                        startActivity(new Intent(Admin_Menu.this, UserProfile.class));
                        return true;
                    default:return false;
                }
            }
        });
        navigationView.getMenu().getItem(0).setChecked(true);
    }
    private void hideMore() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_notice).setVisible(false);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu_dashboard, menu);
        MenuItem menuItem = menu.findItem(R.id.ic_group);
        icon = (LayerDrawable) menuItem.getIcon();

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                NotificationFragment.display(getSupportFragmentManager(), Admin_Menu.this);
                GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());
                editor.putInt("notificationStatusCount",0);
                return true;
            }
        });
        GlobalMethods.setCountForNotifcation(icon,"0",getApplicationContext());


        return true;

    }
}
