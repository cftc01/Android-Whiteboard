package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Location
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationModel: ViewModel() {
    val loading = mutableStateOf(false)
    val loadingResidents = mutableStateOf(false)
    val location = mutableStateOf<Location?>(null)
    val personas = mutableStateListOf<Persona>()

    fun load(url: String) {
        loading.value = true

        viewModelScope.launch {
            try {
                val result = RickAndMorty().location(url)
                location.value = result
                loadResidents(result.residents)
                loading.value = false
            } catch (e: Throwable) {
                loading.value = false
            }
        }
    }

    private fun loadResidents(residents: List<String>) {
        loadingResidents.value = true

        viewModelScope.launch {
            try {
                residents.forEach {
                    personas.add(RickAndMorty().persona(it))
                    delay(500)
                }
                loadingResidents.value = false
            } catch (_: Throwable) {
                loadingResidents.value = false
            }
        }
    }
}
