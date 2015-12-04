package com.payback.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payback.R;
import com.payback.activity.MainActivity;
import com.payback.functions.Constants;
import com.payback.functions.Methods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ReceiveMoneyListFragment extends Fragment {

    private View rootView;
    private Toolbar toolbar;

    String TAG = "LendMoneyList Fragment";
    private AsyncHttpClient requestlist;
    // SharedPreferences sp;
    ProgressDialog dialog;
    Boolean isConnected;
    ListView listview;
    MyAdapter mAdapter;
    Methods sp;

    ArrayList<HashMap<String, String>> MyList = new ArrayList<HashMap<String, String>>();

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

    public ReceiveMoneyListFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_receive_money_list, container, false);

        sp = new Methods(getActivity());

        initUI();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initUI() {
        requestlist = new AsyncHttpClient();
        requestlist.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);

        listview = (ListView) rootView.findViewById(R.id.receivelist);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final String id = MyList.get(position).get("id");
                final String name = MyList.get(position).get("name");
                final String amount = MyList.get(position).get("amount");
                final String status = MyList.get(position).get("lendStatus");
                final String payback = MyList.get(position).get("paybackAmount");
                final String emailid = MyList.get(position).get("emailId");
                final String dop = MyList.get(position).get("dateOfPayback");

                Bundle args = new Bundle();
                args.putString("name", name);
                args.putString("emailid", emailid);
                args.putString("amount", amount);
                args.putString("payback", payback);
                args.putString("dop", dop);
                args.putString("item", "R");
                args.putString("id", id);
                args.putString("lendstatus", status);

                Fragment fragment = new ListActionFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragment.setArguments(args);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        Calllendlist();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_receive_money_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            ReceiveMoney();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void Calllendlist() {
        RequestParams params = new RequestParams();
        params.put("userId", sp.getUserId());

        requestlist.post(getActivity(), Constants.RequestlistURL, params, new JsonHttpResponseHandler() {

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
                        MyList.clear();
                        JSONArray Data = json.getJSONArray("GetData");
                        for (int i = 0; i < Data.length(); i++) {
                            HashMap<String, String> localhashMap = new HashMap<String, String>();
                            JSONObject ob = Data.getJSONObject(i);
                            localhashMap.put("id", ob.getString("id"));
                            localhashMap.put("name", ob.getString("name"));
                            localhashMap.put("requestFrom", ob.getString("requestFrom"));
                            localhashMap.put("requestTo", ob.getString("requestTo"));
                            localhashMap.put("amount", ob.getString("amount"));
                            localhashMap.put("paybackAmount", ob.getString("paybackAmount"));
                            localhashMap.put("dateOfPayback", ob.getString("dateOfPayback"));
                            localhashMap.put("lendStatus", ob.getString("lendStatus"));
                            localhashMap.put("paybackStatus", ob.getString("paybackStatus"));
                            localhashMap.put("emailId", ob.getString("emailId"));
                            MyList.add(localhashMap);
                        }
                        mAdapter = new MyAdapter(MyList, getActivity());
                        listview.setAdapter(mAdapter);
                    } else if (json.getString("MessageWhatHappen").equalsIgnoreCase("no data found")) {
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

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;

        public MyAdapter(ArrayList<HashMap<String, String>> list,
                         Activity activity) {
            mInflater = LayoutInflater.from(getActivity());

        }

        @Override
        public int getCount() {

            return MyList.size();
        }

        @Override
        public Object getItem(int position) {

            return MyList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);

                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.amount = (TextView) convertView.findViewById(R.id.amt);
                holder.status = (TextView) convertView.findViewById(R.id.status);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String name = MyList.get(position).get("name");
            final String amount = MyList.get(position).get("amount");
            final String status = MyList.get(position).get("lendStatus");
            final String payback = MyList.get(position).get("paybackAmount");
            final String emailid = MyList.get(position).get("emailId");
            final String dop = MyList.get(position).get("dateOfPayback");

            holder.name.setText(name);
            holder.amount.setText("$" + amount);

            if (status.equals("1")) {
                holder.status.setText("Approved");
                holder.status.setTextColor(getResources().getColor(R.color.coloraccept));
            } else if (status.equals("2")) {
                holder.status.setText("Decline");
                holder.status.setTextColor(getResources().getColor(R.color.colorerror));
            } else {
                holder.status.setText("Pending");
                holder.status.setTextColor(getResources().getColor(R.color.button));
            }

            return convertView;
        }

        class ViewHolder {
            TextView name, amount, status;
        }
    }

    private void ReceiveMoney() {

        Fragment fragment = new ReceiveMoneyFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        ((MainActivity)getActivity()).setTitle("Receive Money List");
    }
}