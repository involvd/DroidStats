package com.droidstats.sdk

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.droidstats.sdk.utils.SdkUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object SubmissionManager {

    private val TAG = SubmissionManager::class.java.simpleName
    private val BASE_API_URL = " https://us-central1-emailstats-2b046.cloudfunctions.net"

    fun uploadStats(context: Context) {
        if(!hasNetworkConnection(context)) {
            Log.d(TAG, "No network connected, cannot upload..")
            return
        }
        Observable.create<Boolean> {
            val serverURL: String = BASE_API_URL + "/uploadStats"
            val url = URL(serverURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 300000
            connection.connectTimeout = 300000
            connection.doOutput = true

            var appId = SdkUtils.getAppIdForPackage(context, context.packageName)
            if (appId != context.packageName)
                appId = context.packageName + ":" + appId
            val apiKey = SdkUtils.getApiKeyForPackage(context, context.packageName)
            val sigHash = SdkUtils.getCertificateSHA1Fingerprint(context, context.packageName)
            val uniqueId = SdkUtils.getUniqueId(context)

            connection.setRequestProperty("app_id", appId)
            connection.setRequestProperty("api_key", apiKey)
            connection.setRequestProperty("hash", sigHash)
            connection.setRequestProperty("uuid", uniqueId)
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-Type", "application/json")

            val map = SdkUtils.buildMap(context)
            val json = JSONObject(map)
            val postData: ByteArray = json.toString().toByteArray(StandardCharsets.UTF_8)

            connection.setRequestProperty("Content-length", postData.size.toString())

            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            Log.d(TAG, "Error code: " + connection.responseCode)
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                val reader: BufferedReader = BufferedReader(InputStreamReader(connection.errorStream))
                val output: String = reader.readLine()

                Log.d(TAG,"There was error while connecting the chat $output")
            }

        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            Log.d(TAG, "Upload success!")
        }, {
            it.printStackTrace()
        })
    }

    fun hasNetworkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

}