package com.payback.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.payback.R;
import com.payback.activity.MainActivity;
import com.payback.functions.Methods;
import com.payback.functions.NetConnection;


public class HomeFragment extends Fragment {

    private View rootView;
    private Toolbar toolbar;
    private Button btnLend, btnReceive;

    Methods sp;
    ProgressDialog dialog;
    Boolean isConnected;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sp = new Methods(getActivity());
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        initUI();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initUI() {

        btnLend = (Button) rootView.findViewById(R.id.btn_lend);
        btnReceive = (Button) rootView.findViewById(R.id.btn_receive);

        btnLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LendMoneyList();
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveMoneyList();
            }
        });
    }

    private void LendMoneyList() {
        Fragment fragment = new LendMoneyListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void ReceiveMoneyList() {
        Fragment fragment = new ReceiveMoneyListFragment();
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
        ((MainActivity)getActivity()).setTitle("Home");
    }
}
