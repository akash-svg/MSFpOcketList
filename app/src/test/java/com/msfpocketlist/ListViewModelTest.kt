package com.msfpocketlist

import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.util.ConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeConnectivity(connected: Boolean) : ConnectivityObserver {
    override val isConnected = MutableStateFlow(connected)
    override fun isConnectedNow() = isConnected.value
}

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(network: ConnectivityObserver, remote: suspend () -> List<String>? = { listOf("remote") }) =
        ListViewModel(network, remote, cache = { listOf("cache") }, matches = { item, q -> item.contains(q) })

    @Test
    fun offlineShowsCacheThenReloadsFromNetworkWhenConnectionReturns() {
        val network = FakeConnectivity(connected = false)
        val vm = viewModel(network)
        assertFalse(vm.state.value.online)
        assertEquals(listOf("cache"), vm.state.value.items)

        network.isConnected.value = true
        assertTrue(vm.state.value.online)
        assertEquals(listOf("remote"), vm.state.value.items)
    }

    @Test
    fun failedOrEmptyApiFallsBackToCache() {
        assertEquals(listOf("cache"), viewModel(FakeConnectivity(true), remote = { null }).state.value.items)
        assertEquals(listOf("cache"), viewModel(FakeConnectivity(true), remote = { error("timeout") }).state.value.items)
    }

    @Test
    fun queryFiltersLoadedItems() {
        val vm = viewModel(FakeConnectivity(true), remote = { listOf("alpha", "beta") })
        vm.onQueryChange("bet")
        assertEquals(listOf("beta"), vm.state.value.items)
    }
}
