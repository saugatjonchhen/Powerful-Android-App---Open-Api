package com.saugat.openapiapp.ui.main.blog.state

import android.net.Uri
import com.saugat.openapiapp.models.BlogPost
import com.saugat.openapiapp.persistence.BlogQueryUtils

data class BlogViewState(
    //BlogFragment vars
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFragment vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields(),

    //UpdateBlogFragment vars
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()

) {

    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = BlogQueryUtils.ORDER_BY_ASC_DATE_UPDATED,
        var order: String = BlogQueryUtils.BLOG_ORDER_ASC
    )

    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    )

    data class UpdateBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    )

}