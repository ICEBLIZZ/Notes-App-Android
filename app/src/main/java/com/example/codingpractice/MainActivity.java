package com.example.codingpractice;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for floating button
        ActivityResultLauncher<Intent> activityAddNoteResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        //do operations
                        assert data != null;
                        String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                        String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                        int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                        Note note = new Note(title, description, priority);
                        noteViewModel.insert(note);
                        Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(this, "Note not Saved", Toast.LENGTH_SHORT).show();
                    }
                });

        //for item click listener
        ActivityResultLauncher<Intent> activityAddEditNoteResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        //do operations
                        assert data != null;
                        int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
                        if(id == -1){
                            Toast.makeText(MainActivity.this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                        String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                        int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                        Note note = new Note(title, description, priority);
                        note.setId(id);//without this note can't be updated
                        noteViewModel.update(note);
                        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Note Not Updated", Toast.LENGTH_SHORT).show();
                    }
                });

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                activityAddNoteResultLauncher.launch(intent);
            }
        });

        RecyclerView notesRecyclerView = findViewById(R.id.recycler_view);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        NoteAdapter noteAdapter = new NoteAdapter();
        notesRecyclerView.setAdapter(noteAdapter);


        //(In case of fragments we can also pass getActivity() instead of this. Then view model will be scoped to
        // life cycle of the underlying activity, it means that it won't get destroyed when the fragment is
        // detached from the activity. And this is useful when you want to share data between multiple
        // fragments, because the you can use the view model of the underlying activity to store the data)

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //this will be triggered whenever data in our LiveData object changes
                //here we will update recycler view
                //onChanged() will only be called if the activity is in the foreground

                //(In case of notifyDataSetChanged() this works fine, but on notifyItemChanged
                // or other similar methods we need position of the item where the change
                // occurred. Now we will write logic for that: We need a way to compare the old list
                // to the new list and then calculate at which position changes happen, and for that
                // we can use "DiffUtil" class. But we will use ListAdapter class instead.
                // ListAdapter(abstract class): it is a sub class of recyclerview class that already implements
                // DiffUtil. The main advantage of ListAdapter is that it runs all list comparison in the
                // background thread)

                //noteAdapter.setNotes(notes);
                //instead of this we will use a method a the ListAdapter
                noteAdapter.submitList(notes);
            }
        });

        //this class wil make our recycler view swipe able
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {//swipeable on left and right
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNotesAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Notes deleted", Toast.LENGTH_SHORT).show();
            }

        }).attachToRecyclerView(notesRecyclerView);

        //setting on click listener for recycler view items
        noteAdapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
            intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
            intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());

            setResult(RESULT_OK, intent);
            activityAddEditNoteResultLauncher.launch(intent);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_notes) {
            noteViewModel.deleteAllNotes();
            Toast.makeText(MainActivity.this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}