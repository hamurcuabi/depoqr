package com.emrehmrc.depoqr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainTaskAdapter extends RecyclerView.Adapter<MainTaskAdapter.MyviewHolder> {

    ArrayList<MainTaskModel> datalist;
    LayoutInflater layoutInflater;

    public MainTaskAdapter(Context context, ArrayList<MainTaskModel> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;

    }



    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.gorev_recyclerview, parent, false);
        MyviewHolder myViewHolder = new MyviewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        MainTaskModel clicked = datalist.get(position);
        holder.setData(clicked, position);
        holder.lnr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(holder.lnr2.getVisibility()==View.VISIBLE)holder.lnr2.setVisibility(View.GONE);
              else holder.lnr2.setVisibility(View.VISIBLE);
            }
        });



    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {


        TextView txtDate,txtTaskMan,txtTaskContent,txtTotalTaskMan;
        ImageButton imgTaskManList;
        LinearLayout lnr1, lnr2;

        public MyviewHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtTaskDate);
            txtTaskMan = (TextView) itemView.findViewById(R.id.txtTaskMan);
            txtTaskContent = (TextView) itemView.findViewById(R.id.txtTaskContent);
            txtTotalTaskMan = (TextView) itemView.findViewById(R.id.txtTotalTaskMan);
            imgTaskManList=(ImageButton)itemView.findViewById(R.id.imgTaskMans);
            lnr1 = (LinearLayout) itemView.findViewById(R.id.lnr1);
            lnr2 = (LinearLayout) itemView.findViewById(R.id.lnr2);


        }

        public void setData(final MainTaskModel clicked, int position) {
            this.txtDate.setText(clicked.getDate());
            this.txtTaskMan.setText(clicked.getTaskMan());
            this.txtTaskContent.setText(clicked.getContent());
            this.txtTotalTaskMan.setText(""+clicked.getTaskManlistCount());
            imgTaskManList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  // txtTaskMan.setText(clicked.getTaskManlistCount());
                }
            });

        }
    }
}
