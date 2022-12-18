package com.example.codingpractice;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    //Android view model is the subclass of the view model class, and we use it as it have an
    //application object passed in the constructor, which we can use whenever the application
    //context is needed
    //(Note: you should never store a context of an activity or a view that references an activity
    //in the view model because view model is designed to outlive an activity after its is destroyed
    // and if he hold a reference to an already destroyed activity, we have a memory leak. But here
    // we have to pass a context to our repository, because we need it there to instantiate our
    // database instance, and this is where we extend AndroidViewModel because then we can get
    // passed an application and can pass it down to the database)

    private final NoteRepository repository;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }
    //(our activity have reference to the view model, not to the repository so this is where we
    // create wrapper methods to our database operation methods from our repository)

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

}
