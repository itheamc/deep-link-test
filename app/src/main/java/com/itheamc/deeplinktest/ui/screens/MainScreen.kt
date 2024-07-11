package com.itheamc.deeplinktest.ui.screens

import android.Manifest
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itheamc.deeplinktest.helpers.FileSaverHelper

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    data: Map<String, String>,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    pickFileLauncher: ActivityResultLauncher<Intent>
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = if (data.isNotEmpty()) Alignment.Start else Alignment.CenterHorizontally
    ) {
        // Key with bold and value with normal text in a row
        if (data.isNotEmpty()) {
            data.map {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${it.key.uppercase()}:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(3f),
                        text = it.value,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        } else {
            Text(
                text = "Hey, I'm a deeplink test app!",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                text = "Copy browsable link from the below button and try to open with browser",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            // Button to copy the link text
            ElevatedButton(
                onClick = {

                    val granted = FileSaverHelper.hasStoragePermission(context)

                    if (!granted) {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        return@ElevatedButton
                    }

                    // Permission granted, proceed with your operation
                    pickFileLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/html"
                        putExtra(Intent.EXTRA_TITLE, "my_html_file.html")
                    })

                },
            ) {
                Text(text = "Download Test File")
            }
        }
    }
}