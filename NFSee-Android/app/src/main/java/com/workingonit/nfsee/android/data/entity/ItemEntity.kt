package com.workingonit.nfsee.android.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Status of an item relative to its container.
 */
enum class ItemStatus {
    IN,
    OUT,
    REMOVED,
}

/**
 * An item that belongs to exactly one container.
 */
@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ContainerEntity::class,
            parentColumns = ["id"],
            childColumns = ["containerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("containerId")],
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String? = null,
    val status: ItemStatus = ItemStatus.IN,
    val containerId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val markedOutAt: Long? = null,
) {
    val statusIndicator: ItemStatusIndicator
        get() = when (status) {
            ItemStatus.IN -> ItemStatusIndicator.GREEN
            ItemStatus.OUT -> {
                val outAt = markedOutAt ?: createdAt
                val daysSinceOut = (System.currentTimeMillis() - outAt) / (24 * 60 * 60 * 1000L)
                if (daysSinceOut <= 14) ItemStatusIndicator.YELLOW else ItemStatusIndicator.RED
            }
            ItemStatus.REMOVED -> ItemStatusIndicator.GREEN
        }
}

/**
 * Visual indicator for item status (green / yellow / red dot).
 */
enum class ItemStatusIndicator {
    GREEN,   // In container
    YELLOW,  // Out, within last 14 days
    RED,     // Out, over 14 days
}
