package com.example.ravikiranpathade.newstrends.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ravikiranpathade.newstrends.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fragments.FavoritesFragment;
import fragments.SearchLatestNews;
import fragments.TopNewsFragment;
import models.Articles;
import models.CompleteResponse;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TopNewsFragment.OnFragmentInteractionListener, SearchLatestNews.OnFragmentInteractionListener {

    public final String KEY = "16a2ce7a435e4acb8482fae088ba6b9e";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    TabLayout tabLayout;
    ViewPager viewPager;
    FrameLayout frame;
    NavigationView navigation;
    PagerAdapter pagerAdapter;
    android.support.v4.app.FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frame = findViewById(R.id.mainActivityFramelayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigation = findViewById(R.id.navigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.topnews));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.searchlatest));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.viewPager);


        pagerAdapter = new adapters.PagerAdapter(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupDrawer(navigation);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();


    }

    private void selectNavigationDrawerItem(MenuItem menuItem) {


        switch (menuItem.getItemId()) {
            case R.id.home:
                //TODO Add new Fragment
                pagerAdapter = new adapters.PagerAdapter(fragmentManager, tabLayout.getTabCount());
                viewPager.setAdapter(pagerAdapter);
                viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                break;
            case R.id.settings:
                Log.d(String.valueOf(viewPager.getVisibility() == View.VISIBLE), String.valueOf(tabLayout.getVisibility() == View.VISIBLE));
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.favorites:
                Intent intentFav = new Intent(this,FavoritesActivity.class);
                startActivity(intentFav);
                break;
            case R.id.alerts:
                Intent intentAlert = new Intent(this, AlertedNewsActivity.class);
                startActivity(intentAlert);
                break;
            case R.id.addKeywords:
                Intent intentAdd = new Intent(this, AddKeywordActivity.class);
                startActivity(intentAdd);
                break;
            default:

        }
        drawerLayout.closeDrawers();


    }

    private void setupDrawer(NavigationView navigationView) {
        Log.d("Selected", "Item");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("Selected", "Item");
                selectNavigationDrawerItem(item);
                return true;
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
