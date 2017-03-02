package com.example.dn.accounting.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by dn on 2017/2/24.
 */

public class Tag {
    private String tagName;
    private Drawable tagImage;

    public Drawable getTagImage() {
        return tagImage;
    }

    public void setTagImage(Drawable tagImage) {
        this.tagImage = tagImage;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
