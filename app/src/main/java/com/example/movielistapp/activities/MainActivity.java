package com.example.movielistapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movielistapp.R;
import com.example.movielistapp.activities.adapters.MovieAdapter;
import com.example.movielistapp.viewmodels.MovieViewModel;
import com.example.movielistapp.models.Movie;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add Movie Button
        Button addMovieButton = findViewById(R.id.addMovieButton);
        addMovieButton.setOnClickListener(v -> showAddMovieDialog());

        // Fetch and display movies
        fetchMovies();

        MovieViewModel movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        movieViewModel.getMovies().observe(this, movies -> {
            if (movies != null) {
                adapter.updateMovies(movies); // Update the RecyclerView adapter
            }
        });
    }

    // Show Add Movie Dialog
    private void showAddMovieDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Movie");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_movie, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.movieTitleInput);
        EditText studioInput = dialogView.findViewById(R.id.movieStudioInput);
        EditText ratingInput = dialogView.findViewById(R.id.movieRatingInput);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String studio = studioInput.getText().toString();
            double rating;

            try {
                rating = Double.parseDouble(ratingInput.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating input", Toast.LENGTH_SHORT).show();
                return;
            }

            addMovieToFirestore(title, studio, rating);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Add movie to Firestore
    private void addMovieToFirestore(String title, String studio, double rating) {
        Movie movie = new Movie(title, studio, rating);

        db.collection("movies")
                .add(movie)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Movie added successfully!", Toast.LENGTH_SHORT).show();
                    fetchMovies(); // Refresh RecyclerView
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Show Update Movie Dialog (Navigate to AddEditMovieActivity for update)
    public void showUpdateMovieDialog(Movie movie) {
        // Launch AddEditMovieActivity with the movie ID for update
        Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
        intent.putExtra("MOVIE_ID", movie.getDocumentId());  // Pass the movie ID to the activity
        startActivity(intent);
    }

    // Method to delete a movie
    public void deleteMovieFromFirestore(String docId) {
        db.collection("movies")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Movie deleted successfully!", Toast.LENGTH_SHORT).show();
                    fetchMovies(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch movies from Firestore
    private void fetchMovies() {
        db.collection("movies")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Movie> movies = new ArrayList<>(queryDocumentSnapshots.toObjects(Movie.class));
                        if (adapter == null) {
                            adapter = new MovieAdapter(movies, this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateMovies(movies);
                        }
                    }
                });
    }
}
