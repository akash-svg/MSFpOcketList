package com.msfpocketlist.ui.dashboard

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.msfpocketlist.ui.common.ContactCard
import com.msfpocketlist.ui.common.ListViewModel
import com.msfpocketlist.ui.common.NavCard
import com.msfpocketlist.ui.common.ObserveMessages
import com.msfpocketlist.ui.common.SearchableList
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.rememberContactActions
import com.msfpocketlist.ui.dashboard.account.AccountTab

private enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    Mission("Missions", "Missions", Icons.Rounded.Public),
    Hq("HQ", "MSF Headquarters", Icons.Rounded.Apartment),
    Emergency("Emergency", "Emergency Contacts", Icons.Rounded.LocalHospital),
    Account("Account", "My Account", Icons.Rounded.Person),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenMission: (id: Int?, title: String?) -> Unit,
    onOpenProfile: (userId: Int?) -> Unit,
    onSync: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    val context = LocalContext.current
    var tab by rememberSaveable { mutableStateOf(Tab.Mission) }
    var confirmExit by remember { mutableStateOf(false) }
    BackHandler { if (tab != Tab.Mission) tab = Tab.Mission else confirmExit = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
                actions = { IconButton(onClick = onSync) { Icon(Icons.Rounded.Sync, "Sync data") } },
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, null) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                Tab.Mission -> MissionTab(onOpenMission)
                Tab.Hq -> ContactsTab(koinViewModel<HqViewModel>(), onOpenProfile)
                Tab.Emergency -> ContactsTab(koinViewModel<EmergencyViewModel>(), onOpenProfile)
                Tab.Account -> AccountTab(onLoggedOut)
            }
        }
    }

    if (confirmExit) {
        AlertDialog(
            onDismissRequest = { confirmExit = false },
            title = { Text("Exit Pocket List?") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = { TextButton(onClick = { (context as Activity).finish() }) { Text("Exit") } },
            dismissButton = { TextButton(onClick = { confirmExit = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun MissionTab(onOpenMission: (Int?, String?) -> Unit, viewModel: MissionsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveMessages(viewModel)
    SearchableList(state, "Search missions", viewModel::onQueryChange, viewModel::refresh) { mission ->
        NavCard(mission.title ?: "Untitled mission", Icons.Rounded.Public) { onOpenMission(mission.id, mission.title) }
    }
}

@Composable
private fun ContactsTab(viewModel: ListViewModel<Contact>, onOpenProfile: (Int?) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val actions = rememberContactActions()
    ObserveMessages(viewModel)
    SearchableList(state, "Search name, role, phone or email", viewModel::onQueryChange, viewModel::refresh) { contact ->
        ContactCard(contact, actions) { onOpenProfile(contact.id) }
    }
}
