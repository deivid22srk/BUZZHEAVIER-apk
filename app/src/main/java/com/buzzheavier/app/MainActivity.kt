package com.buzzheavier.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.buzzheavier.app.data.repository.BuzzHeavierRepository
import com.buzzheavier.app.ui.navigation.BuzzHeavierNavigation
import com.buzzheavier.app.ui.theme.BuzzHeavierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val repository = BuzzHeavierRepository(applicationContext)
        
        setContent {
            BuzzHeavierTheme {
                BuzzHeavierNavigation(repository = repository)
            }
        }
    }
}
