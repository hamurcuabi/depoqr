package com.emrehmrc.depoqr;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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
Context context;
    public EmreAdaptor(Context context, ArrayList<SevkiyatÜrünleriRecyclerView> data) {
        layoutInflater = LayoutInflater.from(context);
        this.datalist = data;
        this.context = context;

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
        holder.checkBox.setChecked(clicked.isChecked());


        final float deneme = datalist.get(position).getFirstamount();
        final float deneme2 = datalist.get(position).getSecondamount();
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datalist.get(position).setChecked(!datalist.get(position).isChecked());
            }
        });
        holder.lnr1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View subView = inflater.inflate(R.layout.adapterchanger, null);
                final EditText Cfirstamount = (EditText)subView.findViewById(R.id.edtfirstamount);
                final EditText Csecandamount = (EditText)subView.findViewById(R.id.edtsecondtamount);
                final TextView CfirstamountB = (TextView) subView.findViewById(R.id.firstunitamount);
                final TextView CsecondamuntB = (TextView) subView.findViewById(R.id.seconunitamount);
                final TextView CfirstUnit = (TextView) subView.findViewById(R.id.firstunit);
                final TextView CsecondUnit = (TextView) subView.findViewById(R.id.seconunit);
                final TextView txtuyari1 = (TextView) subView.findViewById(R.id.txtuyari1);
                final TextView txtuyari2 = (TextView) subView.findViewById(R.id.txtuyari2);

                Cfirstamount.setText(holder.edtfirstamount.getText().toString());
                Csecandamount.setText(holder.edtsecondamount.getText().toString());
                CfirstamountB.setText(holder.firstamount.getText().toString());
                CsecondamuntB.setText(holder.secondamount.getText().toString());
                Cfirstamount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (deneme < Float.parseFloat(Cfirstamount.getText().toString())) {
                                txtuyari1.setText("MİKTAR AŞILDI!");


                            } else if (deneme >= Float.parseFloat(Cfirstamount.toString())) {
                                txtuyari1.setText("");

                                datalist.get(position).setFirstamount(Float.parseFloat(Cfirstamount.getText().toString()));


                            }else {
                                txtuyari1.setText("");//bunu

                            }

                        } catch (Exception ex) {
                            txtuyari1.setText("MİKTAR BOŞ GİRİLDİ!");

                        }
                    }
                });
                Csecandamount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (deneme2 < Float.parseFloat(Csecandamount.getText().toString())) {
                                txtuyari2.setText("MİKTAR AŞILDI!");


                            } else if (deneme2 >= Float.parseFloat(Csecandamount.getText().toString())) {
                                txtuyari2.setText("");

                                datalist.get(position).setSecondamount(Float.parseFloat(Csecandamount.getText().toString()));


                            }else {
                                txtuyari2.setText("");//bunu


                            }
                        } catch (Exception ex) {
                            txtuyari2.setText("MİKTAR BOŞ GİRİLDİ!");

                        }


                    }
                });
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
               // builder.setTitle("AÇIKLAMA");
                //builder.setMessage("ZORUNLU DEĞIL");
                builder.setView(subView);
                android.app.AlertDialog alertDialog = builder.create();
                builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                builder.setNegativeButton("Iptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();

                return true;
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