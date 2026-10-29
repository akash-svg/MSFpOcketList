package com.msfpocketlist.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.installations.FirebaseInstallations
import com.msfpocketlist.common.Constant
import com.msfpocketlist.remote.ApiInterface
import com.msfpocketlist.ui.common.BaseViewModel
import com.msfpocketlist.util.ConnectivityObserver
import com.msfpocketlist.util.Session
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val loading: Boolean = false,
)

class LoginViewModel(
    private val handle: SavedStateHandle,
    private val api: ApiInterface,
    private val session: Session,
    network: ConnectivityObserver,
) : BaseViewModel(network) {
    // Only the email survives process death; the password is deliberately not persisted.
    private val _state = MutableStateFlow(LoginState(email = handle["email"] ?: ""))
    val state = _state.asStateFlow()

    private val _loggedIn = Channel<Unit>()
    val loggedIn = _loggedIn.receiveAsFlow()

    private var deviceToken = ""

    init {
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener { task ->
            runCatching { deviceToken = task.result.toString() }
        }
    }

    fun onEmailChange(email: String) {
        handle["email"] = email
        _state.update { it.copy(email = email, emailError = false) }
    }

    fun onPasswordChange(password: String) = _state.update { it.copy(password = password, passwordError = false) }

    fun login() {
        val current = _state.updateAndGet { it.copy(emailError = it.email.isBlank(), passwordError = it.password.isBlank()) }
        if (current.emailError || current.passwordError || current.loading) return
        if (!online.value) return message("Please connect to the internet")

        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val info = safely { api.userLogin(Constant.APIKEY, current.email.trim(), current.password, deviceToken) }
            _state.update { it.copy(loading = false) }
            when {
                info == null -> Unit
                info.response == 200 -> {
                    session.saveUser(info)
                    message("Successfully logged in")
                    _loggedIn.send(Unit)
                }
                else -> message(info.message ?: "Login failed")
            }
        }
    }
}
