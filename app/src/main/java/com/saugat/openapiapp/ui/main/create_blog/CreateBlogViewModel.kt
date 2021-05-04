package com.saugat.openapiapp.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.saugat.openapiapp.repository.main.CreateBlogRepository
import com.saugat.openapiapp.session.SessionManager
import com.saugat.openapiapp.ui.BaseViewModel
import com.saugat.openapiapp.ui.DataState
import com.saugat.openapiapp.ui.StateResource
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogStateEvent.CreateNewBlogEvent
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogStateEvent.None
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogViewState
import com.saugat.openapiapp.ui.main.create_blog.state.CreateBlogViewState.NewBlogFields
import com.saugat.openapiapp.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createBlogRepository: CreateBlogRepository
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {
    @InternalCoroutinesApi
    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when (stateEvent) {
            is CreateNewBlogEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    createBlogRepository.createNewBlogPost(
                        authToken = authToken,
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
                return liveData {
                    emit(
                        DataState<CreateBlogViewState>(
                            null,
                            StateResource.Loading(false),
                            data = null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBLogFields = update.newBlogFields
        title?.let { newBLogFields.newBlogTitle = it }
        body?.let { newBLogFields.newBlogBody = it }
        uri?.let { newBLogFields.newBlogImage = it }
        update.newBlogFields = newBLogFields
        setViewState(update)
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewStateOrNew()
        update.newBlogFields = NewBlogFields()
        setViewState(update)
    }

    fun getNewImageUri(): Uri? {
        getCurrentViewStateOrNew().let {
            it.newBlogFields.let {
                return it.newBlogImage
            }
        }
    }

    fun cancelActiveJobs() {
        createBlogRepository.cancelActiveJobs()
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