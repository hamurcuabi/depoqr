package com.emrehmrc.depoqr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by cenah on 2/23/2018.
 */

public class CariAdapter extends BaseAdapter implements Filterable {
    private Context context;
    ArrayList<ModelCari> beanList;
    private ArrayList<ModelCari> mStringFilterList;
    private LayoutInflater inflater;
    ValueFilter valueFilter;

    public CariAdapter(@NonNull Context context, @NonNull ArrayList<ModelCari> objectss) {
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_view_layout, null);
        }
        TextView txName = (TextView) convertView.findViewById(R.id.inputad);
        TextView txKod = (TextView) convertView.findViewById(R.id.inputkod);
        ModelCari bean = beanList.get(position);
        String name = bean.getCariadi();
        String kod = bean.getCarikod();
        txName.setText(name);
        txKod.setText(kod);
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
                ArrayList<ModelCari> filterList = new ArrayList<ModelCari>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getCariadi().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        ModelCari bean = new ModelCari(mStringFilterList.get(i)
                                .getCariadi(), mStringFilterList.get(i)
                                .getCarikod(), mStringFilterList.get(i).getCariId());
                        filterList.add(bean);
                    }
                    if ((mStringFilterList.get(i).getCarikod())
                            .contains(constraint.toString())) {

                        ModelCari bean = new ModelCari(mStringFilterList.get(i)
                                .getCariadi(), mStringFilterList.get(i)
                                .getCarikod(), mStringFilterList.get(i).getCariId());
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
            beanList = (ArrayList<ModelCari>) results.values;
            notifyDataSetChanged();
        }

    }

}




