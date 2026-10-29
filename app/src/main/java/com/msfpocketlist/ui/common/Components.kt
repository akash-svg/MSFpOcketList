package com.msfpocketlist.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit, subtitle: String? = null) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back") }
        },
    )
}

/** Red while offline; briefly turns green with "Back online" when the connection returns. */
@Composable
fun ConnectivityBanner(online: Boolean) {
    var wasOffline by remember { mutableStateOf(!online) }
    var backOnline by remember { mutableStateOf(false) }
    LaunchedEffect(online) {
        if (online && wasOffline) {
            backOnline = true
            delay(2_000)
            backOnline = false
        }
        wasOffline = !online
    }
    val color by animateColorAsState(
        if (online) Color(0xFF1E7D3A) else MaterialTheme.colorScheme.errorContainer,
        label = "bannerColor",
    )
    AnimatedVisibility(!online || backOnline) {
        Surface(color = color, contentColor = if (online) Color.White else MaterialTheme.colorScheme.onErrorContainer) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(if (online) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (online) "Back online" else "No internet connection · showing saved data",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Rounded.Search, null) },
        trailingIcon = {
            if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Rounded.Close, "Clear search") }
        },
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
fun EmptyState(text: String, icon: ImageVector = Icons.Rounded.SearchOff) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(80.dp))
        Icon(icon, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SkeletonList() {
    val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha",
    )
    val block = MaterialTheme.colorScheme.surfaceContainerHighest
    Column(Modifier.padding(16.dp).alpha(alpha), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(6) {
            Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(block))
                    Spacer(Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.fillMaxWidth(0.6f).height(14.dp).clip(CircleShape).background(block))
                        Box(Modifier.fillMaxWidth(0.4f).height(10.dp).clip(CircleShape).background(block))
                    }
                }
            }
        }
    }
}

/** Search box + offline banner + pull-to-refresh list, shared by every list screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableList(
    state: ListState<T>,
    searchHint: String,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    item: @Composable (T) -> Unit,
) {
    val query = state.query
    val shown = state.items
    Column(modifier.fillMaxSize()) {
        ConnectivityBanner(state.online)
        SearchField(query, onQueryChange, searchHint, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        PullToRefreshBox(
            isRefreshing = state.loading && shown != null,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                shown == null -> SkeletonList()
                shown.isEmpty() -> EmptyState(if (query.isBlank()) "No data found" else "No results for \"$query\"")
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            "${shown.size} ${if (shown.size == 1) "result" else "results"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    items(shown) { item(it) }
                }
            }
        }
    }
}

/** Initials underneath, photo on top: a missing or broken photo just shows the initials. */
@Composable
fun Avatar(name: String?, image: Any?, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials(name),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value / 2.6f).sp,
        )
        if (image != null) {
            AsyncImage(image, null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        }
    }
}

private fun initials(name: String?) =
    name.orEmpty().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifEmpty { "?" }

/** Tappable row with a tinted icon tile, used for missions and projects. */
@Composable
fun NavCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                title,
                Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun ContactCard(contact: Contact, actions: ContactActions, onClick: () -> Unit) {
    val heading = contact.name ?: contact.title ?: "Unknown"
    val details = listOfNotNull(contact.title.takeIf { contact.name != null }, contact.subtitle).joinToString(" · ")
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(heading, contact.photoUrl, 48.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        heading,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (details.isNotEmpty()) {
                        Text(
                            details,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            val phones = listOfNotNull(contact.phone1, contact.phone2)
            if (phones.isNotEmpty() || contact.email != null) {
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
            }
            phones.forEach { phone ->
                DetailLine(Icons.Rounded.Phone, phone) {
                    FilledTonalIconButton(onClick = { actions.sms(phone) }, Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.Sms, "Message $phone", Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(onClick = { actions.call(phone) }, Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.Call, "Call $phone", Modifier.size(18.dp))
                    }
                }
            }
            contact.email?.let { email ->
                DetailLine(Icons.Rounded.Email, email) {
                    FilledTonalIconButton(onClick = { actions.email(email) }, Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.Email, "Email $email", Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(icon: ImageVector, text: String, trailing: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        trailing()
    }
}

/** Labelled detail row (profile/account). Long-press support lets callers offer copy. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .combinedClickable(enabled = onClick != null || onLongClick != null, onClick = { onClick?.invoke() }, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
        trailing()
    }
}
