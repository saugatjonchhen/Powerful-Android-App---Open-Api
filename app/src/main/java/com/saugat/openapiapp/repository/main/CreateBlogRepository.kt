package com.saugat.openapiapp.repository.main

import androidx.lifecycle.LiveData
import com.saugat.openapiapp.api.main.OpenApiMainService
import com.saugat.openapiapp.api.main.responses.BlogCreateUpdateResponse
import com.saugat.openapiapp.models.AuthToken
import com.saugat.openapiapp.models.BlogPost
import com.saugat.openapiapp.persistence.BlogPostDao
import com.saugat.openapiapp.repository.JobManager
import com.saugat.openapiapp.repository.NetworkBoundResource
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource.Response
import com.saugat.openapiapp.ui.StateResource.ResponseType
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogViewState
import com.saugat.openapiapp.util.AbsentLiveData
import com.saugat.openapiapp.util.DateUtils
import com.saugat.openapiapp.util.GenericApiResponse
import com.saugat.openapiapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("createBlogRepository") {

    private val TAG = "AppDebug"

    @InternalCoroutinesApi
    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                sessionManager.isConnectedToInternet(),
                true,
                true,
                false
            ) {
            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<BlogCreateUpdateResponse>) {
                // If they don't have a paid membership account if will still return a 20
                // Need an account for that
                if (!response.body.response.equals(RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) {
                    val updateBlogPost = BlogPost(
                        response.body.pk,
                        response.body.title,
                        response.body.slug,
                        response.body.body,
                        response.body.image,
                        DateUtils.convertServerStringDateToLong(response.body.date_updated),
                        response.body.username
                    )
                    updateLocalDatabase(updateBlogPost)
                }

                withContext(Main) {
                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Dialog())
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.createBlog(
                    "Token ${authToken.token}",
                    title,
                    body,
                    image
                )
            }

            //not applicable
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDatabase(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.insert(it)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }

        }.asLiveData()
    }

}