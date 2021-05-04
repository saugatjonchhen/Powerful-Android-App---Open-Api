package com.saugat.openapiapp.di.main

import com.saugat.openapiapp.api.main.OpenApiMainService
import com.saugat.openapiapp.persistence.AccountPropertiesDao
import com.saugat.openapiapp.persistence.AppDatabase
import com.saugat.openapiapp.persistence.BlogPostDao
import com.saugat.openapiapp.repository.main.AccountRepository
import com.saugat.openapiapp.repository.main.BlogRepository
import com.saugat.openapiapp.repository.main.CreateBlogRepository
import com.saugat.openapiapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(
            openApiMainService,
            blogPostDao,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CreateBlogRepository {
        return CreateBlogRepository(
            openApiMainService,
            blogPostDao,
            sessionManager
        )
    }

}