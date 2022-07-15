package de.selfmade4u.glowingp2p

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.databinding.ObservableArrayMap
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

// https://scalereal.com/android/2021/08/17/observing-live-connectivity-status-in-jetpack-compose-way.html

/**
 * Network Utility to observe availability or unavailability of Internet connection
 */
@ExperimentalCoroutinesApi
fun Context.observeNearbyDiscoveriesAsFlow() = callbackFlow {

    val callback = NearbyDiscoveriesCallback({ discoveryState -> trySend(discoveryState) }, { discoveryState -> trySend(discoveryState) })

    Log.e("de.selfmade4u.glowingp2p", "start discovery")
    val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
    Nearby.getConnectionsClient(this@observeNearbyDiscoveriesAsFlow)
        .startDiscovery("de.selfmade4u.glowingp2p", callback, discoveryOptions)
        .addOnSuccessListener { unused: Void? -> Log.e("de.selfmade4u.glowingp2p", "success"); }
        .addOnFailureListener { e: Exception? ->
            Log.e(
                "de.selfmade4u.glowingp2p",
                "failure",
                e
            )
        }

    // Set current state
    trySend("")

    // Remove callback when not used
    awaitClose {
        // Remove listeners
        //connectivityManager.unregisterNetworkCallback(callback)
    }
}

fun NearbyDiscoveriesCallback(addCallback: (String) -> Unit, removeCallback: (String) -> Unit): EndpointDiscoveryCallback {
    return object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.e("de.selfmade4u.glowingp2p", "endpoint found");
            addCallback(endpointId)
            // An endpoint was found. We request a connection to it.
            /*Nearby.getConnectionsClient(this@MainActivity)
                .requestConnection("test", endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { unused: Void? ->
                    Log.e(
                        "de.selfmade4u.glowingp2p",
                        "success"
                    );
                }
                .addOnFailureListener { e: Exception? ->
                    Log.e(
                        "de.selfmade4u.glowingp2p",
                        "failure",
                        e
                    )
                }*/
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.e("de.selfmade4u.glowingp2p", "endpoint lost");
            removeCallback(endpointId)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun nearbyDiscoveriesState(): State<String> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = "") {
        // In a coroutine, can make suspend calls
        context.observeNearbyDiscoveriesAsFlow().collect { value = it }
    }
}
