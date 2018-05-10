package com.emrehmrc.depoqr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonalsAdapter extends BaseAdapter implements Filterable {
    private Context context;
    ArrayList<ModelPersonals> beanList;
    private ArrayList<ModelPersonals> mStringFilterList;
    private LayoutInflater inflater;
    ValueFilter valueFilter;

    public PersonalsAdapter(@NonNull Context context, @NonNull ArrayList<ModelPersonals> objectss) {
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
            convertView = inflater.inflate(R.layout.adapter_view_personals, null);
        }
        TextView txName = (TextView) convertView.findViewById(R.id.inputad);
        ModelPersonals bean = beanList.get(position);
        String name = bean.getPersonalName();
        txName.setText(name);
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
                ArrayList<ModelPersonals> filterList = new ArrayList<ModelPersonals>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getPersonalName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        ModelPersonals bean = new ModelPersonals( mStringFilterList.get(i).getPersonalId(),mStringFilterList.get(i)
                                .getPersonalName());
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
            beanList = (ArrayList<ModelPersonals>) results.values;
            notifyDataSetChanged();
        }

    }

}
