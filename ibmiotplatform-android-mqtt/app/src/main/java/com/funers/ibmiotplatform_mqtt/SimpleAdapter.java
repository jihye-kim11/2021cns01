package com.funers.ibmiotplatform_mqtt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.MyViewHolder> {
    private ArrayList<String> event_log = new ArrayList<>();

    @NonNull
    @Override
    public SimpleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(event_log.get(position));
    }

    @Override
    public int getItemCount() {
        return event_log.size();
    }

    public void addLog(String log){
        event_log.add(log);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.log_view);
        }
    }

}
