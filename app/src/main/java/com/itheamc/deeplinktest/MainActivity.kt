package com.itheamc.deeplinktest

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.itheamc.deeplinktest.helpers.FileSaverHelper
import com.itheamc.deeplinktest.ui.screens.MainScreen
import com.itheamc.deeplinktest.ui.theme.DeeplinkTestTheme


class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle the permission grant result here
        if (isGranted) {
            // Permission granted, proceed with your operation
            pickFileLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/html"
                putExtra(Intent.EXTRA_TITLE, "my_html_file.html")
            })
        }
    }


    @SuppressLint("MissingPermission")
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult

            FileSaverHelper.save(uri, this) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "text/html")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notificationId = 1
                val builder = NotificationCompat.Builder(
                    this,
                    "your_channel_id"
                )
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your notification icon
                    .setContentTitle("File Saved")
                    .setContentText("The HTML file has been saved successfully.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // Set the pending intent to open the file
                    .setAutoCancel(true) // Automatically dismiss the notification when clicked

                with(NotificationManagerCompat.from(this)) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request permission if not granted
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                1
                            )
                        }
                    } else {
                        notify(notificationId, builder.build())
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeeplinkTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        data = parseUri(intent.data),
                        modifier = Modifier.padding(innerPadding),
                        requestPermissionLauncher = requestPermissionLauncher,
                        pickFileLauncher = pickFileLauncher
                    )
                }
            }
        }
    }

    /**
     * Method to parse uri for deeplink
     */
    private fun parseUri(uri: Uri?): Map<String, String> {
        return uri?.let {
            buildMap {
                put("scheme", it.scheme ?: "")
                put("host", it.host ?: "")
                put("path", it.path ?: "")
                put("query", it.query ?: "")
                put("fragment", it.fragment ?: "")
            }
        } ?: emptyMap()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DeeplinkTestTheme {

    }
}