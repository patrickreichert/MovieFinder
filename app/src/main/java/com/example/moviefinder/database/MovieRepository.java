package com.example.moviefinder.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.moviefinder.activities.AppExecutors;
import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.model.trailer.Trailer;
import com.example.moviefinder.network.ApiServices;

import java.util.List;

/**
 * Movie Repository class
 */
public class MovieRepository
{
    private ApiServices apiServices = ApiServices.getInstance();
    private MovieDAO mDao;
    private LiveData<List<Movie>> mFavMovies;
    private AppExecutors mExecutors = AppExecutors.getInstance();

    public MovieRepository(Application application) {
        MovieDB db = MovieDB.getDatabase(application);
        mDao = db.movieDAO();
        mFavMovies = mDao.getAllMovies();
    }

    //  for favorites
    public LiveData<List<Movie>> getFavMovies() {
        return mFavMovies;
    }

    public void insert(final Movie movie) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDao.insertMovie(movie);
            }
        });
    }

    public void delete(final Movie movie) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDao.deleteMovie(movie);
            }
        });
    }

    public LiveData<Movie> getMovieById(int id) {
        return mDao.getMovieById(id);
    }

    //for network
    public LiveData<List<Movie>> getMoviesFromNetwork(String sortType, String apiKey) {
        return apiServices.getMovies(sortType, apiKey);
    }

    public LiveData<List<Trailer>> getTrailers(Integer id, String apiKey) {
        return apiServices.getTrailers(id, apiKey);
    }
}

