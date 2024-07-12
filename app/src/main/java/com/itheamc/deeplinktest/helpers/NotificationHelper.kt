package com.itheamc.deeplinktest.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.itheamc.deeplinktest.R

class NotificationHelper private constructor(
    private val activity: ComponentActivity,
    private val onPermissionGranted: (NotificationHelper) -> Unit = {}
) {
    companion object {

        private const val CHANNEL_ID = "document_create_channel_id"
        private const val NOTIFICATION_ID = 1

        @SuppressLint("StaticFieldLeak")
        private var instance: NotificationHelper? = null

        fun getInstance(
            activity: ComponentActivity,
            onPermissionGranted: (NotificationHelper) -> Unit = {}
        ): NotificationHelper {
            if (instance == null) {
                instance = NotificationHelper(activity, onPermissionGranted)
                instance!!.createNotificationChannel()
            }
            return instance!!
        }
    }

    private val requestNotificationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onPermissionGranted.invoke(this)
    }

    /**
     * Method to create notification channel
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = activity.getString(R.string.channel_name)
            val descriptionText = activity.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Method to show notification
     */
    @SuppressLint("MissingPermission")
    fun showNotification(uri: Uri) {
        val pendingIntent = PendingIntent.getActivity(
            activity,
            0,
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/html")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(activity, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Test file saved")
            .setContentText("The test file has been saved successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(activity)) {
            if (!hasNotificationPermission()) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    /**
     * Method to check if has location permission
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

}