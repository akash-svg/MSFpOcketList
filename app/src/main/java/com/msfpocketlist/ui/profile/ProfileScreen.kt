package com.msfpocketlist.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.msfpocketlist.ui.common.Avatar
import com.msfpocketlist.ui.common.BackTopBar
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.EmptyState
import com.msfpocketlist.ui.common.InfoRow
import com.msfpocketlist.ui.common.ObserveMessages
import com.msfpocketlist.ui.common.ConnectivityBanner
import com.msfpocketlist.ui.common.rememberContactActions

@Composable
fun ProfileScreen(onBack: () -> Unit, viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveMessages(viewModel)
    Scaffold(topBar = { BackTopBar("Profile", onBack) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            ConnectivityBanner(state.online)
            val contact = state.items?.firstOrNull()
            when {
                state.items == null -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                contact == null -> EmptyState("Profile not available", Icons.Rounded.PersonOff)
                else -> ProfileContent(contact)
            }
        }
    }
}

@Composable
private fun ProfileContent(contact: Contact) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val actions = rememberContactActions()
    val copy = { value: String ->
        clipboard.setText(AnnotatedString(value))
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }
    val primaryPhone = contact.phone1 ?: contact.phone2

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        Avatar(contact.name, contact.photoUrl, 112.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            contact.name ?: "Unknown",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        val role = listOfNotNull(contact.title, contact.subtitle).joinToString(" · ")
        if (role.isNotEmpty()) {
            Text(
                role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        contact.mission?.let { mission ->
            Spacer(Modifier.height(12.dp))
            Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Place, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(mission, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(Icons.Rounded.Call, "Call", primaryPhone != null) { primaryPhone?.let(actions.call) }
            QuickAction(Icons.Rounded.Sms, "Message", primaryPhone != null) { primaryPhone?.let(actions.sms) }
            QuickAction(Icons.Rounded.Email, "Email", contact.email != null) { contact.email?.let(actions.email) }
        }

        Spacer(Modifier.height(24.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
            val rows = listOfNotNull(
                contact.phone1?.let { Triple(Icons.Rounded.Phone, "Mobile", it) },
                contact.phone2?.let { Triple(Icons.Rounded.Phone, "Alternate mobile", it) },
                contact.email?.let { Triple(Icons.Rounded.Email, "Email", it) },
            )
            if (rows.isEmpty()) {
                Text("No contact details available", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            rows.forEachIndexed { i, (icon, label, value) ->
                if (i > 0) HorizontalDivider(Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant)
                val isEmail = icon == Icons.Rounded.Email
                InfoRow(
                    icon, label, value,
                    onClick = { if (isEmail) actions.email(value) else actions.call(value) },
                    onLongClick = { copy(value) },
                ) {
                    if (!isEmail) {
                        IconButton(onClick = { actions.sms(value) }) { Icon(Icons.Rounded.Sms, "Message $value") }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap to contact · long-press to copy",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RowScope.QuickAction(icon: ImageVector, label: String, enabled: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(1f).height(72.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
