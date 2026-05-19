package com.roombuddy.pgrentalapp.model;

public class ReviewModel {

    private String userName;
    private float rating;
    private String comment;

    public ReviewModel() {
    }

    public ReviewModel(String userName, float rating, String comment) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
