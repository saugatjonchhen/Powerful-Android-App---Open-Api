package com.saugat.openapiapp.di.auth

import android.content.SharedPreferences
import com.saugat.openapiapp.api.auth.OpenApiAuthService
import com.saugat.openapiapp.persistence.AccountPropertiesDao
import com.saugat.openapiapp.persistence.AuthTokenDao
import com.saugat.openapiapp.repository.auth.AuthRepository
import com.saugat.openapiapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        sharedPrefsEditor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            sharedPrefsEditor
        )
    }

}