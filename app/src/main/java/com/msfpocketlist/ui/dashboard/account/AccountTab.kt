package com.msfpocketlist.ui.dashboard.account

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.msfpocketlist.BuildConfig
import com.msfpocketlist.ui.common.Avatar
import com.msfpocketlist.ui.common.InfoRow
import com.msfpocketlist.ui.common.ConnectivityBanner
import com.msfpocketlist.ui.common.ObserveMessages
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import java.io.File

/** Camera, gallery and crop launchers stay here (they need the Activity); the upload itself is the ViewModel's. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTab(onLoggedOut: () -> Unit, viewModel: AccountViewModel = koinViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val online by viewModel.online.collectAsStateWithLifecycle()
    val me = state.me
    var choosePhoto by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }
    ObserveMessages(viewModel)
    LaunchedEffect(viewModel) { viewModel.loggedOut.collect { onLoggedOut() } }

    val cameraFile = remember { File(context.cacheDir, "camera.jpg") }
    val cameraUri = remember { FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", cameraFile) }

    val crop = rememberLauncherForActivityResult(CropImageContract()) { result ->
        result.takeIf { it.isSuccessful }?.getUriFilePath(context)?.let { viewModel.onPhotoCropped(File(it)) }
    }
    fun startCrop(uri: Uri) = crop.launch(
        CropImageContractOptions(uri, CropImageOptions(guidelines = CropImageView.Guidelines.ON, fixAspectRatio = true))
    )
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { taken ->
        if (taken) startCrop(Uri.fromFile(cameraFile))
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(::startCrop)
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            camera.launch(cameraUri)
        } else {
            Toast.makeText(context, "Camera permission is needed to take a photo", Toast.LENGTH_SHORT).show()
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ConnectivityBanner(online)
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Avatar(me?.name, state.localPhoto ?: me?.photoUrl, 112.dp, Modifier.clickable { choosePhoto = true })
                Surface(
                    onClick = { choosePhoto = true },
                    modifier = Modifier.align(Alignment.BottomEnd).size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 2.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CameraAlt, "Change photo", Modifier.size(18.dp))
                    }
                }
            }
            if (state.uploading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(Modifier.width(112.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(me?.name ?: "Not available", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            me?.title?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            val notAvailable = "Not available"
            InfoRow(Icons.Rounded.Phone, "Mobile", me?.phone1 ?: notAvailable)
            HorizontalDivider(Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant)
            InfoRow(Icons.Rounded.Phone, "Alternate mobile", me?.phone2 ?: notAvailable)
            HorizontalDivider(Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant)
            InfoRow(Icons.Rounded.Email, "Email", me?.email ?: notAvailable)
            HorizontalDivider(Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant)
            InfoRow(Icons.Rounded.Place, "Mission", me?.mission ?: notAvailable)
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = { confirmLogout = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text("Log out", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            "Version ${BuildConfig.VERSION_NAME}",
            Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (choosePhoto) {
        ModalBottomSheet(onDismissRequest = { choosePhoto = false }) {
            Column(Modifier.navigationBarsPadding().padding(bottom = 16.dp)) {
                Text(
                    "Update profile photo",
                    Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                val itemColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ListItem(
                    headlineContent = { Text("Take photo") },
                    leadingContent = { Icon(Icons.Rounded.CameraAlt, null) },
                    colors = itemColors,
                    modifier = Modifier.clickable {
                        choosePhoto = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            camera.launch(cameraUri)
                        } else {
                            cameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    },
                )
                ListItem(
                    headlineContent = { Text("Choose from gallery") },
                    leadingContent = { Icon(Icons.Rounded.PhotoLibrary, null) },
                    colors = itemColors,
                    modifier = Modifier.clickable {
                        choosePhoto = false
                        gallery.launch("image/*")
                    },
                )
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Log out?") },
            text = { Text("You'll need to sign in again to access the directory.") },
            confirmButton = { TextButton(onClick = viewModel::logout) { Text("Log out") } },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancel") } },
        )
    }
}
