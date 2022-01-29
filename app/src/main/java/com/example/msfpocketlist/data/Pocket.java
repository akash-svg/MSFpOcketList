package com.example.msfpocketlist.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "pocket_table")
public class Pocket {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    public Integer id;

    @ColumnInfo(name = "mission")
    @SerializedName("mission")
    @Expose
    public Integer mission;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    public String title;
}
