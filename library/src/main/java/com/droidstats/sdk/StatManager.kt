package com.droidstats.sdk

import android.content.Context
import android.content.pm.PackageManager
import com.droidstats.sdk.utils.PrefUtils
import com.droidstats.sdk.utils.SdkUtils


object StatManager {

    internal const val SPLITTER: String = "::"
    internal const val PREV_INSTALL = "PREV_INSTALL"
    internal const val NEW_INSTALL = "NEW_INSTALL"

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
        return PrefUtils.writeLongPref(context, key, count + 1)
    }

    internal fun logAppUpdated(context: Context) {
        if(!SdkUtils.getCollectionEnabled(context))
            return
        try {
            val info = context.getPackageManager().getPackageInfo(context.packageName, 0)
            var name = "installed"
            //TODO: Logic to know if install was an update or a fresh install, probably requires persisting an install version code pref
//            var prevVersionCode: Long = -1
//            if(PrefUtils.contains(context, name)) {
//                prevVersionCode = PrefUtils.readLongPref(context, name)
//                PrefUtils.removePref(context, name)
//            }
//            if(PrefUtils.contains(context, "updated_to"))
//                prevVersionCode = PrefUtils.readLongPref(context, "updated_to")
//            if(prevVersionCode > -1)
//                name = "updated_to"
            PrefUtils.writeLongPref(context, "INSTALL$SPLITTER$name", info.versionCode.toLong())
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

    enum class Type {
        ACTIVITY_OPEN, BUTTON_CLICK, FEATURE_USED
    }

}