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
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.RickAndMorty
import com.cascade.whiteboard.models.PersonaModel
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun PersonaScreen(
    personaUrl: String,
    onBack: () -> Unit,
    onLocationClick: (String) -> Unit,
    model: PersonaModel
) {
    val persona by model.persona

    LaunchedEffect("load") {
        model.load(personaUrl)
    }

    PersonaScreenContent(
        persona = persona,
        onBack = onBack,
        onLocationClick = onLocationClick
    )
}

@Composable
private fun PersonaScreenContent(
    persona: Persona?,
    onBack: () -> Unit,
    onLocationClick: (String) -> Unit
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
            Crossfade(targetState = persona) {
                if (it == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    PersonaContent(
                        persona = it,
                        onLocationClick = onLocationClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Composable
private fun PersonaContent(
    persona: Persona,
    onLocationClick: (String) -> Unit
) {
    var image by remember { mutableStateOf<Bitmap?>(null)}

    LaunchedEffect("load-image") {
        image = try {
            RickAndMorty().image(persona.image)
        } catch (e: Throwable) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = persona.name,
            style = MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.height(10.dp))
        Crossfade(targetState = image) {
            when (it) {
                is Bitmap -> Image(
                    modifier = Modifier.size(200.dp),
                    bitmap = it.asImageBitmap(),
                    contentDescription = null
                )
                null -> Box(
                    modifier = Modifier
                        .size(120.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
            }
        }
        Text(
            text = persona.species,
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = persona.status,
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(10.dp))
        PersonaLocationInsert(
            label = stringResource(id = R.string.character_screen__origin),
            name = persona.origin.name,
            onClick = { onLocationClick(persona.origin.url) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PersonaLocationInsert(
            label = stringResource(id = R.string.character_screen__location),
            name = persona.location.name,
            onClick = { onLocationClick(persona.location.url) }
        )
    }
}

@Composable
private fun PersonaLocationInsert(
    label: String,
    name: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 5.dp,
        shape = RoundedCornerShape(size = 10.dp),
        color = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = label)
            Text(
                text = name,
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPersonaScreenContent() {
    CodeChallengeTheme {
        PersonaScreenContent(
            persona = Persona(
                name = "Rick",
                status = "Alive",
                species = "Human",
                url = "",
                origin = LocationBrief(
                    name = "Earth",
                    url = ""
                ),
                location = LocationBrief(
                    name = "Earth",
                    url = ""
                ),
                image = ""
            ),
            onBack = {},
            onLocationClick = {}
        )
    }
}
