package com.nfccardmanager.presentation.ui.main

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nfccardmanager.presentation.ui.Screen
import com.nfccardmanager.presentation.ui.detail.DetailScreen
import com.nfccardmanager.presentation.ui.emulation.EmulationScreen
import com.nfccardmanager.presentation.ui.scan.ScanScreen
import com.nfccardmanager.presentation.ui.theme.NFCCardManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val prefs = getSharedPreferences("hce_prefs", Context.MODE_PRIVATE)
        val hasSelectedCard = prefs.getString("uid", null) != null
        val startRoute = if (hasSelectedCard) Screen.Emulation.route else Screen.Main.route

        setContent {
            NFCCardManagerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                Scaffold { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = startRoute,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(
                                onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                                onNavigateToDetail = { cardId ->
                                    navController.navigate(Screen.Detail.createRoute(cardId))
                                },
                                onNavigateToEmulation = { navController.navigate(Screen.Emulation.route) }
                            )
                        }
                        composable(Screen.Scan.route) {
                            ScanScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(navArgument("cardId") { 
                                type = NavType.LongType
                                defaultValue = 0L
                            })
                        ) {
                            DetailScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToEmulation = {
                                    navController.navigate(Screen.Emulation.route)
                                }
                            )
                        }
                        composable(Screen.Emulation.route) {
                            EmulationScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcDispatch()
    }

    private fun enableNfcDispatch() {
        try {
            nfcAdapter?.let { adapter ->
                if (adapter.isEnabled) {
                    val intent = Intent(this, javaClass).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                    val pendingIntent = PendingIntent.getActivity(this, 0, intent, flags)
                    adapter.enableForegroundDispatch(this, pendingIntent, null, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableNfcDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}