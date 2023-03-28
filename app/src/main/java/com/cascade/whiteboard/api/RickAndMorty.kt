package com.cascade.whiteboard.api

import com.google.gson.JsonSyntaxException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class Personas(
    val info: PaginationInfo,
    val results: List<Persona>
)

data class PaginationInfo(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val previous: String? = null
)

data class Persona(
    val name: String,
    val status: String,
    val species: String,
    val origin: LocationBrief,
    val location: LocationBrief,
    val image: String,
    val url: String
)

data class LocationBrief(
    val name: String,
    val url: String
)

data class Locations(
    val info: PaginationInfo,
    val results: List<Location>
)

data class Location(
    val name: String,
    val type: String,
    val url: String,
    val dimension: String,
    val residents: List<String>
)

data class Episodes(
    val info: PaginationInfo,
    val results: List<Episode>
)

data class Episode(
    val name: String,
    val air_date: String,
    val episode: String,
    val url: String,
    val characters: List<String>
)

class RickAndMorty: Base() {
    suspend fun personas(
        pageUrl: String = "https://rickandmortyapi.com/api/character"
    ): Personas = suspendCoroutine { cont ->
        Get(
            pageUrl,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Personas::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun persona(
        url: String
    ): Persona = suspendCoroutine { cont ->
        Get(
            url,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Persona::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun locations(
        pageUrl: String = "https://rickandmortyapi.com/api/location"
    ): Locations = suspendCoroutine { cont ->
        Get(
            pageUrl,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Locations::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun location(
        url: String
    ): Location = suspendCoroutine { cont ->
        Get(
            url,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Location::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun episodes(
        pageUrl: String = "https://rickandmortyapi.com/api/episode"
    ): Episodes = suspendCoroutine { cont ->
        Get(
            pageUrl,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Episodes::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun episode(
        url: String
    ): Episode = suspendCoroutine { cont ->
        Get(
            url,
            successListener = { response ->
                try {
                    cont.resume(gson.fromJson(response, Episode::class.java))
                } catch (e: JsonSyntaxException) {
                    cont.resumeWithException(JSONException())
                }
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }

    suspend fun image(
        url: String
    ) = suspendCoroutine { cont ->
        ImageLoad(
            url,
            successListener = { response ->
                cont.resume(response)
            },
            errorListener = { _, _ ->
                cont.resumeWithException(APIConnectionException())
            }
        ).execute()
    }
}
