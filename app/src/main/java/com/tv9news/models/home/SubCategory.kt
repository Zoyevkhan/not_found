package com.tv9news.models.home

data class SubCategory(
    val id: String,
    val cms_id: String,
    val lang_id: String,
    val parent_id: String,
    val category_title: String,
    val category_logo: String? = null,
    val logo_sizes: String,
    val category_status: String,
    val created_on: String,
    val modified_on: String,
    val app_id: String,
    val position: String,
    val is_display_home: String,
    val app_view_type: String,
    val layout_code: String
)