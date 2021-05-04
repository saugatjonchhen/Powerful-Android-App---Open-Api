package com.saugat.openapiapp.ui.main.account

import androidx.lifecycle.LiveData
import com.saugat.openapiapp.models.AccountProperties
import com.saugat.openapiapp.repository.main.AccountRepository
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.BaseViewModel
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource
import com.saugat.openapiapp.ui.main.account.state.AccountStateEvent
import com.saugat.openapiapp.ui.main.account.state.AccountStateEvent.*
import com.saugat.openapiapp.ui.main.account.state.AccountViewState
import com.saugat.openapiapp.ui.main.blog.state.BlogViewState
import com.saugat.openapiapp.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    @InternalCoroutinesApi
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {

            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken,
                            AccountProperties(
                                pk,
                                stateEvent.email,
                                stateEvent.username
                            )
                        )
                    }
                } ?: AbsentLiveData.create()
            }

            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.newConfirmPassword
                    )
                } ?: AbsentLiveData.create()
            }

            is None -> {
                return object : LiveData<DataState<AccountViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            null,
                            loading = StateResource.Loading(false),
                            null
                        )
                    }
                }
            }

        }
    }

    public fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logOut() {
        sessionManager.logOut()
    }


    fun cancelActiveJobs() {
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun handlePendingData() {
        setStateEvent(None())
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

}