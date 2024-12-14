package util

import kotlinx.serialization.json.Json

val DefaultJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}
