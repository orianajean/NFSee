package com.workingonit.nfsee.android.data

import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import com.workingonit.nfsee.android.data.entity.ItemEntity
import com.workingonit.nfsee.android.data.entity.ItemStatus
import kotlinx.coroutines.flow.first

/**
 * Seeds sample containers and items when the database is empty.
 * Use for testing search, status filter, and Out/In toggle.
 */
object SampleData {
    suspend fun seedIfEmpty(
        containerDao: ContainerDao,
        itemDao: ItemDao,
    ) {
        val containers = containerDao.getAllContainers().first()
        if (containers.isNotEmpty()) return

        val garageId = containerDao.insert(
            ContainerEntity(name = "Garage – Blue Bin #1", locationLabel = "Garage"),
        )
        val kitchenId = containerDao.insert(
            ContainerEntity(name = "Kitchen Drawer", locationLabel = "Kitchen"),
        )
        val closetId = containerDao.insert(
            ContainerEntity(name = "Closet Shelf", locationLabel = "Bedroom"),
        )

        val threeDaysAgo = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000)
        val fifteenDaysAgo = System.currentTimeMillis() - (15L * 24 * 60 * 60 * 1000)

        // Garage items (Extension cord = yellow = out < 14 days)
        itemDao.insert(ItemEntity(name = "Power drill", category = "Tools", status = ItemStatus.IN, containerId = garageId))
        itemDao.insert(ItemEntity(name = "Extension cord", category = "Tools", status = ItemStatus.OUT, containerId = garageId, markedOutAt = threeDaysAgo))
        itemDao.insert(ItemEntity(name = "Paint brushes", category = "Supplies", status = ItemStatus.IN, containerId = garageId))

        // Kitchen items
        itemDao.insert(ItemEntity(name = "Measuring tape", category = "Tools", status = ItemStatus.IN, containerId = kitchenId))
        itemDao.insert(ItemEntity(name = "Screwdriver set", category = "Tools", status = ItemStatus.IN, containerId = kitchenId))

        // Closet items (Holiday lights = red = out > 14 days)
        itemDao.insert(ItemEntity(name = "Holiday lights", category = "Decor", status = ItemStatus.OUT, containerId = closetId, markedOutAt = fifteenDaysAgo))
        itemDao.insert(ItemEntity(name = "Winter gloves", category = "Clothing", status = ItemStatus.IN, containerId = closetId))
        itemDao.insert(ItemEntity(name = "Photo albums", category = "Memorabilia", status = ItemStatus.IN, containerId = closetId))
    }
}
