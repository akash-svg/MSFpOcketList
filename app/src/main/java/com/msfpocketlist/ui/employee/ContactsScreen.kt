package com.msfpocketlist.ui.employee

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.msfpocketlist.ui.common.BackTopBar
import com.msfpocketlist.ui.common.ContactCard
import com.msfpocketlist.ui.common.ObserveMessages
import com.msfpocketlist.ui.common.SearchableList
import com.msfpocketlist.ui.common.rememberContactActions

@Composable
fun ContactsScreen(onBack: () -> Unit, onOpenProfile: (userId: Int?) -> Unit, viewModel: ContactsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val actions = rememberContactActions()
    ObserveMessages(viewModel)
    Scaffold(topBar = { BackTopBar("Contacts", onBack, viewModel.projectTitle) }) { padding ->
        SearchableList(state, "Search name, role, phone or email", viewModel::onQueryChange, viewModel::refresh, Modifier.padding(padding)) { contact ->
            ContactCard(contact, actions) { onOpenProfile(contact.id) }
        }
    }
}
