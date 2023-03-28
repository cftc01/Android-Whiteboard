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
import com.cascade.whiteboard.api.Location
import com.cascade.whiteboard.api.LocationBrief
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.models.LocationModel
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme

@Composable
fun LocationScreen(
    locationUrl: String,
    onBack: () -> Unit,
    onResidentClick: (String) -> Unit,
    model: LocationModel
) {
    val location by model.location
    val residents by snapshotFlow { model.personas.toList() }.collectAsState(initial = listOf())

    LaunchedEffect("load") {
        model.load(locationUrl)
    }

    LocationScreenContent(
        location = location,
        residents = residents,
        onBack = onBack,
        onResidentClick = onResidentClick
    )
}

@Composable
private fun LocationScreenContent(
    location: Location?,
    residents: List<Persona>,
    onBack: () -> Unit,
    onResidentClick: (String) -> Unit
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
            if (location == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LocationContent(
                    location = location,
                    residents = residents,
                    onResidentClick = onResidentClick
                )
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Composable
private fun LocationContent(
    location: Location,
    residents: List<Persona>,
    onResidentClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = location.name,
            style = MaterialTheme.typography.h3.copy(
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.location_screen__type, location.type),
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.location_screen__dimension, location.dimension),
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.location_screen__residents),
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
            residents.forEach {
                PersonaInsert(
                    persona = it,
                    onClick = onResidentClick
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewLocationScreenContent() {
    CodeChallengeTheme {
        LocationScreenContent(
            location = Location(
                name = "Some Location",
                url = "",
                residents = listOf("", "", ""),
                dimension = "Dimension",
                type = "Planet"
            ),
            residents = listOf(
                Persona(
                    name = "Someone",
                    location = LocationBrief(
                        name = "",
                        url = ""
                    ),
                    url = "",
                    image = "",
                    origin = LocationBrief(
                        name = "",
                        url = ""
                    ),
                    species = "",
                    status = ""
                )
            ),
            onBack = {},
            onResidentClick = {}
        )
    }
}
