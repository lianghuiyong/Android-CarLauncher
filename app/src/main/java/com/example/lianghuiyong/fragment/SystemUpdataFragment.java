package com.example.lianghuiyong.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lianghuiyong.R;


/**
 * A simple Fragment.
 */
public class SystemUpdataFragment extends Fragment {
    TextView text_system_version = null;

    public SystemUpdataFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View systemUpdataView = inflater.inflate(R.layout.fragment_system_updata, container, false);
        text_system_version = (TextView)systemUpdataView.findViewById(R.id.text_system_version);
        return systemUpdataView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //init_data
        text_system_version.setText(Build.DISPLAY);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
