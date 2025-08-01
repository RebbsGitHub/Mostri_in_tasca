package com.application.mostridatasca1.database.playerdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertAll(User... users);


    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid = :uid")
    User getProfileByUID(int uid);

    @Query("UPDATE user SET name = :newName WHERE uid = :uid")
    void updateProfileName(int uid, String newName);

    @Query("UPDATE user SET profileversion = :profileversion WHERE uid = :uid")
    void updateProfileVersion(int uid,int profileversion);

    @Query("UPDATE user SET picture = :image WHERE uid = :uid")
    void updateProfileImg(int uid, String image);

    @Query("UPDATE user SET positionshare = :positionshare WHERE uid = :uid")
    void updatePositionShare(int uid, boolean positionshare);

    @Query("DELETE FROM user WHERE uid = :uid")
    void deleteUserByUID(int uid);

    @Query("UPDATE user SET weapon = :new_weapon WHERE uid = :uid")
    void updateWeaponByID(int uid, int new_weapon);
    @Query("UPDATE user SET armor = :new_armor WHERE uid = :uid")
    void updateArmorByID(int uid, int new_armor);
    @Query("UPDATE user SET amulet = :new_amulet WHERE uid = :uid")
    void updateAmuletByID(int uid, int new_amulet);
    @Query("UPDATE user SET life = :new_life WHERE uid = :uid")
    void updateLifeByID(int uid, int new_life);
    @Query("UPDATE user SET experience = :new_experience WHERE uid = :uid")
    void updateExperienceByID(int uid, int new_experience);

    /*
    @Query("UPDATE user SET armor = :i WHERE uid = :uid")
    void updateARMATURA(int uid, int i);
     */

}
