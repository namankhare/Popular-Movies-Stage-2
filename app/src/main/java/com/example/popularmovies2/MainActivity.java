package com.example.popularmovies2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.popularmovies2.DataBase.FavoriteMovie;
import com.example.popularmovies2.DataBase.MovieDatabase;
import com.example.popularmovies2.adapter.GridViewAdapter;
import com.example.popularmovies2.model.MainViewModel;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private ArrayList<String> posters;
    private ArrayList<Integer> ids;
    private List<FavoriteMovie> favMovs;
    private  LiveData<List<FavoriteMovie>> movies;
    private String sortType;

    ArrayList<ImageAdapter> arrayList = new ArrayList<ImageAdapter>();
    ImageAdapter model;
    GridView gridView;
    GridViewAdapter imageAdapter;
    GridViewAdapter imgGvAdapter;
    FetchMovies fetchMovies;

    String LOG_TAG = "MainActivity";
    String image_url = "https://image.tmdb.org/t/p/w780";
    String BASE_URL = "https://api.themoviedb.org/3/movie/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        sharedPreferences = getSharedPreferences("popular_movies",MODE_PRIVATE);
        sortType = sharedPreferences.getString("sort_type", "popular");

        gridView = findViewById(R.id.gvPoster );

        fetchMovies = new FetchMovies();
        if (sortType.equals("popular")){
            getSupportActionBar().setTitle("Popular Movies");
            fetchMovies.execute(sortType);
        }
        else if (sortType.equals("top_rated")){
            getSupportActionBar().setTitle("Top Rated Movies");
            fetchMovies.execute(sortType);
        }
        else if(sortType.equals( "favorite" ))
        {
            getSupportActionBar().setTitle("Your Favourite Movie");
            fetchMovies.execute(sortType);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                if(! sortType.equals( "favorite" )) {
                    intent.putExtra( "Movie ID", ids.get( position ) );
                    intent.putExtra( "Poster", posters.get( position ) );
                }else {
                    intent.putExtra("Movie ID", favMovs.get( position ).getId() );
                    intent.putExtra( "Poster", favMovs.get( position ).getImage() );
                }
                startActivity(intent);
            }
        });
        favMovs = new ArrayList<>(  );

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected)
        {
            fetchMovies = new FetchMovies();
            if(!sortType.equals( "favorite" ))
            {
                fetchMovies.execute(sortType);
            }
            else {
                MovieDatabase database = MovieDatabase.getInstance( MainActivity.this );
                movies = database.movie_query().loadAllMovies();
                setupViewModel();
            }
        }
        else
            Toast.makeText( this, "No internet access", Toast.LENGTH_SHORT ).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favs) {
                if(favs.size()>0) {
                    favMovs.clear();
                    favMovs = favs;
                }
                arrayList.clear();
                for (int i=0; i<favMovs.size(); i++) {
                    model = new ImageAdapter(image_url + favMovs.get(i).getImage());
                    arrayList.add(model);
                }
                imageAdapter = new GridViewAdapter(getApplicationContext(), arrayList);
                try {
                    gridView.setAdapter(imageAdapter);
                } catch (NullPointerException e) {
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        int id = item.getItemId();
        int dialogSelected = 0;
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        if (id == R.id.action_sort_settings) {

            final SharedPreferences.Editor editor = sharedPreferences.edit();

            sortType = sharedPreferences.getString("sort_type", "popular");
            if(sortType.equals("popular"))
                dialogSelected = 0;
            else if(sortType.equals("top_rated"))
                dialogSelected = 1;
            else if(sortType.equals("favorite"))
                dialogSelected = 2;
            builder.setTitle(R.string.dialog_title);

            builder.setSingleChoiceItems(R.array.sort_types, dialogSelected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0)
                                editor.putString("sort_type", "popular");

                            else if (which == 1)
                                editor.putString("sort_type", "top_rated");
                            else if (which == 2)
                                editor.putString("sort_type", "favorite");
                        }
                    });
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    editor.commit();

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //user clicked cancel
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //refresh activity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovies extends AsyncTask<String, Void, Void> {

        String LOG_TAG = "FetchMovies";

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String sMovieJson = null;

            posters = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            try {
                URL url = new URL( BASE_URL + params[0] + "?api_key=" + getString(R.string.apiv3) );

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod( "GET" );
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader( new InputStreamReader( inputStream ) );
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append( line + "\n" );
                }
                if (buffer.length() == 0) {
                    return null;
                }
                sMovieJson = buffer.toString();

                JSONObject jsonObject = new JSONObject( sMovieJson );
                JSONArray jsonArray = jsonObject.getJSONArray( "results" );
                JSONObject jsonObjectMovie;
                arrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObjectMovie = jsonArray.getJSONObject( i );
                    ids.add( jsonObjectMovie.getInt( "id" ) );
                    posters.add( jsonObjectMovie.getString( "poster_path" ) );
                    model = new ImageAdapter( image_url + jsonObjectMovie.getString( "poster_path" ) );
                    arrayList.add( model );
                }
            } catch (Exception e) {
                Log.e( LOG_TAG,  e.toString() );
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e( LOG_TAG,  e.toString() );
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            imgGvAdapter = new GridViewAdapter( getApplicationContext(), arrayList );
            try {
                gridView.setAdapter( imgGvAdapter );
            } catch (NullPointerException e) {
                Log.e( LOG_TAG,  e.toString() );
            }
        }
    }
}
