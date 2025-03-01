package ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import ui.screen.config.ConfigScreen
import ui.screen.diff.DiffScreen
import ui.screen.main.MainScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = ScreenRoute.Main
    ) {
        composable<ScreenRoute.Main> {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                scope = scope,
            )
        }

        dialog<ScreenRoute.Config>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            ConfigScreen(
                navController = navController,
                scope = scope,
            )
        }

        dialog<ScreenRoute.Diff>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            DiffScreen(
                navController = navController,
            )
        }
    }
}
