package com.emrehmrc.depoqr;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emre Hmrc on 26.02.2018.
 */

public class EmreAdaptor extends RecyclerView.Adapter<EmreAdaptor.MyViewHolder> {


    ArrayList<SevkiyatÜrünleriRecyclerView> datalist;
    LayoutInflater layoutInflater;

    public EmreAdaptor(Context context, ArrayList<SevkiyatÜrünleriRecyclerView> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.sevkiyatlistitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        SevkiyatÜrünleriRecyclerView clicked = datalist.get(position);
        holder.setData(clicked, position);
        final float deneme = datalist.get(position).getFirstamount();
        final float deneme2 = datalist.get(position).getSecondamount();
        holder.edtfirstamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                try {
                    if (deneme < Float.parseFloat(holder.edtfirstamount.getText().toString())) {
                        holder.uyari1.setText("MİKTAR AŞILDI!");
                        holder.checkBox.setChecked(false);
                        holder.checkBox.setEnabled(false);

                    } else if (deneme >= Float.parseFloat(holder.edtfirstamount.getText().toString())) {
                        holder.uyari1.setText("");
                        holder.checkBox.setChecked(true);  //bunu
                        holder.checkBox.setEnabled(true); //icindeki ture
                        datalist.get(position).setFirstamount(Float.parseFloat(holder.edtfirstamount.getText().toString()));

                    }else {
                        holder.uyari1.setText("");//bunu
                        holder.checkBox.setEnabled(true); // bunu
                        holder.checkBox.setChecked(true); //bunu
                    }

                } catch (Exception ex) {
                    holder.uyari1.setText("MİKTAR BOŞ GİRİLDİ!");
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setEnabled(false);

                }

            }
        });
        holder.edtsecondamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (deneme2 < Float.parseFloat(holder.edtsecondamount.getText().toString())) {
                        holder.uyari2.setText("MİKTAR AŞILDI!");
                        holder.checkBox.setChecked(false);
                        holder.checkBox.setEnabled(false);

                    } else if (deneme2 >= Float.parseFloat(holder.edtsecondamount.getText().toString())) {
                        holder.uyari2.setText("");
                        holder.checkBox.setChecked(true);  //bunu
                        holder.checkBox.setEnabled(true); //icindeki ture
                        datalist.get(position).setSecondamount(Float.parseFloat(holder.edtsecondamount.getText().toString()));

                    }else {
                        holder.uyari1.setText("");//bunu
                        holder.checkBox.setEnabled(true); // bunu
                        holder.checkBox.setChecked(true); //bunu
                    }
                } catch (Exception ex) {
                    holder.uyari2.setText("MİKTAR BOŞ GİRİLDİ!");
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setEnabled(false);

                }


            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.get(position).setChecked(!datalist.get(position).isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView uniqcode, proname, firstunit, secondunit, firstamount, secondamount, barcodeid, uyari1, uyari2, txtfirst, txtsecond;
        EditText edtfirstamount, edtsecondamount;
        LinearLayout lnr1, lnr2, lnr3;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            uniqcode = (TextView) itemView.findViewById(R.id.uniqcodeeee);
            barcodeid = (TextView) itemView.findViewById(R.id.barcodeid);
            proname = (TextView) itemView.findViewById(R.id.proname);
            uyari1 = (TextView) itemView.findViewById(R.id.txtuyari1);
            uyari2 = (TextView) itemView.findViewById(R.id.txtuyari2);
            txtfirst = (TextView) itemView.findViewById(R.id.txtfirst);
            txtsecond = (TextView) itemView.findViewById(R.id.txtsecond);
            firstunit = (TextView) itemView.findViewById(R.id.firstunit);
            firstamount = (TextView) itemView.findViewById(R.id.firstunitamount);
            secondunit = (TextView) itemView.findViewById(R.id.seconunit);
            secondamount = (TextView) itemView.findViewById(R.id.seconunitamount);
            lnr1 = (LinearLayout) itemView.findViewById(R.id.lnr1);
            lnr2 = (LinearLayout) itemView.findViewById(R.id.lnr2);
            lnr3 = (LinearLayout) itemView.findViewById(R.id.lnr3);
            edtfirstamount = (EditText) itemView.findViewById(R.id.edtfirstamount);
            edtfirstamount.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });
            edtsecondamount = (EditText) itemView.findViewById(R.id.edtsecondtamount);


            edtsecondamount.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });

        }

        public void setData(final SevkiyatÜrünleriRecyclerView clicked, int position) {

            this.checkBox.setChecked(clicked.isChecked());
            this.uniqcode.setText(clicked.getUniqCode());
            this.barcodeid.setText(clicked.getBarcodeid());
            this.proname.setText(clicked.getName());
            this.firstunit.setText(clicked.getFirstUnit());
            this.firstamount.setText(clicked.getFirstamount() + "");
            this.secondunit.setText(clicked.getSecondUnit());
            this.secondamount.setText(clicked.getSecondamount() + "");
            this.edtfirstamount.setText(clicked.getFirstamount() + "");
            this.edtsecondamount.setText(clicked.getSecondamount() + "");
            this.txtfirst.setText(clicked.getFirstUnit());
            this.txtsecond.setText(clicked.getSecondUnit());
            this.uyari1.setText(clicked.getUyari1());
            this.uyari2.setText(clicked.getUyari2());


        }
    }
}