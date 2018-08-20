package com.droidstats.sdk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import com.droidstats.sdk.StatManager
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import kotlin.collections.HashMap


internal object SdkUtils {

    private var TAG = SdkUtils::class.java.simpleName
    const val META_ID = "com.droidstats.app_id"
    const val META_KEY = "com.droidstats.api_key"

    const val UNIQUE_ID = "uniqueId"

    /**
     * @packageName being null retrieves all
     */
    @JvmStatic
    open fun getMetaDataKeyForPackages(context: Context, key: String, packageName: String? = null): HashMap<String, String> {
        val i = Intent("android.intent.action.MAIN")
        i.addCategory("android.intent.category.LAUNCHER")
        if(!TextUtils.isEmpty(packageName))
            i.`package` = packageName
        val list = context.packageManager.queryIntentActivities(i, PackageManager.GET_META_DATA)
        val appMap = HashMap<String, String>()
        if (list?.isNotEmpty() == true) {
            for (packageInfo in list) {
                if (packageInfo?.activityInfo?.applicationInfo?.metaData?.containsKey(key) == true)
                    appMap.put(packageInfo.activityInfo.packageName, packageInfo.activityInfo.applicationInfo.metaData.getString(key))
            }
        }
        return appMap;
    }

    @JvmStatic
    open fun getAppIdForPackage(context: Context, packageName: String): String? {
        val map = getMetaDataKeyForPackages(context, META_ID, packageName)
        if(map.isEmpty())
            return null
        else
            return map.get(packageName)
    }

    @JvmStatic
    open fun getApiKeyForPackage(context: Context, packageName: String): String? {
        val map = getMetaDataKeyForPackages(context, META_KEY, packageName)
        if(map.isEmpty())
            return null
        else
            return map.get(packageName)
    }

//                    Log.d(TAG, "SHA1: " + Base64.encode("3C:13:C4:E2:7E:BB:CF:56:6B:15:86:7E:C8:4D:A9:E0:91:97:D7:A8".toByteArray(), 0))

    @JvmStatic
    fun getCertificateSHA1Fingerprint(context: Context, packageName: String): String? {
        val pm = context.getPackageManager()
        val flags = PackageManager.GET_SIGNATURES
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName, flags)
            val signatures = packageInfo!!.signatures
            val cert = signatures[0].toByteArray()
            val input = ByteArrayInputStream(cert)
            var cf: CertificateFactory? = CertificateFactory.getInstance("X509")
            var c: X509Certificate? = cf!!.generateCertificate(input) as X509Certificate
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(c!!.getEncoded())
            val hexString = byte2HexFormatted(publicKey)

            return hexString
//                return hashString("MD5", hexString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun byte2HexFormatted(arr: ByteArray): String {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.toUpperCase())
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }

    fun getUniqueId(context: Context): String {
        var uniqueId = PrefUtils.readStringPref(context, UNIQUE_ID)
        if(TextUtils.isEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString()
            PrefUtils.writeStringPref(context, UNIQUE_ID, uniqueId);
        }
        return uniqueId
    }

    fun buildMap(context: Context): HashMap<String, HashMap<String, Long>> {
        val statMap = PrefUtils.getAll(context)
        statMap.remove(UNIQUE_ID)

        val typeMap = HashMap<String, HashMap<String, Long>>()

        for(entry in statMap.entries)
            if(entry.value is Long) {
                val innerMap = HashMap<String, Long>()
                val split = entry.key.split(StatManager.SPLITTER)
                innerMap.put(split[1], entry.value as Long)
                typeMap.put(split[0], innerMap)
            }
        return typeMap
    }

}