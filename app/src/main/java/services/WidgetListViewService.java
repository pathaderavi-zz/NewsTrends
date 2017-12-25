package services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.example.ravikiranpathade.newstrends.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import models.Articles;

/**
 * Created by ravikiranpathade on 12/24/17.
 */

public class WidgetListViewService extends RemoteViewsService {
    private Context context;
    private  int widgetId;
    List<Articles> articlesList;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Running Check ","getViewAt");
        return new WidgetListViewFactory(getApplicationContext(),intent);
    }

    class WidgetListViewFactory implements RemoteViewsService.RemoteViewsFactory{
        public WidgetListViewFactory(Context applicationContext, Intent intent) {
            Log.d("Running ","getViewAt");
            context = applicationContext;
            widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
            articlesList = new ArrayList<>();
        }

        public List<Articles> getArticles(){
            articlesList = new ArrayList<>();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String news = pref.getString("topnews","");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Articles>>(){}.getType();
            articlesList = gson.fromJson(news,type);
            return articlesList;
        }
        @Override
        public void onCreate() {
            getArticles();

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return Math.min(articlesList.size(),10);
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews remtoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            Log.d("Check",articlesList.get(i).getTitle());
            if(articlesList.size()>0){
                String title = articlesList.get(i).getTitle();

                remtoteView.setTextViewText(R.id.widgetTitleTextView,title);

            }
            return remtoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
