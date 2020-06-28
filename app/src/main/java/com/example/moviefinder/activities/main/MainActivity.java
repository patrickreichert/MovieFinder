package com.example.moviefinder.activities.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.network.ApiConstants;
import com.example.moviefinder.activities.details.DetailsActivity;
import com.example.moviefinder.adapters.MovieAdapter;

import java.util.List;

/**
 * MainActivity class
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    public final static String LIST_STATE_KEY = "recycler_list_state";
    private static final String PREF = "pref";

    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView movieRecyclerView;

    private String apikey = ApiConstants.API_KEY;
    private String popular = ApiConstants.POPULAR_MOVIES;
    private String topRated = ApiConstants.TOP_RATED;

    private MovieAdapter movieAdapter;
    private SharedPreferences preferences;
    private MainViewModel mainViewModel;
    private Parcelable listState;
    private GridLayoutManager gridLayoutManager;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        movieRecyclerView = findViewById(R.id.movieRecyclerView);

        // Inizializzo le views
        initViews(savedInstanceState);

        //Controllo l'orientazione dello schermo
        checkOrientation();
    }

    private void checkOrientation()
    {
        if (this.movieRecyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            gridLayoutManager = new GridLayoutManager(this, 2);
            movieRecyclerView.setLayoutManager(gridLayoutManager);
        }
        else if (this.movieRecyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
             {
                 gridLayoutManager = new GridLayoutManager(this, 4);
                 movieRecyclerView.setLayoutManager(gridLayoutManager);
             }
    }

    private void initViews(Bundle bundle)
    {
        gridLayoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(gridLayoutManager);
        movieRecyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(getApplicationContext(), this);
        movieRecyclerView.setAdapter(movieAdapter);

        setSupportActionBar(toolbar);

        preferences = getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
        if (isConnected())
        {
            onSharedPreferenceChanged(preferences, getString(R.string.sort_by));
        }
        else
        {
            toolbar.setTitle(R.string.app_name);
            Toast.makeText(getApplicationContext(), "Check your network connection first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(LIST_STATE_KEY, gridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null)
        {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    // Doppio tap per uscire
    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();

            return;
        }

        setKey(getString(R.string.popular));
        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(getApplicationContext(), R.string.double_press_back, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private boolean isConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
        {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onResume()
    {
        if (!isConnected())
        {
            loadFavorites();
        }
        else
        {
            onSharedPreferenceChanged(preferences, getString(R.string.sort_by));
        }

        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void loadMovies(String sort, String apiKey)
    {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.loadAllMovies(sort, apiKey).observe(this, new Observer<List<Movie>>()
        {
            @Override
            public void onChanged(@Nullable List<Movie> movies)
            {
                movieAdapter.setMovieItem(movies);

                if (listState != null)
                {
                    gridLayoutManager.onRestoreInstanceState(listState);
                }
            }
        });
    }

    @SuppressLint("ResourceType")
    private void loadFavorites()
    {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavMovies().observe(this, new Observer<List<Movie>>()
        {
            @Override
            public void onChanged(@Nullable List<Movie> movies)
            {
                movieAdapter.setMovieItem(movies);
                progressBar.setVisibility(View.GONE);

                if (listState != null)
                {
                    gridLayoutManager.onRestoreInstanceState(listState);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.popular:
                setKey(getString(R.string.popular));
                return true;
            case R.id.topRated:
                setKey(getString(R.string.top_rated));
                return true;
            case R.id.favorites:
                setKey(getString(R.string.favorites));
                return true;
            case R.id.refresh:
                getPreference();
                progressBar.setVisibility(View.VISIBLE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPreference()
    {
        String key = getOrderValue();

        if (key.equals(getString(R.string.popular)))
        {
            toolbar.setTitle(R.string.app_name);
            loadMovies(popular, apikey);
        }
        else if (key.equals(getString(R.string.top_rated)))
             {
                 toolbar.setTitle(R.string.top_rated);
                 loadMovies(topRated, apikey);
             }
             else
             {
                 toolbar.setTitle(R.string.favorites);
                 loadFavorites();
             }
    }

    private String getOrderValue()
    {
        String key = getString(R.string.sort_by);
        String value = getString(R.string.popular);

        return preferences.getString(key, value);
    }

    private void setKey(String value)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.sort_by), value);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals(getString(R.string.sort_by)))
        {
            getPreference();
        }
    }

    @Override
    public void onListClick(Movie movie)
    {
        Intent movieIntent = new Intent(this.getApplicationContext(), DetailsActivity.class);
        movieIntent.putExtra(DetailsActivity.EXTRA_VALUE, movie);
        startActivity(movieIntent);
    }
}