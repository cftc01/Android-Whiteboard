package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.launch

class PersonaModel: ViewModel() {
    val loading = mutableStateOf(false)
    val persona = mutableStateOf<Persona?>(null)

    fun load(url: String) {
        loading.value = true

        viewModelScope.launch {
            try {
                persona.value = RickAndMorty().persona(url)
                loading.value = false
            } catch (e: Throwable) {
                loading.value = false
            }
        }
    }
}
