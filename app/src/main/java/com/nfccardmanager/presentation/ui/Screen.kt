package com.nfccardmanager.presentation.ui

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Scan : Screen("scan")
    object Detail : Screen("detail/{cardId}") {
        fun createRoute(cardId: Long) = "detail/$cardId"
    }
    object Emulation : Screen("emulation")
}