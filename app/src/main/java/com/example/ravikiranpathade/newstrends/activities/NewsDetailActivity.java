
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
import fragments.WebViewNewsFragment;

public class NewsDetailActivity extends AppCompatActivity implements NewsDescriptionFragment.OnFragmentInteractionListener,

WebViewNewsFragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager;
    WebViewNewsFragment webFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.newsDescriptionFragment,new NewsDescriptionFragment()).commit();

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

    @Override
    public void onLinkButtonPressed(String url) {
        //String url1 = getIntent().getStringExtra("url");
        Bundle b = new Bundle();
        b.putString("urlForWeb",url);
        webFragment = new WebViewNewsFragment();
        webFragment.setArguments(b);
        fragmentManager.beginTransaction().replace(R.id.newsDescriptionFragment,webFragment).addToBackStack(null).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
