package com.anangkur.wallpaper.features.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anangkur.wallpaper.domain.model.Wallpaper
import com.anangkur.wallpaper.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionViewModel(private val repository: Repository) : ViewModel() {

    private val _loadingCollectionPhotos = MutableLiveData<Boolean>()
    val loadingCollectionPhotos: LiveData<Boolean> = _loadingCollectionPhotos

    private val _errorCollectionPhotos = MutableLiveData<String>()
    val errorCollectionPhotos: LiveData<String> = _errorCollectionPhotos

    private val _collectionPhotos = MutableLiveData<List<Wallpaper>>()
    val collectionPhotos: LiveData<List<Wallpaper>> = _collectionPhotos

    fun fetchCollectionPhotos(clientId: String, collectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingCollectionPhotos.postValue(true)
            runCatching {
                _collectionPhotos.postValue(repository.fetchCollectionPhotos(clientId, collectionId))
            }.onSuccess {
                _loadingCollectionPhotos.postValue(false)
            }.onFailure {
                _loadingCollectionPhotos.postValue(false)
                _errorCollectionPhotos.postValue(it.message)
            }
        }
    }
}