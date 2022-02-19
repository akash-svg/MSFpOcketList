package com.msfpocketlist.ui.dashboard.mission;

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
import com.msfpocketlist.data.Mission;
import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

     List<Mission> dataset;
     Context context;
     OnItemClick onItemClick;

    public MissionAdapter(List<Mission> dataset, Context context,OnItemClick onItemClick) {
        this.dataset = dataset;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mission_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.missionName.setText(Html.fromHtml(dataset.get(position).title));
        holder.itemView.setOnClickListener(v->{
            onItemClick.onMissionItemClick(dataset.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchList(List<Mission> filteredList) {
        dataset = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView missionName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            missionName = itemView.findViewById(R.id.missionName);
        }
    }

    public interface OnItemClick{
        void onMissionItemClick(Mission mission);
    }

}
