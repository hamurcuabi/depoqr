package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskManPopUpAdapter extends RecyclerView.Adapter<TaskManPopUpAdapter.MyviewHolder> {

    ArrayList<String> datalist;
    LayoutInflater layoutInflater;
    Context mContentxt;

    public TaskManPopUpAdapter(Context context, ArrayList<String> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;
        this.mContentxt=context;

    }


    @Override
    public TaskManPopUpAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.taskmanpopuprecycler, parent, false);
        MyviewHolder myViewHolder = new MyviewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        String clicked = datalist.get(position);
        holder.setData(clicked, position);


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        TextView txtFullName;


        public MyviewHolder(View itemView) {
            super(itemView);
           txtFullName=(TextView)itemView.findViewById(R.id.tvFullName);

        }

        @SuppressLint("NewApi")
        public void setData(final String clicked, int position) {
            this.txtFullName.setText(clicked);

        }
    }
}
