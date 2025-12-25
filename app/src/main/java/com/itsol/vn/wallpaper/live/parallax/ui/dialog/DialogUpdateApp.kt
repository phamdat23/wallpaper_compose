package com.itsol.vn.wallpaper.live.parallax.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.itsol.vn.wallpaper.live.parallax.R


@Composable
fun DialogUpdateApp(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {

    AlertDialog(

        title = {
            Text(text = stringResource(R.string.update_now))
        },
        text = {
            Text(text = stringResource(R.string.update_content))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.update_app))
            }
        }
    )
}
