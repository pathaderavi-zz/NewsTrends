package com.example.ravikiranpathade.newstrends.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ravikiranpathade.newstrends.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import adapters.KeywordsAdapter;

public class AddKeywordActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ListAdapter adapter;
    ListView listView;
    JSONArray jArray;
    android.support.v7.widget.SearchView ed;

    NavigationView navigation;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keyword);

        Toolbar t = findViewById(R.id.keywordBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_alert_keywords_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ed = findViewById(R.id.addword);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        drawerLayout = findViewById(R.id.drawerLayoutAlertAddKeyword);
        navigation = findViewById(R.id.navigationViewAlertAddKeyword);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        try {
            jArray = new JSONArray(preferences.getString(getResources().getString(R.string.jArrayWords), getResources().getString(R.string.empty_string)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ed.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                setAdapter(query);

                ed.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        adapterInput(jArray);

        setupDrawer(navigation);
    }
    private void setupDrawer(NavigationView navigation) {
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectNavigationDrawerItem(item);
                return true;
            }
        });
    }
    private void selectNavigationDrawerItem(MenuItem menuItem) {


        switch (menuItem.getItemId()) {
            case R.id.home:

                Intent intentMain = new Intent(this,MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.settings:
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter(String query) {
        if (jArray != null && jArray.length() == 4) {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snackbar_4_words_max), Snackbar.LENGTH_SHORT).show();
        } else {
            boolean dupCheck = true;
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        if (jArray.get(i).equals(query)) {
                            dupCheck = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            if (!dupCheck) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.already_exists), Snackbar.LENGTH_SHORT).show();

            } else {
                try {
                    jArray = new JSONArray(preferences.getString(getResources().getString(R.string.jArrayWords), getResources().getString(R.string.empty_string)));

                } catch (JSONException e) {
                    jArray = new JSONArray();
                    e.printStackTrace();
                }
                jArray.put(query);
                String rep = jArray.toString();

                editor.putString(getResources().getString(R.string.jArrayWords), rep);
                editor.commit();
                adapterInput(jArray);
            }
        }

    }

    public void adapterInput(JSONArray j) {
        List<String> adapterList = new ArrayList<>();
        if (j != null) {
            for (int i = 0; i < j.length(); i++) {
                try {
                    adapterList.add(j.getString(i));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (j.length() == 4) {

            }
            adapter = new KeywordsAdapter(this, adapterList);
            listView = findViewById(R.id.listViewKeywords);
            listView.setAdapter(adapter);
        }
    }
}
