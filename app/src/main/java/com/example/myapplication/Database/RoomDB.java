package com.example.myapplication.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.Models.Notes;

@Database(entities = Notes.class, version =1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB databse;
    private static final String DATABASE_NAME = "NoteApp";

    public synchronized static RoomDB getInstance(Context context) {
        if(databse==null){
            databse= Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build();
        }
        return databse;

    }

    public abstract MainDAO mainDAO();

}
