package com.cascade.whiteboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cascade.whiteboard.api.Episode
import com.cascade.whiteboard.api.PaginationInfo
import com.cascade.whiteboard.models.EpisodesModel
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme

@Composable
fun EpisodesScreen(
    model: EpisodesModel,
    onBack: () -> Unit,
    onEpisodeClick: (String) -> Unit
) {
    val loading by model.state
    val episodes by snapshotFlow { model.episodes.toMutableList() }.collectAsState(initial = listOf())
    val info by model.info

    LaunchedEffect("load") {
        model.load()
    }

    EpisodesScreenContent(
        onBack = onBack,
        loading = loading,
        episodes = episodes,
        info = info,
        onEpisodeClick = onEpisodeClick,
        onLoadMoreClick = { model.load(it) }
    )
}

@Composable
private fun EpisodesScreenContent(
    onBack: () -> Unit,
    loading: Boolean,
    episodes: List<Episode>?,
    info: PaginationInfo?,
    onEpisodeClick: (String) -> Unit,
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
                        painter = painterResource(id = com.cascade.whiteboard.R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = com.cascade.whiteboard.R.string.button__locations),
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
                    /**
                     * TODO: Display a list of episodes and make each episode clickable
                     */
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
                        Text(text = stringResource(id = com.cascade.whiteboard.R.string.button__load_more))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Preview
@Composable
private fun PreviewEpisodesScreenContentLoading() {
    CodeChallengeTheme {
        EpisodesScreenContent(
            onBack = {},
            loading = true,
            episodes = null,
            info = null,
            onEpisodeClick = {},
            onLoadMoreClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewEpisodesScreenContentLoaded() {
    CodeChallengeTheme {
        EpisodesScreenContent(
            onBack = {},
            loading = false,
            episodes = listOf(
                Episode(
                    name = "Pilot",
                    air_date = "December 2, 2013",
                    episode = "S01E01",
                    characters = listOf(),
                    url = ""
                ),
                Episode(
                    name = "Lawnmower Dog",
                    air_date = "December 9, 2013",
                    episode = "S01E02",
                    characters = listOf(),
                    url = ""
                ),
                Episode(
                    name = "Anatomy Park",
                    air_date = "December 16, 2013",
                    episode = "S01E03",
                    characters = listOf(),
                    url = ""
                )
            ),
            info = PaginationInfo(count = 51, pages = 3, next = ""),
            onEpisodeClick = {},
            onLoadMoreClick = {}
        )
    }
}
