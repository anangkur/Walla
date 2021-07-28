package com.anangkur.wallpaper.features.preview

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.anangkur.wallpaper.features.preview.databinding.DialogPreviewBinding
import com.anangkur.wallpaper.R as APP_R
import com.anangkur.wallpaper.features.preview.R as PREVIEW_R
import com.anangkur.wallpaper.utils.*

class PreviewDialog : DialogFragment() {

    private lateinit var binding: DialogPreviewBinding

    private lateinit var title: String
    private lateinit var creator: String
    private lateinit var imageUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DialogPreviewBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgs()
        setData()
        setOnClickListener()
    }

    override fun onStart() {
        super.onStart()
        setDialogToFullscreen()
        setDialogToTransparent()
    }

    private fun getArgs() {
        title = arguments?.getString(ARGS_TITLE).orEmpty()
        creator = arguments?.getString(ARGS_CREATOR).orEmpty()
        imageUrl = arguments?.getString(ARGS_IMAGE_URL).orEmpty()
    }

    private fun setData() {
        binding.tvTitle.text = title
        binding.tvCreator.text = creator
        binding.ivPreview.setImageUrl(imageUrl)
    }

    private fun setDialogToFullscreen() {
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    private fun setDialogToTransparent() {
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.window?.setBackgroundDrawableResource(APP_R.color.black_60)
        }
    }

    private fun setOnClickListener() {
        binding.root.setOnClickListener { dialog?.hide() }
        binding.btnSet.setOnClickListener { setWallpaper() }
        binding.btnSave.setOnClickListener { Toast.makeText(requireContext(), "save", Toast.LENGTH_SHORT).show() }
        binding.btnFullscreen.setOnClickListener {
            requireContext().startPreviewActivity(title = title, creator = creator, imageUrl = imageUrl)
        }
    }

    private fun setWallpaper() {
        requireContext().downloadBitmap(
            onLoading = {
                binding.flipperSet.displayedChild = 1
            },
            onFailed = {
                binding.flipperSet.displayedChild = 0
                requireActivity().showSnackbarShort(getString(PREVIEW_R.string.message_failed_set_wallpaper))
            },
            onResourceReady = {
                requireContext().setWallpaperDevice(it)
                dialog?.hide()
                requireActivity().showSnackbarShort(getString(com.anangkur.wallpaper.features.preview.R.string.message_success_set_wallpaper))
            },
            imageUrl = imageUrl
        )
    }
}