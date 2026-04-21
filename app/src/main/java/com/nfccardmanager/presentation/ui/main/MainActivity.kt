package com.nfccardmanager.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    lateinit var nfcIntentHolder: NfcIntentHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let { nfcIntentHolder.setIntent(it) }
        setContent {
            NFCCardManagerTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        nfcIntentHolder.setIntent(intent)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToScan      = { navController.navigate(Screen.Scan.route) },
                onNavigateToDetail    = { cardId -> navController.navigate(Screen.Detail.createRoute(cardId)) },
                onNavigateToEmulation = { navController.navigate(Screen.Emulation.route) }
            )
        }

        composable(Screen.Scan.route) {
            ScanScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route     = Screen.Detail.route,
            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
        ) {
            DetailScreen(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToEmulation = {
                    navController.navigate(Screen.Emulation.route) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }

        composable(Screen.Emulation.route) {
            EmulationScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
