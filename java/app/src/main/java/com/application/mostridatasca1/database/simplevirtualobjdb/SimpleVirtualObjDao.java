package com.application.mostridatasca1.database.simplevirtualobjdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SimpleVirtualObjDao {

    @Insert
    void insertAll(List<SimpleVirtualObj> simpleVirtualObjs);

    @Query("SELECT * FROM simplevirtualobj")
    List<SimpleVirtualObj> getAll();

    @Query("DELETE FROM simplevirtualobj")
    void deleteAll();


}


