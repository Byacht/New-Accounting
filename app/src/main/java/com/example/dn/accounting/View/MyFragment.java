package com.example.dn.accounting.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dn.accounting.R;

/**
 * Created by dn on 2016/11/17.
 */

public class MyFragment extends Fragment {
    private static final String ARGS_PAGE = "page";
    private int mPage;

    public static MyFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE,page);
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (mPage == 1){
            view = inflater.inflate(R.layout.activity_main,container,false);
        } else {
            view = inflater.inflate(R.layout.activity_add_account,container,false);
        }
        return view;
    }
}
