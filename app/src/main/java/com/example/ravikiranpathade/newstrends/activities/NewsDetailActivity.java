
package com.example.ravikiranpathade.newstrends.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.R;

import java.io.File;

import data.NewsContract;
import fragments.NewsDescriptionFragment;
import fragments.WebViewNewsFragment;
import utils.HelperFunctions;

public class NewsDetailActivity extends AppCompatActivity implements NewsDescriptionFragment.OnFragmentInteractionListener,

        WebViewNewsFragment.OnFragmentInteractionListener {

    FragmentManager fragmentManager;
    WebViewNewsFragment webFragment;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        fragmentManager = getSupportFragmentManager();
//        Fragment f = new NewsDescriptionFragment();
//        f.getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                return false;
//            }
//        });
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.newsDescriptionFragment, new NewsDescriptionFragment()).commit();
        } else {
            fragmentManager.getFragment(savedInstanceState, "fragment_news_detail");
        }
        if (savedInstanceState == null) {
            title = getIntent().getStringExtra("title");
        } else {
            title = savedInstanceState.getString("title_act_news_detail");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //TODO Implement Delete Files
        // put ID and delete status
        // and empty the preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String checkString = "NotSet";
        boolean deleteCheck = preferences.getBoolean("delete_files", false);
        if (deleteCheck) {
            try {
                File check1 = new File(preferences.getString("delete_image", checkString));
                File check2 = new File(preferences.getString("delete_mht", checkString));
                if (check1.exists()) {
                    check1.delete();
                }
                if (check2.exists()) {
                    check2.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Log.d("Check Image",String.valueOf());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLinkButtonPressed(String url) {
        //String url1 = getIntent().getStringExtra("url");
        Cursor check = getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath("id").build(),
                null,
                title,
                null, null, null
        );
        boolean isConnected = new HelperFunctions().getConnectionInfo(this);
        if (check.getCount() == 0 && !isConnected) {
            Log.d("Cheeck H", "1");
            Snackbar.make(findViewById(android.R.id.content), "NO INTERNET CONNECTION", Snackbar.LENGTH_SHORT).show();
        } else {
            Log.d("Cheeck H", "2");
            Bundle b = new Bundle();
            b.putString("urlForWeb", url);
            webFragment = new WebViewNewsFragment();
            webFragment.setArguments(b);
            fragmentManager.beginTransaction().replace(R.id.newsDescriptionFragment, webFragment).addToBackStack(null).commit();

        }
        if (check.getCount() > 0 && !isConnected) {
            check.moveToFirst();
            String id_file = String.valueOf(check.getInt(check.getColumnIndex("_id")));
            Bundle b = new Bundle();
            String urls = "file://" + getFilesDir().getAbsolutePath()
                    + File.separator + id_file + ".mht";
            b.putString("urlForWeb", urls);
            webFragment = new WebViewNewsFragment();
            webFragment.setArguments(b);
            fragmentManager.beginTransaction().replace(R.id.newsDescriptionFragment, webFragment).addToBackStack(null).commit();
        }

    }

    @Override
    public void onBackPressedFromDetail(boolean status, File image, File mht) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentManager.putFragment(outState, "fragment_news_detail", fragmentManager.findFragmentById(R.id.newsDescriptionFragment));
        outState.putString("title_act_news_detail", title);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
