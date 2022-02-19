package com.msfpocketlist.ui.pocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.msfpocketlist.R;
import com.msfpocketlist.data.Project;

import java.util.List;

public class PocketAdapter extends RecyclerView.Adapter<PocketAdapter.ViewHolder> {

    List<Project> pockets;
    Context context;
    OnItemClick onItemClick;

    public PocketAdapter(List<Project> pockets, Context context, OnItemClick onItemClick) {
        this.pockets = pockets;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mission_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project data = pockets.get(position);
        holder.name.setText(Html.fromHtml(data.title));
        holder.itemView.setOnClickListener(v->{
            onItemClick.onPocketListClick(data);
        });
    }

    @Override
    public int getItemCount() {
        return pockets.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchList(List<Project> filteredList) {
        pockets = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.missionName);
        }
    }

    public interface OnItemClick{
        void onPocketListClick(Project pocket);
    }
}
