package com.example.moviefinder.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moviefinder.model.movie.Movie;

/**
 * Movie DB class
 */
@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDB extends RoomDatabase
{
    private static final String DB_NAME = "favorite_db";
    private static MovieDB sInstance;

    static MovieDB getDatabase(final Context context)
    {
        if (sInstance == null)
        {
            synchronized (MovieDB.class)
            {
                if (sInstance == null)
                {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), MovieDB.class, DB_NAME).build();
                }
            }
        }
        return sInstance;
    }

    public abstract MovieDAO movieDAO();
}
