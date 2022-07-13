package de.selfmade4u.glowingp2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.selfmade4u.glowingp2p.ui.theme.GlowingP2PTheme

// https://foso.github.io/Jetpack-Compose-Playground/
// https://developer.android.com/jetpack/compose/navigation
// https://developer.android.com/jetpack/compose/layouts/material
// https://developer.android.com/topic/architecture

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
        GlowingP2PTheme {
            ModalNavigationDrawer(drawerContent = {
                LazyColumn {
                    items(50) {
                        ListItem(
                            headlineText = { Text("Item $it") },
                            
                        )
                    }
                }

            }, drawerState = drawerState) {
                NavHost(navController = navController, startDestination = "profile") {
                    composable("profile") { Text("profile") }
                    composable("friendslist") { Text("friendslist") }
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