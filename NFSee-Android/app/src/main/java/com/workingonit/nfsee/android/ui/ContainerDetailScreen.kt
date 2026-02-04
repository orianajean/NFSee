package com.workingonit.nfsee.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import com.workingonit.nfsee.android.data.entity.ItemEntity
import com.workingonit.nfsee.android.data.entity.ItemStatus
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

    if (container == null) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(container.name) },
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
                                        containerId = containerId,
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
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = {
                            if (item.category != null) {
                                Text(item.category!!)
                            }
                            if (item.status == ItemStatus.OUT) {
                                Text("Out", color = Color(0xFFFF9800))
                            }
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        itemDao.delete(item)
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
