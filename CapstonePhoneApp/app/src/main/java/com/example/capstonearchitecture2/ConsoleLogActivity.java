package com.example.capstonephoneapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ConsoleLogActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    public static Context context;

    Button btn_startClassify;
    ListView lv_consoleLog;
    private ArrayList<LogItem> log = new ArrayList<LogItem>();
    LogAdapter mLogAdapter;
    RequestQueue queue;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_log);
        queue = Volley.newRequestQueue(ConsoleLogActivity.this);

        btn_startClassify = findViewById(R.id.btn_startClassify);
        mLogAdapter = new LogAdapter(this, R.layout.log_item_row, log);

        lv_consoleLog = (ListView) findViewById(R.id.lv_consoleLog);
        lv_consoleLog.setAdapter(mLogAdapter);
        mLogAdapter.notifyDataSetChanged();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("log");
        Log.e("Firebase", databaseReference.toString());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                log.removeAll(log);
                for(DataSnapshot ds : snapshot.getChildren()){
                    String description = ds.child("description").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String picture = ds.child("picture").getValue(String.class);
                    double probability = ds.child("probability").getValue(float.class);
                    String relation = ds.child("relation").getValue(String.class);
                    String date = ds.child("timestamp").getValue(String.class);


                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date convertedDate = null;
                    try {
                        convertedDate = format.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d("DB ACCESS", "Name is " + name);
                    LogItem logItem = new LogItem(description, name, picture, probability, relation, convertedDate);
                    logItem.pictureURL = "http://10.0.2.2:5000/"+picture;
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
                Log.d("GET RESPONSE","Button pressed");
                String url ="http://10.0.2.2:5000/classify";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                               Log.d("GET RESPONSE","Response is: "+ response.substring(0,500));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      Log.d("GET RESPONSE","That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });
    }
}
