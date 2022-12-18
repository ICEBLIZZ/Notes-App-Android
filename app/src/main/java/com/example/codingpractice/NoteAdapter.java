package com.example.codingpractice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder>{

    //private List<Note> notes = new ArrayList<>();
    //(Now we are extending to ListAdapter class so we don't need to store the notes array ourselves
    // we can pass it to the ListAdapter super class and it will take care of the storing)
    private onItemClickListener listener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            //(By doing this return oldItem.equals(newItem), it will always return false
            // as by live data when ever list is updated, an entirely new list is
            // created having different references, so it will be false even though their
            // ids are the same. By using .equals whenever a single item is updated
            // all the items would flash as everything is being updated, whereas we only
            // want the single item that's updated to be flashed(update) not the entire list.
            // So if the contentsAreTheSame returns true then only one item will be updated);
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = getItem(position);//this will access the list that we pass to the super class
        holder.priorityText.setText(String.valueOf(currentNote.getPriority()));
        holder.descriptionText.setText(currentNote.getDescription());
        holder.titleText.setText(currentNote.getTitle());

    }
    //delete getItemCount() as ListAdapter will take care of them
    //and remove setNotes as ListAdapter have a method for that

    //We will pass a note for deletion so we need a method to return a note at a
    //certain position
    public Note getNotesAt(int position){
        return getItem(position);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView priorityText;
        private TextView titleText;
        private TextView descriptionText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            priorityText = itemView.findViewById(R.id.text_view_priority);
            titleText = itemView.findViewById(R.id.text_view_title);
            descriptionText = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION) {
                        //(check for listener not null and in case we click an item view
                        // on delete animation, to prevent crashing of app)
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    //Creating an on item click listener using an interface
    public interface onItemClickListener{
        void onItemClick(Note note);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}
