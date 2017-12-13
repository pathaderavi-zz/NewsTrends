package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragments.SearchLatestNews;
import fragments.TopNewsFragment;

/**
 * Created by ravikiranpathade on 12/12/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numberOfTabs;
    public PagerAdapter(FragmentManager fm,int num) {
        super(fm);
        this.numberOfTabs = num;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                TopNewsFragment topnews = new TopNewsFragment();
                return topnews;
            case 1:
                SearchLatestNews search = new SearchLatestNews();
                return search;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
