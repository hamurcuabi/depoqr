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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DepoListesiAdapter extends BaseAdapter implements Filterable {
    private Context context;
    ArrayList<DepoListesiModel> beanList;
    private ArrayList<DepoListesiModel> mStringFilterList;
    private LayoutInflater inflater;
    ValueFilter valueFilter;
    private final int[] bgColors = new int[] { R.color.list_bg_2, R.color.list_bg_1 };

    public DepoListesiAdapter(@NonNull Context context, @NonNull ArrayList<DepoListesiModel> objectss) {
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
            convertView = inflater.inflate(R.layout.depolistesi_view, null);
        }
        int colorPosition = position % bgColors.length;
        convertView.setBackgroundResource(bgColors[colorPosition]);
        TextView txName = (TextView) convertView.findViewById(R.id.inputad);
        TextView txKod = (TextView) convertView.findViewById(R.id.inputkod);
        TextView txfirst = (TextView) convertView.findViewById(R.id.inputfirst);
        TextView txsecond = (TextView) convertView.findViewById(R.id.inputsecond);
        DepoListesiModel bean = beanList.get(position);
        String name = bean.getProductadi();
        String kod = bean.getProductKod();
        float first = bean.getFirsAmount();
        float second = bean.getSecondAmount();
        String inputsecondname = bean.getSecondName();
        String inputfirstname = bean.getFirstName();
        txName.setText(name);
        txKod.setText(kod);
        txfirst.setText(new DecimalFormat("##.##").format(first)+ " " +inputfirstname);
        if(inputsecondname ==null && second==0){
            txsecond.setText("YOKTUR");
        }else{
            txsecond.setText(new DecimalFormat("##.##").format(second)+" "+inputsecondname);
        }
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
                ArrayList<DepoListesiModel> filterList = new ArrayList<DepoListesiModel>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getProductadi().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        DepoListesiModel bean = new DepoListesiModel(mStringFilterList.get(i).getProductadi(), mStringFilterList.get(i).getProductKod(), mStringFilterList.get(i).getFirsAmount(),mStringFilterList.get(i).getSecondAmount(),mStringFilterList.get(i).getFirstName(),mStringFilterList.get(i).getSecondName());
                        filterList.add(bean);
                    }
                    if ((mStringFilterList.get(i).getProductKod()).contains(constraint.toString())) {
                        DepoListesiModel bean = new DepoListesiModel(mStringFilterList.get(i).getProductadi(), mStringFilterList.get(i).getProductKod(), mStringFilterList.get(i).getFirsAmount(),mStringFilterList.get(i).getSecondAmount(),mStringFilterList.get(i).getFirstName(),mStringFilterList.get(i).getSecondName());
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
            beanList = (ArrayList<DepoListesiModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
