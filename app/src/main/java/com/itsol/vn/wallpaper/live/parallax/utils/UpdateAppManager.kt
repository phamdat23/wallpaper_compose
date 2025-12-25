package com.itsol.vn.wallpaper.live.parallax.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

object UpdateAppManager {
    fun initAppManager(context: Context, launcher: ActivityResultLauncher<IntentSenderRequest>, onRequest: (Boolean)->Unit) {
//        val appUpdateManager = AppUpdateManagerFactory.create(context)
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            CoroutineScope(Dispatchers.Main).launch {
//                Log.e("AAAAAAAAA", "initAppManager:${appUpdateInfo.availableVersionCode()} ", )
//            }
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
//                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                // Phiên bản mới có sẵn, hiển thị hộp thoại cập nhật
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    launcher,
//                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
//                )
//                onRequest.invoke(true)
//            } else {
//                onRequest.invoke(false)
//            }
//        }.addOnFailureListener {
//            Log.e("AAAAAAAA", "initAppManager:addOnFailureListener: ${it.message} ", )
//            onRequest.invoke(false)
//        }
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            onRequest.invoke(updateAvailable)
        }.addOnFailureListener {
            Log.e("AAAAAAAA", "initAppManager:addOnFailureListener: ${it.message} ", )
            onRequest.invoke(false) // Không thể lấy thông tin cập nhật
        }
    }



}