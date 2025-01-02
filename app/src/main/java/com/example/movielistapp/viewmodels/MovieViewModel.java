package com.example.movielistapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movielistapp.models.Movie;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;



public class MovieViewModel extends ViewModel {

    private final MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MovieViewModel() {
        fetchMovies();
    }

    // Expose the list of movies as LiveData
    public LiveData<List<Movie>> getMovies() {
        return moviesLiveData;
    }

    // Fetch movies from Firestore
    public void fetchMovies() {
        db.collection("movies")
                .orderBy("rating", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        moviesLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        List<Movie> movies = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // Use explicit type
                            Movie movie = document.toObject(Movie.class); // Converts to Movie object
                            movie.setDocumentId(document.getId()); // Save document ID
                            movies.add(movie);
                        }
                        moviesLiveData.setValue(movies);
                    }
                });
    }


    public void deleteMovie(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            // Handle invalid documentId case
            System.err.println("Invalid document ID provided for deletion.");
            return;
        }
        db.collection("movies")
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Optionally, handle success: refresh movies list
                    fetchMovies();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Error deleting movie: " + e.getMessage());
                });
    }



    // Add a movie to Firestore
    public void addMovie(Movie movie) {
        db.collection("movies")
                .add(movie)
                .addOnSuccessListener(documentReference -> fetchMovies()) // Refresh movies after adding
                .addOnFailureListener(e -> {
                    // Optionally handle failure
                });
    }

    // Update an existing movie in Firestore
    public void updateMovie(String docId, Movie movie) {
        db.collection("movies")
                .document(docId)
                .set(movie)
                .addOnSuccessListener(aVoid -> fetchMovies()) // Refresh movies after updating
                .addOnFailureListener(e -> {
                    // Optionally handle failure
                });
    }


}
