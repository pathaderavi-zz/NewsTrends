package services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;

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

    public void updateWidgetForNewData() {

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(this, NewsAppWidget.class));
        NewsAppWidget.updateNewsWidget(this,widgetManager,appWidgetIds);
    }
}
