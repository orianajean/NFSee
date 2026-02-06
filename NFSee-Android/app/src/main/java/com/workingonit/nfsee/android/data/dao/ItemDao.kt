package com.workingonit.nfsee.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.workingonit.nfsee.android.data.entity.ItemEntity
import com.workingonit.nfsee.android.data.entity.ItemSearchResult
import com.workingonit.nfsee.android.data.entity.ItemStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE containerId = :containerId AND status != 'REMOVED' ORDER BY name ASC")
    fun getItemsByContainer(containerId: Long): Flow<List<ItemEntity>>

    @Query(
        """
        SELECT items.id, items.name, items.category, items.status, items.containerId, items.createdAt,
               containers.name AS containerName, containers.locationLabel AS containerLocation
        FROM items
        INNER JOIN containers ON items.containerId = containers.id
        WHERE items.status != 'REMOVED'
        AND (:statusFilter IS NULL OR items.status = :statusFilter)
        AND (:query IS NULL OR :query = '' OR items.name LIKE '%' || :query || '%'
             OR items.category LIKE '%' || :query || '%'
             OR containers.name LIKE '%' || :query || '%')
        ORDER BY items.name ASC
        """,
    )
    fun searchItems(query: String?, statusFilter: String?): Flow<List<ItemSearchResult>>

    @Insert
    suspend fun insert(item: ItemEntity): Long

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)
}
