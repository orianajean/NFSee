package com.workingonit.nfsee.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.workingonit.nfsee.android.data.entity.ItemEntity
import com.workingonit.nfsee.android.data.entity.ItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE containerId = :containerId AND status != 'REMOVED' ORDER BY name ASC")
    fun getItemsByContainer(containerId: Long): Flow<List<ItemEntity>>

    @Insert
    suspend fun insert(item: ItemEntity): Long

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)
}
