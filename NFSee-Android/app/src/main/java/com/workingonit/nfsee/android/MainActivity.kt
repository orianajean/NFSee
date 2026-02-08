package com.workingonit.nfsee.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.workingonit.nfsee.android.data.dao.ContainerDao
import com.workingonit.nfsee.android.data.dao.ItemDao
import com.workingonit.nfsee.android.data.SampleData
import com.workingonit.nfsee.android.ui.ContainerDetailScreen
import com.workingonit.nfsee.android.ui.ContainerListScreen
import com.workingonit.nfsee.android.ui.theme.NFSeeAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = (application as NFSeeApplication).database
        setContent {
            NFSeeAndroidTheme {
                NFSeeApp(
                    containerDao = database.containerDao(),
                    itemDao = database.itemDao(),
                )
            }
        }
    }
}

@Composable
fun NFSeeApp(
    containerDao: ContainerDao,
    itemDao: ItemDao,
) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        SampleData.seedIfEmpty(containerDao, itemDao)
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "containers",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("containers") {
                ContainerListScreen(
                    containerDao = containerDao,
                    itemDao = itemDao,
                    navController = navController,
                )
            }
            composable("container/{containerId}") { backStackEntry ->
                val containerId = backStackEntry.arguments?.getString("containerId")?.toLongOrNull()
                if (containerId != null) {
                    ContainerDetailScreen(
                        containerId = containerId,
                        containerDao = containerDao,
                        itemDao = itemDao,
                        navController = navController,
                    )
                }
            }
        }
    }
}
