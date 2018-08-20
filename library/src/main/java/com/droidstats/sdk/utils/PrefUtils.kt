package com.droidstats.sdk.utils

import android.content.Context
import android.content.SharedPreferences

import com.droidstats.BuildConfig

/**
 * Created by JJ on 23/05/15.
 */
internal object PrefUtils {

    private val PREFS = BuildConfig.APPLICATION_ID + "_Prefs"

    internal fun writeLongPref(context: Context, name: String, l: Long) {
        val editor = getSharedPrefs(context).edit()
        editor.putLong(name, l)
        editor.apply()
    }

    internal fun readLongPref(context: Context, name: String): Long {
        val sp = getSharedPrefs(context)
        return sp.getLong(name, 0)
    }

    internal fun writeStringPref(context: Context, name: String, s: String) {
        val editor = getSharedPrefs(context).edit()
        editor.putString(name, s)
        editor.apply()
    }

    internal fun readStringPref(context: Context, name: String): String {
        val sp = getSharedPrefs(context)
        return sp.getString(name, "")
    }

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName + PREFS, Context.MODE_PRIVATE)
    }

    internal fun contains(context: Context, pref: String): Boolean {
        return getSharedPrefs(context).contains(pref)
    }

    internal fun getAll(context: Context): MutableMap<String, *> {
        return getSharedPrefs(context).all
    }

    internal fun removePrefs(context: Context, prefs: Array<String>) {
        val editor = getSharedPrefs(context).edit()
        for (pref in prefs)
            editor.remove(pref)
        editor.apply()
    }

    internal fun removePref(context: Context, pref: String) {
        val editor = getSharedPrefs(context).edit()
        editor.remove(pref)
        editor.apply()
    }

}
