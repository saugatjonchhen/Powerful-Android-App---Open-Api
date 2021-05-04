package com.saugat.openapiapp.ui.main.blog.viewmodel

import android.util.Log
import com.saugat.openapiapp.ui.main.blog.state.BlogStateEvent.BlogSearchEvent
import com.saugat.openapiapp.ui.main.blog.state.BlogViewState

fun BlogViewModel.resetPage() {
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage() {
    setQueryinProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.nextPage() {
    if (!getIsQueryExhausted() && !getIsQueryInProgress()) {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryinProgress(true)
        setStateEvent(BlogSearchEvent())

    }
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState) {
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryinProgress(viewState.blogFields.isQueryInProgress)
    setBlogList(viewState.blogFields.blogList)
}

