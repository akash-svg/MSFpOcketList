package com.msfpocketlist.ui.pocket

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.msfpocketlist.ui.common.BackTopBar
import com.msfpocketlist.ui.common.NavCard
import com.msfpocketlist.ui.common.ObserveMessages
import com.msfpocketlist.ui.common.SearchableList

@Composable
fun ProjectsScreen(onBack: () -> Unit, onOpenProject: (id: Int?, title: String?) -> Unit, viewModel: ProjectsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveMessages(viewModel)
    Scaffold(topBar = { BackTopBar("Projects", onBack, viewModel.missionTitle) }) { padding ->
        SearchableList(state, "Search projects", viewModel::onQueryChange, viewModel::refresh, Modifier.padding(padding)) { project ->
            NavCard(project.title ?: "Untitled project", Icons.Rounded.Folder) { onOpenProject(project.id, project.title) }
        }
    }
}
