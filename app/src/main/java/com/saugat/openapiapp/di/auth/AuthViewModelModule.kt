package com.saugat.openapiapp.di.auth

import androidx.lifecycle.ViewModel
import com.saugat.openapiapp.di.ViewModelKey
import com.saugat.openapiapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}