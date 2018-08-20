package com.droidstats.sdk.utils

import android.content.Context
import android.content.SharedPreferences

import com.droidstats.BuildConfig

/**
 * Created by JJ on 23/05/15.
 */
internal object PrefUtils {

    private val PREFS = BuildConfig.APPLICATION_ID + "_Prefs"

    fun writeLongPref(context: Context, name: String, l: Long) {
        val editor = getSharedPrefs(context).edit()
        editor.putLong(name, l)
        editor.apply()
    }

    fun readLongPref(context: Context, name: String): Long {
        val sp = getSharedPrefs(context)
        return sp.getLong(name, 0)
    }

    fun writeStringPref(context: Context, name: String, s: String) {
        val editor = getSharedPrefs(context).edit()
        editor.putString(name, s)
        editor.apply()
    }

    fun readStringPref(context: Context, name: String): String {
        val sp = getSharedPrefs(context)
        return sp.getString(name, "")
    }

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName + PREFS, Context.MODE_PRIVATE)
    }

    fun contains(context: Context, pref: String): Boolean {
        return getSharedPrefs(context).contains(pref)
    }

    fun getAll(context: Context): MutableMap<String, *> {
        return getSharedPrefs(context).all
    }

    fun removePrefs(context: Context, prefs: Array<String>) {
        val editor = getSharedPrefs(context).edit()
        for (pref in prefs)
            editor.remove(pref)
        editor.apply()
    }

    fun removePref(context: Context, pref: String) {
        val editor = getSharedPrefs(context).edit()
        editor.remove(pref)
        editor.apply()
    }

}
