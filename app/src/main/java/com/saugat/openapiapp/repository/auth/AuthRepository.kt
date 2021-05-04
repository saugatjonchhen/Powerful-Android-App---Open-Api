package com.saugat.openapiapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.saugat.openapiapp.api.auth.OpenApiAuthService
import com.saugat.openapiapp.api.auth.network_reposones.LoginResponse
import com.saugat.openapiapp.api.auth.network_reposones.RegistrationResponse
import com.saugat.openapiapp.models.AccountProperties
import com.saugat.openapiapp.models.AuthToken
import com.saugat.openapiapp.persistence.AccountPropertiesDao
import com.saugat.openapiapp.persistence.AuthTokenDao
import com.saugat.openapiapp.repository.JobManager
import com.saugat.openapiapp.repository.NetworkBoundResource
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource.Response
import com.saugat.openapiapp.ui.StateResource.ResponseType
import com.saugat.openapiapp.ui.auth.state.AuthViewState
import com.saugat.openapiapp.ui.auth.state.LoginFields
import com.saugat.openapiapp.ui.auth.state.RegistrationFields
import com.saugat.openapiapp.util.AbsentLiveData
import com.saugat.openapiapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.saugat.openapiapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.saugat.openapiapp.util.GenericApiResponse
import com.saugat.openapiapp.util.GenericApiResponse.ApiSuccessResponse
import com.saugat.openapiapp.util.PreferenceKeys
import com.saugat.openapiapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
) : JobManager("AuthRepository") {

    private val TAG = "AppDebug"

    @InternalCoroutinesApi
    fun attemptLogin(
        email: String,
        password: String
    ): LiveData<DataState<AuthViewState>> {

        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (!loginFieldErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.i(TAG, "handleApiSuccessResponse: ${response}")

                //Handle Incorrect login credentials
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedToPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                addJob("attemptLogin", job)
            }

            //Not used
            override suspend fun createCacheRequestAndReturn() {

            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: Any?) {

            }
        }.asLiveData()

    }

    @InternalCoroutinesApi
    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {

        val registrationFieldErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (!registrationFieldErrors.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.i(TAG, "handleApiSuccessResponse: ${response}")

                //Handle Incorrect login credentials
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedToPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                addJob("attemptRegistration", job)
            }

            //Not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: Any?) {

            }
        }.asLiveData()

    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        Log.i(TAG, "returnErrorResponse: ${errorMessage}")
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        message = errorMessage,
                        responseType = responseType
                    )
                )
            }
        }
    }

    @InternalCoroutinesApi
    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {

        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.i(TAG, "checkPreviousAuthUser: No previously authenticated user foind...")
            return returnNoTokenFound()
        }

        return object : NetworkBoundResource<Void, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            false,
            false,
            false
        ) {
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->

                    Log.d(
                        TAG,
                        "checkPreviousAuthUser: searching for token: $accountProperties"
                    )

                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.i(TAG, "checkPreviousAuthUser: AuthToken not found..")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )

                }
            }

            // not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {

            }

            //not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                addJob("checkPreviousAuthUser", job)
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(
                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                        ResponseType.None()
                    )
                )
            }
        }
    }

    private fun saveAuthenticatedToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

}