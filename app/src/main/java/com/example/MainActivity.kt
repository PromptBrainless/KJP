package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.DeeskalationRepository
import com.example.ui.DeeskalationApp
import com.example.ui.DeeskalationViewModel
import com.example.ui.DeeskalationViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Local Database & Repository
        val database = AppDatabase.getDatabase(this)
        val repository = DeeskalationRepository(database.dao())

        // 2. Instantiate ViewModel using Factory directly
        val viewModel: DeeskalationViewModel by viewModels {
            DeeskalationViewModelFactory(repository)
        }

        // 3. Render modern Jetpack Compose interface
        setContent {
            MyApplicationTheme {
                DeeskalationApp(viewModel)
            }
        }
    }
}
