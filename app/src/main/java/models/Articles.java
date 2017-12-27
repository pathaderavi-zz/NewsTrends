package models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class Articles implements Parcelable,Comparable<Articles> {
    public Articles() {
    }

    protected Articles(Parcel in) {
        author = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        publishedAt = in.readString();
        source = in.readParcelable(Source.class.getClassLoader());
    }

    public static final Creator<Articles> CREATOR = new Creator<Articles>() {
        @Override
        public Articles createFromParcel(Parcel in) {
            return new Articles(in);
        }

        @Override
        public Articles[] newArray(int size) {
            return new Articles[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @SerializedName("author")
    private String author;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("url")
    private String url;

    @SerializedName("urlToImage")
    private String urlToImage;

    @SerializedName("publishedAt")
    private String publishedAt;


    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @SerializedName("source")
    @Expose
    private Source source;

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    private Date publishedDate;

    public Articles(String author,String title,String description,String url,String urlToImage,String publishedAt,Source source){

        //this.setSource(source);
        this.setAuthor(author);
        this.setTitle(title);
        this.setDescription(description);
        this.setUrl(url);
        this.setUrlToImage(urlToImage);
        this.setPublishedAt(publishedAt);
        this.setSource(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getAuthor());
        parcel.writeString(getTitle());
        parcel.writeString(getDescription());
        parcel.writeString(getUrl());
        parcel.writeString(getUrlToImage());
        parcel.writeString(getPublishedAt());

    }

    @Override
    public int compareTo(@NonNull Articles articles) {
        return getPublishedDate().compareTo(articles.getPublishedDate());
    }
}
