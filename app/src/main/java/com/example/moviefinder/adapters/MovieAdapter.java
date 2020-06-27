package com.example.moviefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviefinder.R;
import com.example.moviefinder.model.movie.Movie;
import com.example.moviefinder.network.ApiConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

//import butterknife.BindView;
//import butterknife.ButterKnife;

/**
 * Movie adapter
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>
{
    private List<Movie> mMovies;
    final private ListClickListener mListClickListener;
    private Context context;
    private MovieViewHolder holder;

    public MovieAdapter(Context context, ListClickListener onListClickListener)
    {
        this.context = context;
        this.mListClickListener = onListClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_item, viewGroup, false);
        return new MovieViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position)
    {
        this.holder = movieViewHolder;
        bindViews(movieViewHolder);
    }

    // Metodo di supporto per associare le viste ai dati
    // Da chiamare nel metodo onBindViewHolder
    private void bindViews(MovieViewHolder holder)
    {
        Movie movie = mMovies.get(holder.getAdapterPosition());
        loadImagePoster(holder, movie.getMovieImagePath());
    }

    // Metodo di supporto per caricare l'immagine del poster con Picasso
    // Da chiamare nel metodo bindViews
    private void loadImagePoster(final MovieViewHolder holder, String imageUrl)
    {
        String posterUrl = ApiConstants.MOVIES_POSTER_BASE_URL;

        Picasso
                .with(context)
                .load(posterUrl + imageUrl)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.mipmap.ic_launcher)
                .into(holder.movieImageView);
    }

    @Override
    public int getItemCount()
    {
        if (mMovies == null)
        {
            return 0;
        }

        return mMovies.size();
    }

    public void setMovieItem(List<Movie> movie)
    {
        this.mMovies = movie;

        notifyDataSetChanged();
    }

    public interface ListClickListener
    {
        void onListClick(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        //@BindView(R.id.movie_imageView)
        ImageView movieImageView;

        MovieViewHolder(View itemView)
        {
            super(itemView);

            //ButterKnife.bind(this, itemView);
            movieImageView = itemView.findViewById(R.id.movie_imageView);
            itemView.setOnClickListener(this);
        }

        // Chiamato quando una view viene cliccata
        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            mListClickListener.onListClick(mMovies.get(position));
        }
    }
}