package com.example.ravikiranpathade.newstrends.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ravikiranpathade.newstrends.R;

import fragments.FavoritesFragment;

public class FavoritesActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FavoritesFragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar t = findViewById(R.id.favoritesBar);
        setSupportActionBar(t);
        t.setTitle("Favorite News");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();


        if (savedInstanceState == null) {
            favoritesFragment = new FavoritesFragment();
            fragmentManager.beginTransaction().add(R.id.favoritesFrame, favoritesFragment).commit();
        } else {
            fragmentManager.getFragment(savedInstanceState, "favorite_fragment");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentManager.putFragment(outState,"favorite_fragment",fragmentManager.findFragmentById(R.id.favoritesFrame));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
