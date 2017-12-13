package com.example.ravikiranpathade.newstrends.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import java.util.ArrayList;
import java.util.List;

import models.Articles;
import models.CompleteResponse;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public final String KEY ="16a2ce7a435e4acb8482fae088ba6b9e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView t = (TextView) findViewById(R.id.textView);

        final GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);

        Call<CompleteResponse> call = service.getTopNewsArticles(KEY,"en");

        final List<Articles>[] a1 = new List[]{new ArrayList<>()};


        call.enqueue(new Callback<CompleteResponse>() {
            @Override
            public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                a1[0] = response.body().getArticles();

                Log.d("Check Here", String.valueOf(a1[0].size()));
            }

            @Override
            public void onFailure(Call<CompleteResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
