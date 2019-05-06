package com.ethanmck.a382finalproject;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.provider.ContactsContract;

import java.util.List;

/*
* Data access object for interacting with the notecard database.
 */
@Dao
public interface NotecardDao {

    @Insert
    void insert(Notecard notecard);

    @Update
    void update(Notecard notecard);

    @Delete
    void delete(Notecard notecard);

    @Query("DELETE FROM notecard_table")
    void deleteAll();

    @Query("SELECT * from notecard_table where gen_id= :genId LIMIT 1")
    Notecard[] codeExists(int genId);

    @Query("SELECT * from notecard_table ORDER BY `order` DESC")
    LiveData<List<Notecard>> getAllNotecards();

    @Query("SELECT * from notecard_table ORDER BY gen_id ASC, un_id ASC")
    List<Notecard> getAllNotecardsQ();

    @Query("SELECT * from notecard_table where gen_id= :genId ORDER BY `order` DESC")
    LiveData<List<Notecard>> getNotecard(int genId);

    @Query("SELECT * from notecard_table where gen_id= :genId")
    List<Notecard> getNotecardQ(int genId);

    @Query("SELECT * from notecard_table LIMIT 1")
    Notecard[] getAnyNotecard();
}
