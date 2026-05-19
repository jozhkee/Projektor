package com.example.eventappgroup17

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eventappgroup17.navigation.AppNavigation
import com.example.eventappgroup17.ui.theme.EventAppGroup17Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventAppGroup17Theme {
                AppNavigation()
            }
        }
    }
}
