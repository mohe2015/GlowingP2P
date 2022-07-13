package de.selfmade4u.glowingp2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.selfmade4u.glowingp2p.ui.theme.GlowingP2PTheme
import kotlinx.coroutines.launch

// https://foso.github.io/Jetpack-Compose-Playground/
// https://developer.android.com/jetpack/compose/navigation
// https://developer.android.com/jetpack/compose/layouts/material
// https://developer.android.com/topic/architecture
// https://developers.google.com/nearby/connections/overview

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @Preview
    @Composable
    fun Content() {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val coroutineScope = rememberCoroutineScope()
        GlowingP2PTheme {
            ModalNavigationDrawer(drawerContent = {
                Column {
                    NavigationDrawerItem(label = { Text("Welcome") }, selected = currentDestination?.hierarchy?.any { it.route == "welcome" } == true, onClick = {
                        navController.navigate("welcome") {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    })
                    NavigationDrawerItem(label = { Text("Setup Nearby") }, selected = currentDestination?.hierarchy?.any { it.route == "setup-nearby" } == true, onClick = {
                        navController.navigate("setup-nearby") {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    })
                }
            }, drawerState = drawerState) {
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { Text("welcome") }
                    composable("setup-nearby") { Text("setup-nearby") }
                    /*...*/
                }
            }
            /*
            // A surface container using the 'background' color from the theme
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {


            }*/
        }
    }
}