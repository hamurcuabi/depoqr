package com.emrehmrc.depoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlasiyerProductAdapter extends BaseAdapter{
    private Context context;
    ArrayList<PlasiyerProduct.PlasiyerProductModel> beanList;
    private ArrayList<PlasiyerProduct.PlasiyerProductModel> mStringFilterList;
    private LayoutInflater inflater;
    private int[] colors = new int[] { 0x304267B2, 0x300000FF };
    private final int[] bgColors = new int[] { R.color.list_bg_2, R.color.list_bg_1 };

    public PlasiyerProductAdapter(Context context, ArrayList<PlasiyerProduct.PlasiyerProductModel> objectss) {
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
            convertView = inflater.inflate(R.layout.plasiyerpro_adapter_view, null);

        }
        int colorPosition = position % bgColors.length;
        convertView.setBackgroundResource(bgColors[colorPosition]);
        TextView view_name = (TextView) convertView.findViewById(R.id.view_name);
        TextView view_fiyat = (TextView) convertView.findViewById(R.id.view_fiyat);
        TextView view_miktar = (TextView) convertView.findViewById(R.id.view_miktar);
        TextView view_toplamtutar = (TextView) convertView.findViewById(R.id.view_toplamtutar);
        TextView view_kdv = (TextView) convertView.findViewById(R.id.view_kdv);
        TextView view_geneltutar = (TextView) convertView.findViewById(R.id.view_geneltutar);

        PlasiyerProduct.PlasiyerProductModel bean = beanList.get(position);
        view_name.setText(bean.getProductName());
        view_fiyat.setText(bean.getFiyat());
        view_miktar.setText(bean.getMiktar());

        view_toplamtutar.setText(bean.getToplamTutar());
        view_kdv.setText(bean.getKdv());
        view_geneltutar.setText(bean.getGenelTutar());
        return convertView;
    }
}
