package com.cascade.whiteboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cascade.whiteboard.R
import com.cascade.whiteboard.api.*
import com.cascade.whiteboard.models.LocationsModel
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme

@Composable
fun LocationsScreen(
    model: LocationsModel,
    onBack: () -> Unit,
    onLocationClick: (String) -> Unit
) {
    val loading by model.state
    val locations by snapshotFlow { model.locations.toMutableList() }.collectAsState(initial = listOf())
    val info by model.info

    LaunchedEffect("load") {
        model.load()
    }

    LocationsScreenContent(
        onBack = onBack,
        loading = loading,
        locations = locations,
        info = info,
        onLocationClick = onLocationClick,
        onLoadMoreClick = { model.load(it) }
    )
}

@Composable
private fun LocationsScreenContent(
    onBack: () -> Unit,
    loading: Boolean,
    locations: List<Location>?,
    info: PaginationInfo?,
    onLocationClick: (String) -> Unit,
    onLoadMoreClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                IconButton(onClick = onBack) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.button__locations),
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    locations?.forEach {
                        /**
                         * TODO: Style this in the same way the personas in [PersonasScreen] are displayed
                         */
                        LocationInsert(
                            location = it,
                            onClick = onLocationClick
                        )
                    }
                }
                if (loading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                if (info != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { info.next?.let { onLoadMoreClick(it) } },
                        enabled = info.next != null
                    ) {
                        Text(text = stringResource(id = R.string.button__load_more))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Composable
private fun LocationInsert(
    location: Location,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(location.url)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = location.name,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCharactersScreenContent() {
    CodeChallengeTheme {
        LocationsScreenContent(
            onBack = {},
            loading = false,
            locations = null,
            info = null,
            onLocationClick = {},
            onLoadMoreClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewLocationsScreenContentLoading() {
    CodeChallengeTheme {
        LocationsScreenContent(
            onBack = {},
            loading = true,
            locations = null,
            info = null,
            onLocationClick = {},
            onLoadMoreClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewLocationsScreenContentLoaded() {
    CodeChallengeTheme {
        LocationsScreenContent(
            onBack = {},
            loading = false,
            locations = listOf(
                Location(
                    name = "Earth",
                    type = "",
                    url = "",
                    dimension = "",
                    residents = listOf()
                ),
                Location(
                    name = "Blah",
                    type = "",
                    url = "",
                    dimension = "",
                    residents = listOf()
                ),
                Location(
                    name = "Some other planet",
                    type = "",
                    url = "",
                    dimension = "",
                    residents = listOf()
                )
            ),
            info = PaginationInfo(count = 1, pages = 2, next = ""),
            onLocationClick = {},
            onLoadMoreClick = {}
        )
    }
}
