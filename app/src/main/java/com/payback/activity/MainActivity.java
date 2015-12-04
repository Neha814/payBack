package com.payback.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.fragment.ChangePasswordFragment;
import com.payback.fragment.CreditCardFragment;
import com.payback.fragment.FragmentDrawer;
import com.payback.fragment.HomeFragment;
import com.payback.fragment.UserProfileFragment;
import com.payback.functions.Constants;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Methods sp;
    private AsyncHttpClient logout;
    ProgressDialog dialog;
    Boolean isConnected;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private String regId;

    protected void showDialog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);

        Button ok;
        TextView message;

        dialog.setContentView(R.layout.dialog);
        ok = (Button) dialog.findViewById(R.id.ok);
        message = (TextView) dialog.findViewById(R.id.message);

        message.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new Methods(getApplicationContext());
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawerFragment = (FragmentDrawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        Boolean isOpened = drawerFragment.isDrawerOpen();


        init();
        // display the first navigation drawer view on app launch
        iniTView();
    }

    private void init() {
        logout = new AsyncHttpClient();
        logout.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        regId = sp.getRegId();
    }

    private void iniTView() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                iniTView();
                break;
            case 1:
                fragment = new UserProfileFragment();
                title = "Profile";
                break;
            case 2:
                fragment = new CreditCardFragment();
                title = "Credit Card Details";
                break;
            case 3:
                fragment = new ChangePasswordFragment();
                title = "Change Password";
                break;
            case 4:
                LogoutDialog();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public void LogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you really want to Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                logout_API();
                            }
                        });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void logout_API()
    {
        try {
            RequestParams params = new RequestParams();
            params.put("userId", sp.getUserId());
            params.put("tokenId", regId);
            params.put("device", "0");

            Log.e("URL", params.toString());
            logout.post(MainActivity.this, Constants.LogoutUrl, params, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    dialog.setMessage("Logging Out..");
                    dialog.show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    dialog.dismiss();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.e("Logout Response", json.toString());
                    try {
                        if (json.getBoolean("ResponseCode")) {
                            clearpref();
                        }
                        else{
                            showDialog(json.getString("MessageWhatHappen"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dialog.dismiss();
                    Log.e("responseString", responseString);
                    Log.e("ERROR", throwable.toString());
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clearpref() {
        try {
            sp.RemoveLoginData();
            Login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Login() {
        Intent in = new Intent(this, LoginActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        this.finish();
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment != null
                && drawerFragment.isDrawerOpen())
            drawerFragment.closeDrawer();

        else {
            try {

                int count = getFragmentManager().getBackStackEntryCount();

                if (count == 0) {
                    super.onBackPressed();
                    // additional code
                } else {
                    getFragmentManager().popBackStack();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}