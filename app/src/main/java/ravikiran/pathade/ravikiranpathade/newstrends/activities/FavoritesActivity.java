package ravikiran.pathade.ravikiranpathade.newstrends.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;



import fragments.FavoritesFragment;
import ravikiran.pathade.ravikiranpathade.newstrends.R;

public class FavoritesActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FavoritesFragment favoritesFragment;
    NavigationView navigation;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar t = findViewById(R.id.favoritesBar);
        setSupportActionBar(t);
        t.setTitle(getResources().getString(R.string.toolbar_favorite_activity));

        drawerLayout = findViewById(R.id.drawerLayoutFav);
        navigation = findViewById(R.id.navigationViewFav);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();


        if (savedInstanceState == null) {
            favoritesFragment = new FavoritesFragment();
            fragmentManager.beginTransaction().add(R.id.favoritesFrame, favoritesFragment).commit();
        } else {
            fragmentManager.getFragment(savedInstanceState, getResources().getString(R.string.favorite_fragment_key));
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fragmentManager.putFragment(outState,getResources().getString(R.string.favorite_fragment_key),fragmentManager.findFragmentById(R.id.favoritesFrame));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

          if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
