package services;

import android.annotation.SuppressLint;

import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.MainActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    public final String KEY = "16a2ce7a435e4acb8482fae088ba6b9e";
    SharedPreferences preferences;
    List<List<Articles>> newAlerts;
    int newAlertsNumber;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        newAlertsNumber = 0;
        newAlerts = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String words = preferences.getString("jArrayWords", "");
        if (words != null || words != "" || !words.isEmpty()) {
            JSONArray wordsJsonArray = null;
            try {
                wordsJsonArray = new JSONArray(words);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < wordsJsonArray.length(); i++) {
                String keyword = null;
                try {
                    keyword = wordsJsonArray.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (keyword != null || keyword != "" || !keyword.isEmpty()) {
                    final GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);
                    Call<CompleteResponse> call = service.getForAlerts(KEY, keyword);
                    call.enqueue(new Callback<CompleteResponse>() {
                        @Override
                        public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {
                            newAlerts.add(response.body().getArticles());
                        }

                        @Override
                        public void onFailure(Call<CompleteResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }


            }
            List<ContentValues> listValues = null;
            for (int i = 0; i < newAlerts.size(); i++) {
                List<Articles> articles = newAlerts.get(i);

                listValues = new ArrayList<>();
                for (Articles a : articles) {
                    ContentValues values = new ContentValues();

                    //TODO ADD VALUES TO CONTENTVALUES

                    Date dateInsert = DateTimeUtils.formatDate(a.getPublishedAt());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_TITLE, a.getTitle());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DESCRIPTION, a.getDescription());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL, a.getUrl());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_URL_TO_IMAGE, a.getUrlToImage());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_AUTHOR, a.getAuthor());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_PUBLISHED_AT, a.getPublishedAt());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_ID, a.getSource().getId());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_SOURCE_NAME, a.getSource().getName());
                    values.put(NewsContract.NewsFavoritesEntry.COLUMN_NAME_DATE, simpleDateFormat.format(dateInsert));

                    listValues.add(values);
                }
            }
            if (listValues != null) {
                ContentValues[] cv = new ContentValues[listValues.size()];
                listValues.toArray(cv);
                newAlertsNumber = getApplicationContext().getContentResolver().bulkInsert(
                        NewsContract.NewsAlertsEntry.FINAL_URI, cv
                );
            }
        }

        //TODO Call API and Store it in List for all the Keywords in preferences
        //TODO Get All sharedpreferences keywords

        //TODO Save all List in Database
        //TODO Save the difference and set Notification Title as N number of new News is available
        if (newAlertsNumber > 0) {
            setNotification(newAlertsNumber);
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
        notification.setSmallIcon(R.drawable.ic_favorite_black_24px)
                .setTicker("Testing Notifications")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("You have new " + String.valueOf(n) + " NEWS Alerts")
                .setContentText("Open The App");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification.build());


    }
}
