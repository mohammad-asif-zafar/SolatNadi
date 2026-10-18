package com.hathway.solatnadi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform