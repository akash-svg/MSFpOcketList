package com.msfpocketlist.ui.dashboard.hq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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


        if (dataset.mobileNo1.equalsIgnoreCase("null") || dataset.mobileNo1.equalsIgnoreCase("-")){
            holder.deptContact.setVisibility(View.GONE);
            holder.one.setVisibility(View.GONE);
        }else{
            holder.one.setVisibility(View.VISIBLE);
            holder.deptContact.setVisibility(View.VISIBLE);
            holder.deptContact.setText(dataset.mobileNo1);
        }

        if (dataset.mobileNo2.equalsIgnoreCase("null")||dataset.mobileNo2.equalsIgnoreCase("-")){
            holder.deptContactOne.setVisibility(View.GONE);
            holder.two.setVisibility(View.GONE);
        }else{
            holder.two.setVisibility(View.VISIBLE);
            holder.deptContactOne.setVisibility(View.VISIBLE);
            holder.deptContactOne.setText(dataset.mobileNo2);
        }

        if (!(dataset.emailId.equalsIgnoreCase("null")||dataset.emailId.equalsIgnoreCase("-"))){
            holder.deptEmail.setVisibility(View.VISIBLE);
            holder.deptEmail.setText(dataset.emailId);
        }else{
            holder.deptEmail.setVisibility(View.GONE);
        }

        if (!dataset.fullName.equalsIgnoreCase("null")){
            holder.employeeName.setVisibility(View.VISIBLE);
            holder.employeeName.setText(dataset.fullName);
        }else{
            holder.employeeName.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(v->{onItemClick.onHqItemClick(dataset);});
        holder.offlineCall.setOnClickListener(v->{onItemClick.onOfflineCall(dataset);});
        holder.onLineCall.setOnClickListener(v->{onItemClick.onOnlineCall(dataset);});
        holder.msgOne.setOnClickListener(v->{onItemClick.onMsgOne(dataset);});
        holder.msgTwo.setOnClickListener(v->{onItemClick.onMsgTwo(dataset);});

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
        TextView deptName, designation, deptContact,deptEmail,deptContactOne,employeeName;
        CardView cardView;
        LinearLayout one,two;
        ImageButton offlineCall,onLineCall,msgOne,msgTwo;

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
            msgOne = itemView.findViewById(R.id.messageOne);
            msgTwo = itemView.findViewById(R.id.messengeTwo);
            employeeName = itemView.findViewById(R.id.employeeName);
            one = itemView.findViewById(R.id.LayOne);
            two = itemView.findViewById(R.id.LayTwo);
        }
    }

    public interface OnItemClick {
        void onHqItemClick(EmployeeHq employeeHq);
        void onOfflineCall(EmployeeHq employeeHq);
        void onOnlineCall(EmployeeHq employeeHq);
        void onMsgOne(EmployeeHq employeeHq);
        void onMsgTwo(EmployeeHq employeeHq);
    }
}
