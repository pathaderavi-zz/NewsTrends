package rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class Client {
    public static final String BASE_URL = "https://newsapi.org/v2/";

    public static Retrofit retrofit = null;

    public static Retrofit getClient(){

        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
