package com.saugat.openapiapp.ui.main.create_blog.state

import android.net.Uri

data class CreateBlogViewState(

    //CreateBlogFragment vars
    var newBlogFields: NewBlogFields = NewBlogFields()
) {

    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newBlogImage: Uri? = null,
    )

}