package com.example.capstonephoneapp;

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

import com.example.capstonephoneapp.LogAdapter;
import com.example.capstonephoneapp.LogItem;
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
import java.util.Date;

public class ConsoleLogActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    Button btn_startClassify;
    ListView lv_consoleLog;
    private ArrayList<LogItem> log = new ArrayList<LogItem>();
    LogAdapter mLogAdapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_log);

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

                    log.add(logItem);
                    mLogAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(eventListener);

        btn_startClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
