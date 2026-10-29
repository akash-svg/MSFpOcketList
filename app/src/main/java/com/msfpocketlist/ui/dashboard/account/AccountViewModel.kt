package com.msfpocketlist.ui.dashboard.account

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.msfpocketlist.common.Constant
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.BaseViewModel
import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.toContact
import com.msfpocketlist.util.ConnectivityObserver
import com.msfpocketlist.util.Session
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class AccountState(
    val me: Contact? = null,
    /** Just-cropped photo, shown before the server copy is available. */
    val localPhoto: Uri? = null,
    val uploading: Boolean = false,
)

class AccountViewModel(
    private val api: ApiInterface,
    private val session: Session,
    network: ConnectivityObserver,
) : BaseViewModel(network) {
    private val _state = MutableStateFlow(AccountState(me = session.user?.profile?.toContact()))
    val state = _state.asStateFlow()

    private val _loggedOut = Channel<Unit>()
    val loggedOut = _loggedOut.receiveAsFlow()

    fun onPhotoCropped(file: File) {
        _state.update { it.copy(localPhoto = Uri.fromFile(file)) }
        if (!online.value) return message("Please connect to the internet")
        val id = _state.value.me?.id ?: return
        _state.update { it.copy(uploading = true) }
        viewModelScope.launch {
            val info = safely {
                val part = MultipartBody.Part.createFormData("avatar", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                api.updateProfile(Constant.APIKEY, id, part)
            }
            _state.update { it.copy(uploading = false) }
            when {
                info == null -> Unit
                info.response == 200 -> {
                    session.saveUser(info)
                    info.profile?.let { profile -> _state.update { it.copy(me = profile.toContact()) } }
                    message("Profile picture updated")
                }
                else -> message(info.message ?: "Upload failed")
            }
        }
    }

    fun logout() {
        session.logout()
        message("Successfully logged out")
        _loggedOut.trySend(Unit)
    }
}
