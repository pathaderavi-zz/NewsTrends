
package com.example.ravikiranpathade.newstrends.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import fragments.NewsDescriptionFragment;

public class NewsDetailActivity extends AppCompatActivity implements NewsDescriptionFragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.newsDescriptionFragment,new NewsDescriptionFragment()).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
