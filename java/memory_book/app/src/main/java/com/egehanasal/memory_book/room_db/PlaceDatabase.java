package com.egehanasal.memory_book.room_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.egehanasal.memory_book.Model.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class PlaceDatabase extends RoomDatabase {

    public abstract PlaceDao placeDao();

}
