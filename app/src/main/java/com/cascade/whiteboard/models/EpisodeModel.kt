package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Episode
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EpisodeModel: ViewModel() {
    val loading = mutableStateOf(false)
    val loadingPersonas = mutableStateOf(false)
    val episode = mutableStateOf<Episode?>(null)
    val personas = mutableStateListOf<Persona>()

    fun load(url: String) {
        loading.value = true

        viewModelScope.launch {
            try {
                val result = RickAndMorty().episode(url)
                episode.value = result
                loadPersonas(result.characters)
                loading.value = false
            } catch (e: Throwable) {
                loading.value = false
            }
        }
    }

    private fun loadPersonas(characters: List<String>) {
        loadingPersonas.value = true

        viewModelScope.launch {
            try {
                characters.forEach {
                    personas.add(RickAndMorty().persona(it))
                    delay(500)
                }
                loadingPersonas.value = false
            } catch (_: Throwable) {
                loadingPersonas.value = false
            }
        }
    }
}
