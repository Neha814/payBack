package com.payback.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.activity.LoginActivity;
import com.payback.activity.MainActivity;
import com.payback.functions.Constants;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Ravi on 29/07/15.
 */
public class CreditCardFragment extends Fragment {

    private View rootView;
    private Toolbar toolbar;
    private EditText inputName, inputCardno, inputExpdate, inputCvv;
    private TextInputLayout inputLayoutName, inputLayoutCardno, inputLayoutExpdate,inputLayoutCvv;
    private Button btnSave;
    private CheckBox check;
    private TextView terms, policy;

    String TAG = "BankInfoActivity";
    private Methods sp;
    private AsyncHttpClient bank;
    ProgressDialog dialog;
    Boolean isConnected;
    boolean isCheck = false;
    String name, cardno, expdate, cvv,userId;

    private int year, month, day;
    static final int DATE_PICKER_ID = 1111;
    public ContentResolver appContext;


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
            }
        });
        dialog.show();

    }


    public CreditCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_creditcard, container, false);


        sp = new Methods(getActivity());
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return rootView;
    }

    private void init() {

        bank = new AsyncHttpClient();
        bank.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        userId = sp.getUserId();

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutCardno = (TextInputLayout) rootView.findViewById(R.id.input_layout_card);
        inputLayoutExpdate = (TextInputLayout) rootView.findViewById(R.id.input_layout_expdate);
        inputLayoutCvv = (TextInputLayout) rootView.findViewById(R.id.input_layout_cv);
        inputName = (EditText) rootView.findViewById(R.id.input_name);
        inputCardno = (EditText) rootView.findViewById(R.id.input_card);
        inputExpdate = (EditText) rootView.findViewById(R.id.input_expdate);
        inputCvv= (EditText) rootView.findViewById(R.id.input_cv);
        btnSave = (Button) rootView.findViewById(R.id.btn_save);
        check = (CheckBox) rootView.findViewById(R.id.check);
        terms = (TextView) rootView.findViewById(R.id.terms);
        policy = (TextView) rootView.findViewById(R.id.policy);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputCardno.addTextChangedListener(new MyTextWatcher(inputCardno));
        inputExpdate.addTextChangedListener(new MyTextWatcher(inputExpdate));
        inputCvv.addTextChangedListener(new MyTextWatcher(inputCvv));

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "terms conditon", Toast.LENGTH_SHORT).show();
            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Policy", Toast.LENGTH_SHORT).show();
            }
        });


        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck = isChecked;
            }
        });

        inputExpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    private void submit() {
        if (!validateName()) {
            return;
        }
        if (!validateCard()) {
            return;
        }
        if (!validateExpdate()) {
            return;
        }
        if (!validateCvv()) {
            return;
        }
        if(isCheck==false){
            Toast.makeText(getActivity(), "You must agree the terms, conditon and policy.", Toast.LENGTH_SHORT).show();
        }
        else {
            if (isConnected) {
                BankInfo();
            } else {
                showDialog(Constants.NO_INTERNET);
            }
        }
    }

    private void BankInfo() {
        RequestParams params = new RequestParams();
        params.put("cardholderName", name);
        params.put("creditCardNumber", cardno);
        params.put("expirationDate", expdate);
        params.put("cvv", cvv);
        params.put("userId", userId);

        bank.post(getActivity(), Constants.CCUrl, params, new JsonHttpResponseHandler() {

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

                    }
                    else if (json.getString("MessageWhatHappen").equalsIgnoreCase("Credit card type is not accepted")) {
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
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_card:
                    validateCard();
                    break;
                case R.id.input_expdate:
                    validateExpdate();
                    break;
                case R.id.input_cv:
                    validateCvv();
                    break;
            }
        }
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
    private boolean validateCard() {
        cardno = inputCardno.getText().toString().trim();
        if (name.isEmpty()) {
            inputLayoutCardno.setError("Enter card number");
            requestFocus(inputCardno);
            return false;
        } else {
            inputLayoutCardno.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateExpdate() {
        expdate = inputExpdate.getText().toString().trim();
        if (expdate.isEmpty()) {
            inputLayoutExpdate.setError("Enter card expiry date");
            requestFocus(inputExpdate);
            return false;
        } else {
            inputLayoutExpdate.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateCvv() {
        cvv = inputCvv.getText().toString().trim();
        if (cvv.isEmpty()) {
            inputLayoutCvv.setError("Enter CVV number");
            requestFocus(inputCvv);
            return false;
        } else {
            inputLayoutCvv.setErrorEnabled(false);
        }

        return true;
    }

    private void Home() {
        Intent in = new Intent(getActivity(), LoginActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            String formattedDate = sdf.format(c.getTime());

            Log.e("date====", "" + formattedDate);
            inputExpdate.setText(formattedDate);


        }
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
        ((MainActivity)getActivity()).setTitle("Banking Info");
    }
}
