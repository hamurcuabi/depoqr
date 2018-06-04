package com.emrehmrc.depoqr.model;

public class PlasiyerProductModel {
    String kod;
    String productName;
    float fiyat;
    float miktar;
    float toplamTutar;
    float kdv;
    float genelTutar;

    public PlasiyerProductModel(String kod, String productName, float fiyat, float miktar, float toplamTutar, float kdv, float genelTutar) {
        this.kod = kod;
        this.productName = productName;
        this.fiyat = fiyat;
        this.miktar = miktar;
        this.toplamTutar = toplamTutar;
        this.kdv = kdv;
        this.genelTutar = genelTutar;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getFiyat() {
        return fiyat;
    }

    public void setFiyat(float fiyat) {
        this.fiyat = fiyat;
    }

    public float getMiktar() {
        return miktar;
    }

    public void setMiktar(float miktar) {
        this.miktar = miktar;
    }

    public float getToplamTutar() {
        return toplamTutar;
    }

    public void setToplamTutar(float toplamTutar) {
        this.toplamTutar = toplamTutar;
    }

    public float getKdv() {
        return kdv;
    }

    public void setKdv(float kdv) {
        this.kdv = kdv;
    }

    public float getGenelTutar() {
        return genelTutar;
    }

    public void setGenelTutar(float genelTutar) {
        this.genelTutar = genelTutar;
    }
}
