package com.example.koirahieronta.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room3.Room
import com.example.koirahieronta.database.ClientDAO
import com.example.koirahieronta.database.DogDAO
import com.example.koirahieronta.database.MassageDAO
import com.example.koirahieronta.database.MassageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): MassageDatabase {
        return Room.databaseBuilder(
            appContext, MassageDatabase::class.java, "MassageDatabase"
        ).build()
    }

    @Provides
    fun provideClientDao(massageDatabase: MassageDatabase): ClientDAO = massageDatabase.clientDao()

    @Provides
    fun provideDogDao(massageDatabase: MassageDatabase): DogDAO = massageDatabase.dogDao()

    @Provides
    fun provideMassageDao(massageDatabase: MassageDatabase): MassageDAO =
        massageDatabase.massageDao()
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler {
                emptyPreferences()
            },
            migrations = listOf(SharedPreferencesMigration(context, "userSettings")),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile("userSettings") })
    }
}