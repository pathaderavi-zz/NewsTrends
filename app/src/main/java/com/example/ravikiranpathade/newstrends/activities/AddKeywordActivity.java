package com.example.ravikiranpathade.newstrends.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    List<String> list;
    ListAdapter adapter;
    ListView listView;
    JSONArray jArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keyword);
        list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        list.add("Five");
        Toolbar t = findViewById(R.id.keywordBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("Manage Alert Keywords");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final android.support.v7.widget.SearchView ed = findViewById(R.id.addword);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


        try {
            jArray = new JSONArray(preferences.getString("jArrayWords", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ed.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                setAdapter(query);
                //TODO Change Adapter
                ed.onActionViewCollapsed();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if (jArray != null) {
            Log.d("Check Array 1", jArray.toString());
        }
        adapterInput(jArray);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter(String query) {
        //TODO Get put jArray String in preferences
        try {
            jArray = new JSONArray(preferences.getString("jArrayWords", ""));
            Log.d("Check setAdapter", jArray.toString());
        } catch (JSONException e) {
            jArray = new JSONArray();
            e.printStackTrace();
        }
        jArray.put(query);
        String rep = jArray.toString();
        Log.d("Check", rep);
        editor.putString("jArrayWords", rep);
        editor.commit();
        adapterInput(jArray);

    }

    public void adapterInput(JSONArray j) {
        List<String> adapterList = new ArrayList<>();
        if(j!=null) {
            for (int i = 0; i < j.length(); i++) {
                try {
                    adapterList.add(j.getString(i));
                    Log.d("Check String", j.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new KeywordsAdapter(this, adapterList);
            listView = findViewById(R.id.listViewKeywords);
            listView.setAdapter(adapter);
        }
    }
}
