package com.nfccardmanager.di

import android.content.Context
import androidx.room.Room
import com.nfccardmanager.data.local.AppDatabase
import com.nfccardmanager.data.local.dao.CardDao
import com.nfccardmanager.data.repository.CardRepositoryImpl
import com.nfccardmanager.domain.repository.CardRepository
import com.nfccardmanager.nfc.NfcCardReader
import com.nfccardmanager.nfc.NfcHelper
import com.nfccardmanager.util.HcePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nfc_cards_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCardDao(database: AppDatabase): CardDao {
        return database.cardDao()
    }

    @Provides
    @Singleton
    fun provideCardRepository(cardDao: CardDao): CardRepository {
        return CardRepositoryImpl(cardDao)
    }

    @Provides
    @Singleton
    fun provideNfcHelper(@ApplicationContext context: Context): NfcHelper {
        return NfcHelper(context)
    }

    @Provides
    @Singleton
    fun provideNfcCardReader(): NfcCardReader {
        return NfcCardReader()
    }

    @Provides
    @Singleton
    fun provideHcePreferences(@ApplicationContext context: Context): HcePreferences {
        return HcePreferences(context)
    }
}