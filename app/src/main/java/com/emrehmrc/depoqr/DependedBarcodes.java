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
