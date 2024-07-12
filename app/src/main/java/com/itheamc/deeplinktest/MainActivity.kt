package com.itheamc.deeplinktest

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.itheamc.deeplinktest.helpers.FileSaverHelper
import com.itheamc.deeplinktest.helpers.NotificationHelper
import com.itheamc.deeplinktest.ui.screens.MainScreen
import com.itheamc.deeplinktest.ui.theme.DeeplinkTestTheme


class MainActivity : ComponentActivity() {

    private lateinit var fileSaverHelper: FileSaverHelper
    private lateinit var notificationHelper: NotificationHelper

    private var _uri: Uri? = null

    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/html")
    ) { uri ->

        if (uri == null) return@registerForActivityResult

        fileSaverHelper.save(uri) {
            this._uri = uri
            notificationHelper.showNotification(uri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileSaverHelper = FileSaverHelper.getInstance(this) {
            createFileLauncher.launch("test_html_file.html")
        }

        notificationHelper = NotificationHelper.getInstance(this) { helper ->
            _uri?.let {
                helper.showNotification(it)
            }
        }

        enableEdgeToEdge()

        setContent {
            DeeplinkTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        data = parseUri(intent.data),
                        modifier = Modifier.padding(innerPadding),
                        fileSaverHelper = fileSaverHelper,
                        createFileLauncher = createFileLauncher
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
                put("uri", it.toString())
                put("scheme", it.scheme ?: "")
                put("host", it.host ?: "")
                put("path", it.path ?: "")
                put("query", it.query ?: "")
            }
        } ?: emptyMap()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DeeplinkTestTheme {
        Text(text = "Hello, Android!")
    }
}