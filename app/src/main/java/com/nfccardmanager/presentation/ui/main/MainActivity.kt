package com.nfccardmanager.presentation.ui.main

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.nfccardmanager.presentation.ui.theme.NFCCardManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            NFCCardManagerTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "NFC Card Manager\n\nTap + to scan card",
                        color = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
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
