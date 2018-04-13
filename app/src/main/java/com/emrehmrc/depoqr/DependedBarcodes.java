package com.emrehmrc.depoqr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Emre Hmrc on 28.03.2018.
 */

public class DependedBarcodes implements Serializable {

    private String Name;
    private String Code;
    private String CodeNo;
    private Boolean IsCheck;
    private String FirstUnit;
    private String SecondUnit;
    private String FirstAmount;
    private String SecondAmount;
    private String ProductCode;

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getFirstUnit() {
        return FirstUnit;
    }

    public void setFirstUnit(String firstUnit) {
        FirstUnit = firstUnit;
    }

    public String getSecondUnit() {
        return SecondUnit;
    }

    public void setSecondUnit(String secondUnit) {
        SecondUnit = secondUnit;
    }

    public String getFirstAmount() {
        return FirstAmount;
    }

    public void setFirstAmount(String firstAmount) {
        FirstAmount = firstAmount;
    }

    public String getSecondAmount() {
        return SecondAmount;
    }

    public void setSecondAmount(String secondAmount) {
        SecondAmount = secondAmount;
    }

    public String getCodeNo() {
        return CodeNo;
    }

    public void setCodeNo(String codeNo) {
        CodeNo = codeNo;
    }

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
