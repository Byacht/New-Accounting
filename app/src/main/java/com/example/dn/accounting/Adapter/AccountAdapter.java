package com.example.dn.accounting.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public AccountAdapter(Context context,List<Account> accounts){
        this.context = context;
        this.accounts = accounts;
        Log.d("out",accounts.size()+"");
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.account_view,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Account account = accounts.get(position);
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

        public MyViewHolder(View itemView) {
            super(itemView);
            accountView = (AccountView) itemView.findViewById(R.id.account);
        }
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
}
