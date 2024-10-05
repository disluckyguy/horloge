package io.github.clock

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform