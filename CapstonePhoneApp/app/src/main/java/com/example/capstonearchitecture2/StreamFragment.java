package com.example.capstonearchitecture2;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.capstonephoneapp.R;

import java.util.stream.Stream;

public class StreamFragment extends Fragment {

    private String mParam1;
    private int page;

    public StreamFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public static StreamFragment newInstance(int page, String param1){
        StreamFragment fragment = new StreamFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_stream, container, false);
        return view;
    }

}
