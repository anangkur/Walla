package com.anangkur.wallpaper.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

private const val HOME_FRAGMENT = "com.anangkur.wallpaper.features.home.HomeFragment"
private const val SEARCH_FRAGMENT = "com.anangkur.wallpaper.features.search.SearchFragment"
private const val SAVED_FRAGMENT = "com.anangkur.wallpaper.features.saved.SavedFragment"
private const val PREVIEW_DIALOG_FRAGMENT = "com.anangkur.wallpaper.features.preview.PreviewDialog"
private const val PREVIEW_ACTIVITY = "com.anangkur.wallpaper.features.preview.PreviewActivity"
private const val COLLECTIONS_ACTIVITY = "com.anangkur.wallpaper.features.collection.CollectionsActivity"
private const val SEARCH_RESULT_ACTIVITY = "com.anangkur.wallpaper.features.search.SearchResultActivity"

const val ARGS_ID = "id"
const val ARGS_TITLE = "title"
const val ARGS_CREATOR = "creator"
const val ARGS_IMAGE_URL = "imageUrl"
const val ARGS_IS_SAVED = "isSaved"
const val ARGS_THUMBNAIL_URL = "thumbnailUrl"
const val ARGS_COLOR = "color"

const val REQUEST_PREVIEW = 100
const val RESULT_CHANGE_SAVED_STATE = 101

private fun getFragment(className: String) = Class.forName(className).newInstance() as Fragment
private fun getDialogFragment(className: String) = Class.forName(className).newInstance() as DialogFragment

fun getHomeFragment() = getFragment(HOME_FRAGMENT)
fun getSearchFragment() = getFragment(SEARCH_FRAGMENT)
fun getSavedFragment() = getFragment(SAVED_FRAGMENT)
fun getPreviewDialog(
    id: String,
    title: String,
    creator: String,
    imageUrl: String,
    isSaved: Boolean,
    thumbnailUrl: String
) = getDialogFragment(PREVIEW_DIALOG_FRAGMENT).apply {
    arguments = Bundle().apply {
        putString(ARGS_ID, id)
        putString(ARGS_TITLE, title)
        putString(ARGS_CREATOR, creator)
        putString(ARGS_IMAGE_URL, imageUrl)
        putBoolean(ARGS_IS_SAVED, isSaved)
        putString(ARGS_THUMBNAIL_URL, thumbnailUrl)
    }
}

fun Activity.startPreviewActivity(
    id: String,
    title: String,
    creator: String,
    imageUrl: String,
    isSaved: Boolean,
    thumbnailUrl: String
) {
    startActivityForResult(
        Intent(this, Class.forName(PREVIEW_ACTIVITY)).apply {
            putExtra(ARGS_ID, id)
            putExtra(ARGS_TITLE, title)
            putExtra(ARGS_CREATOR, creator)
            putExtra(ARGS_IMAGE_URL, imageUrl)
            putExtra(ARGS_IS_SAVED, isSaved)
            putExtra(ARGS_THUMBNAIL_URL, thumbnailUrl)
        },
        REQUEST_PREVIEW
    )
}

fun Fragment.startPreviewActivity(
    id: String,
    title: String,
    creator: String,
    imageUrl: String,
    isSaved: Boolean,
    thumbnailUrl: String
) {
    startActivityForResult(
        Intent(requireContext(), Class.forName(PREVIEW_ACTIVITY)).apply {
            putExtra(ARGS_ID, id)
            putExtra(ARGS_TITLE, title)
            putExtra(ARGS_CREATOR, creator)
            putExtra(ARGS_IMAGE_URL, imageUrl)
            putExtra(ARGS_IS_SAVED, isSaved)
            putExtra(ARGS_THUMBNAIL_URL, thumbnailUrl)
        },
        REQUEST_PREVIEW
    )
}

fun Context.startCollectionsActivity(id: String, title: String) {
    startActivity(Intent(this, Class.forName(COLLECTIONS_ACTIVITY)).apply {
        putExtra(ARGS_ID, id)
        putExtra(ARGS_TITLE, title)
    })
}

fun Context.startSearchResultActivity(color: Int?) {
    startActivity(Intent(this, Class.forName(SEARCH_RESULT_ACTIVITY)).apply {
        putExtra(ARGS_COLOR, color ?: -1)
    })
}