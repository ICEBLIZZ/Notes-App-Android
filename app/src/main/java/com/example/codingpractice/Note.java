package com.example.codingpractice;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

//that's how we define an entity which will be a table in our SQLite database
@Entity(tableName = "note_table") //by default table name is Note
public class Note {
    //Room will automatically generate column for these fields

    @PrimaryKey(autoGenerate = true)
    private int id; //id is set as a primary key

    private String title;
    //@Ignore by using ignore the elements won't be added to the table
    private String description;
    //@ColumnInfo(name = "priority_column") //we can name our columns like this
    private int priority;

    //constructor
    public Note(String title, String description, int priority) {
        //we won't include id here as it will be generated automatically
        //if we do not have any attribute here then room cannot recreate it later
        //so for them we will create a setter method(id in this case)
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    //setter
    public void setId(int id) {
        this.id = id;
    }

    //getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
