
package com.example.ravikiranpathade.newstrends.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import fragments.NewsDescriptionFragment;

public class NewsDetailActivity extends AppCompatActivity implements NewsDescriptionFragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.newsDescriptionFragment,new NewsDescriptionFragment()).commit();

//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarDetail);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Back","Working");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Back","Working2");
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }
}
