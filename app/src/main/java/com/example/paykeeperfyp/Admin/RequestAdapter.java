package com.example.paykeeperfyp.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.User.ProductsAdapter;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    List<RequestModel> modelList;

    public RequestAdapter(List<RequestModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_list, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(modelList.get(position).getMessage());
        holder.email.setText(modelList.get(position).getReq_by());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.ur_body);
            email = itemView.findViewById(R.id.ur_email);
        }
    }
}
