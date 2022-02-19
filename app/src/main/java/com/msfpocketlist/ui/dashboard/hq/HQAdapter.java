package com.msfpocketlist.ui.dashboard.hq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.msfpocketlist.R;
import com.msfpocketlist.data.EmployeeHq;


import java.util.List;

public class HQAdapter extends RecyclerView.Adapter<HQAdapter.ViewHolder> {

    List<EmployeeHq> employeeHqList;
    Context context;
    OnItemClick onItemClick;

    public HQAdapter(List<EmployeeHq> employeeHqList, Context context, OnItemClick onItemClick) {
        this.employeeHqList = employeeHqList;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hq_items_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmployeeHq dataset = employeeHqList.get(position);

        holder.deptName.setText(dataset.deptTitle);
        holder.designation.setText(dataset.desgTitle);
        holder.deptContact.setText(dataset.mobileNo1);
        holder.deptContactOne.setText(dataset.mobileNo2);
        holder.deptEmail.setText(dataset.emailId);

        holder.cardView.setOnClickListener(v->{onItemClick.onHqItemClick(dataset);});
        holder.offlineCall.setOnClickListener(v->{onItemClick.onOfflineCall(dataset);});
        holder.onLineCall.setOnClickListener(v->{onItemClick.onOnlineCall(dataset);});

    }

    @Override
    public int getItemCount() {
        return employeeHqList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchList(List<EmployeeHq> filteredList) {
        employeeHqList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deptName, designation, deptContact,deptEmail,deptContactOne;
        CardView cardView;
        ImageButton offlineCall,onLineCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deptName = itemView.findViewById(R.id.deptName);
            designation = itemView.findViewById(R.id.designation);
            deptContact = itemView.findViewById(R.id.deptContact);
            deptContactOne = itemView.findViewById(R.id.deptContactOne);
            deptEmail = itemView.findViewById(R.id.dept_email);
            cardView = itemView.findViewById(R.id.cardView);
            offlineCall = itemView.findViewById(R.id.directCall);
            onLineCall = itemView.findViewById(R.id.onlineCalling);
        }
    }

    public interface OnItemClick {
        void onHqItemClick(EmployeeHq employeeHq);
        void onOfflineCall(EmployeeHq employeeHq);
        void onOnlineCall(EmployeeHq employeeHq);
    }
}
