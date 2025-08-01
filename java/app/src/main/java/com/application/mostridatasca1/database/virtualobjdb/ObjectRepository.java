package com.application.mostridatasca1.database.virtualobjdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {VirtualObj.class}, version = 1)
public abstract class ObjectRepository extends RoomDatabase {
    public abstract ObjectDao objectDao();
}
