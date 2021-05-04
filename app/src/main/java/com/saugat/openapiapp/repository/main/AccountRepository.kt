package com.saugat.openapiapp.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.saugat.openapiapp.api.GenericResponse
import com.saugat.openapiapp.api.main.OpenApiMainService
import com.saugat.openapiapp.models.AccountProperties
import com.saugat.openapiapp.models.AuthToken
import com.saugat.openapiapp.persistence.AccountPropertiesDao
import com.saugat.openapiapp.repository.JobManager
import com.saugat.openapiapp.repository.NetworkBoundResource
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource
import com.saugat.openapiapp.ui.main.account.state.AccountViewState
import com.saugat.openapiapp.util.AbsentLiveData
import com.saugat.openapiapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {

    private val TAG: String = "AppDebug"

    @InternalCoroutinesApi
    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToInternet(),
                true,
                false,
                true
            ) {

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {

                    //finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDatabase(response.body)

                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService
                    .getAccountProperties(
                        "Token ${authToken.token}"
                    )
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDatabase(cacheObject: AccountProperties?) {

                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
            }


        }.asLiveData()
    }

    @InternalCoroutinesApi
    fun saveAccountProperties(
        authToken: AuthToken,
        accountProperties: AccountProperties
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                updateLocalDatabase(null) // The update does not return a CacheObject

                withContext(Main) {
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = StateResource.Response(
                                response.body.response,
                                StateResource.ResponseType.Toast()
                            )
                        )
                    )
                }
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

            override suspend fun updateLocalDatabase(cacheObject: Any?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

        }.asLiveData()
    }

    @InternalCoroutinesApi
    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {

                withContext(Main) {
                    //finish with a success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = StateResource.Response(
                                response.body.response,
                                StateResource.ResponseType.Toast()
                            )
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            //not applicable
            override suspend fun updateLocalDatabase(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }

        }.asLiveData()
    }

}