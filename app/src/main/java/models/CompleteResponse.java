package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class CompleteResponse {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }



    @SerializedName("status")
    private String status;

    @SerializedName("totalResults")
    private String totalResults;

    public List<Articles> getArticles() {
        return articles;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }

    @SerializedName("articles")
    private List<Articles> articles;

    public CompleteResponse(String status, String totalResults, List<Articles> articles){
        this.setStatus(status);
        this.setTotalResults(totalResults);
        this.setArticles(articles);
    }
}
