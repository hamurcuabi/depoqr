package com.emrehmrc.depoqr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Emre Hmrc on 28.03.2018.
 */

public class DependedBarcodes implements Serializable{

    private String Name;
    private String Code;
    private  String ParentID;




    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    private Boolean IsCheck;

    public static ArrayList<DependedBarcodes> getData() {

        ArrayList<DependedBarcodes> datalist = new ArrayList<DependedBarcodes>();
        DependedBarcodes gecici = new DependedBarcodes();

        for (int i = 0; i < 5; i++) {

            gecici.setName("fsdfsdfds");
            gecici.setCode("fdsfdsfdsgdsgds");
            gecici.setCheck(true);
            datalist.add(gecici);
        }
        return datalist;


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
