package com.cascade.whiteboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cascade.whiteboard.screens.*
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme

enum class Screens(val route: String) {
    HOME("home"),
    PERSONA("personas"),
    LOCATION("location"),
    EPISODE("episode")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeChallengeTheme {
                ActivityScaffold()
            }
        }
    }
}

@Composable
fun ActivityScaffold() {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Screens.HOME.route
        ) {
            composable(Screens.HOME.route) {
                HomeScreen(
                    onCharactersClick = { navController.navigate(Screens.PERSONA.route) },
                    onLocationsClick = { navController.navigate(Screens.LOCATION.route) },
                    onEpisodesClick = { navController.navigate(Screens.EPISODE.route) }
                )
            }
            composable(Screens.PERSONA.route) {
                PersonasScreen(
                    model = viewModel(),
                    onBack = { navController.popBackStack() },
                    onPersonaClick = {
                        navController.navigate("${Screens.PERSONA.route}?url=$it")
                    }
                )
            }
            composable(Screens.LOCATION.route) {
                LocationsScreen(
                    model = viewModel(),
                    onBack = { navController.popBackStack() },
                    onLocationClick = {
                        navController.navigate("${Screens.LOCATION.route}?url=$it")
                    }
                )
            }
            composable(Screens.EPISODE.route) {
                EpisodesScreen(
                    model = viewModel(),
                    onBack = { navController.popBackStack() },
                    onEpisodeClick = {
                        navController.navigate("${Screens.EPISODE.route}?url=$it")
                    }
                )
            }
            composable(
                "${Screens.PERSONA.route}?url={url}",
                arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url")

                if (url == null) {
                    navController.popBackStack()
                    return@composable
                }

                PersonaScreen(
                    personaUrl = url,
                    onBack = { navController.popBackStack() },
                    onLocationClick = {
                        navController.navigate("${Screens.LOCATION.route}?url=$it")
                    },
                    model = viewModel()
                )
            }
            composable(
                "${Screens.LOCATION.route}?url={url}",
                arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url")

                if (url == null) {
                    navController.popBackStack()
                    return@composable
                }

                LocationScreen(
                    locationUrl = url,
                    onBack = { navController.popBackStack() },
                    onResidentClick = {
                        navController.navigate("${Screens.PERSONA.route}?url=$it")
                    },
                    model = viewModel()
                )
            }
            composable(
                "${Screens.EPISODE.route}?url={url}",
                arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url")

                if (url == null) {
                    navController.popBackStack()
                    return@composable
                }

                EpisodeScreen(
                    episodeUrl = url,
                    onBack = { navController.popBackStack() },
                    onPersonaClick = {
                        navController.navigate("${Screens.PERSONA.route}?url=$it")
                    },
                    model = viewModel()
                )
            }
        }
    }
}
