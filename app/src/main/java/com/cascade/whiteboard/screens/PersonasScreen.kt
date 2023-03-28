package com.cascade.whiteboard.screens

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cascade.whiteboard.R
import com.cascade.whiteboard.api.LocationBrief
import com.cascade.whiteboard.api.PaginationInfo
import com.cascade.whiteboard.models.PersonasModel
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.RickAndMorty
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun PersonasScreen(
    model: PersonasModel,
    onBack: () -> Unit,
    onPersonaClick: (String) -> Unit
) {
    val loading by model.loading
    val personas by snapshotFlow { model.personas.toMutableList() }.collectAsState(initial = listOf())
    val info by model.info

    LaunchedEffect("load") {
        model.load()
    }

    PersonasScreenContent(
        onBack = onBack,
        loading = loading,
        personas = personas,
        info = info,
        onPersonaClick = onPersonaClick,
        onLoadMoreClick = { model.load(it) }
    )
}

@Composable
private fun PersonasScreenContent(
    onBack: () -> Unit,
    loading: Boolean,
    personas: List<Persona>?,
    info: PaginationInfo?,
    onPersonaClick: (String) -> Unit,
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
                text = stringResource(id = R.string.button__characters),
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
                    personas?.forEach {
                        PersonaInsert(
                            persona = it,
                            onClick = onPersonaClick
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
fun PersonaInsert(
    persona: Persona,
    onClick: (String) -> Unit
) {
    var image by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect("image-load") {
        image = try {
            RickAndMorty().image(persona.image)
        } catch (e: Throwable) {
            null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(persona.url)
            },
        elevation = 5.dp,
        shape = RoundedCornerShape(size = 10.dp),
        color = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Crossfade(targetState = image) {
                when (it) {
                    is Bitmap -> Image(
                        modifier = Modifier.size(30.dp),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null
                    )
                    null -> Box(
                        modifier = Modifier
                            .size(30.dp)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer()
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = persona.name,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPersonasScreenContent() {
    CodeChallengeTheme {
        PersonasScreenContent(
            onBack = {},
            loading = false,
            personas = null,
            info = null,
            onPersonaClick = {},
            onLoadMoreClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPersonasScreenContentLoading() {
    CodeChallengeTheme {
        PersonasScreenContent(
            onBack = {},
            loading = true,
            personas = null,
            info = null,
            onPersonaClick = {},
            onLoadMoreClick = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPersonasScreenContentLoaded() {
    CodeChallengeTheme {
        PersonasScreenContent(
            onBack = {},
            loading = false,
            personas = listOf(
                Persona(
                    name = "Rick Sanchez",
                    image = "",
                    location = LocationBrief(name = "", url = ""),
                    origin = LocationBrief(name = "", url = ""),
                    url = "",
                    species = "",
                    status = ""
                ),
                Persona(
                    name = "Morty",
                    image = "",
                    location = LocationBrief(name = "", url = ""),
                    origin = LocationBrief(name = "", url = ""),
                    url = "",
                    species = "",
                    status = ""
                ),
                Persona(
                    name = "Summer",
                    image = "",
                    location = LocationBrief(name = "", url = ""),
                    origin = LocationBrief(name = "", url = ""),
                    url = "",
                    species = "",
                    status = ""
                )
            ),
            info = PaginationInfo(count = 1, pages = 2, next = ""),
            onPersonaClick = {},
            onLoadMoreClick = {}
        )
    }
}
