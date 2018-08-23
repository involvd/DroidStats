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
    internal fun getMetaDataKeyForPackages(context: Context): HashMap<String, String> {
        val i = Intent("android.intent.action.MAIN")
        i.addCategory("android.intent.category.LAUNCHER")
        i.`package` = context.packageName
        val list = context.packageManager.queryIntentActivities(i, PackageManager.GET_META_DATA)
        val appMap = HashMap<String, String>()
        if (list?.isNotEmpty() == true) {
            for (packageInfo in list) {
                if (packageInfo?.activityInfo?.applicationInfo?.metaData?.containsKey(META_KEY) == true)
                    appMap.put(META_KEY, packageInfo.activityInfo.applicationInfo.metaData.getString(META_KEY))
                if (packageInfo?.activityInfo?.applicationInfo?.metaData?.containsKey(META_ID) == true)
                    appMap.put(META_ID, packageInfo.activityInfo.applicationInfo.metaData.getString(META_ID))
            }
        }
        return appMap;
    }

    internal fun getCertificateSHA1Fingerprint(context: Context, packageName: String): String? {
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

    internal fun getUniqueId(context: Context): String {
        var uniqueId = PrefUtils.readStringPref(context, UNIQUE_ID)
        if(TextUtils.isEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString()
            PrefUtils.writeStringPref(context, UNIQUE_ID, uniqueId);
        }
        return uniqueId
    }

    internal fun buildMap(context: Context): HashMap<String, HashMap<String, Long>> {
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