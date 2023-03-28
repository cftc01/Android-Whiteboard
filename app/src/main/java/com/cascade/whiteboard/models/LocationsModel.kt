package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Location
import com.cascade.whiteboard.api.PaginationInfo
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.launch

class LocationsModel: ViewModel() {
    val state = mutableStateOf(false)
    val locations = mutableStateListOf<Location>()
    val info = mutableStateOf<PaginationInfo?>(null)

    fun load(
        next: String? = null
    ) {
        state.value = true
        viewModelScope.launch {
            try {
                val result = next?.let { RickAndMorty().locations(pageUrl = it) } ?: RickAndMorty().locations()

                if (next == null) locations.removeAll { true }
                locations.addAll(result.results)

                info.value = result.info

                state.value = false
            } catch (e: Throwable) {
                state.value = false
            }
        }
    }
}
