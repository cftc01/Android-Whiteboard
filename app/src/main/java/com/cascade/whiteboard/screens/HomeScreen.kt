package com.cascade.whiteboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cascade.whiteboard.ui.theme.CodeChallengeTheme
import com.cascade.whiteboard.R

@Composable
fun HomeScreen(
    onCharactersClick: () -> Unit,
    onLocationsClick: () -> Unit,
    onEpisodesClick: () -> Unit
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
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.home_screen__title),
                style = MaterialTheme.typography.h2.copy(
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCharactersClick
            ) {
                Text(text = stringResource(id = R.string.button__characters))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLocationsClick
            ) {
                Text(text = stringResource(id = R.string.button__locations))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onEpisodesClick
            ) {
                Text(text = stringResource(id = R.string.button__episodes))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    CodeChallengeTheme {
        HomeScreen(
            onCharactersClick = {},
            onLocationsClick = {},
            onEpisodesClick = {}
        )
    }
}
