package com.egehanasal.memory_book.Model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

import java.io.Serializable;

@Entity
public class Place implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="memory")
    public String memory;

    @ColumnInfo(name="latitude")
    public double latitude;

    @ColumnInfo(name="longitude")
    public double longitude;

    public Place(String name, String memory, double latitude, double longitude) {
        this.name = name;
        this.memory = memory;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
