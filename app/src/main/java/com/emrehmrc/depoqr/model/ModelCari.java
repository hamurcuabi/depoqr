package com.emrehmrc.depoqr.model;

/**
 * Created by cenah on 2/23/2018.
 */

public class ModelCari {
    private String cariadi ;
    private String carikod ;
    private String cariId;

    public String getCariId() {
        return cariId;
    }

    public void setCariId(String cariId) {
        this.cariId = cariId;
    }

    public ModelCari(String cariadi, String carikod , String cariId) {
        this.cariadi = cariadi;
        this.carikod = carikod;
        this.cariId = cariId;
    }
    public  ModelCari (){


    }

    public String getCariadi() {
        return cariadi;
    }

    public void setCariadi(String cariadi) {
        this.cariadi = cariadi;
    }

    public String getCarikod() {
        return carikod;
    }

    public void setCarikod(String carikod) {
        this.carikod = carikod;
    }
}
