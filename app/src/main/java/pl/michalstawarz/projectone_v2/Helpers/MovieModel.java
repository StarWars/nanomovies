package pl.michalstawarz.projectone_v2.Helpers;

import java.io.Serializable;

/**
 * Created by propr_000 on 18.08.2015.
 */

public class MovieModel implements Serializable {

    String poster_path;
    String release_date;
    String title;
    double vote_average;
    String plot_overview;
    String movie_id;

    public MovieModel() {
    }

    public MovieModel(String poster_path, String release_date, String title, double vote_average, String plot_overview, String movie_id) {
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.title = title;
        this.vote_average = vote_average;
        this.plot_overview = plot_overview;
        this.movie_id = movie_id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getPlot_overview() {
        return plot_overview;
    }

    public void setPlot_overview(String plot_overview) {
        this.plot_overview = plot_overview;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

}

