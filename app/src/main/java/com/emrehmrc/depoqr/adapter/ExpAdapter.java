package com.emrehmrc.depoqr.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.emrehmrc.depoqr.R;

import java.util.HashMap;
import java.util.List;

public class ExpAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;


    public ExpAdapter(Context context, List<String> listDataHeader,
                      HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exlist_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.exlistitem);
        switch (childText) {

            case "GRUP BARKOD":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.ic_action_grup, 0, 0, 0);
                break;
            case "BARKOD BILGI":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds( R
                        .drawable.ic_action_about, 0,0, 0);
                break;
            case "DEPO LISTESI":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.ic_action_depolistesi, 0, 0, 0);
                break;
            case "MAL KABUL":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.barcode, 0,0 , 0);
                break;
            case "SEVKİYAT":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.transfer, 0, 0, 0);
                break;
            case "İADE":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds( R
                        .drawable.returning, 0,0, 0);
                break;
            case "DEPOLAR ARASI TRANSFER":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds( R
                        .drawable.transfering, 0,0, 0);
                break;
            case "PLASİYER SATIŞ":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.menuplasiyer, 0, 0, 0);
                break;
            case "TAKVİM":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.takvim, 0, 0, 0);
                break;
            case "PAYLAŞIM":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.share, 0, 0, 0);
                break;
            case "SATIN ALMA1":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.pay, 0, 0, 0);
                break;
            case "SATIN ALMA2":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds( R
                        .drawable.settings, 0,0, 0);
                break;
            case "SATIN ALMA3":
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R
                        .drawable.count, 0, 0, 0);
                break;

            default:
                break;


        }


        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exlist_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.exlistTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        switch (headerTitle) {

            case "LOJİSTİK":
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R
                        .drawable.loj, 0);

                break;
            case "PANO":
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R
                        .drawable.pano, 0);

                break;
            case "SATIN ALMA":
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R
                        .drawable.pay, 0);

                break;
            default:
                break;


        }



        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}