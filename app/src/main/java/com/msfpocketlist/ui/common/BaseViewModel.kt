package com.msfpocketlist.ui.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msfpocketlist.util.ConnectivityObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Connectivity and one-off toast messages, shared by every screen's ViewModel. */
abstract class BaseViewModel(network: ConnectivityObserver) : ViewModel() {
    val online = network.isConnected.stateIn(viewModelScope, SharingStarted.Eagerly, network.isConnectedNow())

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    protected fun message(text: String) {
        _messages.trySend(text)
    }

    /** Runs [block], turning failures into a message and a null result. */
    protected suspend fun <T> safely(block: suspend () -> T): T? = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        message(e.message ?: "Something went wrong")
        null
    }
}

/** Shows the ViewModel's one-off messages as toasts. */
@Composable
fun ObserveMessages(viewModel: BaseViewModel) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
}

data class ListState<T>(
    val items: List<T>? = null,
    val query: String = "",
    val loading: Boolean = true,
    val online: Boolean = true,
)

/**
 * Network first when online, Room cache when offline or when the API answers without data.
 * Reloads whenever connectivity changes; [onQueryChange] filters with [matches].
 */
open class ListViewModel<T>(
    network: ConnectivityObserver,
    private val remote: suspend () -> List<T>?,
    private val cache: suspend () -> List<T>,
    private val matches: (T, String) -> Boolean = { _, _ -> true },
) : BaseViewModel(network) {
    private val _state = MutableStateFlow(ListState<T>())
    val state = _state.asStateFlow()
    private var all: List<T>? = null
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            online.collect { isOnline ->
                _state.update { it.copy(online = isOnline) }
                refresh()
            }
        }
    }

    fun onQueryChange(query: String) = _state.update { it.copy(query = query, items = filter(query)) }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            all = (if (online.value) safely { remote() } else null) ?: cache()
            _state.update { it.copy(loading = false, items = filter(it.query)) }
        }
    }

    private fun filter(query: String) = all?.filter { query.isBlank() || matches(it, query.trim()) }
}
