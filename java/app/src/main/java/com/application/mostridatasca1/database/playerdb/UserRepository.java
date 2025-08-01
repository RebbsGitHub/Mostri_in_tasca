package com.application.mostridatasca1.database.playerdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class UserRepository extends RoomDatabase {
    public abstract UserDao userDao();
}
