package com.saugat.openapiapp.di

import androidx.lifecycle.ViewModelProvider
import com.saugat.openapiapp.videmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}