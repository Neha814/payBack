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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class ListActionFragment extends Fragment {

    private View rootView;
    private EditText inputName, inputEmail, inputAmount,inputDate,inputPayback;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutAmount,
            inputLayoutDate,inputLayoutPayback;
    private Button btnAcc, btnDec;
    private LinearLayout layAction;

    String TAG = "List Action Fragment";
    private AsyncHttpClient action;
   // SharedPreferences sp;
    Methods sp;
    ProgressDialog dialog;
    Boolean isConnected;

    String id,name,email,amount,date,payback,item,status,lendstatus;

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

    public ListActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listaction, container, false);

        //sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp = new Methods(getActivity());
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        initUI();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initUI() {
        name = getArguments().getString("name");
        email = getArguments().getString("emailid");
        amount = getArguments().getString("amount");
        date = getArguments().getString("dop");
        payback = getArguments().getString("payback");
        item = getArguments().getString("item");
        id = getArguments().getString("id");
        lendstatus = getArguments().getString("lendstatus");

        action = new AsyncHttpClient();
        action.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        layAction = (LinearLayout) rootView.findViewById(R.id.lay_action);
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
        btnAcc = (Button) rootView.findViewById(R.id.btn_accept);
        btnDec = (Button) rootView.findViewById(R.id.btn_decline);

        inputLayoutName.setHint("Person name");

        inputName.setText(name);
        inputEmail.setText(email);
        inputAmount.setText(amount);
        inputPayback.setText(payback);
        inputDate.setText(date);

        if(item.contains("L")){
            if(lendstatus.contains("0")) {
                layAction.setVisibility(View.VISIBLE);
            }
            else{
                layAction.setVisibility(View.GONE);
            }
        }
        else {
            layAction.setVisibility(View.GONE);
        }

        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="1";
                submit(status);

            }
        });

        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status="2";
                submit(status);

            }
        });
    }

    private void submit(String status) {

        RequestParams params = new RequestParams();
        params.put("userId",sp.getUserId());
        params.put("LendMoneyRequestId", id);
        params.put("status", status);

        action.post(getActivity(), Constants.ActionURL, params, new JsonHttpResponseHandler() {

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
        ((MainActivity)getActivity()).setTitle("Action");
    }
}
