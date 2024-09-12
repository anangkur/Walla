package com.anangkur.wallpaper.local

import android.content.Context
import android.content.SharedPreferences
import com.anangkur.wallpaper.data.repository.LocalRepository
import com.anangkur.wallpaper.domain.model.Wallpaper
import com.anangkur.wallpaper.local.db.AppDatabase
import com.anangkur.wallpaper.local.model.toDatabaseEntity
import com.anangkur.wallpaper.local.model.toWallpaper

class LocalRepository(
    private val preferences: SharedPreferences,
    private val appDatabase: AppDatabase
): LocalRepository {

    companion object{
        private var INSTANCE: LocalRepository? = null
        fun getInstance(context: Context) = INSTANCE ?: LocalRepository(
            context.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE),
            AppDatabase.getDatabase(context)
        )
    }

    private val expirationTime = (60 * 10 * 1000).toLong()
    override suspend fun searchWallpaper(query: String): List<Wallpaper> {
        return appDatabase.getDao().selectWallpapersContainsQuery(query).map { it.toWallpaper() }
    }

    override suspend fun deleteWallpaper(id: String) {
        appDatabase.getDao().deleteWallpaperById(id)
    }

    override suspend fun insertWallpaper(wallpaper: Wallpaper, isReplace: Boolean) {
        if (isReplace) {
            appDatabase.getDao().insertWallpaperReplace(wallpaper = wallpaper.toDatabaseEntity())
        } else {
            appDatabase.getDao().insertWallpaper(wallpaper = wallpaper.toDatabaseEntity())
        }
    }

    override suspend fun retrieveWallpapers(): List<Wallpaper> {
        return appDatabase.getDao().loadAllWallpaper().map { it.toWallpaper() }
    }

    override suspend fun retrieveSavedWallpapers(): List<Wallpaper> {
        return appDatabase.getDao().loadAllSavedWallpaper().map { it.toWallpaper() }
    }

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = this.getLastCacheUpdateTimeMillis()
        return currentTime - lastUpdateTime > expirationTime
    }

    private fun getLastCacheUpdateTimeMillis(): Long {
        return preferences.getLong(Const.PREF_CACHED_TIME, 0)
    }
}