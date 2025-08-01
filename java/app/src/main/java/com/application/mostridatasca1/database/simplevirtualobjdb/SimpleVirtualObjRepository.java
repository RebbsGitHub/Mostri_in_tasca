package com.application.mostridatasca1.database.simplevirtualobjdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SimpleVirtualObj.class}, version = 1)
public abstract class SimpleVirtualObjRepository extends RoomDatabase {
    public abstract SimpleVirtualObjDao simpleVirtualObjDao();


}

