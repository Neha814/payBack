package com.payback.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.functions.Constants;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword, inputCity, inputState,inputZipcode;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword,inputLayoutCity,inputLayoutState,inputLayoutZipcode;
    private Button btnSignUp;
    private CheckBox check;
    private ImageView profile,cam,gallery;
    private TextView terms, policy;

    String TAG = "RegisterActivity";
    private Methods sp;
    private AsyncHttpClient register;
    ProgressDialog dialog;
    Boolean isConnected;
    boolean isCamera = false, isGallery = false, isCheck = false;
    Bitmap takenImage;
    File imgFileGallery;
    String name, email, password, city, state,zip;
    String photoFileName;
    public static ContentResolver appContext;

    protected void showDialog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(RegisterActivity.this);
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
        setContentView(R.layout.activity_register);

        Methods.set_title_to_actionbar("Register", this, (Toolbar) findViewById(R.id.toolbar));
        sp = new Methods(getApplicationContext());
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());

        init();

    }

    private void init() {

        imgFileGallery = new File("");
        appContext = getContentResolver();
        register = new AsyncHttpClient();
        register.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutCity = (TextInputLayout) findViewById(R.id.input_layout_city);
        inputLayoutState= (TextInputLayout) findViewById(R.id.input_layout_state);
        inputLayoutZipcode= (TextInputLayout) findViewById(R.id.input_layout_zip);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputCity = (EditText) findViewById(R.id.input_city);
        inputState = (EditText) findViewById(R.id.input_state);
        inputZipcode = (EditText) findViewById(R.id.input_zip);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        check = (CheckBox) findViewById(R.id.check);
        terms = (TextView) findViewById(R.id.terms);
        policy = (TextView) findViewById(R.id.policy);

        profile = (ImageView) findViewById(R.id.img_profile);
        cam = (ImageView) findViewById(R.id.img_cam);
        gallery = (ImageView) findViewById(R.id.img_gall);

        int width_height[] = Methods
                .getImageHeightAndWidthForProFileImageHomsecreen( this );

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_height[1], width_height[0]);
        profile .setLayoutParams(layoutParams);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputCity.addTextChangedListener(new MyTextWatcher(inputCity));
        inputState.addTextChangedListener(new MyTextWatcher(inputState));
        inputZipcode.addTextChangedListener(new MyTextWatcher(inputZipcode));

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "terms conditon", Toast.LENGTH_SHORT).show();
            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "policy", Toast.LENGTH_SHORT).show();
            }
        });


        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck = isChecked;
            }
        });

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


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
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
        else if (!validatePassword()) {
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
        else if(isCheck==false){
            Toast.makeText(RegisterActivity.this, "You must agree the terms, conditon and policy.", Toast.LENGTH_SHORT).show();
        }
        else {
            if (isConnected) {
                Register();
            } else {
                showDialog(Constants.NO_INTERNET);
            }
        }

    }

    private void Home() {
        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
        Intent in = new Intent(this, LoginActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }

    private boolean validateName() {
        name = inputName.getText().toString().trim();
        if (name.isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
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

    private boolean validatePassword() {
        password = inputPassword.getText().toString().trim();
        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                case R.id.input_password:
                    validatePassword();
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

    private void Register() {

        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("city", city);
        params.put("state", state);
        params.put("zip", zip);
        try {
            params.put("userfile",imgFileGallery);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        register.post(RegisterActivity.this, Constants.RegisterUrl, params, new JsonHttpResponseHandler() {

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
                        Home();
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
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null,
                null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
        Cursor cursor = getContentResolver().query(contentURI,
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