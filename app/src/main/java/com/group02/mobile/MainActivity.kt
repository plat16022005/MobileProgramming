package com.group02.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.group02.mobile.navigation.AuthNavGraph
import com.group02.mobile.ui.theme.InkBlack
import com.group02.mobile.ui.theme.SakumiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SakumiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = InkBlack
                ) {
                    AuthNavGraph()
                }
            }
        }
    }
}