package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MainTaskAdapter extends RecyclerView.Adapter<MainTaskAdapter.MyviewHolder> {

    ArrayList<MainTaskModel> datalist;
    LayoutInflater layoutInflater;
    Context mContentxt;

    public MainTaskAdapter(Context context, ArrayList<MainTaskModel> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;
        this.mContentxt = context;

    }


    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.maintaskrecycler, parent, false);
        MyviewHolder myViewHolder = new MyviewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        MainTaskModel clicked = datalist.get(position);
        holder.setData(clicked, position);


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {


        TextView txtDate, txtTaskCreater, txtTag, txtTotalTaskMan, txtDescription;
        ImageButton imgTaskMans;


        public MyviewHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.tvTaskDate);
            txtTaskCreater = (TextView) itemView.findViewById(R.id.tvTaskCreater);
            txtTag = (TextView) itemView.findViewById(R.id.tvTaskTag);
            txtDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            txtTotalTaskMan = (TextView) itemView.findViewById(R.id.tvTaskTotalMan);
            imgTaskMans = (ImageButton) itemView.findViewById(R.id.imgTaskMans);


        }

        @SuppressLint("NewApi")
        public void setData(final MainTaskModel clicked, int position) {
            this.txtDate.setText(clicked.getTaskDate().substring(0, 16));
            this.txtTaskCreater.setText(clicked.getTaskCreater());
            String htmldes = Html.fromHtml(clicked.getTaskDescription()).toString();
            this.txtDescription.setText(Html.fromHtml(htmldes));
            this.txtTag.setText(clicked.getTaskTag());
            this.txtTotalTaskMan.setText(clicked.getTaskCountMan());
            imgTaskMans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContentxt, TaskManPopUp.class);
                    i.putExtra("id", clicked.getTaskId());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContentxt.startActivity(i);


                }
            });

        }
    }
}
