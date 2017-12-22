package services;

import android.annotation.SuppressLint;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.widget.Toast;

/**
 * Created by ravikiranpathade on 12/22/17.
 */

@SuppressLint("NewApi")
public class JobDispatcherForNotifications extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(getApplicationContext(),"Service is running",Toast.LENGTH_SHORT).show();
        jobFinished(jobParameters,false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
      
        return false;
    }
}
