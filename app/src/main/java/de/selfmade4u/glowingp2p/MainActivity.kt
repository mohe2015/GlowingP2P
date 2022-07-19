package de.selfmade4u.glowingp2p

import android.Manifest
import android.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.databinding.Observable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import de.selfmade4u.glowingp2p.ui.theme.GlowingP2PTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


// https://foso.github.io/Jetpack-Compose-Playground/
// https://developer.android.com/jetpack/compose/navigation
// https://developer.android.com/jetpack/compose/layouts/material
// https://developer.android.com/topic/architecture
// https://developers.google.com/nearby/connections/overview
// https://google.github.io/accompanist/permissions/
// https://developer.android.com/jetpack/compose/architecture
// https://developer.android.com/topic/libraries/architecture/viewmodel
// TODO https://developer.android.com/topic/libraries/architecture/lifecycle
// https://developer.android.com/codelabs/jetpack-compose-state?index=..%2F..index#0
// https://developer.android.com/jetpack/compose/libraries#streams

// https://developer.android.com/courses/android-basics-compose/course
// https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0

// maybe use the Room shit for the connections to different devices
// idea would maybe be using the Room api to have a list of "Chats" and
// on connecting a loading indicator and when connected you can open the chat
// then you can write in there and see the chat history.

internal class ReceiveBytesPayloadListener : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        // This always gets the full data of the payload. Is null if it's not a BYTES payload.
        if (payload.type == Payload.Type.BYTES) {
            val receivedBytes = payload.asBytes()
            receivedBytes?.decodeToString()?.let { Log.e("de.selfmade4u.glowingp2p", it) }
        }
    }

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
        // after the call to onPayloadReceived().
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun SetupNearby() {
        var enableDiscovery by remember { mutableStateOf(false) };
        val nearbyDiscoveries =
            if (enableDiscovery) nearbyDiscoveriesState(this@MainActivity) else remember {
                mutableStateOf(
                    listOf()
                )
            };

        val multiplePermissionsState = rememberMultiplePermissionsState(
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            }*/
            listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        )

        if (multiplePermissionsState.allPermissionsGranted) {
            Column {
                // If all permissions are granted, then show screen with the feature enabled
                Text("All permissions granted. Thank you!")
                Button(onClick = { NearbyHelper().startAdvertising(this@MainActivity) }) {
                    Text("Start advertising")
                }
                Button(onClick = { enableDiscovery = true; }) {
                    Text("Start discovery")
                }
                Button(onClick = {
                    val intent = Intent(this@MainActivity, NearbyDiscoverService::class.java);
                    startForegroundService(intent);
                }) {
                    Text("Start service")
                }
                LazyColumn {
                    items(nearbyDiscoveries.value) { message ->
                        Card(
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .clickable {
                                    /*
                                    Nearby
                                        .getConnectionsClient(this@MainActivity)
                                        .requestConnection(
                                            "test",
                                            message,
                                            connectionLifecycleCallback,
                                        )
                                        .addOnSuccessListener { unused: Void? ->
                                            Log.e(
                                                "de.selfmade4u.glowingp2p",
                                                "success, connected"
                                            );
                                        }
                                        .addOnFailureListener { e: Exception? ->
                                            Log.e(
                                                "de.selfmade4u.glowingp2p",
                                                "failure",
                                                e
                                            )
                                        }

                                     */
                                }
                        ) {
                            Row(modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()) {
                                Text(text = message)
                            }
                        }
                    }
                }
            }
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
            Surface {
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
                    NavHost(navController = navController, startDestination = "setup-nearby") {
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
}