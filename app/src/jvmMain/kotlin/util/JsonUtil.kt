package util

import kotlinx.serialization.json.Json

val DefaultJson = Json {
    ignoreUnknownKeys = true
}
