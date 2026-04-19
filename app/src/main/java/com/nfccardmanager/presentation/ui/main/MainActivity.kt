package com.nfccardmanager.presentation.ui.main

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nfccardmanager.R
import com.nfccardmanager.nfc.NfcHelper
import com.nfccardmanager.presentation.ui.Screen
import com.nfccardmanager.presentation.ui.detail.DetailScreen
import com.nfccardmanager.presentation.ui.emulation.EmulationScreen
import com.nfccardmanager.presentation.ui.scan.ScanScreen
import com.nfccardmanager.presentation.ui.theme.NFCCardManagerTheme
import com.nfccardmanager.presentation.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var nfcHelper: NfcHelper

    @Inject
    lateinit var scanViewModel: ScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NFCCardManagerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Main.route,
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
                            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
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

        if (!nfcHelper.isNfcAvailable()) {
            Toast.makeText(this, R.string.error_nfc_not_available, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcHelper.enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        nfcHelper.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        scanViewModel.processNfcIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}