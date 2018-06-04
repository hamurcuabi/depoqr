package com.emrehmrc.depoqr.model;

/**
 * Created by cenah on 2/28/2018.
 */

public class ModelProductInfo {

    private String productPrice;
    private String productKdv;
    private String productKdvC;
    private String typeName;
    private String moneyUnitId;
    private String moneyunit;

    public ModelProductInfo(String productPrice, String productKdv, String productKdvC, String typeName, String moneyUnitId, String moneyunit) {
        this.productPrice = productPrice;
        this.productKdv = productKdv;
        this.productKdvC = productKdvC;
        this.typeName = typeName;
        this.moneyUnitId = moneyUnitId;
        this.moneyunit = moneyunit;

    }

    public ModelProductInfo() {

    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductKdv() {
        return productKdv;
    }

    public void setProductKdv(String productKdv) {
        this.productKdv = productKdv;
    }

    public String getProductKdvC() {
        if (productKdvC.equals("0")) {
            return "HAYIR";
        } else return "EVET";
    }

    public void setProductKdvC(String productKdvC) {
        this.productKdvC = productKdvC;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMoneyUnitId() {
        return moneyUnitId;
    }

    public void setMoneyUnitId(String moneyUnitId) {
        this.moneyUnitId = moneyUnitId;
    }

    public String getMoneyunit() {
        return moneyunit;
    }

    public void setMoneyunit(String moneyunit) {
        this.moneyunit = moneyunit;
    }
}
