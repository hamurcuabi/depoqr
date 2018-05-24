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

public class SarfAdapter extends BaseAdapter implements Filterable {
    private Context context;
    ArrayList<SarfListe.SarfAnaListeModel> beanList;
    private ArrayList<SarfListe.SarfAnaListeModel> mStringFilterList;
    private LayoutInflater inflater;
    private final int[] bgColors = new int[] { R.color.list_bg_2, R.color.list_bg_1 };
    ValueFilter valueFilter;


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
        int colorPosition = position % bgColors.length;
        convertView.setBackgroundResource(bgColors[colorPosition]);
        TextView txTarih = (TextView) convertView.findViewById(R.id.view_tarih);
        TextView txCariAdi = (TextView) convertView.findViewById(R.id.view_kullanici);
        TextView txToplamTutar = (TextView) convertView.findViewById(R.id.view_cariPersonel);

        SarfListe.SarfAnaListeModel bean = beanList.get(position);
        txTarih.setText(bean.getDate());
        txCariAdi.setText(bean.getUserName());
        txToplamTutar.setText(bean.getCurrentName());
        return convertView;
    }
    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<SarfListe.SarfAnaListeModel> filterList = new ArrayList<SarfListe.SarfAnaListeModel>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getDate().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        SarfListe.SarfAnaListeModel bean = new SarfListe.SarfAnaListeModel(mStringFilterList.get(i).getSarfNo()
                                , mStringFilterList.get(i).getUserName()
                                , mStringFilterList.get(i).getCurrentName()
                                , mStringFilterList.get(i).getDate());
                        filterList.add(bean);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }


            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            beanList = (ArrayList<SarfListe.SarfAnaListeModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
