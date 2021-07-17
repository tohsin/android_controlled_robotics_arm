package com.timilehinaregbesola.remoteaccessroboarm

fun String.toCommandCode(): Int {
    return when (this) {
        "Base" -> 1
        "Left" -> 2
        "Right" -> 3
        "Grip" -> 4
        "Ungrip" -> 5
        "Timer" -> 6
        else -> 1
    }
}

fun Int.fromCommandCode(): String {
    return when (this) {
        1 -> "Base"
        2 -> "Left"
        3 -> "Right"
        4 -> "Grip"
        5 -> "Ungrip"
        6 -> "Timer"
        else -> "Base"
    }
}
