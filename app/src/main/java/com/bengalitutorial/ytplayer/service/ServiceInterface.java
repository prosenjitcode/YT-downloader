package com.bengalitutorial.ytplayer.service;

import com.bengalitutorial.ytplayer.models.MovieDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceInterface {

    @GET("search/")
    Call<MovieDetails> getAllMovies(@Query("part") String part,
                                    @Query("maxResults") String maxResults,
                                    @Query("q") String q,
                                    @Query("type") String type,
                                    @Query("key") String api_key
                                    );

    @GET("search/")
    Call<MovieDetails> getReletedMovies(@Query("part") String part,
                                    @Query("maxResults") String maxResults,
                                    @Query("relatedToVideoId") String relatedToVideoId,
                                    @Query("type") String type,
                                    @Query("key") String api_key
    );
}
