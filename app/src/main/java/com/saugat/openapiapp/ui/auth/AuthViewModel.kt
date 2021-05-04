package com.saugat.openapiapp.ui.auth

import androidx.lifecycle.LiveData
import com.saugat.openapiapp.models.AuthToken
import com.saugat.openapiapp.repository.auth.AuthRepository
import com.saugat.openapiapp.ui.BaseViewModel
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.auth.state.AuthStateEvent
import com.saugat.openapiapp.ui.auth.state.AuthStateEvent.*
import com.saugat.openapiapp.ui.auth.state.AuthViewState
import com.saugat.openapiapp.ui.auth.state.LoginFields
import com.saugat.openapiapp.ui.auth.state.RegistrationFields
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    @InternalCoroutinesApi
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {

            is LoginAttemptEvent -> {
                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirmPassword
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }

            is None -> {
                return object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    fun cancelActiveJobs() {
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun handlePendingData() {
        setStateEvent(None())
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

}