package com.emrehmrc.depoqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlasiyerProductDetailsAdapter extends BaseAdapter {
    private Context context;
    ArrayList<PlasiyerProductDetails.PlasiyerProductDetailModel> beanList;
    private ArrayList<PlasiyerProductDetails.PlasiyerProductDetailModel> mStringFilterList;
    private LayoutInflater inflater;
    private final int[] bgColors = new int[] { R.color.list_bg_2, R.color.list_bg_1 };

    public PlasiyerProductDetailsAdapter(Context context, ArrayList<PlasiyerProductDetails.PlasiyerProductDetailModel> objectss) {
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

        PlasiyerProductDetails.PlasiyerProductDetailModel bean = beanList.get(position);
        view_name.setText(bean.getBarkod());
        view_fiyat.setText("" + new DecimalFormat("##.##").format(bean.getFiyat()));
        view_miktar.setText("" + new DecimalFormat("##.##").format(bean.getMiktar()));

        view_toplamtutar.setText("" + new DecimalFormat("##.##").format(bean.getToplamTutar()));
        view_kdv.setText("" + new DecimalFormat("##.##").format(bean.getKdv()));
        view_geneltutar.setText("" + new DecimalFormat("##.##").format(bean.getGenelTutar()));
        return convertView;
    }
}
