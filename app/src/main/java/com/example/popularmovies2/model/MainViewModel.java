package com.example.popularmovies2.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies2.DataBase.FavoriteMovie;
import com.example.popularmovies2.DataBase.MovieDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {


    private LiveData<List<FavoriteMovie>> movies;

    public MainViewModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        movies = database.movie_query().loadAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getMovies() {
        return movies;
    }
}






