package com.workingonit.nfsee.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import kotlinx.coroutines.launch

enum class StatusFilterOption(val displayName: String, val roomValue: String?) {
    ALL("All", null),
    IN("In", "IN"),
    OUT("Out", "OUT"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerListScreen(
    containerDao: ContainerDao,
    itemDao: ItemDao,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val containers by containerDao.getAllContainers().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf(StatusFilterOption.ALL) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val isSearching = searchQuery.isNotBlank() || statusFilter != StatusFilterOption.ALL

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Containers") },
                actions = {
                    Box {
                        IconButton(
                            onClick = { filterMenuExpanded = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Filter by status",
                            )
                        }
                        DropdownMenu(
                            expanded = filterMenuExpanded,
                            onDismissRequest = { filterMenuExpanded = false },
                        ) {
                            StatusFilterOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        statusFilter = option
                                        filterMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                containerDao.insert(
                                    ContainerEntity(name = "New Container"),
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Container",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search items, categories, containers") },
                singleLine = true,
            )
            if (isSearching) {
                SearchResultsContent(
                    searchQuery = searchQuery,
                    statusFilter = statusFilter.roomValue,
                    itemDao = itemDao,
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (containers.isEmpty()) {
                Text(
                    text = "No containers yet. Tap + to add one.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = containers,
                        key = { it.id },
                    ) { container ->
                        ListItem(
                            headlineContent = { Text(container.name) },
                            supportingContent = container.locationLabel?.let { location ->
                                { Text(location) }
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            containerDao.delete(container)
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete container",
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("container/${container.id}")
                            },
                        )
                    }
                }
            }
        }
    }
}
