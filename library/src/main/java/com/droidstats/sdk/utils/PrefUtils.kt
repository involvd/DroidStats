package com.droidstats.sdk.utils

import android.content.Context
import android.content.SharedPreferences

import com.droidstats.BuildConfig

/**
 * Created by JJ on 23/05/15.
 */
internal object PrefUtils {

    private val PREFS = BuildConfig.APPLICATION_ID + "_Prefs"

    internal fun writeBooleanPref(context: Context, name: String, b: Boolean) {
        val editor = getSharedPrefs(context).edit()
        editor.putBoolean(name, b)
        editor.apply()
    }

    internal fun readBooleanPref(context: Context, name: String): Boolean {
        val sp = getSharedPrefs(context)
        return sp.getBoolean(name, false)
    }

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

    internal fun clearAll(context: Context) {
        return getSharedPrefs(context).edit().clear().apply()
    }

    internal fun removePrefs(context: Context, exceptions: ArrayList<String>) {
        val prefs = getSharedPrefs(context)
        val editor = getSharedPrefs(context).edit()
        for(key in prefs.all.keys)
            if(!exceptions.contains(key))
                editor.remove(key)
        editor.apply()
    }

    internal fun removePref(context: Context, pref: String) {
        val editor = getSharedPrefs(context).edit()
        editor.remove(pref)
        editor.apply()
    }

}
