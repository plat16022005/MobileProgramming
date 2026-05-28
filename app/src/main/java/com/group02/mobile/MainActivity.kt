package com.group02.mobile

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
import com.group02.mobile.navigation.AuthNavGraph
import com.group02.mobile.ui.theme.InkBlack
import com.group02.mobile.ui.theme.SakumiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.group02.mobile.utils.TtsManager.init(this)
        enableEdgeToEdge()
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