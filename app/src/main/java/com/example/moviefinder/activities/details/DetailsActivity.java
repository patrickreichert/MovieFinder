package com.example.moviefinder.activities.details;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.activities.main.MainActivity;
import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.model.trailer.Trailer;
import com.example.moviefinder.network.ApiConstants;
import com.example.moviefinder.adapters.TrailerAdapter;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * DetailsActivity class
 */
public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener
{
    public final static String EXTRA_VALUE = "extraMovie";

    private String apiKey = ApiConstants.API_KEY;

    TextView titleTv;
    TextView dateTv;
    TextView overviewTv;
    ImageView backdropIv;
    RatingBar ratingBar;
    Toolbar toolbar;
    RecyclerView trailerView;
    CoordinatorLayout coordinatorLayout;

    String title, date, overview, backdrop, poster;
    Movie movie;
    double rating;
    Context context;

    private LikeButton likeButton;
    private TrailerAdapter trailerAdapter;
    private DetailsViewModel detailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTv = findViewById(R.id.titleTv);
        dateTv = findViewById(R.id.release_date);
        overviewTv = findViewById(R.id.overview);
        backdropIv = findViewById(R.id.main_backdrop);
        ratingBar = findViewById(R.id.movie_rating);
        toolbar = findViewById(R.id.main_toolbar);
        trailerView = findViewById(R.id.trailer_rv);
        coordinatorLayout = findViewById(R.id.detail_coordinator);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("");

        detailViewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);

        Intent intent = getIntent();
        if (intent == null)
        {
            closeOnError();
        }

        assert intent != null;

        if (intent.hasExtra(EXTRA_VALUE))
        {
            movie = getIntent().getParcelableExtra(EXTRA_VALUE);
            populateUI(movie);
        }

        initViews();
        loadTrailers();
        checkIfFavorite();
    }

    private void initViews()
    {
        likeButton = findViewById(R.id.fav_button);
        addFavorite();

        trailerAdapter = new TrailerAdapter(this, this);

        trailerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trailerView.setAdapter(trailerAdapter);
        trailerView.setHasFixedSize(false);
    }

    private void addFavorite()
    {
        likeButton.setOnLikeListener(new OnLikeListener()
        {
            @Override
            public void liked(LikeButton likeButton)
            {
                detailViewModel.saveMovie(movie);
                Toast.makeText(getApplicationContext(), "Added " + movie.getOriginalTitle() + " to favorites!", Toast.LENGTH_SHORT).show();

                Intent likedIntent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(likedIntent);
                finish();
            }
            @Override
            public void unLiked(LikeButton likeButton)
            {
                detailViewModel.deleteMovie(movie);
                Toast.makeText(getApplicationContext(), "Removed " + movie.getOriginalTitle() + " to favorites!", Toast.LENGTH_SHORT).show();

                Intent unLikedIntent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(unLikedIntent);
                finish();
            }
        });
    }

    void checkIfFavorite()
    {
        int id = movie.getId();

        detailViewModel.loadFavById(id).observe(this, new Observer<Movie>()
        {
            @Override
            public void onChanged(@Nullable Movie movie)
            {
                if (movie == null)
                {
                    likeButton.setLiked(false);
                }
                else
                {
                    likeButton.setLiked(true);
                }
            }
        });
    }

    void populateUI(Movie movie)
    {
        title = movie.getOriginalTitle();
        date = movie.getReleaseDate();
        overview = movie.getOverview();
        poster = movie.getMovieImagePath();
        backdrop = movie.getBackdropPath();
        rating = movie.getVoteAverage();

        titleTv.setText(title);
        dateTv.setText(date.substring(0, 4));
        overviewTv.setText(overview);

        String backdropUrl = ApiConstants.MOVIES_BACKDROP_BASE_URL;
        loadImage(backdropIv, backdrop, backdropUrl);

        float rated = (((float) rating) / 2);
        ratingBar.setRating(rated);
    }

    private void loadImage(ImageView imageView, String imageUrl, String posterUrl)
    {
        Picasso
                .with(context)
                .load(posterUrl + imageUrl)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    private void loadTrailers()
    {
        detailViewModel.getTrailers(movie.getId(), apiKey).observe(this, new Observer<List<Trailer>>()
        {
            @Override
            public void onChanged(@Nullable List<Trailer> trailers)
            {
                trailerAdapter.setTrailer(trailers);
            }
        });
    }

    @Override
    public void onItemClick(Trailer trailer)
    {
        final String url = "https://www.youtube.com/watch?v=";
        final String key = trailer.getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.
                setTitle("Would you like to see this trailer on YouTube's app?")
                .setCancelable(true).setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            //TODO: nothing
                        }
                    })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url + key));
                            startActivity(intent);
                        }
                    })
                .show();
    }

    private void closeOnError()
    {
        finish();

        Toast.makeText(this, R.string.movie_data_unavailable, Toast.LENGTH_SHORT).show();
    }
}