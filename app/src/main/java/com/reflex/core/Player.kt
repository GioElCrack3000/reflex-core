package com.reflexcore.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import com.reflexcore.game.utils.Vector2
import kotlin.math.*

class Player(
    private val centerX: Float,
    private val centerY: Float
) {

    private var angle = -1.5708f // start at top (-PI/2)
    val position = Vector2(0f, 0f)

    init {
        update(0)
    }

    fun update(direction: Int) {
        // direction: -1 left, 0 none, 1 right
        angle += direction * com.reflexcore.game.core.Constants.ROTATION_SPEED
        position.x = centerX + cos(angle) * com.reflexcore.game.core.Constants.ORBIT_RADIUS
        position.y = centerY + sin(angle) * com.reflexcore.game.core.Constants.ORBIT_RADIUS
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawCircle(position.x, position.y, com.reflexcore.game.core.Constants.PLAYER_RADIUS, paint)

        // small inner glow
        paint.color = Color.argb(80, 124, 5, 255)
        canvas.drawCircle(position.x, position.y, com.reflexcore.game.core.Constants.PLAYER_RADIUS * 1.6f, paint)
    }
}
