package com.emrehmrc.depoqr.model;

public class DepoListesiModel {

    private String productadi;
    private String productKod;
    private float firsAmount;
    private float secondAmount;
    private String firstName;
    private String secondName;

    public DepoListesiModel(String productadi, String productKod, float firsAmount, float secondAmount, String firstName, String secondName) {
        this.productadi = productadi;
        this.productKod = productKod;
        this.firsAmount = firsAmount;
        this.secondAmount = secondAmount;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public String getProductadi() {
        return productadi;
    }

    public void setProductadi(String productadi) {
        this.productadi = productadi;
    }

    public String getProductKod() {
        return productKod;
    }

    public void setProductKod(String productKod) {
        this.productKod = productKod;
    }

    public float getFirsAmount() {
        return firsAmount;
    }

    public void setFirsAmount(float firsAmount) {
        this.firsAmount = firsAmount;
    }

    public float getSecondAmount() {
        return secondAmount;
    }

    public void setSecondAmount(float secondAmount) {
        this.secondAmount = secondAmount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}
