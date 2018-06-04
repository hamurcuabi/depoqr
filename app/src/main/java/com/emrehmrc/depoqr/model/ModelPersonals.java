package com.emrehmrc.depoqr.model;

public class ModelPersonals {
    private String personalId;
    private String personalName;

    public ModelPersonals(String personalId, String personalName) {
        this.personalId = personalId;
        this.personalName = personalName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }
}
