package com.saugat.openapiapp.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saugat.openapiapp.models.AuthToken
import com.saugat.openapiapp.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val TAG = "SessionManager"

    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun logOut() {
        Log.i(TAG, "logOut....: ")

        GlobalScope.launch(IO) {
            var errorMessage: String? = null

            try {

                cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }

            } catch (e: CancellationException) {
                Log.i(TAG, "logOut: ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.i(TAG, "logOut: ${e.message}")
                errorMessage = errorMessage + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.i(TAG, "logOut: ${errorMessage}")
                }
                Log.i(TAG, "logOut: finally.....")
                setValue(null)
            }

        }

    }

    public fun setValue(newValue: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }

    fun isConnectedToInternet(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            return cm.activeNetworkInfo.isConnected
        } catch (e: Exception) {
            Log.i(TAG, "isConnectedToInternet: ${e.message}")
        }
        return false

    }

}