package com.workingonit.nfsee.android.data.entity

/**
 * Search result combining item with its container's name and location.
 */
data class ItemSearchResult(
    val id: Long,
    val name: String,
    val category: String?,
    val status: ItemStatus,
    val containerId: Long,
    val createdAt: Long,
    val markedOutAt: Long?,
    val containerName: String,
    val containerLocation: String?,
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
