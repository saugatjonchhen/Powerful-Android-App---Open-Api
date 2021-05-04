package com.saugat.openapiapp.di

import com.saugat.openapiapp.di.auth.AuthFragmentBuildersModule
import com.saugat.openapiapp.di.auth.AuthModule
import com.saugat.openapiapp.di.auth.AuthScope
import com.saugat.openapiapp.di.auth.AuthViewModelModule
import com.saugat.openapiapp.di.main.MainFragmentBuildersModule
import com.saugat.openapiapp.di.main.MainModule
import com.saugat.openapiapp.di.main.MainScope
import com.saugat.openapiapp.di.main.MainViewModelModule
import com.saugat.openapiapp.ui.auth.AuthActivity
import com.saugat.openapiapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}