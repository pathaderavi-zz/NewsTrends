package rest;

import java.util.List;

import models.Articles;
import models.CompleteResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public interface GetTopNewsWorldEnglish {

    @GET("/v2/top-headlines/")
    Call<CompleteResponse> getTopNewsArticles(@Query("apiKey") String key, @Query("language")String lang,@Query("country") String country,@Query("category") String category);


    @GET("/v2/everything/")
    Call<CompleteResponse> getSearchEverything(@Query("apiKey") String key, @Query("sortBy") String sort,@Query("q") String query);

    @GET("/v2/everything/")
    Call<CompleteResponse> getForAlerts(@Query("apiKey") String key, @Query("q") String query);

}
