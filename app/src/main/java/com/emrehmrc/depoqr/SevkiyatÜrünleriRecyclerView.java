package com.emrehmrc.depoqr;

import java.util.ArrayList;

/**
 * Created by Emre Hmrc on 26.02.2018.
 */

public class SevkiyatÜrünleriRecyclerView {

    private boolean isChecked;
    private String uyari2;
    private String uyari1;
    private String uniqCode;
    private String barcodeid;
    private String productid;
    private String paletid;
    private String name;
    private String firstUnit;
    private String secondUnit;
    private float firstamount;
    private float secondamount;
    private String prductionDate;


    public static ArrayList<SevkiyatÜrünleriRecyclerView> getData() {

        ArrayList<SevkiyatÜrünleriRecyclerView> datalist = new ArrayList<SevkiyatÜrünleriRecyclerView>();
        SevkiyatÜrünleriRecyclerView gecici = new SevkiyatÜrünleriRecyclerView();

        for (int i = 0; i < 5; i++) {
            gecici.setChecked(true);
            gecici.setFirstUnit("1");
            gecici.setFirstUnit("2");
            gecici.setName("Ürün Adı");
            gecici.setFirstamount((float) 1.0);
            gecici.setSecondamount((float) 2.0);
            gecici.setUniqCode("uniq kod");
            datalist.add(gecici);
        }
        return datalist;


    }

    public String getPrductionDate() {
        return prductionDate;
    }

    public void setPrductionDate(String prductionDate) {
        this.prductionDate = prductionDate;
    }

    public String getUyari2() {
        return uyari2;
    }

    public void setUyari2(String uyari2) {
        this.uyari2 = uyari2;
    }

    public String getUyari1() {
        return uyari1;
    }

    public void setUyari1(String uyari1) {
        this.uyari1 = uyari1;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getPaletid() {
        return paletid;
    }

    public void setPaletid(String paletid) {
        this.paletid = paletid;
    }

    public String getSecondUnit() {
        return secondUnit;
    }

    public void setSecondUnit(String secondUnit) {
        this.secondUnit = secondUnit;
    }

    public float getFirstamount() {
        return firstamount;
    }

    public void setFirstamount(float firstamount) {
        this.firstamount = firstamount;
    }

    public float getSecondamount() {
        return secondamount;
    }

    public void setSecondamount(float secondamount) {
        this.secondamount = secondamount;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getUniqCode() {
        return uniqCode;
    }

    public void setUniqCode(String uniqCode) {
        this.uniqCode = uniqCode;
    }

    public String getBarcodeid() {
        return barcodeid;
    }

    public void setBarcodeid(String barcodeid) {
        this.barcodeid = barcodeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstUnit() {
        return firstUnit;
    }

    public void setFirstUnit(String firstUnit) {
        this.firstUnit = firstUnit;
    }


}
