package com.anangkur.wallpaper.features.saved

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anangkur.wallpaper.domain.model.Wallpaper
import com.anangkur.wallpaper.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedViewModel(private val repository: Repository): ViewModel() {

    private val _wallpapers = MutableLiveData<List<Wallpaper>>()
    val wallpapers = _wallpapers

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading

    private val _error = MutableLiveData<String>()
    val error = _error

    fun retrieveWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            repository.retrieveSavedWallpaper().runCatching {
                _loading.postValue(false)
                _wallpapers.postValue(this)
            }.onFailure {
                _loading.postValue(false)
                _error.postValue(it.message)
            }
        }
    }

    fun searchWallpaper(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            repository.searchWallpaper(query).runCatching {
                _loading.postValue(false)
                _wallpapers.postValue(this)
            }.onFailure {
                _loading.postValue(false)
                _error.postValue(it.message)
            }
        }
    }
}