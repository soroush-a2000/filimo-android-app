package com.movieboxtv.app.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {


    @SerializedName("slides")
    @Expose
    private List<Slide> slides = null;

    @SerializedName("channels")
    @Expose
    private List<Channel> channels = null;

    @SerializedName("actors")
    @Expose
    private List<Actor> actors = null;

    @SerializedName("countries")
    @Expose
    private List<Country> countries = null;

    @SerializedName("posters")
    @Expose
    private List<Poster> posters = null;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;

    @SerializedName("genre")
    @Expose
    private Genre genre;

    private int viewType = 1;

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }


    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Genre getGenre() {
        return genre;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Data setViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }
    public int getViewType() {
        return viewType;
    }

    public List<Poster> getPosters() {
        return posters;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }
}
