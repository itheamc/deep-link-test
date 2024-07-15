package com.itheamc.deeplinktest.ui.screens

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.itheamc.deeplinktest.helpers.FileSaverHelper
import com.itheamc.deeplinktest.helpers.VersionInfoHelper

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    data: Map<String, String>,
    fileSaverHelper: FileSaverHelper,
    createFileLauncher: ActivityResultLauncher<String>
) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }

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
                        modifier = Modifier
                            .weight(3f)
                            .clickable(
                                enabled = it.key.lowercase() == "uri",
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    // Copy the value to clipboard
                                    clipboardManager.setText(
                                        buildAnnotatedString {
                                            append(it.value)
                                        }
                                    )

                                    // SHow toast message
                                    Toast
                                        .makeText(
                                            context,
                                            "Uri copied to clipboard!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            ),
                        text = it.value,
                        color = if (it.key.lowercase() == "uri") Color.Magenta else MaterialTheme.colorScheme.primary,
                        textDecoration = if (it.key.lowercase() == "uri") TextDecoration.Underline else TextDecoration.None
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hey, I'm a deeplink test app!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                    text = "Get a file with browsable link from the below button and try to open with browser",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                ElevatedButton(
                    onClick = {
                        // Check if storage permission is granted
                        if (!fileSaverHelper.hasStoragePermission()) {
                            fileSaverHelper.requestStoragePermission()
                            return@ElevatedButton
                        }
                        // Permission granted, proceed with your operation
                        createFileLauncher.launch("test_html_file.html")
                    },
                ) {
                    Text(text = "Get a Test File")
                }
            }
            // Add text to the bottom of the column
            Text(
                text = buildAnnotatedString {
                    append("Version ")
                    this.append(
                        AnnotatedString(
                            text = VersionInfoHelper.getVersionName(context),
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}