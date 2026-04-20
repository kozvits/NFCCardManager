package com.nfccardmanager.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nfccardmanager.R
import com.nfccardmanager.nfc.NfcHelper
import com.nfccardmanager.nfc.NfcIntentHolder
import com.nfccardmanager.presentation.ui.Screen
import com.nfccardmanager.presentation.ui.detail.DetailScreen
import com.nfccardmanager.presentation.ui.emulation.EmulationScreen
import com.nfccardmanager.presentation.ui.scan.ScanScreen
import com.nfccardmanager.presentation.ui.theme.NFCCardManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var nfcHelper: NfcHelper

    @Inject
    lateinit var nfcIntentHolder: NfcIntentHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NFCCardManagerTheme {
                try {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()

                    Scaffold { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Main.route,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(Screen.Main.route) {
                                try {
                                    MainScreen(
                                        onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                                        onNavigateToDetail = { cardId ->
                                            navController.navigate(Screen.Detail.createRoute(cardId))
                                        },
                                        onNavigateToEmulation = { navController.navigate(Screen.Emulation.route) }
                                    )
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error in MainScreen", e)
                                    Text("Error loading main screen")
                                }
                            }
                            composable(Screen.Scan.route) {
                                try {
                                    ScanScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error in ScanScreen", e)
                                    Text("Error loading scan screen")
                                }
                            }
                            composable(
                                route = Screen.Detail.route,
                                arguments = listOf(navArgument("cardId") { 
                                    type = NavType.LongType
                                    defaultValue = 0L
                                })
                            ) {
                                try {
                                    DetailScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToEmulation = {
                                            navController.navigate(Screen.Emulation.route)
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error in DetailScreen", e)
                                    Text("Error loading detail screen")
                                }
                            }
                            composable(Screen.Emulation.route) {
                                try {
                                    EmulationScreen(
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error in EmulationScreen", e)
                                    Text("Error loading emulation screen")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error setting up navigation", e)
                    Text("Error initializing app: ${e.message}")
                }
            }
        }

        try {
            if (!nfcHelper.isNfcAvailable()) {
                Toast.makeText(this, R.string.error_nfc_not_available, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking NFC", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            nfcHelper.enableForegroundDispatch(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onResume", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            nfcHelper.disableForegroundDispatch(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onPause", e)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        nfcIntentHolder.setIntent(intent)
    }
}