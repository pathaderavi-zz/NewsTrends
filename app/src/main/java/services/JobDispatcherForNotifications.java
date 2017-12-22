package services;

import android.annotation.SuppressLint;

import com.example.ravikiranpathade.newstrends.R;
import com.example.ravikiranpathade.newstrends.activities.MainActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by ravikiranpathade on 12/22/17.
 */

@SuppressLint("NewApi")
public class JobDispatcherForNotifications extends JobService {
    NotificationCompat.Builder notification;
    private static final int NOTIFICATION_ID = 80878085;
    private static final String CHANNEL_ID = "NEWSTRENDS";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_favorite_black_24px)
                .setTicker("Testing Notifications")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("You have new NEWS Alerts")
                .setContentText("Open The App");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID,notification.build());

        jobFinished(jobParameters, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }
}
