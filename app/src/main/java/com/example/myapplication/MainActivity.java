package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.example.myapplication.Adapters.NotesListAdapter;
import com.example.myapplication.Database.RoomDB;
import com.example.myapplication.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    List<Notes> notes = new ArrayList<>();
    RoomDB database;
    FloatingActionButton fab_add;
    Notes selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView =findViewById(R.id.recycler_home);
        fab_add=findViewById(R.id.fab_add);

        database = RoomDB.getInstance(this);
        notes=database.mainDAO().getAll();

        updateRecycler(notes);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
                startActivityForResult(intent,101);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==101){
            if (resultCode== Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();

            }
        } else if (requestCode ==102) {
            if (resultCode==Activity.RESULT_OK){
                Notes new_notes= (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(),new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter=new NotesListAdapter(MainActivity.this,notes,notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener=new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this,NotesTakerActivity.class);
            intent.putExtra("old_note",notes);
            startActivityForResult(intent,102);


        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote=new Notes();
            selectedNote=notes;
            showPopup(cardView);

        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.inflate(R.menu.popup_menu);

        Menu menu = popupMenu.getMenu();
        MenuItem pinItem = menu.findItem(R.id.pin);
        MenuItem deleteItem = menu.findItem(R.id.delete);

        // Set click listeners for each menu item
        pinItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (selectedNote.isPinned()) {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(MainActivity.this, "Unpinned", Toast.LENGTH_SHORT).show();
                } else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(MainActivity.this, "Pinned!", Toast.LENGTH_SHORT).show();
                }

                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
            }
        });

        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popupMenu.show();
    }

}