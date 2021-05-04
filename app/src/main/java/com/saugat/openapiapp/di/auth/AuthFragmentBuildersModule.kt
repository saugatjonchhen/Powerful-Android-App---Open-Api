package com.saugat.openapiapp.di.auth

import com.saugat.openapiapp.ui.auth.ForgotPasswordFragment
import com.saugat.openapiapp.ui.auth.LauncherFragment
import com.saugat.openapiapp.ui.auth.LoginFragment
import com.saugat.openapiapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}