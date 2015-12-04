package com.payback.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class ReceiveMoneyFragment extends Fragment {

    private View rootView;
    private EditText inputName, inputEmail, inputAmount,inputDate,inputPayback;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutAmount,inputLayoutDate, inputLayoutPayback;
    private Button btnSubmit;

    String TAG = "Receive Money Fragment";
    private AsyncHttpClient receive;
    Methods sp;
    ProgressDialog dialog;
    Boolean isConnected;

    String userId,name,email,amount,date,payback;
    int amt,pay_amt;

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

    public ReceiveMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_receive_money, container, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        sp = new Methods(getActivity());
        initUI();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initUI() {
        userId = sp.getUserId();

        receive = new AsyncHttpClient();
        receive.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_email);
        inputLayoutAmount = (TextInputLayout) rootView.findViewById(R.id.input_layout_amount);
        inputLayoutDate = (TextInputLayout) rootView.findViewById(R.id.input_layout_dop);
        inputLayoutPayback = (TextInputLayout) rootView.findViewById(R.id.input_layout_paybackamt);
        inputName = (EditText) rootView.findViewById(R.id.input_name);
        inputEmail = (EditText) rootView.findViewById(R.id.input_email);
        inputAmount = (EditText) rootView.findViewById(R.id.input_amount);
        inputDate = (EditText) rootView.findViewById(R.id.input_dop);
        inputPayback = (EditText) rootView.findViewById(R.id.input_paybackamt);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputAmount.addTextChangedListener(new MyTextWatcher(inputAmount));
        inputDate.addTextChangedListener(new MyTextWatcher(inputDate));
        inputPayback.addTextChangedListener(new MyTextWatcher(inputPayback));

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });
    }

    private void submit() {
        if (!validateName()) {
            return;
        } else if (!validateEmail()) {
            return;
        }else if (!validateAmount()) {
            return;
        }
        else if (!validateDate()) {
            return;
        }else if (!validatePayback()) {
            return;
        }else {
            if (isConnected) {
                ReceiveMoney();
            } else {
                showDialog(Constants.NO_INTERNET);
            }
        }
    }

    private void ReceiveMoney() {
        RequestParams params = new RequestParams();
        params.put("requestTo", email);
        params.put("requestFrom", userId);
        params.put("amount", amount);
        params.put("dateOfPayback", date);
        params.put("paybackAmount", payback);

        receive.post(getActivity(), Constants.RequestLendURL, params, new JsonHttpResponseHandler() {

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

                    } else if (json.getString("MessageWhatHappen").equalsIgnoreCase("Email id not exists in our database.")) {
                        inputLayoutPayback.setError(json.getString("MessageWhatHappen"));
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
                if (headers!=null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers!=null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers!=null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.input_amount:
                    amount = inputAmount.getText().toString();
                    if(amount.length()>0)
                    amt = Integer.parseInt(amount);
                    pay_amt = (amt/100)*5;
                    pay_amt = amt + pay_amt;
                    inputPayback.setText(pay_amt+"");
                    break;
            }

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_amount:
                    validateAmount();
                    break;
                case R.id.input_dop:
                    validateDate();
                    break;
                case R.id.input_paybackamt:
                    validatePayback();
                    break;
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

    private boolean validateAmount() {
        amount = inputAmount.getText().toString().trim();
        if (amount.isEmpty()) {
            inputLayoutAmount.setError("Enter the amount");
            requestFocus(inputAmount);
            return false;
        } else {
            inputLayoutAmount.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateDate() {
        date = inputDate.getText().toString().trim();
        if (date.isEmpty()) {
            inputLayoutDate.setError("Enter payback date");
            requestFocus(inputDate);
            return false;
        } else {
            inputLayoutDate.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePayback() {
        payback = inputPayback.getText().toString().trim();
        if (payback.isEmpty()) {
            inputLayoutPayback.setError("Enter payback amount");
            requestFocus(inputPayback);
            return false;
        } else {
            inputLayoutPayback.setErrorEnabled(false);
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

    public void Home(){
        Fragment fragment = new ReceiveMoneyListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(c.getTime());

            Log.e("date====", "" + formattedDate);
            inputDate.setText(formattedDate);


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
        ((MainActivity)getActivity()).setTitle("Receive Money");
    }
}
