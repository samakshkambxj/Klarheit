package com.klarheit.audio.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.klarheit.audio.R
import com.klarheit.audio.ui.components.UiDimens

private const val PROFILE_URL = "https://github.com/samakshkambxj"
private const val SOURCE_URL = "https://github.com/samakshkambxj/Klarheit"

@Composable
fun AboutDialog(
    appVersionName: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.about_title))
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(UiDimens.AppIcon)
                                .clip(RoundedCornerShape(UiDimens.IconMedium))
                                .background(colorResource(R.color.ic_launcher_background)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Spacer(modifier = Modifier.width(UiDimens.Large))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = appVersionName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(UiDimens.Standard))
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(UiDimens.Standard))
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { openUrl(PROFILE_URL) }
                            .padding(vertical = UiDimens.XSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_samakshkambxj),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .size(UiDimens.AppIcon)
                                .clip(CircleShape),
                    )
                    Spacer(modifier = Modifier.width(UiDimens.Large))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.about_developer),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "samakshkambxj",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { openUrl(SOURCE_URL) }
                            .padding(vertical = UiDimens.XSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.about_source),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "github.com/samakshkambxj/Klarheit",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {},
    )
}
