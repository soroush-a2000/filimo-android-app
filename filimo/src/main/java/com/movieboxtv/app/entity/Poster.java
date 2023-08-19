package com.movieboxtv.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Poster implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("imdb")
    @Expose
    private String imdb;

    @SerializedName("downloadas")
    @Expose
    private String downloadas;

    @SerializedName("comment")
    @Expose
    private Boolean comment;

    @SerializedName("playas")
    @Expose
    private String playas;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("classification")
    @Expose
    private String classification;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();

    @SerializedName("country")
    @Expose
    private List<Country> country = new ArrayList<>();

    @SerializedName("sources")
    @Expose
    private List<Source> sources = new ArrayList<>();


    @SerializedName("trailer")
    @Expose
    private Source trailer;

    private int typeView = 1;

    public Poster() {
    }


    protected Poster(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        type = in.readString();
        imdb = in.readString();
        downloadas = in.readString();
        byte tmpComment = in.readByte();
        comment = tmpComment == 0 ? null : tmpComment == 1;
        playas = in.readString();
        description = in.readString();
        classification = in.readString();
        year = in.readString();
        duration = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        image = in.readString();
        cover = in.readString();
        genres = in.createTypedArrayList(Genre.CREATOR);
        country = in.createTypedArrayList(Country.CREATOR);
        sources = in.createTypedArrayList(Source.CREATOR);
        trailer = in.readParcelable(Source.class.getClassLoader());
        typeView = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(imdb);
        dest.writeString(downloadas);
        dest.writeByte((byte) (comment == null ? 0 : comment ? 1 : 2));
        dest.writeString(playas);
        dest.writeString(description);
        dest.writeString(classification);
        dest.writeString(year);
        dest.writeString(duration);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(rating);
        }
        dest.writeString(image);
        dest.writeString(cover);
        dest.writeTypedList(genres);
        dest.writeTypedList(country);
        dest.writeTypedList(sources);
        dest.writeParcelable(trailer, flags);
        dest.writeInt(typeView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getClassification() {
        return classification;
    }

    public String getYear() {
        return year;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Country> getCountry() { return country; }

    public void setCountry(List<Country> country) { this.country = country; }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<Source> getSources() {
        return sources;
    }

    public Source getTrailer() {
        return trailer;
    }

    public void setTrailer(Source trailer) {
        this.trailer = trailer;
    }

    public int getTypeView() {
        return typeView;
    }

    public Poster setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getDownloadas() {
        return downloadas;
    }

    public void setDownloadas(String downloadas) {
        this.downloadas = downloadas;
    }

    public String getPlayas() {
        return playas;
    }

    public void setPlayas(String playas) {
        this.playas = playas;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }
}


