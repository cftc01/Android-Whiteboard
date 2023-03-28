package com.cascade.whiteboard.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cascade.whiteboard.api.Episode
import com.cascade.whiteboard.api.PaginationInfo
import com.cascade.whiteboard.api.RickAndMorty
import kotlinx.coroutines.launch

class EpisodesModel: ViewModel() {
    val state = mutableStateOf(false)
    val episodes = mutableStateListOf<Episode>()
    val info = mutableStateOf<PaginationInfo?>(null)

    fun load(
        next: String? = null
    ) {
        state.value = true
        viewModelScope.launch {
            try {
                val result = next?.let { RickAndMorty().episodes(pageUrl = it) } ?: RickAndMorty().episodes()

                if (next == null) episodes.removeAll { true }
                episodes.addAll(result.results)

                info.value = result.info

                state.value = false
            } catch (e: Throwable) {
                state.value = false
            }
        }
    }
}
