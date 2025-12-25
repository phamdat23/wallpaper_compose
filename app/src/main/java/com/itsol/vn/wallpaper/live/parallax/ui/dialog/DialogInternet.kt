package com.itsol.vn.wallpaper.live.parallax.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.itsol.vn.wallpaper.live.parallax.R


@Composable
fun DialogInternet(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {

    AlertDialog(

        title = {
            Text(text = stringResource(R.string.internet_disconnected))
        },
        text = {
            Text(text = stringResource(R.string.please_connect_to_the_internet_to_use_the_app))
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
                Text(stringResource(R.string.go_to_setting))
            }
        }
    )
}
