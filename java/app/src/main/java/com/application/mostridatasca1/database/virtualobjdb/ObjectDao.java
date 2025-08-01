package com.application.mostridatasca1.database.virtualobjdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface ObjectDao {
    @Insert
    void insertAll(VirtualObj... virtualObjs);

    @Query("SELECT * FROM virtualobj")
    List<VirtualObj> getAll();

    @Query("SELECT * FROM virtualobj WHERE id = :id")
    VirtualObj getObjectByID( int id);

    @Query("DELETE FROM virtualobj")
    void deleteAll();

    @Query("SELECT * FROM virtualobj WHERE id = :id")
    ListenableFuture<VirtualObj> getObjectByIDAsync(int id);
}
