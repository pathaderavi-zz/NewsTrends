package com.example.ravikiranpathade.newstrends.activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.ravikiranpathade.newstrends.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapters.NewsRecyclerAdapter;
import data.NewsContract;
import models.Articles;
import models.Source;

public class AlertedNewsActivity extends AppCompatActivity {
    Cursor alertCursor;
    List<Articles> articleList;
    RecyclerView alertsRecycler;
    NewsRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerted_news);
        Toolbar t = findViewById(R.id.alertsBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("News Alerts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        alertCursor = getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                null,
                null,
                null,
                "DATE DESC");
        articleList = new ArrayList<>();


        while(alertCursor.moveToNext()){

            articleList.add(new Articles(alertCursor.getString(alertCursor.getColumnIndex("AUTHOR")),
                    alertCursor.getString(alertCursor.getColumnIndex("TITLE")),
                    alertCursor.getString(alertCursor.getColumnIndex("DESCRIPTION")),
                    alertCursor.getString(alertCursor.getColumnIndex("URL")),
                    alertCursor.getString(alertCursor.getColumnIndex("URLTOIMAGE")),
                    alertCursor.getString(alertCursor.getColumnIndex("PUBLISHEDAT")),
                    new Source(
                            alertCursor.getString(alertCursor.getColumnIndex("SOURCEID")),
                            alertCursor.getString(alertCursor.getColumnIndex("SOURCENAME"))
                    )
            ));
        }

        adapter = new NewsRecyclerAdapter(articleList);
        alertsRecycler = findViewById(R.id.alertsRecyclerView);
        if(alertCursor.getCount()==0){
//            textView.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        alertsRecycler.setLayoutManager(layoutManager);
        alertsRecycler.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
