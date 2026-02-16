package com.myown.musictome.ui

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MusicPlayerManagerModule {
    @Provides
    fun provideMusicPlayerManager(): MusicPlayerManager {
        return MusicPlayerManager
    }
}