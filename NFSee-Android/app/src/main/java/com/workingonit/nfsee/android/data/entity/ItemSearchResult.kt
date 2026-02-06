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
    val containerName: String,
    val containerLocation: String?,
)
