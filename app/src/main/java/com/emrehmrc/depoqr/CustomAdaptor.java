package com.emrehmrc.depoqr;

/**
 * Created by Emre Hmrc on 6.02.2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;
import java.util.Map;

public class CustomAdaptor extends ArrayAdapter {
    Model[] modelItems = null;
    Context context;

    public CustomAdaptor(Context context, Model[] resource) {
        super(context, R.layout.depolist, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.depolist, parent, false);

  
        return convertView;
    }
}