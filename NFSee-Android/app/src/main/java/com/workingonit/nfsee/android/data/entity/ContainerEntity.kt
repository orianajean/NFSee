package com.workingonit.nfsee.android.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A physical place (bin, drawer, shelf, closet, garage box, etc.)
 */
@Entity(tableName = "containers")
data class ContainerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val locationLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
