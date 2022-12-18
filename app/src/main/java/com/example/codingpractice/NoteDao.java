package com.example.codingpractice;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table")//we can pass custom queries like this
    void deleteAll();

    @Query("SELECT * FROM note_table ORDER BY priority")
    LiveData<List<Note>> getAllNotes();//Room will generate a Note arraylist
    /*
     * Room can return live data, which will make the object observable, as soon as
     * there are any changes in the notes table, List<Note> will automatically be updated
     * and the activity will be notified. And room takes care of all the necessary stuff to
     * update this room data object
     */
}
