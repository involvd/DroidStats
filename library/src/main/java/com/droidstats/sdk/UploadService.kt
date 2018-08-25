package com.droidstats.sdk

import android.util.Log
import com.droidstats.sdk.utils.PrefUtils
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import kotlinx.coroutines.experimental.launch

class UploadService : JobService() {

    private val TAG = UploadService::class.java.simpleName

    override fun onStartJob(job: JobParameters): Boolean {
        Log.d(TAG, "Started..")
        launch {
            try {
                SubmissionManager.uploadStats(this@UploadService)
            } catch(e: Exception) {
                e.printStackTrace()
            }
            PrefUtils.clearAll(this@UploadService) //Clears regardless of success
            Log.d(TAG, "Finished..")
            jobFinished(job, false);
        }
        return false;
    }

    override fun onStopJob(job: JobParameters): Boolean {
        Log.d(TAG, "Stopped..")
        return false //TODO
    }


}