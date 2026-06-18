package com.daniel.boardgametracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.daniel.boardgametracker.data.prefs.LastSessionPrefs
import com.daniel.boardgametracker.data.prefs.OathswornPrefs
import com.daniel.boardgametracker.data.repository.SessionRepository
import com.daniel.boardgametracker.ui.detail.SessionDetailScreen
import com.daniel.boardgametracker.ui.helper.HelperScreen
import com.daniel.boardgametracker.ui.helper.OathswornHelperScreen
import com.daniel.boardgametracker.ui.history.HistoryScreen
import com.daniel.boardgametracker.ui.home.HomeScreen
import com.daniel.boardgametracker.ui.newsession.NewSessionScreen
import com.daniel.boardgametracker.ui.stats.StatsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Helper : Screen("helper")
    object OathswornHelper : Screen("helper/oathsworn")
    object NewSession : Screen("new_session/{gameType}") {
        fun route(gameType: String) = "new_session/$gameType"
    }
    object EditSession : Screen("edit_session/{gameType}/{sessionId}") {
        fun route(gameType: String, sessionId: Long) = "edit_session/$gameType/$sessionId"
    }
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun route(sessionId: Long) = "session_detail/$sessionId"
    }
}

private data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
    BottomNavItem(Screen.History, "History", Icons.Default.History),
    BottomNavItem(Screen.Stats, "Stats", Icons.Default.BarChart),
    BottomNavItem(Screen.Helper, "Helper", Icons.Default.Build)
)

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: SessionRepository,
    prefs: LastSessionPrefs,
    oathswornPrefs: OathswornPrefs
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route, Screen.History.route, Screen.Stats.route, Screen.Helper.route
    )

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController, repository = repository)
            }
            composable(Screen.History.route) {
                HistoryScreen(navController = navController, repository = repository)
            }
            composable(Screen.Stats.route) {
                StatsScreen(repository = repository)
            }
            composable(Screen.Helper.route) {
                HelperScreen(navController = navController)
            }
            composable(Screen.OathswornHelper.route) {
                OathswornHelperScreen(navController = navController, prefs = oathswornPrefs)
            }
            composable(
                route = Screen.NewSession.route,
                arguments = listOf(navArgument("gameType") { type = NavType.StringType })
            ) { backStack ->
                val gameType = backStack.arguments?.getString("gameType") ?: return@composable
                NewSessionScreen(
                    gameType = gameType,
                    editSessionId = null,
                    navController = navController,
                    repository = repository,
                    prefs = prefs
                )
            }
            composable(
                route = Screen.EditSession.route,
                arguments = listOf(
                    navArgument("gameType") { type = NavType.StringType },
                    navArgument("sessionId") { type = NavType.LongType }
                )
            ) { backStack ->
                val gameType = backStack.arguments?.getString("gameType") ?: return@composable
                val sessionId = backStack.arguments?.getLong("sessionId") ?: return@composable
                NewSessionScreen(
                    gameType = gameType,
                    editSessionId = sessionId,
                    navController = navController,
                    repository = repository,
                    prefs = prefs
                )
            }
            composable(
                route = Screen.SessionDetail.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStack ->
                val sessionId = backStack.arguments?.getLong("sessionId") ?: return@composable
                SessionDetailScreen(
                    sessionId = sessionId,
                    navController = navController,
                    repository = repository
                )
            }
        }
    }
}
