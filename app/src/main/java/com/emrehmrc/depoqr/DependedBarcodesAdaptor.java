package com.emrehmrc.depoqr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emre Hmrc on 28.03.2018.
 */

public class DependedBarcodesAdaptor extends RecyclerView.Adapter<DependedBarcodesAdaptor.MyViewHolder> {

    ArrayList<DependedBarcodes> datalist;
    LayoutInflater layoutInflater;

    public DependedBarcodesAdaptor(Context context, ArrayList<DependedBarcodes> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.dependedbarcodeslist, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        DependedBarcodes clicked = datalist.get(position);
        holder.setData(clicked, position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.get(position).setCheck(!datalist.get(position).getCheck());
            }
        });


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView txtcode, txtname,txtBarcode,txtFirst,txtSecond,edtFirst,edtSecond;


        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            txtcode = (TextView) itemView.findViewById(R.id.txtcode);
            txtname = (TextView) itemView.findViewById(R.id.txtname);
            txtBarcode=(TextView)itemView.findViewById(R.id.txtbarcode);
            txtFirst=(TextView)itemView.findViewById(R.id.txtfirst);
            txtSecond=(TextView)itemView.findViewById(R.id.txtsecond);
            edtFirst=(EditText)itemView.findViewById(R.id.edtfirstamount);
            edtSecond=(EditText)itemView.findViewById(R.id.edtsecondtamount);



        }

        public void setData(final DependedBarcodes clicked, int position) {

            this.checkBox.setChecked(clicked.getCheck());
            this.txtname.setText(clicked.getName());
            this.txtcode.setText(clicked.getProductCode());
            this.txtBarcode.setText(clicked.getCodeNo());
            this.txtFirst.setText((clicked.getFirstUnit()));
            this.txtSecond.setText(clicked.getSecondUnit());
            this.edtFirst.setText(clicked.getFirstAmount());
            this.edtSecond.setText(clicked.getSecondAmount());


        }
    }
}
