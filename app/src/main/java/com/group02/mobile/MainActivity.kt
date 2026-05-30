package com.group02.mobile

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.group02.mobile.navigation.AuthNavGraph
import com.group02.mobile.ui.theme.InkBlack
import com.group02.mobile.ui.theme.SakumiTheme
import com.group02.mobile.viewmodel.NotificationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.group02.mobile.utils.TtsManager.init(this)
        enableEdgeToEdge()
        val sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPrefs.edit().putLong("last_time_open_app", System.currentTimeMillis()).apply()
//        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.group02.mobile.utils.StudyPlanWorker>(
//            15, java.util.concurrent.TimeUnit.MINUTES // Android quy định tối thiểu là 15 phút quét 1 lần để bảo vệ pin
//        ).build()
//
//        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "NihonlishAutoRemind",
//            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
//            workRequest
//        )
        if (FirebaseAuth.getInstance().currentUser != null) {
            val notificationViewModel = NotificationViewModel()
            notificationViewModel.asyncStateSystem()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val checkPermission = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }
        setContent {
            SakumiTheme {
                val focusManager = LocalFocusManager.current
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        },
                    color = InkBlack
                ) {
                    AuthNavGraph()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        com.group02.mobile.utils.TtsManager.shutdown()
    }
}