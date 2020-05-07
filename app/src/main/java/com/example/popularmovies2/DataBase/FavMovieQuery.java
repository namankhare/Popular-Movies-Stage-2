package com.example.popularmovies2.DataBase;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//fav movie database stackoverflow
@Dao
public interface FavMovieQuery {
    @Query("SELECT * FROM fav_movies")
    LiveData<List<FavoriteMovie>> loadAllMovies();

    @Insert
    void insertMovie(FavoriteMovie favMovie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(FavoriteMovie favMovie);

    @Delete
    void deleteMovie(FavoriteMovie favMovie);

    @Query("SELECT * FROM fav_movies WHERE id = :id")
    FavoriteMovie loadMovieById(int id);
}

