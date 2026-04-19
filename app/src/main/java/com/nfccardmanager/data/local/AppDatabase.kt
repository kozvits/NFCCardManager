package com.nfccardmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nfccardmanager.data.local.dao.CardDao
import com.nfccardmanager.data.local.entity.CardEntity

@Database(
    entities = [CardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}