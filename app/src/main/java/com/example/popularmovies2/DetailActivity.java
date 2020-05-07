package com.example.popularmovies2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.popularmovies2.DataBase.FavoriteMovie;
import com.example.popularmovies2.DataBase.MovieDatabase;
import com.example.popularmovies2.adapter.ListViewTrailerAdapter;
import com.example.popularmovies2.model.ModelTrailer;
import com.example.popularmovies2.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView tvYear, tvDuration, tvDescription, tvReviews, tvTitle;
    ImageView imgPoster;
    ListView lvTrailer;
    RatingBar ratingBar;
    ToggleButton toggleButton;

    private ModelTrailer modelTrailer;
    private ArrayList<ModelTrailer> arrayList = new ArrayList<ModelTrailer>(  );
    private ListViewTrailerAdapter listViewTrailerAdapter;

    final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";
    private MovieDatabase mDb;
    private InputStream inputStream;
    private StringBuffer buffer;

    int id;

    Boolean isFav = false;
    String sReview="", sAuthor, sContent, line, poster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle("Movie Detail: ");

        tvYear = findViewById( R.id.tvReleaseDate);
        tvDuration = findViewById( R.id.tvDuration );
        tvDescription = findViewById( R.id.tvDescription );
        tvReviews = findViewById( R.id.tvReviews );
        imgPoster = findViewById( R.id.imgSinglePoster );
        toggleButton = findViewById( R.id.toggleButton );
        ratingBar = findViewById( R.id.ratingBar );
        tvTitle = findViewById(R.id.tv_title);

        lvTrailer = findViewById( R.id.lvTrailer );
        Intent intent = getIntent();
        id = intent.getIntExtra("Movie ID", 0);
        poster = intent.getStringExtra("Poster");

        mDb = MovieDatabase.getInstance(getApplicationContext());

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService( getApplicationContext().CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            FetchMovieData fetchMoviesData = new FetchMovieData();
            fetchMoviesData.execute();
        }
        else
            Toast.makeText( this, "No internet access", Toast.LENGTH_SHORT ).show();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Isfav is = new Isfav();
        is.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask( this );
        }
        return super.onOptionsItemSelected( item );
    }
    public class Fav extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            final FavoriteMovie mov = new FavoriteMovie(id,poster);
            if(params[0].equals("false"))
            {
                mDb.movie_query().deleteMovie(mov);
            } else {
                mDb.movie_query().insertMovie(mov);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    public class Isfav extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            final FavoriteMovie fmov = mDb.movie_query().loadMovieById(id);
            if (fmov==null)
                isFav=false;
            else
                isFav = true;
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            toggleButton.setChecked( isFav );
            if(isFav)
                toggleButton.setBackgroundResource( R.drawable.ic_favorite_black_24dp );
            else
                toggleButton.setBackgroundResource( R.drawable.ic_favorite_border_white_24dp );

            toggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        toggleButton.setBackgroundResource( R.drawable.ic_favorite_black_24dp );
                    else
                        toggleButton.setBackgroundResource( R.drawable.ic_favorite_border_white_24dp );
                    Fav fa = new Fav();
                    fa.execute(""+b);

                }
            } );
        }
    }
    public class FetchMovieData extends AsyncTask<Void, Void, Void> {

        String LOG_TAG = "FetchMovies";
        String sName, sReleaseDate, sRating, sDuration, sDescription, sPosterUrl;

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String mainJsonObject = null;
            JSONObject jsonObject;
            try {

                URL url = new URL(BASE_URL + id + "?api_key=" + getString(R.string.apiv3) );

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                mainJsonObject = buffer.toString();

                jsonObject = new JSONObject(mainJsonObject);
                sName = jsonObject.getString( "original_title" );
                sReleaseDate = "Release Date : " + jsonObject.getString( "release_date" );
                sDuration = "Duration: " + jsonObject.getString( "runtime" )+ " mins";
                sDescription = jsonObject.getString( "overview" );
                sRating = jsonObject.getString( "vote_average" );
                sPosterUrl = IMAGE_BASE_URL + jsonObject.getString( "poster_path" );

            }catch(Exception e){
                Log.e( LOG_TAG,  e.toString() );
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e( LOG_TAG,  e.toString() );
                    }
                }
            }

            try {

                URL urlTrailer = new URL(BASE_URL + id + "/videos?api_key=" + getString(R.string.apiv3) );

                urlConnection = (HttpURLConnection) urlTrailer.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                mainJsonObject = buffer.toString();

                jsonObject = new JSONObject(mainJsonObject);
                JSONArray jsonArray = jsonObject.getJSONArray( "results");
                JSONObject jsonObjectTrailer;
                String sKey,sTrailerName;
                arrayList.clear();
                for (int  i = 0; i<jsonArray.length(); i++)
                {
                    jsonObjectTrailer = jsonArray.getJSONObject( i );
                    sKey = jsonObjectTrailer.getString( "key" );
                    sTrailerName = jsonObjectTrailer.getString( "name" );

                    modelTrailer = new ModelTrailer( sKey,sTrailerName );
                    arrayList.add( modelTrailer );
                }
            }catch(Exception e){
                Log.e( LOG_TAG,  e.toString() );
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e( LOG_TAG,  e.toString() );
                    }
                }
            }

            try {

                URL urlReviews = new URL(BASE_URL + id + "/reviews?api_key=" + getString(R.string.apiv3) );

                urlConnection = (HttpURLConnection) urlReviews.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                mainJsonObject = buffer.toString();

                jsonObject = new JSONObject(mainJsonObject);
                JSONArray jsonArray = jsonObject.getJSONArray( "results");
                JSONObject jsonObjectReview;

                for (int  i = 0; i<jsonArray.length(); i++)
                {
                    jsonObjectReview = jsonArray.getJSONObject( i );
                    sAuthor = jsonObjectReview.getString( "author" );
                    sContent = jsonObjectReview.getString( "content" );
                    sReview += "\n"+sContent+"\n-"+sAuthor+"\n\n-----------------------------------------------------------------------------------";
                }
            }catch(Exception e){
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
            try {
                tvYear.setText( sReleaseDate );
                tvDescription.setText( sDescription );
                tvDuration.setText( sDuration );
                tvTitle.setText(sName);

                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.setTint( Color.YELLOW );
                ratingBar.setRating( Float.parseFloat( sRating ) );
                Picasso.get().load(  sPosterUrl ).into( imgPoster );

                if(!sName.isEmpty())
                    getSupportActionBar().setTitle("Movie Detail: " + sName) ;



                listViewTrailerAdapter = new ListViewTrailerAdapter( getApplicationContext() , arrayList);
                lvTrailer.setAdapter( listViewTrailerAdapter );

                tvReviews.setText( sReview );
            } catch (NullPointerException e) {
                Log.e( LOG_TAG,  e.toString() );
            }
        }

    }
}
