package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class Articles {
    public Articles() {
    }

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


//    public Source getSource() {
//        return source;
//    }
//
//    public void setSource(Source source) {
//        this.source = source;
//    }
//
//    @SerializedName("source")
//    private Source source;

    public Articles(String author,String title,String description,String url,String urlToImage,String publishedAt){

        //this.setSource(source);
        this.setAuthor(author);
        this.setTitle(title);
        this.setDescription(description);
        this.setUrl(url);
        this.setUrlToImage(urlToImage);
        this.setPublishedAt(publishedAt);
    }
}
