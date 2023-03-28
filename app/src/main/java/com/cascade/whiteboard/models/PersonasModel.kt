package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Persona
import com.cascade.whiteboard.api.PaginationInfo
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.launch

class PersonasModel: ViewModel() {
    val loading = mutableStateOf(false)
    val personas = mutableStateListOf<Persona>()
    val info = mutableStateOf<PaginationInfo?>(null)

    fun load(
        next: String? = null
    ) {
        loading.value = true
        viewModelScope.launch {
            try {
                val result = next?.let { RickAndMorty().personas(pageUrl = it) } ?: RickAndMorty().personas()

                if (next == null) personas.removeAll { true }
                personas.addAll(result.results)

                info.value = result.info

                loading.value = false
            } catch (e: Throwable) {
                loading.value = false
            }
        }
    }
}
