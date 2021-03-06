package de.selfmade4u.glowingp2p

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.random.Random

class NearbyHelper {

    fun startDiscovery(context: Context) {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery("de.selfmade4u.glowingp2p", endpointDiscoveryCallback(context), discoveryOptions)
            .addOnSuccessListener { unused: Void? -> Log.e("de.selfmade4u.glowingp2p", "success"); }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "de.selfmade4u.glowingp2p",
                    "failure",
                    e
                )
            }

    }

    private fun endpointDiscoveryCallback(context: Context): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Log.e("de.selfmade4u.glowingp2p", "endpoint found");

                AppDatabase.getInstance(context).activeEndpointDao().insert(ActiveEndpoint(endpointId));

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
            }
        }
    }

    fun startAdvertising(activity: Context) {
        Log.e("de.selfmade4u.glowingp2p", "start advertising");
        val advertisingOptions: AdvertisingOptions = AdvertisingOptions.Builder().setStrategy(
            Strategy.P2P_CLUSTER
        ).build()
        Nearby.getConnectionsClient(activity)
            .startAdvertising(
                "test", "de.selfmade4u.glowingp2p", connectionLifecycleCallback(activity), advertisingOptions
            )
            .addOnSuccessListener { unused: Void? -> Log.e("de.selfmade4u.glowingp2p", "success"); }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "de.selfmade4u.glowingp2p",
                    "failure",
                    e
                )
            }
    }

    private fun connectionLifecycleCallback(activity: Context): ConnectionLifecycleCallback {
        return object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                Log.e("de.selfmade4u.glowingp2p", "onConnectionInitiated");
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(activity)
                    .acceptConnection(endpointId, ReceiveBytesPayloadListener())
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                Log.e("de.selfmade4u.glowingp2p", "onConnectionResult ${result.status}");
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Log.e(
                            "de.selfmade4u.glowingp2p",
                            "success CONNECTED"
                        );
                        Nearby.getConnectionsClient(activity)
                            .sendPayload(endpointId, Payload.fromBytes("Hello".encodeToByteArray()))
                            .addOnSuccessListener { unused: Void? ->
                                Log.e(
                                    "de.selfmade4u.glowingp2p",
                                    "success sending"
                                );
                            }
                            .addOnFailureListener { e: Exception? ->
                                Log.e(
                                    "de.selfmade4u.glowingp2p",
                                    "failure",
                                    e
                                )
                            }
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {}
                    ConnectionsStatusCodes.STATUS_ERROR -> {}
                    else -> {}
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.e("de.selfmade4u.glowingp2p", "disconnected");
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }
    }
}