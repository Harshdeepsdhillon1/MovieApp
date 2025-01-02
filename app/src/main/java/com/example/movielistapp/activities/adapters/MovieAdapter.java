package com.example.movielistapp.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movielistapp.R;
import com.example.movielistapp.activities.MainActivity;
import com.example.movielistapp.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final Context context;

    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    public void updateMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Set serial number dynamically
        holder.serialNumber.setText(String.valueOf(position + 1)); // Serial number starts from 1
        holder.title.setText(movie.getTitle());
        holder.studio.setText(movie.getStudio());
        holder.rating.setText(String.valueOf(movie.getRating()));

        // Item click listener for update
        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showUpdateMovieDialog(movie);
            }
        });

        // Item long-click listener for direct delete
        holder.itemView.setOnLongClickListener(v -> {
            if (context instanceof MainActivity) {
                String docId = movie.getTitle(); // Get the Firestore document ID from the Movie object
                ((MainActivity) context).deleteMovieFromFirestore(docId); // Call delete method directly
            }
            return true; // Consume the long-click event
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private void showDeleteConfirmationDialog(Movie movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Movie");
        builder.setMessage("Are you sure you want to delete this movie?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (context instanceof MainActivity) {
                String docId = movie.getTitle(); // Extract the document ID from the Movie object
                ((MainActivity) context).deleteMovieFromFirestore(docId); // Pass the document ID
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView serialNumber, title, studio, rating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            serialNumber = itemView.findViewById(R.id.serialNumber);
            title = itemView.findViewById(R.id.movieTitle);
            studio = itemView.findViewById(R.id.movieStudio);
            rating = itemView.findViewById(R.id.movieRating);
        }
    }
}
