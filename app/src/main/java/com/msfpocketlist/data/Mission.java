package com.msfpocketlist.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "mission_table")
public class Mission {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    public Integer id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    public String title;
}
