package com.example.dn.accounting.Model;

/**
 * Created by dn on 2017/2/27.
 */

public class TagInformation {
    private String tagName;
    private float tagCost = 0;

    public float getTagCost() {
        return tagCost;
    }

    public void setTagCost(float tagCost) {
        this.tagCost = tagCost;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
