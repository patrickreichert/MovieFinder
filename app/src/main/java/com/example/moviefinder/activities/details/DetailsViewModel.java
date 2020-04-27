package com.example.moviefinder.activities.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.moviefinder.database.MovieRepository;
import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.model.trailer.Trailer;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel
{
    private MovieRepository mRepository;

    public DetailsViewModel(@NonNull Application application)
    {
        super(application);

        mRepository = new MovieRepository(application);
    }

    public LiveData<List<Trailer>> getTrailers(int id, String apiKey)
    {
        return mRepository.getTrailers(id, apiKey);
    }

    public void saveMovie(Movie movie) {
        mRepository.insert(movie);
    }

    public void deleteMovie(Movie movie) {
        mRepository.delete(movie);
    }

    public LiveData<Movie> loadFavById(int id) {
        return mRepository.getMovieById(id);
    }
}
