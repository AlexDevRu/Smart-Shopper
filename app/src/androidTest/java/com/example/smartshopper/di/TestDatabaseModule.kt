package com.example.smartshopper.di

import android.content.Context
import androidx.room.Room
import com.example.data.db.ChatDatabase
import com.example.data.db.dao.ChatDao
import com.example.data.db.dao.MessageDao
import com.example.data.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao = database.messageDao()
}
