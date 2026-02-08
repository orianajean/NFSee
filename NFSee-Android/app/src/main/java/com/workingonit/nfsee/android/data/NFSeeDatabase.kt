package com.workingonit.nfsee.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import com.workingonit.nfsee.android.data.entity.ItemEntity

@Database(
    entities = [ContainerEntity::class, ItemEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class NFSeeDatabase : RoomDatabase() {
    abstract fun containerDao(): ContainerDao
    abstract fun itemDao(): ItemDao

    companion object {
        private const val DATABASE_NAME = "nfsee.db"

        fun create(context: Context): NFSeeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NFSeeDatabase::class.java,
                DATABASE_NAME,
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
