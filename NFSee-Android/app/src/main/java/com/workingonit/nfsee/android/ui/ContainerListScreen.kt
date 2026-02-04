package com.workingonit.nfsee.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.entity.ContainerEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerListScreen(
    containerDao: ContainerDao,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val containers by containerDao.getAllContainers().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Containers") },
                actions = {
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
        if (containers.isEmpty()) {
            Text(
                text = "No containers yet. Tap + to add one.",
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
