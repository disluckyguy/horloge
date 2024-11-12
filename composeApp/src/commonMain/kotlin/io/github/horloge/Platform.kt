package io.github.horloge

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform