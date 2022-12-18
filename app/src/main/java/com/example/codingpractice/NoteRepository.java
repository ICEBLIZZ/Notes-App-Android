package com.example.codingpractice;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        //Later in our view model we will also pass an Application
        //Application is the subclass of context we can use it as a context to create
        //our database instance

        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();//(we normally can't call an abstract class like this
        //as it don't have a body, but we made a database instance using a builder so Room
        // auto-generates all the necessary code for noteDao() method, in short Room
        // sub-classes our abstract class)

        allNotes = noteDao.getAllNotes();
    }

    //these methods are the API that the repository exposes to the outside
    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllNoteAsyncTask(noteDao).execute();
    }

    //(Room will automatically execute the database operations that returns the LiveData
    // on the background thread, so we don't have to take care of getAllNotes(). But for other database
    // operations as above methods, we have to execute our code on the background thread ourselves,
    // because room doesn't allow database operations on the main thread, since this could freeze our app)
    public LiveData<List<Note>> getAllNotes() {
        //retrieved from noteDao
        return allNotes;
    }

    //to execute our tasks on the background thread we will use async tasks

    //it has to be static so it doesn't have a reference to the repository itself,
    //otherwise this could cause a memory leak
    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {//AsyncTask<param, progress, result>
        private NoteDao noteDao; //we need noteDao to make database operations
        //since the class is static so we can't access the noteDao of our repository directly
        //so we have to pass it over a constructor

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {//AsyncTask<param, progress, result>
        private NoteDao noteDao; //we need noteDao to make database operations
        //since the class is static so we can't access the noteDao of our repository directly
        //so we have to pass it over a constructor

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {//AsyncTask<param, progress, result>
        private NoteDao noteDao; //we need noteDao to make database operations
        //since the class is static so we can't access the noteDao of our repository directly
        //so we have to pass it over a constructor

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNoteAsyncTask extends AsyncTask<Void, Void, Void> {//AsyncTask<param, progress, result>
        private NoteDao noteDao; //we need noteDao to make database operations
        //since the class is static so we can't access the noteDao of our repository directly
        //so we have to pass it over a constructor

        private DeleteAllNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAll();
            return null;
        }
    }
}
