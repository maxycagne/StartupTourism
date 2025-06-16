package com.example.startuptourism.Database.RoomDb.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.startuptourism.Database.RoomDb.Entity.User;

@Dao
public interface UserDao {
    @Insert
    void addUser(User user);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User LIMIT 1")
    User getCurrentUser();
}
