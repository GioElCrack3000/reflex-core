package com.reflexcore.game.utils

data class Vector2(var x: Float = 0f, var y: Float = 0f) {
    fun set(nx: Float, ny: Float) {
        x = nx
        y = ny
    }
    fun copy(): Vector2 = Vector2(x, y)
}
