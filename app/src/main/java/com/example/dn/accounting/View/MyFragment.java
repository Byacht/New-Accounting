package com.example.dn.accounting.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dn.accounting.R;

/**
 * Created by dn on 2016/11/17.
 */

public class MyFragment extends Fragment {
    private static final String ARGS_PAGE = "page";
    private int mPage;
    private TextView mTextView;

    public interface OnShowTime{
        void showTime(String time);
    }

    private OnShowTime onShowTimeListener;

    public void setOnShowTimeListener(OnShowTime onShowTimeListener){
        this.onShowTimeListener = onShowTimeListener;
    }

    public static MyFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE, page);
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
            view = inflater.inflate(R.layout.choose_month_layout,container,false);
            mTextView = (TextView) view.findViewById(R.id.Jan);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onShowTimeListener != null) {
                        onShowTimeListener.showTime("2017-01");
                    }
                }
            });
        } else {
            view = inflater.inflate(R.layout.choose_month_layout,container,false);
        }
        return view;
    }
}
