package com.example.data.appupdate

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val changelog: String
)

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Checking : UpdateState()
    data object UpToDate : UpdateState()
    data class Available(val info: UpdateInfo) : UpdateState()
    data class Error(val message: String) : UpdateState()
    data object Downloading : UpdateState()
}

/**
 * Checks the deployed web app for a newer APK release. The CI pipeline writes
 * web/latest.json (versionCode, versionName, apkUrl, changelog) next to the
 * APK on every build, so any install older than the latest release can
 * upgrade itself in-place via DownloadManager + package installer.
 */
object AppUpdateChecker {

    // Keep in sync with the Render deployment in render.yaml.
    private const val MANIFEST_URL = "https://speakora-tjjz.onrender.com/latest.json"

    @Suppress("unused")
    private val CURRENT_VERSION_CODE = BuildConfig.VERSION_CODE

    suspend fun check(): UpdateState = withContext(Dispatchers.IO) {
        try {
            val connection = URL(MANIFEST_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            try {
                if (connection.responseCode != 200) {
                    return@withContext UpdateState.Error("Update server returned ${connection.responseCode}")
                }
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                val remoteCode = json.getInt("versionCode")
                val remoteName = json.optString("versionName", "unknown")
                val apkUrl = json.optString("apkUrl", "")
                val changelog = json.optString("changelog", "Bug fixes and improvements")
                if (remoteCode > BuildConfig.VERSION_CODE && apkUrl.isNotBlank()) {
                    UpdateState.Available(UpdateInfo(remoteCode, remoteName, apkUrl, changelog))
                } else {
                    UpdateState.UpToDate
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            UpdateState.Error(e.message ?: "Network error")
        }
    }
}
