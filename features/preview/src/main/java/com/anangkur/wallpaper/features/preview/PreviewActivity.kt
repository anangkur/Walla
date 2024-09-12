package com.anangkur.wallpaper.features.preview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.anangkur.wallpaper.domain.model.Wallpaper
import com.anangkur.wallpaper.features.preview.databinding.ActivityPreviewBinding
import com.anangkur.wallpaper.features.preview.di.ViewModelFactory
import com.anangkur.wallpaper.presentation.*
import com.anangkur.wallpaper.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PreviewActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding

    private lateinit var id: String
    private lateinit var title: String
    private lateinit var creator: String
    private lateinit var imageUrl: String
    private lateinit var thumbnailUrl: String
    private var isSaved = false
    private var isChanged = false

    private lateinit var previewViewModel: PreviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getArgs()
        setData()
        setupViewModel()
        observeViewModel()
        setAction()
        setBottomSheetExpand()
    }

    override fun onBackPressed() {
        if (isChanged) {
            setResult(
                RESULT_CHANGE_SAVED_STATE,
                Intent().putExtra(ARGS_IS_SAVED, isSaved)
            )
        }
        super.onBackPressed()
    }

    private fun setupViewModel() {
        previewViewModel = obtainViewModel(
            PreviewViewModel::class.java,
            ViewModelFactory.getInstance(provideRepository()),
        )
    }

    private fun observeViewModel() {
        previewViewModel.apply {
            loading.observe(this@PreviewActivity, Observer {
                setLoadingSave(it)
            })
            error.observe(this@PreviewActivity, Observer {
                showSnackbarShort(it)
            })
            success.observe(this@PreviewActivity, Observer {
                isChanged = true
                when (it) {
                    PreviewViewModel.Companion.Action.Delete -> {
                        isSaved = !isSaved
                        setData()
                        showSnackbarShort(getString(R.string.message_wallpaper_deleted))
                    }
                    PreviewViewModel.Companion.Action.Insert -> {
                        isSaved = !isSaved
                        setData()
                        showSnackbarShort(getString(R.string.message_wallpaper_saved))
                    }
                }
            })
        }
    }

    private fun getArgs() {
        id = intent.getStringExtra(ARGS_ID).orEmpty()
        title = intent.getStringExtra(ARGS_TITLE).orEmpty()
        creator = intent.getStringExtra(ARGS_CREATOR).orEmpty()
        imageUrl = intent.getStringExtra(ARGS_IMAGE_URL).orEmpty()
        isSaved = intent.getBooleanExtra(ARGS_IS_SAVED, false)
        thumbnailUrl = intent.getStringExtra(ARGS_THUMBNAIL_URL).orEmpty()
    }

    private fun setData() {
        binding.ivPreview.setImageUrl(thumbnailUrl)
        binding.tvTitle.text = title
        binding.tvCreator.text = creator
        binding.ivSave.setImageDrawable(getIconSave(isSaved))
        binding.tvSave.text = getTextSave(isSaved)
    }

    private fun getIconSave(isSaved: Boolean) = if (isSaved)
        ContextCompat.getDrawable(this, R.drawable.ic_delete)
    else
        ContextCompat.getDrawable(this, R.drawable.ic_save)

    private fun getTextSave(isSaved: Boolean) = if (isSaved) {
        getString(R.string.btn_delete)
    } else {
        getString(R.string.btn_save)
    }

    private fun saveAction(wallpaper: Wallpaper) {
        if (isSaved) {
            previewViewModel.deleteWallpaper(wallpaper)
        } else {
            previewViewModel.insertWallpaper(wallpaper)
        }
    }

    private fun setAction() {
        binding.btnClose.setOnClickListener { onBackPressed() }
        binding.btnSet.setOnClickListener { setWallpaper() }
        binding.btnSave.setOnClickListener {
            saveAction(
                Wallpaper(
                    id = id,
                    title = title,
                    imageUrl = imageUrl,
                    creator = creator,
                    isSaved = isSaved,
                    thumbnailUrl = thumbnailUrl
                )
            )
        }
        binding.btnFullscreen.setOnClickListener { onBackPressed() }
    }

    private fun setBottomSheetExpand() {
        val bottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setWallpaper() {
        setLoadingSet(true)
        downloadBitmap(
            onLoading = {
            },
            onFailed = {
                setLoadingSet(false)
                showSnackbarShort(getString(R.string.message_failed_set_wallpaper))
            },
            onResourceReady = {
                setLoadingSet(false)
                setWallpaperDevice(it)
                showSnackbarShort(getString(R.string.message_success_set_wallpaper))
            },
            imageUrl = imageUrl
        )
    }

    private fun setLoadingSet(isLoading: Boolean) {
        binding.flipperSet.displayedChild = if (isLoading) 1 else 0
    }

    private fun setLoadingSave(isLoading: Boolean) {
        binding.flipperSave.displayedChild = if (isLoading) 1 else 0
    }
}