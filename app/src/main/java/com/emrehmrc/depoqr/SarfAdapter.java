package com.emrehmrc.depoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class SarfAdapter extends BaseAdapter  {
    private Context context;
    ArrayList<SarfListe.SarfAnaListeModel> beanList;
    private ArrayList<SarfListe.SarfAnaListeModel> mStringFilterList;
    private LayoutInflater inflater;


    public SarfAdapter(Context context, ArrayList<SarfListe.SarfAnaListeModel> objectss) {
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
            convertView = inflater.inflate(R.layout.sarf_adapter_view, null);
        }
        TextView txTarih = (TextView) convertView.findViewById(R.id.view_tarih);
        TextView txCariAdi = (TextView) convertView.findViewById(R.id.view_kullanici);
        TextView txToplamTutar = (TextView) convertView.findViewById(R.id.view_cariPersonel);

        SarfListe.SarfAnaListeModel bean = beanList.get(position);
        txTarih.setText(bean.getDate());
        txCariAdi.setText(bean.getUserName());
        txToplamTutar.setText(bean.getCurrentName());
        return convertView;
    }
}
