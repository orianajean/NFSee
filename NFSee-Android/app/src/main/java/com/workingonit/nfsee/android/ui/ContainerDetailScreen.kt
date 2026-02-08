package com.workingonit.nfsee.android.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import com.workingonit.nfsee.android.data.entity.ItemEntity
import com.workingonit.nfsee.android.data.entity.ItemStatus
import com.workingonit.nfsee.android.data.entity.ItemStatusIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerDetailScreen(
    containerId: Long,
    containerDao: ContainerDao,
    itemDao: ItemDao,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var container by remember { mutableStateOf<ContainerEntity?>(null) }
    LaunchedEffect(containerId) {
        container = containerDao.getContainerById(containerId)
    }
    val items by itemDao.getItemsByContainer(containerId).collectAsState(initial = emptyList())

    val c = container
    if (c == null) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(c.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                itemDao.insert(
                                    ItemEntity(
                                        name = "New Item",
                                        containerId = c.id,
                                    ),
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Item",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        if (items.isEmpty()) {
            Text(
                text = "No items yet. Tap + to add one.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(
                    items = items,
                    key = { it.id },
                ) { item ->
                    ItemRow(
                        item = item,
                        onLongPress = {
                            if (item.status == ItemStatus.IN) {
                                scope.launch {
                                    itemDao.update(
                                        item.copy(
                                            status = ItemStatus.OUT,
                                            markedOutAt = System.currentTimeMillis(),
                                        ),
                                    )
                                }
                            } else {
                                scope.launch {
                                    itemDao.update(
                                        item.copy(
                                            status = ItemStatus.IN,
                                            markedOutAt = null,
                                        ),
                                    )
                                }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                itemDao.delete(item)
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemRow(
    item: ItemEntity,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(indicator = item.statusIndicator)
                Text(item.name, modifier = Modifier.padding(start = 12.dp))
            }
        },
        supportingContent = {
            item.category?.let { Text(it) }
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item",
                )
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = { },
            onLongClick = onLongPress,
        ),
    )
}

@Composable
fun StatusDot(
    indicator: ItemStatusIndicator,
    modifier: Modifier = Modifier,
) {
    val color = when (indicator) {
        ItemStatusIndicator.GREEN -> Color(0xFF4CAF50)
        ItemStatusIndicator.YELLOW -> Color(0xFFFFC107)
        ItemStatusIndicator.RED -> Color(0xFFE53935)
    }
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}
