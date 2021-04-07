package com.example.capstonearchitecture2;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;


public class StreamFragment extends Fragment {

    private String mParam1;
    private int page;
    VideoView videoView;
    private String videoUrl ="http://192.168.1.127:5000/video_feed";

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
        WebView myWebView = (WebView) view.findViewById(R.id.webview);
        myWebView.loadUrl("http://192.168.1.127:5000/");

        return view;
    }

}
