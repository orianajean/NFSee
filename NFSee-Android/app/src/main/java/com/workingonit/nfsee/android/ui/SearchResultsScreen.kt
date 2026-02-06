package com.workingonit.nfsee.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.entity.ItemSearchResult
import com.workingonit.nfsee.android.data.entity.ItemStatus

/**
 * Reusable search results list for embedding in other screens.
 */
@Composable
fun SearchResultsContent(
    searchQuery: String,
    statusFilter: String?,
    itemDao: ItemDao,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val results by itemDao.searchItems(
        query = searchQuery.ifBlank { null },
        statusFilter = statusFilter,
    ).collectAsState(initial = emptyList())

    if (results.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No items found",
                modifier = Modifier.padding(16.dp),
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(items = results, key = { it.id }) { result ->
                SearchResultRow(
                    result = result,
                    onClick = { navController.navigate("container/${result.containerId}") },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String,
    statusFilter: String?,
    itemDao: ItemDao,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val results by itemDao.searchItems(
        query = searchQuery.ifBlank { null },
        statusFilter = statusFilter,
    ).collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Search") },
            )
        },
    ) { paddingValues ->
        if (results.isEmpty()) {
            Text(
                text = "No items found",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(
                    items = results,
                    key = { it.id },
                ) { result ->
                    SearchResultRow(
                        result = result,
                        onClick = { navController.navigate("container/${result.containerId}") },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    result: ItemSearchResult,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(result.name)
                if (result.status == ItemStatus.OUT) {
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF9800),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .padding(vertical = 2.dp)
                            .background(
                                Color(0xFFFF9800).copy(alpha = 0.2f),
                                RoundedCornerShape(4.dp),
                            ),
                    )
                }
            }
        },
        supportingContent = {
            Text(result.containerName)
            result.containerLocation?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
