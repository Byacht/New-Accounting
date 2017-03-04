package com.example.dn.accounting.Model;

import java.io.Serializable;

/**
 * Created by dn on 2016/11/13.
 */

public class Account implements Serializable{
    private float cost;
    private String information;
    private String time;
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    private int type;
    private boolean isCheck = false;
    private boolean isShowCheckBox = false;

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public static final int TYPE_COST = 0;
    public static final int TYPE_INCOME = 1;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean getIsCheck(){
        return isCheck;
    }
}
