package com.example.ravikiranpathade.newstrends.activities;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.example.ravikiranpathade.newstrends.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapters.NewsCursorAdapter;
import adapters.NewsRecyclerAdapter;
import data.NewsContract;
import models.Articles;
import models.Source;

public class AlertedNewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    NewsCursorAdapter adapter;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(1, null, this);
        setContentView(R.layout.activity_alerted_news);
        Toolbar t = findViewById(R.id.alertsBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("News Alerts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new NewsCursorAdapter(this, null,"CHECK");
        listView = findViewById(R.id.alertsRecyclerView);
        text = findViewById(R.id.noAlertTextView);
        listView.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, NewsContract.NewsAlertsEntry.FINAL_URI,
                null,
                null,
                null,
                "DATE DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Cursor cursor1 = getContentResolver().query(NewsContract.NewsAlertsEntry.FINAL_URI,
                null,
                null,
                null,
                "DATE DESC");
        adapter.swapCursor(cursor1);
        if (cursor1 == null || cursor1.getCount() == 0) {
            text.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }
}
