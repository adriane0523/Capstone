package com.example.capstonephoneapp;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstonearchitecture2.PreferenceManager;
import com.example.capstonephoneapp.LogAdapter;
import com.example.capstonephoneapp.LogItem;
import com.example.capstonephoneapp.MainActivity;
import com.example.capstonephoneapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ConsoleLogFragment extends Fragment {

    private DatabaseReference databaseReference;
    public static Context context;

    private ViewPager viewPager;

    private LinearLayout dots_layout;
    private TextView[] dots;
    private Button btn_startClassify;
    private Button btn_stopClassify;
    private Button btn_clearLog;
    private ListView lv_consoleLog;
    private int[] layouts;

    private ArrayList<LogItem> log = new ArrayList<LogItem>();

    LogAdapter mLogAdapter;
    RequestQueue queue;

    private PreferenceManager preferenceManager;

    public ConsoleLogFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){

        }
    }

    public static ConsoleLogFragment newInstance(int page, String param1){
        ConsoleLogFragment fragment = new ConsoleLogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_console_log, container, false);
        initWidgets(view, inflater, container);

        preferenceManager = new PreferenceManager(getActivity());
        if(!preferenceManager.isFirstTimeLaunch()){
            Toast.makeText(getActivity(), "Swipe right to view the smart glass live stream.", Toast.LENGTH_SHORT);
        }

        queue = Volley.newRequestQueue(getActivity());

        mLogAdapter = new LogAdapter(getActivity(), R.layout.log_item_row, log);
        lv_consoleLog.setAdapter(mLogAdapter);
        mLogAdapter.notifyDataSetChanged();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("log");
        Log.e("FIREBASE", databaseReference.toString());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                log.removeAll(log);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String description = ds.child("description").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String picture = ds.child("picture").getValue(String.class);
                    double probability = ds.child("probability").getValue(float.class);
                    String relation = ds.child("relation").getValue(String.class);
                    String date = ds.child("timestamp").getValue(String.class);
                    String grade = ds.child("grade").getValue(String.class);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date convertedDate = null;
                    try {
                        convertedDate = format.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d("DB ACCESS", "Name is " + name);
                    LogItem logItem = new LogItem(description, name, picture, probability, relation, grade, convertedDate);
                    logItem.pictureURL = "http://10.0.2.2:5000/"+picture;
                    Picasso.get().invalidate( logItem.pictureURL);
                    Picasso.get().load( logItem.pictureURL).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE);

                    log.add(logItem);
                    Collections.sort(log, Collections.<LogItem>reverseOrder());
                    mLogAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(eventListener);

        btn_startClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onClick", "Start Classifying button pressed");
                String url = "http://10.0.2.2:5000/classify";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Log.d("GET RESPONSE", "Response is: " + response.substring(0, 500));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GET RESPONSE", "That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });

        btn_stopClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onClick", "Stop Classifying button pressed");
                // Request to endpoint goes here
            }
        });

        btn_clearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clears ArrayList, updates widget
                Log.d("onClick", "Clear Log button pressed");
                log.clear();
                mLogAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void initWidgets(View view, LayoutInflater inflater, ViewGroup container) {
        btn_startClassify = (Button) view.findViewById(R.id.btn_startClassify);
        btn_stopClassify = (Button) view.findViewById(R.id.btn_stopClassify);
        btn_clearLog = (Button) view.findViewById(R.id.btn_clearLog);
        lv_consoleLog = (ListView) view.findViewById(R.id.lv_consoleLog);

    }

}
