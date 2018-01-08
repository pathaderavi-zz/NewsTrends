package services;

import android.annotation.SuppressLint;


import ravikiran.pathade.ravikiranpathade.newstrends.R;
import ravikiran.pathade.ravikiranpathade.newstrends.activities.AlertedNewsActivity;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import data.NewsContract;
import models.Articles;
import models.CompleteResponse;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ravikiranpathade on 12/22/17.
 */

@SuppressLint("NewApi")
public class JobDispatcherForNotifications extends JobService {
    NotificationCompat.Builder notification;
    private static final int NOTIFICATION_ID = 80878085;
    private static final String CHANNEL_ID = "NEWSTRENDS";
    String KEY;
    SharedPreferences preferences;
    List<List<Articles>> newAlerts;
    int newAlertsNumber;
    Cursor mCursor;
    Cursor checkDeleted;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        KEY = getApplicationContext().getResources().getString(R.string.API_KEY);
        List<ContentValues> listValues = null;
        newAlertsNumber = 0;
        newAlerts = new ArrayList<>();
        final String checkNull = getApplicationContext().getResources().getString(R.string.empty_string);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String words = preferences.getString(getApplicationContext().getResources().getString(R.string.jArrayWords), checkNull);
        listValues = new ArrayList<>();

        if (words != null || words != checkNull || !words.isEmpty() || !words.equals(getApplicationContext().getResources().getString(R.string.empty_array))) {
            JSONArray wordsJsonArray = null;
            try {
                wordsJsonArray = new JSONArray(words);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (wordsJsonArray != null) {
                final List<ContentValues> finalListValues = listValues;
                for (int i = 0; i < wordsJsonArray.length(); i++) {
                    String keyword = null;
                    try {
                        keyword = wordsJsonArray.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String id_append = getApplicationContext().getResources().getString(R.string.id_append_string);
                    if (keyword != null || keyword != checkNull || !keyword.isEmpty()) {

                        final GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);
                        Call<CompleteResponse> call = service.getForAlerts(KEY, keyword);

                        final String finalKeyword = keyword;
                        call.enqueue(new Callback<CompleteResponse>() {
                                         @Override
                                         public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                                             List<Articles> articles = response.body().getArticles();
                                             for (Articles a : articles) {
                                                 mCursor = getApplicationContext().getContentResolver().query(
                                                         NewsContract.NewsAlertsEntry.FINAL_URI.buildUpon().appendPath(id_append)
                                                                 .build(),
                                                         null,
                                                         a.getTitle(),
                                                         null, null, null);
                                                 checkDeleted = getApplicationContext().getContentResolver().query(
                                                         NewsContract.NewsDeletedAlerts.FINAL_URI.buildUpon().appendPath(id_append)
                                                                 .build(),
                                                         null,
                                                         a.getTitle(),
                                                         null, null, null);

                                                 if (mCursor != null && mCursor.getCount() == 0 && checkDeleted != null && checkDeleted.getCount() == 0) {

                                                     ContentValues values = new ContentValues();
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_TITLE, a.getTitle());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_DESCRIPTION, a.getDescription());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_URL, a.getUrl());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_URL_TO_IMAGE, a.getUrlToImage());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_AUTHOR, a.getAuthor());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_PUBLISHED_AT, a.getPublishedAt());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_SOURCE_ID, a.getSource().getId());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_SOURCE_NAME, a.getSource().getName());
                                                     values.put(NewsContract.NewsAlertsEntry.COLUMN_NAME_KEYWORD, finalKeyword);


                                                     if (a.getPublishedAt() != checkNull || !a.getPublishedAt().isEmpty()) {
                                                         Date dateInsert = DateTimeUtils.formatDate(a.getPublishedAt());
                                                         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getApplicationContext().getResources().getString(R.string.dateFormatToInsert));
                                                         values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE, simpleDateFormat.format(dateInsert));
                                                     }

                                                     finalListValues.add(values);

                                                 }
                                             }

                                         }

                                         @Override
                                         public void onFailure(Call<CompleteResponse> call, Throwable t) {
                                             t.printStackTrace();
                                         }
                                     }
                        );

                    }


                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalListValues.size() > 0) {
                            ContentValues[] cv = new ContentValues[finalListValues.size()];
                            finalListValues.toArray(cv);

                            newAlertsNumber = getApplicationContext().getContentResolver().bulkInsert(
                                    NewsContract.NewsAlertsEntry.FINAL_URI, cv
                            );
                        }
                        if (finalListValues.size() > 0) {
                            setNotification(finalListValues.size());
                        }
                    }
                }, 5000); //TODO Check if this works now


            }
        }

        jobFinished(jobParameters, false);
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }

    public void setNotification(int n) {

        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_favorite_black_24px) //TODO Change Notification Logo
                .setTicker(getApplicationContext().getResources().getString(R.string.notification_ticker))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getApplicationContext().getResources().getString(R.string.notification_title))
                .setContentText(getApplicationContext().getResources().getString(R.string.notification_text_1) + String.valueOf(n) + getApplicationContext().getResources().getString(R.string.notification_text_2))
        ;

        Uri u = Uri.parse(preferences.getString(getApplicationContext().getResources().getString(R.string.notifications_new_message_ringtone), getApplicationContext().getResources().getString(R.string.default_sound)));
        notification.setSound(u);

        boolean isVibrate = preferences.getBoolean(getApplicationContext().getResources().getString(R.string.vibrate_setting), false);
        if (isVibrate) {
            notification.setVibrate(new long[]{1000, 1000});
        }
        Intent intent = new Intent(getApplicationContext(), AlertedNewsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification.build());
    }
}
