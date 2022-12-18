package com.example.codingpractice;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Version number is whenever we make changes to our database we have to increment
 * this version number.And in practice scenario we keep it 1, and when we make changes
 * we just uninstall and reinstall our app
 */

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;//single instance

    public abstract NoteDao noteDao();//we later use this method to excess our DAO

    //singleton pattern -> only one instance of our database
    public static synchronized NoteDatabase getInstance(Context context){
        //synchronized means that only one thread at a time can access this method
        //so we can't accidentally create two instances of NoteDatabase class when two
        //different threads try to create NoteDatabase instances

        if(instance == null){
            //we can't use new as we are in a abstract class so we do the following
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class,
                    "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)//here we attach call back to our database
                    //when our instance is create a call back's on create will be called and our database
                    //will be populated
                    .build();

            //(if we update the version number of the database we have to tell Room how to
            // migrate to the new schema, if we don't do this and try to increase the version number
            // our app will crash as we will get an "illegal state exception" and by
            // fallbackToDestructiveMigration() we can avoid this, as it would delete our database
            // with all its tables and create it from scratch. So if we increment the version number
            // we will start with a new database)

        }
        return instance;
    }

    //This way we can populate our database right when we create it, so we won't have an empty
    //table, we will have notes in it
    private static final RoomDatabase.Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //we call async task here
            new PopulateDbAsyncTask(instance).execute();
            //now we have to attach this call back to our database
        }
    };

    //we have to call fill the table in the background thread
    //so we use an Async task
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            return null;
        }
    }
}
