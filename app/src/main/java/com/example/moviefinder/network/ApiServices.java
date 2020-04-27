package com.example.moviefinder.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.model.movie.MovieResponse;
import com.example.moviefinder.model.trailer.Trailer;
import com.example.moviefinder.model.trailer.TrailerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API services
 */
public class ApiServices
{
    private static ApiServices apiServices = null;
    private static ApiInterface apiInterface;

    private ApiServices()
    {
        // Retrofit trasforma la tua API in un'interfaccia Java
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    public synchronized static ApiServices getInstance()
    {
        if (apiServices == null)
        {
            apiServices = new ApiServices();
        }

        return apiServices;
    }

    /**
     * Get movies
     */
    public LiveData<List<Movie>> getMovies(String sort, String apiKey)
    {
        final MutableLiveData<List<Movie>> mutableLiveData = new MutableLiveData<>();

        apiInterface.getMovies(sort, apiKey).enqueue(new Callback<MovieResponse>()
        {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response)
            {
                int statusCode = response.code();
                Log.d(ApiServices.class.getSimpleName(), "onResponse: " + statusCode);
                mutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable)
            {
                mutableLiveData.setValue(null);
                Log.e(ApiServices.class.getSimpleName(), "onFailure: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });

        return mutableLiveData;
    }

    /**
     * Get trailers
     */
    public LiveData<List<Trailer>> getTrailers(Integer id, String apiKey)
    {
        final MutableLiveData<List<Trailer>> mutableLiveData = new MutableLiveData<>();

        apiInterface.getTrailers(id, apiKey).enqueue(new Callback<TrailerResponse>()
        {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response)
            {
                int statusCode = response.code();
                Log.d(ApiServices.class.getSimpleName(), "onResponse: " + statusCode);
                mutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable throwable)
            {
                mutableLiveData.setValue(null);
                Log.e(ApiServices.class.getSimpleName(), "onFailure: " + throwable.getMessage());
            }
        });

        return mutableLiveData;
    }
}