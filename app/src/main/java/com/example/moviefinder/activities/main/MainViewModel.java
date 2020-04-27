package com.example.moviefinder.activities.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.moviefinder.database.MovieRepository;
import com.example.moviefinder.model.movie.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel
{
    private MovieRepository mRepository;
    private LiveData<List<Movie>> mAllMovies;

    public MainViewModel(@NonNull Application application)
    {
        super(application);

        mRepository = new MovieRepository(application);
    }

    LiveData<List<Movie>> loadAllMovies(String sort, String apiKey)
    {
        return mAllMovies = mRepository.getMoviesFromNetwork(sort, apiKey);
    }

    LiveData<List<Movie>> getFavMovies()
    {
        return mRepository.getFavMovies();
    }

    public LiveData<Movie> loadFavById(int id)
    {
        return mRepository.getMovieById(id);
    }
}
