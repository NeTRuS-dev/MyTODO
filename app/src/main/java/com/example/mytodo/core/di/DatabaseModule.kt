package com.example.mytodo.core.di

import android.content.Context
import androidx.room.Room
import com.example.mytodo.core.AppDatabase
import com.example.mytodo.core.dao.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        )
            .enableMultiInstanceInvalidation()
            .build()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): NotesDao {
        return appDatabase.noteDao()
    }
}