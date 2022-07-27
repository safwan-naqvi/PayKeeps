package com.example.paykeeperfyp.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.Admin.RequestAdapter;
import com.example.paykeeperfyp.R;

import java.util.List;

public class KhaataAdapterPaid extends RecyclerView.Adapter<KhaataAdapterPaid.ViewHolder> {

    List<KhaataModel> model;

    public KhaataAdapterPaid(List<KhaataModel> model) {
        this.model = model;
    }

    @NonNull
    @Override
    public KhaataAdapterPaid.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.khaata_paid_layout, parent, false);
        return new KhaataAdapterPaid.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhaataAdapterPaid.ViewHolder holder, int position) {
        holder.date.setText("Date: "+model.get(position).getDebit_date());
        holder.return_date.setText("Return Date: "+model.get(position).getDebit_return());
        holder.amount.setText("Total: "+model.get(position).getDebit_balance()+"/- Rs");
        holder.reason.setText("Reason: "+model.get(position).getDebit_name());
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, return_date, reason, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.paid_payment_date);
            return_date = itemView.findViewById(R.id.paid_payment_return_date);
            reason = itemView.findViewById(R.id.paid_payment_reason);
            amount = itemView.findViewById(R.id.paid_payment_amount);
        }
    }
}
