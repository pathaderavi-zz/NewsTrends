package services;

import android.annotation.SuppressLint;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import models.Articles;
import models.CompleteResponse;
import ravikiran.pathade.ravikiranpathade.newstrends.R;
import rest.Client;
import rest.GetTopNewsWorldEnglish;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.text.UnicodeSet.CASE;

/**
 * Created by ravikiranpathade on 12/24/17.
 */

@SuppressLint("NewApi")
public class FetchTopNewsService extends JobService {
    String KEY;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    List<Articles> newAlerts;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        KEY = getApplicationContext().getResources().getString(R.string.API_KEY);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        String country = preferences.getString(getApplicationContext().getResources().getString(R.string.pref_country_key), getApplicationContext().getResources().getString(R.string.empty_string));

        String language = preferences.getString(getApplicationContext().getResources().getString(R.string.pref_language_key), getApplicationContext().getResources().getString(R.string.empty_string));

        String category = preferences.getString(getApplicationContext().getResources().getString(R.string.pref_category_key), getApplicationContext().getResources().getString(R.string.empty_string));

        final String checkNull  = getApplicationContext().getResources().getString(R.string.null_value_string);
        String checkEmpty = getApplicationContext().getResources().getString(R.string.empty_string);
        String checkZero = getApplicationContext().getResources().getString(R.string.zero);

        if (String.valueOf(language).equals(checkNull) || String.valueOf(language).equals(checkEmpty)
                || String.valueOf(language).equals(checkZero)) {
            language = getApplicationContext().getResources().getString(R.string.english);
            editor.putString(getApplicationContext().getResources().getString(R.string.pref_language_key), getApplicationContext().getResources().getString(R.string.english));
            editor.commit();
        }
        //TODO Implement Counrty Specific API
        if (String.valueOf(country).equals(checkNull) || String.valueOf(country).equals(checkZero)) {
            country = checkEmpty;
            editor.putString(getApplicationContext().getResources().getString(R.string.pref_country_key), checkNull);
        }

        if (String.valueOf(category).equals(checkNull) || String.valueOf(category).equals(checkZero)) {
            category = checkEmpty;
            editor.putString(getApplicationContext().getResources().getString(R.string.pref_category_key), checkNull);
        }

        GetTopNewsWorldEnglish service = Client.getClient().create(GetTopNewsWorldEnglish.class);
        Call<CompleteResponse> call = service.getTopNewsArticles(KEY, language, country, category);

        call.enqueue(new Callback<CompleteResponse>() {
            @Override
            public void onResponse(Call<CompleteResponse> call, Response<CompleteResponse> response) {

                newAlerts = new ArrayList<>();
                newAlerts = response.body().getArticles();

                String json = new Gson().toJson(newAlerts);
                editor.putString(getApplicationContext().getResources().getString(R.string.topnews_key), json);
                editor.putLong(getApplicationContext().getResources().getString(R.string.topNewsFetchedAt), System.currentTimeMillis());
                editor.commit();
                //TODO Update Widget
                if (newAlerts.size() == 0) {
                    editor.putString(getApplicationContext().getResources().getString(R.string.topnews_key), checkNull);
                    editor.putLong(getApplicationContext().getResources().getString(R.string.topNewsFetchedAt), 1080000000);
                    editor.commit();
                }
                WidgetUpdateService updateWidget = new WidgetUpdateService();
                updateWidget.updateWidget(getApplicationContext());

            }

            @Override
            public void onFailure(Call<CompleteResponse> call, Throwable t) {

            }
        });


        jobFinished(jobParameters, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


}
