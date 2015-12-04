package com.payback.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.activity.MainActivity;
import com.payback.functions.Constants;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Ravi on 29/07/15.
 */
public class ChangePasswordFragment extends Fragment {

    private View rootView;
    private Toolbar toolbar;
    private EditText inputOld, inputNew, inputConfirm;
    private TextInputLayout inputLayoutOld, inputLayoutNew, inputLayoutAConfirm;
    private Button btnSave;
    Methods sp;
    String TAG = "ChangePassword Activity";
    private AsyncHttpClient change;
   // SharedPreferences sp;
    ProgressDialog dialog;
    Boolean isConnected;

    String newpass, oldpass, confirmpass;

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
                Home();
            }
        });
        dialog.show();
    }

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_changepassword, container, false);

        // sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp = new Methods(getActivity());
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        initUI();

        return rootView;
    }

    private void initUI() {

        change = new AsyncHttpClient();
        change.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);


        inputLayoutNew = (TextInputLayout) rootView.findViewById(R.id.input_layout_new);
        inputLayoutOld = (TextInputLayout) rootView.findViewById(R.id.input_layout_old);
        inputLayoutAConfirm = (TextInputLayout) rootView.findViewById(R.id.input_layout_confirm);
        inputNew = (EditText) rootView.findViewById(R.id.input_new);
        inputOld = (EditText) rootView.findViewById(R.id.input_old);
        inputConfirm = (EditText) rootView.findViewById(R.id.input_confirm);
        btnSave = (Button) rootView.findViewById(R.id.btn_save);

        inputNew.addTextChangedListener(new MyTextWatcher(inputNew));
        inputOld.addTextChangedListener(new MyTextWatcher(inputOld));
        inputConfirm.addTextChangedListener(new MyTextWatcher(inputConfirm));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateOld()) {
            return;
        }
        if (!validateNew()) {
            return;
        }

        if (!validateConfirm()) {
            return;
        }
        else {
            if (isConnected) {
                Change();
            } else {
                showDialog(Constants.NO_INTERNET);
            }
        }
    }

    private boolean validateNew() {
        newpass = inputNew.getText().toString().trim();
        if (newpass.isEmpty()) {
            inputLayoutNew.setError("Enter New Password");
            requestFocus(inputNew);
            return false;
        } else {
            inputLayoutNew.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOld() {
        oldpass = inputOld.getText().toString().trim();
        if (oldpass.isEmpty()) {
            inputLayoutOld.setError("Enter Old Password");
            requestFocus(inputOld);
            return false;
        } else {
            inputLayoutOld.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirm() {
        confirmpass = inputConfirm.getText().toString().trim();
        if (!confirmpass.equals(newpass)) {
            inputLayoutAConfirm.setError("New and confirm password does not match!");
            requestFocus(inputConfirm);
            return false;
        } else {
            inputLayoutAConfirm.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                case R.id.input_new:
                    validateNew();
                    break;
                case R.id.input_old:
                    validateOld();
                    break;
                case R.id.input_confirm:
                    validateConfirm();
                    break;
            }
        }
    }

    private void Change() {

        RequestParams params = new RequestParams();
        params.put("userid", sp.getUserId());
        params.put("oldpassword", oldpass);
        params.put("newpassword", newpass);

        change.post(getActivity(), Constants.ChangeURL, params, new JsonHttpResponseHandler() {

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

                        String message = json.getString("MessageWhatHappen");

                        showDialog(message);

                    } else if (json.getString("MessageWhatHappen").equalsIgnoreCase("Incorrect Old Password")) {
                        inputLayoutAConfirm.setError(json.getString("MessageWhatHappen"));
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


    private void Home() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public  void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setTitle("Change Password");
    }

}
