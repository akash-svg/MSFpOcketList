package com.example.msfpocketlist.ui.employee;

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
import com.example.msfpocketlist.R;
import com.example.msfpocketlist.data.EmployeeHq;
import com.example.msfpocketlist.data.PocketEm;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    List<PocketEm> pocketEmList;
    Context context;
    OnItemClick onItemClick;

    public EmployeeAdapter(List<PocketEm> pocketEmList, Context context, OnItemClick onItemClick) {
        this.pocketEmList = pocketEmList;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hq_items_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PocketEm dataset = pocketEmList.get(position);

        holder.deptName.setText(dataset.deptTitle);
        holder.designation.setText(dataset.desgTitle);
        holder.deptContact.setText(dataset.mobileNo);
        holder.deptEmail.setText(dataset.emailId);

        holder.cardView.setOnClickListener(v->{onItemClick.onHqItemClick(dataset);});
        holder.offlineCall.setOnClickListener(v->{onItemClick.onOfflineCall(dataset);});
        holder.onLineCall.setOnClickListener(v->{onItemClick.onOnlineCall(dataset);});
    }

    @Override
    public int getItemCount() {
        return pocketEmList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchList(List<PocketEm> filteredList) {
        pocketEmList = filteredList;
        notifyDataSetChanged();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        TextView deptName, designation, deptContact,deptEmail;
        CardView cardView;
        ImageButton offlineCall,onLineCall;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deptName = itemView.findViewById(R.id.deptName);
            designation = itemView.findViewById(R.id.designation);
            deptContact = itemView.findViewById(R.id.deptContact);
            deptEmail = itemView.findViewById(R.id.dept_email);
            cardView = itemView.findViewById(R.id.cardView);
            offlineCall = itemView.findViewById(R.id.directCall);
            onLineCall = itemView.findViewById(R.id.onlineCalling);
        }
    }

    public interface OnItemClick{
        void onHqItemClick(PocketEm pocketEm);
        void onOfflineCall(PocketEm pocketEm);
        void onOnlineCall(PocketEm pocketEm);
    }
}
