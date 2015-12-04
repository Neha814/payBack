package com.payback.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.functions.CircleTransform;
import com.payback.functions.Constants;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UserProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private View rootView;
    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputCity, inputState,inputZipcode;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutCity,inputLayoutState,inputLayoutZipcode;
    private Button btnUpdate;
    private ImageView profile,cam,gallery;
    private ImageView img_fb, img_tw, img_in, img_email, img_msg;
    private LinearLayout sharelayout;

    String TAG = "User Profile Fragment";
    private Methods sp;
    private AsyncHttpClient update;
    ProgressDialog dialog;
    Boolean isConnected;
    boolean isCamera = false, isGallery = false;
    Bitmap takenImage;
    File imgFileGallery;
    String photoFileName;
    public static ContentResolver appContext;

    String userId,name,email,city, state,zip,pic;

    protected void showDialog(String msg) {

        final Dialog dialog;
        dialog = new Dialog(getActivity());
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
                UneditProfile();
            }
        });
        dialog.show();
    }

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_userprofile, container, false);

        sp = new Methods(getActivity());
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        initUI();

        return rootView;
    }

    private void initUI() {

        imgFileGallery = new File("");
        appContext = getActivity().getContentResolver();
        update = new AsyncHttpClient();
        update.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        userId = sp.getUserId();
        pic = sp.getProfilepic();
        name = sp.getUserName();
        email = sp.getEmail();
        city = sp.getCity();
        state = sp.getState();
        zip = sp.getZip();

        sharelayout = (LinearLayout) rootView.findViewById(R.id.sharelayout);
        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_email);
        inputLayoutCity = (TextInputLayout) rootView.findViewById(R.id.input_layout_city);
        inputLayoutState= (TextInputLayout) rootView.findViewById(R.id.input_layout_state);
        inputLayoutZipcode= (TextInputLayout) rootView.findViewById(R.id.input_layout_zip);
        inputName = (EditText) rootView.findViewById(R.id.input_name1);
        inputEmail = (EditText) rootView.findViewById(R.id.input_email);
        inputCity = (EditText) rootView.findViewById(R.id.input_city);
        inputState = (EditText) rootView.findViewById(R.id.input_state);
        inputZipcode = (EditText) rootView.findViewById(R.id.input_zip);
        btnUpdate = (Button) rootView.findViewById(R.id.btn_update);

        profile = (ImageView) rootView.findViewById(R.id.img_profile);
        cam = (ImageView) rootView.findViewById(R.id.img_cam);
        gallery = (ImageView) rootView.findViewById(R.id.img_gall);

        img_fb = (ImageView) rootView.findViewById(R.id.img_fb);
        img_tw = (ImageView) rootView.findViewById(R.id.img_tw);
        img_in = (ImageView) rootView.findViewById(R.id.img_in);
        img_email = (ImageView) rootView.findViewById(R.id.img_email);
        img_msg = (ImageView) rootView.findViewById(R.id.img_msg);

        img_fb.setOnClickListener(this);
        img_tw.setOnClickListener(this);
        img_in.setOnClickListener(this);
        img_email.setOnClickListener(this);
        img_msg.setOnClickListener(this);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputCity.addTextChangedListener(new MyTextWatcher(inputCity));
        inputState.addTextChangedListener(new MyTextWatcher(inputState));
        inputZipcode.addTextChangedListener(new MyTextWatcher(inputZipcode));

        inputName.setText(name);
        inputEmail.setText(email);
        inputCity.setText(city);
        inputState.setText(state);
        inputZipcode.setText(zip);

       // Picasso.with(getActivity()).load(pic).transform(new CircleTransform()).fit().into(profile);
        Picasso.with(getActivity())
                .load( pic)
                .placeholder(R.drawable.profile_default_pic).error(R.drawable.profile_default_pic)
                .transform( new CircleTransform()).fit()
                .into(profile);

        int width_height[] = Methods.getImageHeightAndWidthForProFileImageHomsecreen(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_height[1], width_height[0]);
        profile .setLayoutParams(layoutParams);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isGallery) {
                    photoFileName = System.currentTimeMillis() + ".png";
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            getPhotoFileUri(photoFileName)); // set the image
                    // file name
                    isCamera = true;
                    startActivityForResult(intent, 0);
                } else {
                    sp.showToast("Photo is already uploaded");
                }

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCamera) {
                    Intent GaleryIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    isGallery = true;
                    startActivityForResult(GaleryIntent, 1);
                } else {
                    sp.showToast("Photo is already captured");
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            EditProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void EditProfile() {

        inputName.setFocusable(true);
        inputName.setFocusableInTouchMode(true);
        inputName.setClickable(true);
        inputCity.setFocusable(true);
        inputCity.setFocusableInTouchMode(true);
        inputCity.setClickable(true);
        inputState.setFocusable(true);
        inputState.setFocusableInTouchMode(true);
        inputState.setClickable(true);
        inputZipcode.setFocusable(true);
        inputZipcode.setFocusableInTouchMode(true);
        inputZipcode.setClickable(true);
        btnUpdate.setVisibility(View.VISIBLE);
        cam.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);
        sharelayout.setVisibility(View.GONE);
    }

    private void UneditProfile() {

        inputName.setFocusable(false);
        inputName.setFocusableInTouchMode(false);
        inputName.setClickable(false);
        inputCity.setFocusable(false);
        inputCity.setFocusableInTouchMode(false);
        inputCity.setClickable(false);
        inputState.setFocusable(false);
        inputState.setFocusableInTouchMode(false);
        inputState.setClickable(false);
        inputZipcode.setFocusable(false);
        inputZipcode.setFocusableInTouchMode(false);
        inputZipcode.setClickable(false);
        btnUpdate.setVisibility(View.GONE);
        cam.setVisibility(View.INVISIBLE);
        gallery.setVisibility(View.INVISIBLE);
        sharelayout.setVisibility(View.VISIBLE);
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }
        else if (!validateEmail()) {
            return;
        }
        else if (!validateCity()) {
            return;
        }
        else if (!validateState()) {
            return;
        }
        else if (!validateZip()) {
            return;
        }
        else {
            if (isConnected) {
                UpdateProfile();
            } else {
                showDialog(Constants.NO_INTERNET);
            }
        }

    }

    private boolean validateName() {
        name = inputName.getText().toString().trim();
        if (name.isEmpty()) {
            inputLayoutName.setError("Enter name");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCity() {
        city = inputCity.getText().toString().trim();
        if (city.isEmpty()) {
            inputLayoutCity.setError("Enter city");
            requestFocus(inputCity);
            return false;
        } else {
            inputLayoutCity.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateState() {
        state = inputState.getText().toString().trim();
        if (state.isEmpty()) {
            inputLayoutState.setError("Enter state");
            requestFocus(inputState);
            return false;
        } else {
            inputLayoutState.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateZip() {
        zip = inputZipcode.getText().toString().trim();
        if (state.isEmpty()) {
            inputLayoutZipcode.setError("Enter zipcode");
            requestFocus(inputZipcode);
            return false;
        } else {
            inputLayoutZipcode.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view==img_fb){
            FacebookSharing();
        } else if(view==img_tw){
            TwitterSharing();
        } else if(view == img_in){
            instagramSharing();
        } else if (view == img_email){
            GooglePlusSharing();
        } else if(view == img_msg){
            MessageSharing();
        }
    }

    private void FacebookSharing() {
        try {

            String urlToShare = "http://www.google.com";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
            intent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
            final PackageManager pm = getActivity().getPackageManager();
            final List<?> activityList = pm.queryIntentActivities(
                    intent, 0);
            int len = activityList.size();
            for (int i = 0; i < len; i++) {

                final ResolveInfo app = (ResolveInfo) activityList.get(i);
                Log.i("pckg name==", "" + app.activityInfo.packageName);
                if (app.activityInfo.packageName.toLowerCase()
                        .startsWith("com.facebook.katana")) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
                            + urlToShare;
                    intent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse(sharerUrl));
                    startActivity(intent);

                    break;

                } else {
                    if (i + 1 == len) {
                        String link = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                     Toast.makeText(getActivity(),"App not installed",Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
            }
        } catch (Exception ae) {
            ae.printStackTrace();
        }

    }

    private void TwitterSharing() {
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "It's a Tweet!"
                    + "#BuckUp");
            intent.setType("text/plain");
            final PackageManager pm = getActivity().getPackageManager();
            final List<?> activityList = pm.queryIntentActivities(
                    intent, 0);
            int len = activityList.size();
            for (int i = 0; i < len; i++) {

                final ResolveInfo app = (ResolveInfo) activityList
                        .get(i);

                if ((app.activityInfo.name.contains("twitter"))) {
                    Log.i("twitter==<>", "" + app.activityInfo.name);

                    final ActivityInfo activity = app.activityInfo;
                    final ComponentName x = new ComponentName(
                            activity.applicationInfo.packageName,
                            activity.name);

                    intent = new Intent(Intent.ACTION_SEND);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.setComponent(x);

                    intent.putExtra(Intent.EXTRA_TEXT,
                            "It's a tweet #payback");
                    intent.setType("application/twitter");
                    startActivity(intent);

                    break;

                } else {
                    if (i + 1 == len) {
                        String link = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                        Toast.makeText(getActivity(),"App not installed",Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
            }
        } catch (Exception ae) {
            ae.printStackTrace();
        }

    }

    private void instagramSharing() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "# instagram.. checkout this amazing app.");
        intent.setType("text/plain");

        final PackageManager pm = getActivity().getPackageManager();
        final List<?> activityList = pm.queryIntentActivities(
                intent, 0);
        int len = activityList.size();
        for (int i = 0; i < len; i++) {

            final ResolveInfo app = (ResolveInfo) activityList
                    .get(i);

            if ((app.activityInfo.name.contains("instagram"))) {
                Log.i("instagram==<>", "" + app.activityInfo.name);

                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,"# instagram.. checkout this amazing app.");
                intent.setPackage("com.instagram.android");
                startActivity(intent);

                break;

            } else {
                if (i + 1 == len) {
                    String link = "https://play.google.com/store/apps/details?id=com.instagram.android&hl=en";
                    Toast.makeText(getActivity(), "App not installed", Toast.LENGTH_SHORT).show();
                    break;
                }

            }
        }
    }

    private void GooglePlusSharing() {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(
                    android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            List<ResolveInfo> resInfo = getActivity()
                    .getPackageManager()
                    .queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(
                            android.content.Intent.ACTION_SEND);
                    targetedShare.setType("text/plain"); // put here
                    // your mime
                    // type
                    if (info.activityInfo.packageName.toLowerCase()
                            .contains("plus")
                            || info.activityInfo.name.toLowerCase()
                            .contains("plus")) {

                        targetedShare.putExtra(Intent.EXTRA_TEXT,
                                "#payback...Check Out this amazing app !");
                        targetedShare
                                .setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    } else {
                        Toast.makeText(getActivity(),"App not installed",Toast.LENGTH_SHORT).show();
                    }
                }
                Intent chooserIntent = Intent.createChooser(
                        targetedShareIntents.remove(0),
                        "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents
                                .toArray(new Parcelable[] {}));
                startActivity(chooserIntent);
            }
        } catch (Exception e) {
            Log.v("VM", "Exception while sending image on" + "plus"
                    + " " + e.getMessage());
        }
    }

    private void MessageSharing() {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("sms_body", "#PayBack - Check out this amazing app!!!");
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);

        } catch (Exception ex) {
            Toast.makeText(getActivity(),
                    ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_city:
                    validateCity();
                    break;
                case R.id.input_state:
                    validateState();
                    break;
                case R.id.input_zip:
                    validateZip();
                    break;
            }
        }
    }

    private void UpdateProfile() {

        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("userId", userId);
        params.put("city", city);
        params.put("state", state);
        params.put("zip", zip);
        try {
            params.put("userfile",imgFileGallery);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        update.post(getActivity(), Constants.UpdateProfileURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    Log.e("onsuccess", json.toString());
                    if (json.getBoolean("ResponseCode")) {
                        showDialog(json.getString("MessageWhatHappen"));
                        JSONObject getData = json.getJSONObject("GetData");
                        JSONArray userdata = getData.getJSONArray("userdata");
                        String id = userdata.getJSONObject(0).getString("id");
                        String userType = userdata.getJSONObject(0).getString("userType");
                        String name = userdata.getJSONObject(0).getString("name");
                        String emailId = userdata.getJSONObject(0).getString("emailId");
                        String password = userdata.getJSONObject(0).getString("password");
                        String city = userdata.getJSONObject(0).getString("city");
                        String state = userdata.getJSONObject(0).getString("state");
                        String zip = userdata.getJSONObject(0).getString("zip");
                        String profilePic = userdata.getJSONObject(0).getString("profilePic");
                        String customerId = userdata.getJSONObject(0).getString("customerId");
                        String cvv = userdata.getJSONObject(0).getString("cvv");
                        String status = userdata.getJSONObject(0).getString("status");
                        String created = userdata.getJSONObject(0).getString("created");

                        sp.SaveLoginData(id,emailId,name,profilePic,city,state,zip,true);

                        UpdateProfileFields();
                    }
                    else if (json.getString("MessageWhatHappen").equalsIgnoreCase("Email already exists")) {
                        showDialog(json.getString("MessageWhatHappen"));
                    } else {
                        showDialog(json.getString("MessageWhatHappen"));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                access_token=&authkey=Auth_MicroMovers2015&user_id=54
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    private void UpdateProfileFields() {
        userId = sp.getUserId();
        pic = sp.getProfilepic();
        name = sp.getUserName();
        email = sp.getEmail();
        city = sp.getCity();
        state = sp.getState();
        zip = sp.getZip();



        inputName.setText(name);
        inputEmail.setText(email);
        inputCity.setText(city);
        inputState.setText(state);
        inputZipcode.setText(zip);

        // Picasso.with(getActivity()).load(pic).transform(new CircleTransform()).fit().into(profile);
        Picasso.with(getActivity())
                .load( pic)
                .placeholder(R.drawable.profile_default_pic).error(R.drawable.profile_default_pic)
                .transform( new CircleTransform()).fit()
                .into(profile);

        FragmentDrawer.UpdateImage(pic,getActivity());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        try {
            if (requestCode == 0) {

                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                String imagePath = getRealPathFromURI(takenPhotoUri);
                bitmap = resizeBitmap(imagePath);
                profile.setImageBitmap(Methods.getCircleBitmap(bitmap));

                imgFileGallery = new File(takenPhotoUri.getPath());
                Log.e("imgFileGallery==", "" + imgFileGallery);

            }

            else if (requestCode == 1) {
                Uri selectedImage = data.getData();

                String imagePath = getRealPathFromURI(selectedImage);
                bitmap = resizeBitmap(imagePath);

                profile.setImageBitmap(Methods.getCircleBitmap(bitmap));

                Uri SelectedImage = data.getData();
                String[] FilePathColumn = { MediaStore.Images.Media.DATA };

                Cursor SelectedCursor = appContext.query(SelectedImage,
                        FilePathColumn, null, null, null);
                SelectedCursor.moveToFirst();

                int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
                String picturePath = SelectedCursor.getString(columnIndex);
                SelectedCursor.close();

                Log.e("picturePath==", "" + picturePath);

                imgFileGallery = new File(picturePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap resizeBitmap(String path) {
        Bitmap photoBitmap;

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions. inJustDecodeBounds = true ;
        Bitmap bm = BitmapFactory.decodeFile(path, bmpFactoryOptions);

        int heightRatio = ( int ) Math.ceil(bmpFactoryOptions. outHeight / ( float ) 600);
        int widthRatio = ( int ) Math.ceil(bmpFactoryOptions. outWidth / ( float ) 800);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions. inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions. inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions. inJustDecodeBounds = false ;

        photoBitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);
        int outWidth = photoBitmap.getWidth();
        int outHeight = photoBitmap.getHeight();

        try {
            ExifInterface exif = new ExifInterface(path);
            String orientString = exif.getAttribute(ExifInterface. TAG_ORIENTATION );
            Log.e( "orientString" , orientString);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface. ORIENTATION_NORMAL ;
            int rotationAngle = 0;
            if (orientation == ExifInterface. ORIENTATION_ROTATE_90 ) rotationAngle = 90;
            if (orientation == ExifInterface. ORIENTATION_ROTATE_180 ) rotationAngle = 180;
            if (orientation == ExifInterface. ORIENTATION_ROTATE_270 ) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, ( float ) outWidth, ( float ) outHeight);
            photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, outWidth, outHeight, matrix, true );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoBitmap;
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI,
                null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator
                + fileName));
    }
}
