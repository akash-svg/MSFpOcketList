package com.msfpocketlist.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Whether the device can actually reach the internet. An interface so tests can drive it with a fake. */
interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    fun isConnectedNow(): Boolean
}

/**
 * Counts a network only once Android has *validated* it, so Wi-Fi without internet
 * (captive portals, dead hotspots) reads as offline.
 */
class AndroidConnectivityObserver(context: Context) : ConnectivityObserver {
    private val cm = context.getSystemService(ConnectivityManager::class.java)

    override fun isConnectedNow(): Boolean = cm.getNetworkCapabilities(cm.activeNetwork)?.hasInternet() == true

    override val isConnected: Flow<Boolean> = callbackFlow {
        val validated = mutableSetOf<Network>()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                if (capabilities.hasInternet()) validated += network else validated -= network
                trySend(validated.isNotEmpty())
            }

            override fun onLost(network: Network) {
                validated -= network
                trySend(validated.isNotEmpty())
            }
        }
        trySend(isConnectedNow())
        val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        cm.registerNetworkCallback(request, callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}

private fun NetworkCapabilities.hasInternet() =
    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
