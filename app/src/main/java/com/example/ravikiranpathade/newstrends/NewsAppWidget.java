package com.example.ravikiranpathade.newstrends;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.ravikiranpathade.newstrends.activities.MainActivity;

import services.WidgetListViewService;

/**
 * Implementation of App Widget functionality.
 */
public class NewsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_app_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.appwidget_text,pendingIntent);
        String check = PreferenceManager.getDefaultSharedPreferences(context).getString("topnews","");


        if(check=="" || check.isEmpty() || check==null){
            views.setTextViewText(R.id.appwidget_text, widgetText);
        }
        else{
           // views.setTextViewText(R.id.appwidget_text,"");
            //TODO
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listViewWidget);
        }
        Intent listIntent = new Intent(context, WidgetListViewService.class);
        views.setRemoteAdapter(R.id.listViewWidget,listIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    public static void updateNewsWidget(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

