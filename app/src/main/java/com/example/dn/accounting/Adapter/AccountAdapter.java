package com.example.dn.accounting.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.dn.accounting.Activity.MainActivity;
import com.example.dn.accounting.View.AccountView;
import com.example.dn.accounting.Model.Account;
import com.example.dn.accounting.R;

import java.util.List;

/**
 * Created by dn on 2016/11/13.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {

    private List<Account> accounts;
    private Context context;
    private float cost;
    private String information;
    private String time;
    private int type;

    private OnItemLongClickListener onItemLongClickListener;
    private OnItemIsCheckedListener onItemIsCheckedListener;

    public AccountAdapter(Context context,List<Account> accounts){
        this.context = context;
        this.accounts = accounts;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemIsCheckedListener(OnItemIsCheckedListener onItemIsCheckedListener){
        this.onItemIsCheckedListener = onItemIsCheckedListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.account_view,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Account account = accounts.get(position);
        cost = account.getCost();
        information = account.getInformation();
        time = account.getTime();
        type = account.getType();
        if (type == Account.TYPE_COST){
            holder.accountView.setCostColor(context.getResources().getColor(R.color.costColor));
            holder.accountView.setTitleColor(context.getResources().getColor(R.color.costColor));
            holder.accountView.setTitle("账户支出");
        } else{
            holder.accountView.setCostColor(context.getResources().getColor(R.color.incomeColor));
            holder.accountView.setTitleColor(context.getResources().getColor(R.color.incomeColor));
            holder.accountView.setTitle("账户收入");
        }
        holder.accountView.setCost(cost);
        holder.accountView.setTime(time);
        holder.accountView.setInformation(information);
        holder.accountView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener!=null){
                    onItemLongClickListener.onItemLongClick(holder.accountView,position);
                }
                return false;
            }
        });
        if (account.isShowCheckBox()){
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (holder.checkBox.getVisibility() == View.VISIBLE){
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        account.setIsCheck(true);
                    } else {
                        account.setIsCheck(false);
                    }
                    if (onItemIsCheckedListener != null){
                        onItemIsCheckedListener.onItemIsChecked(account);
                    }
                }
            });
        }
        if (account.getIsCheck()){
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        AccountView accountView;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            accountView = (AccountView) itemView.findViewById(R.id.account);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    public interface OnItemIsCheckedListener{
        void onItemIsChecked(Account account);
    }
}
