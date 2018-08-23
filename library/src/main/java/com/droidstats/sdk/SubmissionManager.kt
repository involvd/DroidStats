package com.droidstats.sdk

import android.content.Context
import android.util.Log
import com.droidstats.sdk.utils.PrefUtils
import com.droidstats.sdk.utils.SdkUtils
import com.firebase.jobdispatcher.*
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.HOURS



internal object SubmissionManager {

    private val TAG = SubmissionManager::class.java.simpleName
    private val BASE_API_URL = "https://us-central1-emailstats-2b046.cloudfunctions.net"

    internal fun uploadStats(context: Context) : Boolean {
        if(!SdkUtils.getCollectionEnabled(context))
            return false
        val serverURL: String = BASE_API_URL + "/uploadStats"
        val url = URL(serverURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 300000
        connection.connectTimeout = 300000
        connection.doOutput = true

        buildHeaders(context, connection)

        val map = SdkUtils.buildMap(context)
        val json = JSONObject(map)
        val postData: ByteArray = json.toString().toByteArray(Charsets.UTF_8)

        connection.setRequestProperty("Content-length", postData.size.toString())

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(postData)
        outputStream.flush()

        Log.d(TAG, "Response code: " + connection.responseCode)
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            val reader: BufferedReader = BufferedReader(InputStreamReader(connection.errorStream))
            val output: String = reader.readLine()

            Log.d(TAG,"Api threw error $output")
            return false
        }
        return true
    }

    private fun buildHeaders(context: Context, connection: HttpURLConnection) {
        val metadata = SdkUtils.getMetaDataKeyForPackages(context);
        var appId = metadata.get(SdkUtils.META_ID)
        if (appId != context.packageName)
            appId = context.packageName + ":" + appId
        val apiKey = metadata.get(SdkUtils.META_KEY)
        val sigHash = SdkUtils.getCertificateSHA1Fingerprint(context, context.packageName)
        val uniqueId = SdkUtils.getUniqueId(context)

        connection.setRequestProperty("app_id", appId)
        connection.setRequestProperty("api_key", apiKey)
        connection.setRequestProperty("hash", sigHash)
        connection.setRequestProperty("uuid", uniqueId)
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-Type", "application/json")
    }

    val windowStart = TimeUnit.HOURS.toSeconds(23).toInt()
    val toleranceInterval = TimeUnit.HOURS.toSeconds(1).toInt()

    internal fun scheduleUpload(context: Context) {
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context));
        val myJob = dispatcher.newJobBuilder()
                .setService(UploadService::class.java)
                .setTag(UploadService::class.java.simpleName)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(windowStart, windowStart + toleranceInterval))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    internal fun testUpload(context: Context) {
        launch {
            uploadStats(context)
            PrefUtils.clearAll(context) //Clears regardless of success
        }
    }

}