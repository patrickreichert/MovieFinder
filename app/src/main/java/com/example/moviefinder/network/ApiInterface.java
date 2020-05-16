package com.example.moviefinder.network;

import com.example.moviefinder.model.movie.MovieResponse;
import com.example.moviefinder.model.trailer.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API interface
 */
public interface ApiInterface
{
    @GET("movie/{sorting}")
    Call<MovieResponse> getMovies
     (
            @Path("sorting")
            String sort,
            @Query(ApiConstants.API_KEY_LABEL)
            String apiKey
    );

    @GET(ApiConstants.TRAILERS)
    Call<TrailerResponse> getTrailers
     (
             @Path("id")
             int id,
             @Query(ApiConstants.API_KEY_LABEL)
             String apiKey
     );
}