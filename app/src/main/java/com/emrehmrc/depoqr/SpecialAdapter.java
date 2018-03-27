package com.emrehmrc.depoqr;

/**
 * Created by Emre Hmrc on 9.11.2017.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SpecialAdapter extends SimpleAdapter {
    private int[] colors = new int[] { 0x304267B2, 0x300000FF };


      public SpecialAdapter(Context context, List<Map<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        int colorPos = position % colors.length;
        if(colorPos==1)
        view.setBackgroundColor(Color.parseColor("#E0F7FA"));
        else  view.setBackgroundColor(Color.parseColor("#d849a9ed"));

        return view;
    }

}
