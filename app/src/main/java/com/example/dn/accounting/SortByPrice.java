package com.example.dn.accounting;

import android.util.Log;

import com.example.dn.accounting.Model.TagInformation;

import java.util.Comparator;

/**
 * Created by dn on 2017/3/9.
 */

public class SortByPrice implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        TagInformation tagInformation1 = (TagInformation) o1;
        TagInformation tagInformation2 = (TagInformation) o2;
        if (tagInformation1.getTagCost() < tagInformation2.getTagCost()) {
            return 1;
        }
        return -1;
    }
}
