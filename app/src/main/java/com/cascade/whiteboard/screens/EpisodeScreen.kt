package com.cascade.whiteboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cascade.whiteboard.R
import com.cascade.whiteboard.api.Episode
import com.cascade.whiteboard.api.LocationBrief
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.models.EpisodeModel
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme

@Composable
fun EpisodeScreen(
    episodeUrl: String,
    onBack: () -> Unit,
    onPersonaClick: (String) -> Unit,
    model: EpisodeModel
) {
    val episode by model.episode
    val personas by snapshotFlow { model.personas.toList() }.collectAsState(initial = listOf())

    LaunchedEffect("load") {
        model.load(episodeUrl)
    }

    EpisodeScreenContent(
        episode = episode,
        personas = personas,
        onBack = onBack,
        onPersonaClick = onPersonaClick
    )
}

@Composable
private fun EpisodeScreenContent(
    episode: Episode?,
    personas: List<Persona>,
    onBack: () -> Unit,
    onPersonaClick: (String) -> Unit
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
            if (episode == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                EpisodeContent(
                    episode = episode,
                    personas = personas,
                    onPersonaClick = onPersonaClick
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Composable
private fun EpisodeContent(
    episode: Episode,
    personas: List<Persona>,
    onPersonaClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = episode.name,
            style = MaterialTheme.typography.h3.copy(
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.episode_screen__air_date, episode.air_date),
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.episode_screen__episode, episode.episode),
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.button__characters),
            style = MaterialTheme.typography.h5.copy(
                textAlign = TextAlign.Left
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            personas.forEach {
                PersonaInsert(
                    persona = it,
                    onClick = onPersonaClick
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewEpisodeScreenContent() {
    CodeChallengeTheme {
        EpisodeScreenContent(
            episode = Episode(
                name = "Pilot",
                air_date = "December 2, 2013",
                episode = "S01E01",
                characters = listOf(),
                url = ""
            ),
            personas = listOf(
                Persona(
                    name = "Rick Sanchez",
                    location = LocationBrief(
                        name = "Citadel of Ricks",
                        url = ""
                    ),
                    url = "",
                    image = "",
                    origin = LocationBrief(
                        name = "Earth (C-137)",
                        url = ""
                    ),
                    species = "Human",
                    status = "Alive"
                ),
                Persona(
                    name = "Morty Smith",
                    location = LocationBrief(
                        name = "Citadel of Ricks",
                        url = ""
                    ),
                    url = "",
                    image = "",
                    origin = LocationBrief(
                        name = "unknown",
                        url = ""
                    ),
                    species = "Human",
                    status = "Alive"
                )
            ),
            onBack = {},
            onPersonaClick = {}
        )
    }
}
