package com.payback.functions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.payback.R;
import com.payback.model.NavDrawerItem;

import java.util.ArrayList;

/**
 * Created by sanjay on 11/25/15.
 */
public class Methods {
    public static void set_title_to_actionbar(String Title, Context ctx, Toolbar mToolbar) {

        ((AppCompatActivity) ctx).setSupportActionBar(mToolbar);
        /*((AppCompatActivity) ctx).getSupportActionBar().setDisplayShowHomeEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayUseLogoEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayHomeAsUpEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayHomeAsUpEnabled(value);*/

        if (!Title.equals(null) && Title.length() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            Spannable wordtoSpan=new SpannableString(Title.subSequence(
                    0, Title.length()));

            wordtoSpan.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#ffffff")), 1, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(wordtoSpan);

            ((AppCompatActivity) ctx).getSupportActionBar().setTitle(builder);
        }
    }

    private Context mAct;
    private String PREFS_NAME = "PayBack";

    public Methods(Context context) {
        this.mAct = context;
    }

    public void SaveLoginData(String userid, String email, String name, String pic,String city,String state,String zip, Boolean IsLogin) {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userid", userid);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("profilePic", pic);
        editor.putString("city", city);
        editor.putString("state", state);
        editor.putString("zip", zip);
        editor.putBoolean("IsLogin", IsLogin);
        // Commit the edits!
        editor.commit();
        Log.e("PrefStore" , "userName " + name);
    }

    public void setRegId(String regId){
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("regId", regId);
        editor.commit();
    }

    public String getRegId() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String regid = settings.getString("regId", "");
        Log.e("PrefStore", "regId " + regid);
        return regid;
    }
    public void setAppId(int notif){
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.APP_VERSION, notif);
        editor.commit();
    }

    public int getAppId() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        int notify = settings.getInt(Constants.APP_VERSION, 0);
        Log.e("PrefStore", "notifyID " + notify);
        return notify;
    }


    public void setNotifyId(int notif){
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.NotifyId, notif);
        editor.commit();
    }

    public int getNotifyId() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        int notify = settings.getInt(Constants.NotifyId, 0);
        Log.e("PrefStore", "notifyID " + notify);
        return notify;
    }

    public String getUserName() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String userName = settings.getString("name", null);
        Log.e("PrefStore", "userName " + userName);
        return userName;
    }

    public String getUserId() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String userId = settings.getString("userid", null);
        Log.e("PrefStore", "userId " + userId);
        return userId;
    }

    public String getEmail() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String email = settings.getString("email", null);
        Log.e("PrefStore", "userEmail " + email);
        return email;
    }

    public String getProfilepic() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String profilePic = settings.getString("profilePic", null);
        Log.e("PrefStore", "userProfile " + profilePic);
        return profilePic;
    }

    public String getCity() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String city = settings.getString("city", null);
        Log.e("PrefStore", "userCity " + city);
        return city;
    }

    public String getState() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String state = settings.getString("state", null);
        Log.e("PrefStore", "userState " + state);
        return state;
    }

    public String getZip() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        String zip = settings.getString("zip", null);
        Log.e("PrefStore", "userZip " + zip);
        return zip;
    }

    public boolean getLogin() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        boolean st = settings.getBoolean("IsLogin", false);
        Log.e("PrefStore", "userLofin" + st);
        return st;
    }

    public void RemoveLoginData() {
        SharedPreferences settings = mAct.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putBoolean("IsLogin", false);
        // Commit the edits!
        editor.commit();
    }

    public void showToast(String message) {
        Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
    }


    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config. ARGB_8888 );
        final Canvas canvas = new Canvas(output);

        final int color = Color. RED ;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias( true );
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode( new PorterDuffXfermode(PorterDuff.Mode. SRC_IN ));
        canvas.drawBitmap(bitmap, rect, rect, paint);

// bitmap.recycle();

        return output;
    }

    @SuppressLint ( "NewApi" )
    public static int getWidth(Context mContext) {
        int width = 0;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context. WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION. SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            width = size. x ;
        } else {
            width = display.getWidth(); // deprecated
        }
        return width;
    }

    @SuppressLint( "NewApi" )
    public static int getHeight(Context mContext) {
        int height = 0;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context. WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION. SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            height = size. y ;
        } else {
            height = display.getHeight(); // deprecated
        }
        return height;
    }

    // imageLayoutHeightandWidth
    public static int [] getImageHeightAndWidthForProFileImageHomsecreen(
            Activity activity) {
// //Log.i(TAG, "getImageHeightAndWidth");

        int imageHeightAndWidth[] = new int [2];
        int screenHeight = getHeight(activity);
        int screenWidth = getWidth(activity);
// //Log.i(TAG, "getImageHeightAndWidth screenHeight "+screenHeight);
// //Log.i(TAG, "getImageHeightAndWidth screenWidth "+screenWidth);
        int imagehiegth;
        int imagewidth;
        if ((screenHeight <= 500 && screenHeight >= 480)
                && (screenWidth <= 340 && screenWidth >= 300)) {
// //Log.i(TAG, "getImageHeightAndWidth mdpi");
            imagehiegth = 200;
            imagewidth = 200;
            imageHeightAndWidth[0] = imagehiegth;
            imageHeightAndWidth[1] = imagewidth;

        } else if ((screenHeight <= 400 && screenHeight >= 300)
                && (screenWidth <= 240 && screenWidth >= 220))

        {

// //Log.i(TAG, "getImageHeightAndWidth ldpi");
            imagehiegth = 150;
            imagewidth = 150;
            imageHeightAndWidth[0] = imagehiegth;
            imageHeightAndWidth[1] = imagewidth;
        } else if ((screenHeight <= 840 && screenHeight >= 780)
                && (screenWidth <= 500 && screenWidth >= 440)) {

// //Log.i(TAG, "getImageHeightAndWidth hdpi");
            imagehiegth = 220;
            imagewidth = 220;
            imageHeightAndWidth[0] = imagehiegth;
            imageHeightAndWidth[1] = imagewidth;
        } else if ((screenHeight <= 1280 && screenHeight >= 840)
                && (screenWidth <= 720 && screenWidth >= 500)) {

// //Log.i(TAG, "getImageHeightAndWidth xdpi");
            imagehiegth = 250;
            imagewidth = 250;
            imageHeightAndWidth[0] = imagehiegth;
            imageHeightAndWidth[1] = imagewidth;
        } else {
            imagehiegth = 250;
            imagewidth = 250;
            imageHeightAndWidth[0] = imagehiegth;
            imageHeightAndWidth[1] = imagewidth;
        }
        Log.e( "width-height" , imagewidth + "-" + imagehiegth);
        return imageHeightAndWidth;
    }

    public static ArrayList<NavDrawerItem> drawer_items(Context ctx) {
        ArrayList<NavDrawerItem> items = new ArrayList<>();

        items.add( new NavDrawerItem(R.drawable.ic_home ,ctx.getString(R.string.nav_item_home) ));
        items.add( new NavDrawerItem(R.drawable.ic_user,ctx.getString(R.string.nav_item_profile) ));
        items.add( new NavDrawerItem(R.drawable.ic_bank ,ctx.getString(R.string.nav_item_cc) ));
        items.add( new NavDrawerItem(R.drawable.ic_pswd ,ctx.getString(R.string.nav_item_cp) ));
        items.add( new NavDrawerItem(R.drawable.ic_logout ,ctx.getString(R.string.nav_item_logout) ));
        return items;
    }

}
