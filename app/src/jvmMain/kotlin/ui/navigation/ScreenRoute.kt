package ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenRoute {
    @Serializable
    data object Main : ScreenRoute
    @Serializable
    data object Config : ScreenRoute
    @Serializable
    data object Diff : ScreenRoute
}