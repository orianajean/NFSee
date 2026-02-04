package com.workingonit.nfsee.android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Query("SELECT * FROM containers ORDER BY name ASC")
    fun getAllContainers(): Flow<List<ContainerEntity>>

    @Query("SELECT * FROM containers WHERE id = :containerId")
    suspend fun getContainerById(containerId: Long): ContainerEntity?

    @Insert
    suspend fun insert(container: ContainerEntity): Long

    @Update
    suspend fun update(container: ContainerEntity)

    @Delete
    suspend fun delete(container: ContainerEntity)
}
