package com.example.movielistapp.models;

public class Movie {
    private String documentId; // Firestore document ID
    private String title;
    private String studio;
    private double rating;

    public Movie() {}

    public Movie(String title, String studio, double rating) {
        this.title = title;
        this.studio = studio;
        this.rating = rating;
    }

    // Getters and setters
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public String getStudio() { return studio; }
    public double getRating() { return rating; }

    public void setTitle(String title) { this.title = title; }
    public void setStudio(String studio) { this.studio = studio; }
    public void setRating(double rating) { this.rating = rating; }
}
