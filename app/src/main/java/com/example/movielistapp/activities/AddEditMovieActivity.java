package com.example.movielistapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movielistapp.R;
import com.example.movielistapp.models.Movie;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddEditMovieActivity extends AppCompatActivity {
    private EditText titleInput, studioInput, ratingInput;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie);

        db = FirebaseFirestore.getInstance();

        titleInput = findViewById(R.id.titleInput);
        studioInput = findViewById(R.id.studioInput);
        ratingInput = findViewById(R.id.ratingInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        String movieId = getIntent().getStringExtra("MOVIE_ID");

        if (movieId != null) {
            db.collection("movies").document(movieId).get().addOnSuccessListener(snapshot -> {
                Movie movie = snapshot.toObject(Movie.class);
                titleInput.setText(movie.getTitle());
                studioInput.setText(movie.getStudio());
                ratingInput.setText(String.valueOf(movie.getRating()));
            });
        }

        saveButton.setOnClickListener(v -> {
            Movie movie = new Movie(
                    titleInput.getText().toString(),
                    studioInput.getText().toString(),
                    Double.parseDouble(ratingInput.getText().toString())
            );

            if (movieId != null) {
                db.collection("movies").document(movieId).set(movie);
            } else {
                db.collection("movies").add(movie);
            }

            finish();
        });

        cancelButton.setOnClickListener(v -> finish());
    }

}
