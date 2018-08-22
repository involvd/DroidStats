package com.droidstats.sdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.annotation.CallSuper
import android.util.Log
import com.droidstats.sdk.StatManager
import com.droidstats.sdk.SubmissionManager

/**
 * Created by jj on 11/01/18.
 */

class AppUpdatedReceiver : BroadcastReceiver() {

    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "app updated..")
        StatManager.logAppUpdated(context)
        SubmissionManager.scheduleUpload(context)
    }


    companion object {
        private val TAG = AppUpdatedReceiver::class.java.simpleName
    }

}
