package com.tdp2.tripplanner.modelo;

/**
 * Created by matias on 5/12/17.
 */

public class Comment {

    private String comment;
    private String username;
    private String dateAndTime;
    private Float rating;

    public Comment(String comment, String username, String dateAndTime, Float rating) {
        this.comment = comment;
        this.username = username;
        this.dateAndTime = dateAndTime;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public Float getRating() {
        return rating;
    }
}
