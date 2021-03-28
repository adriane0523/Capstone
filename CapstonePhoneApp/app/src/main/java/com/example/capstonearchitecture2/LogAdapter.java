package com.example.capstonephoneapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.capstonephoneapp.LogItem;
import com.example.capstonephoneapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LogAdapter extends ArrayAdapter<LogItem> {

    private int resourceLayout;
    private Context mContext;

    public LogAdapter(Context context, int resource, List<LogItem> items){
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        LogItem item = getItem(position);

        if(item != null){
            TextView tv_name = v.findViewById(R.id.tv_name);
            TextView tv_description = v.findViewById(R.id.tv_description);
            TextView tv_probability = v.findViewById(R.id.tv_probability);
            TextView tv_relation = v.findViewById(R.id.tv_relation);
            TextView tv_grade = v.findViewById(R.id.tv_grade);
            TextView tv_timestamp = v.findViewById(R.id.tv_timestamp);
            ImageView iv_logImage = v.findViewById(R.id.iv_logImage);

            tv_name.setText(item.getName());
            tv_description.setText(item.getDescription());
            tv_probability.setText(item.getProbability() + "");
            tv_relation.setText(item.getRelation());
            tv_grade.setText(item.getGrade());
            tv_timestamp.setText(item.getTimestamp() + "");
            Picasso.get().load(item.getPictureURL()).into(iv_logImage);
        }
        return v;
    }

}
