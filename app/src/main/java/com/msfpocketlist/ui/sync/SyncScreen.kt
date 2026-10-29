package com.msfpocketlist.ui.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.msfpocketlist.syncutil.SyncTask
import com.msfpocketlist.ui.common.BackTopBar
import com.msfpocketlist.ui.common.ObserveMessages
import com.msfpocketlist.ui.common.ConnectivityBanner

@Composable
fun SyncScreen(onBack: () -> Unit, viewModel: SyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val online by viewModel.online.collectAsStateWithLifecycle()
    ObserveMessages(viewModel)

    Scaffold(topBar = { BackTopBar("Sync data", onBack) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            ConnectivityBanner(online)
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Download the latest data so the directory works without a connection.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.lastSync != null) {
                    Text(
                        "Last automatic sync: ${state.lastSync}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                viewModel.tasks.forEach { task ->
                    SyncRow(task, syncing = task in state.running) { viewModel.sync(task) }
                }
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.syncAll() },
                    enabled = state.running.isEmpty(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(Icons.Rounded.Sync, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sync everything", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SyncRow(task: SyncTask, syncing: Boolean, onSync: () -> Unit) {
    Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(task.icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (syncing) {
                CircularProgressIndicator(Modifier.padding(horizontal = 20.dp).size(24.dp), strokeWidth = 2.dp)
            } else {
                FilledTonalButton(onClick = onSync) { Text("Sync") }
            }
        }
    }
}
