package com.emrehmrc.depoqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.emrehmrc.depoqr.PlasiyerList.PlasiyerListModel;

import java.util.ArrayList;

public class PlasiyerListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    ArrayList<PlasiyerListModel> beanList;
    private ArrayList<PlasiyerListModel> mStringFilterList;
    private LayoutInflater inflater;
    ValueFilter valueFilter;

    public PlasiyerListAdapter(@NonNull Context context, @NonNull ArrayList<PlasiyerListModel> objectss) {
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
            convertView = inflater.inflate(R.layout.plasiyer_adapter_view, null);
        }
        TextView txTarih = (TextView) convertView.findViewById(R.id.view_tarih);
        TextView txCariAdi = (TextView) convertView.findViewById(R.id.view_cariadi);
        TextView txToplamTutar = (TextView) convertView.findViewById(R.id.view_toplamtutar);
        TextView txKdv = (TextView) convertView.findViewById(R.id.view_kdv);
        TextView txGenelTuatar = (TextView) convertView.findViewById(R.id.view_geneltutar);

        PlasiyerListModel bean = beanList.get(position);
        txTarih.setText(bean.getCariTarih());
        txCariAdi.setText(bean.getCariAdi());
        txToplamTutar.setText("" + bean.getToplamTutar());
        txGenelTuatar.setText("" + bean.getGenelTutar());
        txKdv.setText("" + bean.getKdv());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new PlasiyerListAdapter.ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<PlasiyerListModel> filterList = new ArrayList<PlasiyerListModel>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getCariAdi().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        PlasiyerListModel bean = new PlasiyerListModel(mStringFilterList.get(i).getId(), mStringFilterList.get(i).getCariTarih(), mStringFilterList.get(i).getCariAdi(), mStringFilterList.get(i).getToplamTutar(), mStringFilterList.get(i).getKdv(), mStringFilterList.get(i).getGenelTutar());
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
            beanList = (ArrayList<PlasiyerListModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
