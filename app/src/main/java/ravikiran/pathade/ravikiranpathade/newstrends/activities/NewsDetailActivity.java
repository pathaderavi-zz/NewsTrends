
package ravikiran.pathade.ravikiranpathade.newstrends.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import java.io.File;

import data.NewsContract;
import fragments.NewsDescriptionFragment;
import fragments.WebViewNewsFragment;
import ravikiran.pathade.ravikiranpathade.newstrends.R;
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

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.newsDescriptionFragment, new NewsDescriptionFragment()).commit();
        } else {
            fragmentManager.getFragment(savedInstanceState, getResources().getString(R.string.fragment_news_detail_key));
        }
        if (savedInstanceState == null) {
            title = getIntent().getStringExtra(getResources().getString(R.string.title_cursor_adapter));
        } else {
            title = savedInstanceState.getString(getResources().getString(R.string.title_act_news_detail));
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        String checkString = getResources().getString(R.string.checkString_setCheck);
        boolean deleteCheck = preferences.getBoolean(getResources().getString(R.string.delete_files), false);
        if (deleteCheck) {
            editor.putBoolean(getResources().getString(R.string.favorites_changed), true);
            try {
                File check1 = new File(preferences.getString(getResources().getString(R.string.delete_image_boolean_key), checkString));
                File check2 = new File(preferences.getString(getResources().getString(R.string.delete_mht_boolean_key), checkString));
                if (check1.exists()) {
                    check1.delete();
                    editor.putString(getResources().getString(R.string.delete_image_boolean_key), checkString);

                }
                if (check2.exists()) {
                    check2.delete();
                    editor.putString(getResources().getString(R.string.delete_mht_boolean_key), checkString);
                }
                editor.putBoolean(getResources().getString(R.string.delete_files), false);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }

        //Log.d("Check Image",String.valueOf());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onLinkButtonPressed(String url, String titleFrom) {
        //String url1 = getIntent().getStringExtra("url");
        Cursor check = getContentResolver().query(

                NewsContract.NewsFavoritesEntry.FINAL_URI.buildUpon().appendPath(getResources().getString(R.string.id_append_string)).build(),
                null,
                title,
                null, null, null
        );
        boolean isConnected = new HelperFunctions().getConnectionInfo(this);
        if (check != null && check.getCount() == 0 && !isConnected) {

            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_SHORT).show();
        } else {

            Bundle b = new Bundle();
            b.putString(getResources().getString(R.string.bundle_key_urlForWeb), url);
            b.putString(getResources().getString(R.string.bundle_key_titleToWeb), titleFrom);
            b.putString(getResources().getString(R.string.bundle_key_realWebUrl),url);
            webFragment = new WebViewNewsFragment();
            webFragment.setArguments(b);
            fragmentManager.beginTransaction().replace(R.id.newsDescriptionFragment, webFragment).addToBackStack(null).commit();

        }
        if (check != null && check.getCount() > 0 && !isConnected) {
            check.moveToFirst();
            String id_file = String.valueOf(check.getInt(check.getColumnIndex(getResources().getString(R.string._id))));
            Bundle b = new Bundle();
            String urls = getResources().getString(R.string.file_prepend) + getFilesDir().getAbsolutePath()
                    + File.separator + id_file + getResources().getString(R.string.MHT);
            b.putString(getResources().getString(R.string.bundle_key_urlForWeb), urls);
            b.putString(getResources().getString(R.string.bundle_key_titleToWeb), titleFrom);
            b.putString(getResources().getString(R.string.bundle_key_realWebUrl),url);
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
        fragmentManager.putFragment(outState, getResources().getString(R.string.fragment_news_detail_key), fragmentManager.findFragmentById(R.id.newsDescriptionFragment));
        outState.putString(getResources().getString(R.string.title_act_news_detail), title);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
