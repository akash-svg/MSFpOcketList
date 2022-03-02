package com.msfpocketlist.ui.employee;


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
import com.msfpocketlist.data.Employee;
import com.msfpocketlist.data.EmployeeAll;
import java.util.List;

public class EmployeeCacheAdapter extends RecyclerView.Adapter<EmployeeCacheAdapter.ViewHolder> {
    List<EmployeeAll> pocketEmList;
    Context context;
    OnItemClick onItemClick;

    public EmployeeCacheAdapter(List<EmployeeAll> pocketEmList, Context context, OnItemClick onItemClick) {
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
        EmployeeAll dataset = pocketEmList.get(position);

        holder.deptName.setText(dataset.department);
        holder.designation.setText(dataset.designation);
        holder.deptEmail.setText(dataset.emailId);
        if (dataset.mobileNo1==null){
            holder.deptContact.setVisibility(View.GONE);
            holder.offlineCall.setVisibility(View.GONE);
            holder.msgOne.setVisibility(View.GONE);

        }else{
            holder.deptContact.setVisibility(View.VISIBLE);
            holder.offlineCall.setVisibility(View.VISIBLE);
            holder.msgOne.setVisibility(View.VISIBLE);
            holder.deptContact.setText(dataset.mobileNo1);
        }

        if (dataset.mobileNo2==null){
            holder.deptContactOne.setVisibility(View.GONE);
            holder.onLineCall.setVisibility(View.GONE);
            holder.msgTwo.setVisibility(View.GONE);
        }else{
            holder.deptContactOne.setVisibility(View.VISIBLE);
            holder.onLineCall.setVisibility(View.VISIBLE);
            holder.msgTwo.setVisibility(View.VISIBLE);
            holder.deptContactOne.setText(dataset.mobileNo2);
        }

        holder.cardView.setOnClickListener(v->{onItemClick.onHqItemClickOne(dataset);});
        holder.offlineCall.setOnClickListener(v->{onItemClick.onOfflineCallOne(dataset);});
        holder.onLineCall.setOnClickListener(v->{onItemClick.onOnlineCallOne(dataset);});
    }

    @Override
    public int getItemCount() {
        return pocketEmList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchList(List<EmployeeAll> filteredList) {
        pocketEmList = filteredList;
        notifyDataSetChanged();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        TextView deptName, designation, deptContact,deptEmail,deptContactOne;
        CardView cardView;
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
        }
    }

    public interface OnItemClick{
        void onHqItemClickOne(EmployeeAll pocketEm);
        void onOfflineCallOne(EmployeeAll pocketEm);
        void onOnlineCallOne(EmployeeAll pocketEm);
        void onOnMsgOne(EmployeeAll pocketEm);
        void onOnMsgTwo(EmployeeAll pocketEm);
    }
}
