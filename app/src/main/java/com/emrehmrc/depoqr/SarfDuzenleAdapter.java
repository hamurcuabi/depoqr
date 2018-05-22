package com.emrehmrc.depoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SarfDuzenleAdapter extends BaseAdapter {

    private Context context;
    ArrayList<SarfDuzenle.SarfProducts> beanList;
    private ArrayList<SarfDuzenle.SarfProducts> mStringFilterList;
    private LayoutInflater inflater;
    private final int[] bgColors = new int[] { R.color.list_bg_2, R.color.list_bg_1 };

    public SarfDuzenleAdapter(Context context, ArrayList<SarfDuzenle.SarfProducts> objectss) {
        this.context = context;
        mStringFilterList = objectss;
        this.beanList = objectss;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sarf_view, null);
        }
        int colorPosition = position % bgColors.length;
        convertView.setBackgroundResource(bgColors[colorPosition]);
        TextView txProductName = (TextView) convertView.findViewById(R.id.view_productname);
        TextView txBarkodeNo = (TextView) convertView.findViewById(R.id.view_barkodeNo);
        TextView txFirstAmount = (TextView) convertView.findViewById(R.id.view_firstamount);
        TextView txSecondAmount = (TextView) convertView.findViewById(R.id.view_secondamount);

        SarfDuzenle.SarfProducts bean = beanList.get(position);
        txBarkodeNo.setText(bean.getBarkodeNo());
        txFirstAmount.setText(bean.getBirincibirim());
        txProductName.setText(bean.getProductname());
        if(bean.getIkincibirim().contains("null")){
            txSecondAmount.setText("YOKTUR");
        }
       else txSecondAmount.setText(bean.getIkincibirim());
        return convertView;
    }
}
