package com.denghaoqing.btsmartlock;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denghaoqing.btsmartlock.fragments.HomePageFragment;
import com.denghaoqing.btsmartlock.fragments.UnlockPageFragment;
import com.denghaoqing.btsmartlock.utilities.security;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MSG_AUTH_SUCCESS = 100;
    public static final int MSG_AUTH_FAILED = 101;
    public static final int MSG_AUTH_ERROR = 102;
    public static final int MSG_AUTH_HELP = 103;
    public static final int READ_PHONE_STATE_REQUEST_CODE = 100;
    public static final int LOGIN_RESUULT_REQUEST_CODE = 801;

    public boolean isAccountValidationDone = false;

    private CheckValid mCheckValid=null;
    private TextView username;
    private TextView usergroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View navHeader = navigationView.getHeaderView(0);//initialize the header view
        username =(TextView)navHeader.findViewById(R.id.txtusername);
        usergroup=(TextView)navHeader.findViewById(R.id.groupname);

        SharedPreferences userinfo = this.getSharedPreferences("userinfo", MODE_PRIVATE);


        if(userinfo.getInt("isLoggedin",0)==1){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            username.setText(userinfo.getString("username","Null"));
            usergroup.setText(userinfo.getString("gpname","Null"));
        }else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            username.setText(getString(R.string.default_username));
            usergroup.setText(getString(R.string.default_title));
        }

        mCheckValid = new CheckValid(String.valueOf(userinfo.getInt("uid",-1)),userinfo.getString("token","null"));
        mCheckValid.execute((Void) null);
        setBlankPage();



        //Permission Self Check
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_REQUEST_CODE);
        }



        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    //Handle the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    //Post procedure
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == READ_PHONE_STATE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
                Toast.makeText(this,"Warning: The Application Can't continue working without the requested permission",Toast.LENGTH_LONG);
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_REQUEST_CODE);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        SharedPreferences userinfo = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if(userinfo.getInt("isLoggedin",0)==1){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            username.setText(userinfo.getString("username","Null"));
            usergroup.setText(userinfo.getString("gpname","Null"));
        }else{
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        switch(id){
            case R.id.nav_login:
                Intent mIntent =new Intent(this,LoginActivity.class);
                startActivityForResult(mIntent,LOGIN_RESUULT_REQUEST_CODE);
                break;
            case R.id.nav_unlock:
                fragment = UnlockPageFragment.newInstances();
                fragmentManager.beginTransaction().replace(R.id.coo_homepage_content, fragment).commit();
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_logout :
                SharedPreferences userinfo = this.getSharedPreferences("userinfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = userinfo.edit();
                editor.clear();
                editor.apply();
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                username.setText(getString(R.string.default_username));
                usergroup.setText(getString(R.string.default_title));
                setBlankPage();
                break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setBlankPage(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        fragment = HomePageFragment.newInstances();
        fragmentManager.beginTransaction().replace(R.id.coo_homepage_content, fragment).commit();

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CheckValid extends AsyncTask<Void, Void, Boolean> {

        private final String mUID;
        private final String mToken;

        CheckValid(String uid, String token) {
            mUID = uid;
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject result;
            try {


                URL url= new URL("http://58.63.232.138:62078/lock/api.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                String content = "action=3&uid="+mUID+"&token="+mToken;
                out.writeBytes(content);
                out.flush();
                out.close();
                InputStreamReader input=new InputStreamReader(conn.getInputStream());
                BufferedReader buffer = new BufferedReader(input);
                String resultData="",inputLine=null;
                while(((inputLine=buffer.readLine())!=null)) {
                    resultData += inputLine + "\n";
                }
                result=new JSONObject(resultData);

            } catch (Exception e){

                return false;
            }
            boolean isValid=false;
            try {

                if(result.getInt("error")==0) {
                    isAccountValidationDone = true;
                    if(result.getInt("valid")==1){
                        isValid = true;
                    }
                }
            }catch (Exception e){
                isValid = false;
            }

            return isValid;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success)
            {
                if(isAccountValidationDone){
                    SharedPreferences userinfo = MainActivity.this.getSharedPreferences("userinfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userinfo.edit();
                    editor.clear();
                    editor.apply();
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_unlock).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                    username.setText(getString(R.string.default_username));
                    usergroup.setText(getString(R.string.default_title));
                    setBlankPage();
                }else{
                    Toast.makeText(getApplicationContext(), "Network error, try again later.",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

}
