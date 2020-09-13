package com.ctrlaltfix.indishare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrlaltfix.indishare.ChatSection.ChatHomeActivity;
import com.ctrlaltfix.indishare.Utils.LoadData;
import com.ctrlaltfix.indishare.Utils.Method;
import com.ctrlaltfix.indishare.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 101 ;
    String wifiP2pDevice;
    EditText deviceName;
    ImageButton edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id. drawer_layout ) ;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer , toolbar , R.string. navigation_drawer_open ,
                R.string. navigation_drawer_close ) ;
        drawer.addDrawerListener(toggle) ;
        toggle.syncState() ;
        NavigationView navigationView = findViewById(R.id. nav_view ) ;
        navigationView.setNavigationItemSelectedListener( this ) ;

        new LoadData(this).execute();

        View headerView = navigationView.getHeaderView(0);
        deviceName = headerView.findViewById(R.id.deviceName);
        edit = headerView.findViewById(R.id.edit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                    this.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                deviceName.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24, null));
                }else{
                    edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
                }
                setName();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deviceName.isEnabled()) {
                    deviceName.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24, null));
                    }else{
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
                    }
                }else{
                    deviceName.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24, null));
                    }else{
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
                    }
                    setName();
                }
            }
        });

        deviceName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Method.getSharedPreferences(MainActivity.this).edit().putString(getResources().getString(R.string.app_name), deviceName.getText().toString()).commit();
                    deviceName.setText(deviceName.getText());
                    deviceName.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24, null));
                    }else{
                        edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24));
                    }
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setName();
    }

    public void setName(){
        wifiP2pDevice = Method.getSharedPreferences(this).getString(getResources().getString(R.string.app_name), null);
        if (wifiP2pDevice == null){
            String name = Build.BRAND +" "+ Build.MODEL;
            deviceName.setText(name);
            deviceName.setEnabled(false);
        }else{
            deviceName.setText(wifiP2pDevice);
        }
    }
    public void send(View v){
        startActivity(
                new Intent(MainActivity.this, SendActivity.class)
        );
    }

    public void receive(View v) {
        startActivity(
                new Intent(MainActivity.this, ReceiveActivity.class)
        );
    }

    public void sharePC(View v) {
        startActivity(
                new Intent(MainActivity.this, ConnectToPC.class)
        );
    }

    public void sendAnyWhere(View v){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        if (connected){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null){
                   startActivity(
                           new Intent(this, LoginActivity.class)
                                   .putExtra("go","send")
                   );
            }else{
                Method.getSharedPreferences(this).edit().putString("id", auth.getUid()).commit();
                startActivity(
                        new Intent(this, SendAnywhere.class)
                );
            }
        }else{
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    public void chat(View v){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        if (connected){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null){
                   startActivity(
                           new Intent(this, LoginActivity.class)
                            .putExtra("go","chat")
                   );
            }else{
                Method.getSharedPreferences(this).edit().putString("id", auth.getUid()).commit();
                startActivity(
                        new Intent(this, ChatHomeActivity.class)
                );
            }
        }else{
            Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed () {
        DrawerLayout drawer = findViewById(R.id. drawer_layout ) ;
        if (drawer.isDrawerOpen(GravityCompat. START )) {
            drawer.closeDrawer(GravityCompat. START ) ;
        } else {
            super .onBackPressed() ;
        }
    }

    @Override
    public boolean onNavigationItemSelected ( @NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId() ;
        if (id == R.id. nav_camera ) {
            // Handle the camera action
        } else if (id == R.id. nav_gallery ) {
        } else if (id == R.id. nav_slideshow ) {
        } else if (id == R.id. nav_manage ) {
        } else if (id == R.id. nav_share ) {
        } else if (id == R.id. nav_send ) {
        }
        DrawerLayout drawer = findViewById(R.id. drawer_layout ) ;
        drawer.closeDrawer(GravityCompat. START ) ;
        return true;
    }
}