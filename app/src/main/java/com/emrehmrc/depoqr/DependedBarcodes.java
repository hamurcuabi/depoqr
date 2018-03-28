package com.emrehmrc.depoqr;

/**
 * Created by Emre Hmrc on 28.03.2018.
 */

public class DependedBarcodes {

    private String Name;
    private String Code;
    private Boolean IsCheck;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Boolean getCheck() {
        return IsCheck;
    }

    public void setCheck(Boolean check) {
        IsCheck = check;
    }
}
