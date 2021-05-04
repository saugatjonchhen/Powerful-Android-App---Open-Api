package com.saugat.openapiapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.LiveData
import com.saugat.openapiapp.persistence.BlogQueryUtils
import com.saugat.openapiapp.repository.main.BlogRepository
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.BaseViewModel
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource
import com.saugat.openapiapp.ui.main.blog.state.BlogStateEvent
import com.saugat.openapiapp.ui.main.blog.state.BlogStateEvent.*
import com.saugat.openapiapp.ui.main.blog.state.BlogViewState
import com.saugat.openapiapp.util.AbsentLiveData
import com.saugat.openapiapp.util.PreferenceKeys
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        setBlogFilter(
            sharedPreferences.getString(
                PreferenceKeys.BLOG_FILTER,
                BlogQueryUtils.BLOG_FILTER_DATE_UPDATED
            )
        )

        setBlogOrder(
            sharedPreferences.getString(
                PreferenceKeys.BLOG_ORDER,
                BlogQueryUtils.BLOG_ORDER_ASC
            )!!
        )
    }

    @InternalCoroutinesApi
    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }

            is CheckAuthorOfBlogPost -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                } ?: AbsentLiveData.create()
            }

            is DeleteBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken = authToken,
                        blogPost = getBlogPost()
                    )
                } ?: AbsentLiveData.create()
            }

            is UpdatedBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.updateBLogPost(
                        authToken = authToken,
                        slug = getSlug(),
                        title = RequestBody.create(
                            MediaType.parse("text/plain"),
                            stateEvent.title
                        ),
                        body = RequestBody.create(
                            MediaType.parse("text/plain"),
                            stateEvent.body
                        ),
                        image = stateEvent.image
                    )
                } ?: AbsentLiveData.create()
            }

            is None -> {
                object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            null,
                            loading = StateResource.Loading(false),
                            null
                        )
                    }
                }
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(PreferenceKeys.BLOG_FILTER, filter)
        editor.apply()

        editor.putString(PreferenceKeys.BLOG_ORDER, order)
        editor.apply()
    }

    fun getUpdatedImageUri(): Uri? {
        getCurrentViewStateOrNew().let {
            it.updateBlogFields.updatedImageUri.let {
                return it
            }
        }
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}