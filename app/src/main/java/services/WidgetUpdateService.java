package services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ravikiranpathade.newstrends.NewsAppWidget;

/**
 * Created by ravikiranpathade on 12/24/17.
 */

public class WidgetUpdateService extends IntentService {
    public static final String ACTION_UPDATE_WIDGET = "cpm.example.ravikiranpathade.newstrends.action_update_widget";

    public WidgetUpdateService() {
        super("NewsUpatesWidget");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent!=null){
            if(intent.getAction().equals(ACTION_UPDATE_WIDGET)){
                updateWidgetForNewData();
            }
        }

    }
    public void updateWidget(Context context){
        Intent i = new Intent(context,WidgetUpdateService.class);
        i.setAction(ACTION_UPDATE_WIDGET);
        context.startService(i);


    }
    public void updateWidgetForNewData() {

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(this, NewsAppWidget.class));
        NewsAppWidget.updateNewsWidget(this,widgetManager,appWidgetIds);
    }
}
