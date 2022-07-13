package de.selfmade4u.glowingp2p

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import de.selfmade4u.glowingp2p.ui.theme.GlowingP2PTheme
import kotlinx.coroutines.launch

// https://foso.github.io/Jetpack-Compose-Playground/
// https://developer.android.com/jetpack/compose/navigation
// https://developer.android.com/jetpack/compose/layouts/material
// https://developer.android.com/topic/architecture
// https://developers.google.com/nearby/connections/overview
// https://google.github.io/accompanist/permissions/

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @Composable
    fun SetupNearby() {
        val multiplePermissionsState = rememberMultiplePermissionsState(
            listOfNotNull(
                Manifest.permission.ACCESS_FINE_LOCATION,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_ADVERTISE else null,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )

        if (multiplePermissionsState.allPermissionsGranted) {
            // If all permissions are granted, then show screen with the feature enabled
            Text("All permissions granted. Thank you!")
        } else {
            Column {
                Text(
                    getTextToShowGivenPermissions(
                        multiplePermissionsState.revokedPermissions,
                        multiplePermissionsState.shouldShowRationale
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Request permissions")
                }
            }
        }
    }

    private fun getTextToShowGivenPermissions(
        permissions: List<PermissionState>,
        shouldShowRationale: Boolean
    ): String {
        val revokedPermissionsSize = permissions.size
        if (revokedPermissionsSize == 0) return ""

        val textToShow = StringBuilder().apply {
            append("The ")
        }

        for (i in permissions.indices) {
            textToShow.append(permissions[i].permission)
            when {
                revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                    textToShow.append(", and ")
                }
                i == revokedPermissionsSize - 1 -> {
                    textToShow.append(" ")
                }
                else -> {
                    textToShow.append(", ")
                }
            }
        }
        textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
        textToShow.append(
            if (shouldShowRationale) {
                " important. Please grant all of them for the app to function properly."
            } else {
                " denied. The app cannot function without them."
            }
        )
        return textToShow.toString()
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
                    NavigationDrawerItem(
                        label = { Text("Welcome") },
                        selected = currentDestination?.hierarchy?.any { it.route == "welcome" } == true,
                        onClick = {
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
                    NavigationDrawerItem(
                        label = { Text("Setup Nearby") },
                        selected = currentDestination?.hierarchy?.any { it.route == "setup-nearby" } == true,
                        onClick = {
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
                    composable("setup-nearby") { SetupNearby() }
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