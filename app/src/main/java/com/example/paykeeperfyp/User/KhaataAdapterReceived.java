package com.example.paykeeperfyp.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.R;

import java.util.List;

public class KhaataAdapterReceived extends RecyclerView.Adapter<KhaataAdapterReceived.ViewHolder> {

    List<KhaataModelReceived> model;

    public KhaataAdapterReceived(List<KhaataModelReceived> model) {
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.khaata_received_layout, parent, false);
        return new KhaataAdapterReceived.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText("Date: "+model.get(position).getCredit_date());
        holder.amount.setText("Total: "+model.get(position).getCredit_balance()+"/- Rs");
        holder.reason.setText("Reason: "+model.get(position).getCredit_name());
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date, reason, amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.received_payment_date);
            amount = itemView.findViewById(R.id.received_payment_amount);
            reason = itemView.findViewById(R.id.received_payment_reason);
        }
    }
}
