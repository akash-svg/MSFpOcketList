package com.msfpocketlist.ui.sync

import androidx.lifecycle.viewModelScope
import com.msfpocketlist.syncutil.SyncTask
import com.msfpocketlist.syncutil.SyncRepository
import com.msfpocketlist.ui.common.BaseViewModel
import com.msfpocketlist.util.ConnectivityObserver
import com.msfpocketlist.util.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SyncState(
    val running: Set<SyncTask> = emptySet(),
    val lastSync: String? = null,
)

class SyncViewModel(private val repository: SyncRepository, session: Session, network: ConnectivityObserver) : BaseViewModel(network) {
    private val _state = MutableStateFlow(SyncState(lastSync = session.lastSync))
    val state = _state.asStateFlow()

    val tasks get() = repository.tasks

    fun syncAll() = sync(repository.tasks)

    fun sync(task: SyncTask) = sync(listOf(task))

    private fun sync(tasks: List<SyncTask>) {
        if (!online.value) return message("Please connect to the internet")
        viewModelScope.launch {
            tasks.filter { it !in _state.value.running }.forEach { task ->
                _state.update { it.copy(running = it.running + task) }
                val ok = safely { task.run() }
                _state.update { it.copy(running = it.running - task) }
                when (ok) {
                    true -> message("${task.title} synced")
                    false -> message("${task.title}: error occurred")
                    null -> Unit
                }
            }
        }
    }
}
