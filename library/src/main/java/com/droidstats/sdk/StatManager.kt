package com.droidstats.sdk

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.droidstats.sdk.utils.PrefUtils
import com.droidstats.sdk.utils.SdkUtils


object StatManager {

    internal const val SPLITTER: String = "::"
    private val TAG = StatManager::class.java.simpleName
    internal const val UPDATED_TO = "UPDATED_TO"
    internal const val FIRST_INSTALL = "FIRST_INSTALL"
    internal const val FIRST_INSTALL_SENT = "FIRST_INSTALL_SENT"

    @JvmStatic
    fun logEvent(context: Context, type: Type, name: String, logOnce: Boolean = false) {
        return logEvent(context, type.name, name, logOnce)
    }

    @JvmStatic
    fun logEvent(context: Context, customType: String, name: String, logOnce: Boolean = false) {
        if(!SdkUtils.getCollectionEnabled(context))
            return
        var count: Long = 0
        val key = "$customType$SPLITTER$name"
        if(PrefUtils.contains(context, key)) {
            if(logOnce)
                return
            count = PrefUtils.readLongPref(context, key)
        }
        Log.d(TAG, "Logged $name of $customType..")
        return PrefUtils.writeLongPref(context, key, count + 1)
    }

    internal fun logAppUpdated(context: Context) {
        try {
            val info = context.getPackageManager().getPackageInfo(context.packageName, 0)
            var name = FIRST_INSTALL
            if(PrefUtils.contains(context, FIRST_INSTALL))
                name = UPDATED_TO
            Log.d(TAG, "Logged app installed..")
            PrefUtils.writeLongPref(context, name, info.versionCode.toLong())
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun testUpload(context: Context) {
        logEvent(context, Type.ACTIVITY_OPEN, "TestActivity")
        logEvent(context, Type.ACTIVITY_OPEN, "TestActivity")
        logEvent(context, Type.ACTIVITY_OPEN, "TestActivity")
        SubmissionManager.testUpload(context)
    }

    @JvmStatic
    fun enableCollection(context: Context, isEnabled: Boolean) {
        PrefUtils.writeBooleanPref(context, SdkUtils.ENABLED, isEnabled);
    }

    fun clear(context: Context) {
        val exceptions = ArrayList<String>()
        exceptions.add(FIRST_INSTALL)
        exceptions.add(FIRST_INSTALL_SENT)
        PrefUtils.removePrefs(context, exceptions)
    }

    enum class Type {
        ACTIVITY_OPEN, BUTTON_CLICK, FEATURE_USED
    }

}