package com.reflexcore.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import kotlin.math.*
import kotlin.random.Random

class Obstacle(
    private val centerX: Float,
    private val centerY: Float,
    private val difficulty: Int
) {

    var radius = 900f
    private val baseSpeed = 3.0f
    private val speed = (baseSpeed + difficulty * 0.6f).toFloat()
    private val gapAngle: Float = Random.nextFloat() * (2 * Math.PI).toFloat()
    private val gapSize = (Math.PI / 4f).toFloat() // gap radians
    private val stroke = 12f
    private val type = Random.nextInt(0, 2)

    fun update() {
        radius -= speed
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = stroke
        paint.color = Color.rgb(255, 90, 90)

        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        val startDeg = Math.toDegrees((gapAngle + gapSize).toDouble()).toFloat()
        val sweepDeg = Math.toDegrees((2 * Math.PI - gapSize * 2).toDouble()).toFloat()

        if (type == 0) {
            canvas.drawArc(rect, startDeg, sweepDeg, false, paint)
        } else {
            val start2 = Math.toDegrees(gapAngle.toDouble()).toFloat()
            val sweep2 = Math.toDegrees((Math.PI - gapSize).toDouble()).toFloat()
            canvas.drawArc(rect, start2, sweep2, false, paint)
        }
    }

    fun isColliding(playerX: Float, playerY: Float): Boolean {
        val dx = playerX - centerX
        val dy = playerY - centerY
        val dist = sqrt(dx * dx + dy * dy)
        // collision when near the ring AND not in the gap angular area
        if (abs(dist - radius) < 18f) {
            // calculate angle of player relative to center
            val ang = atan2(playerY - centerY, playerX - centerX)
            // normalize gap angles
            var ga = gapAngle
            var gapStart = (ga - gapSize/2f)
            var gapEnd = (ga + gapSize/2f)

            fun inRange(a: Float, start: Float, end: Float): Boolean {
                var s = start; var e = end; var x = a
                while (s < -PI) { s += (2*PI).toFloat() }
                while (s > PI) { s -= (2*PI).toFloat() }
                while (e < -PI) { e += (2*PI).toFloat() }
                while (e > PI) { e -= (2*PI).toFloat() }
                while (x < -PI) { x += (2*PI).toFloat() }
                while (x > PI) { x -= (2*PI).toFloat() }
                if (s <= e) return x >= s && x <= e
                return x >= s || x <= e
            }
            if (!inRange(ang.toFloat(), gapStart, gapEnd)) {
                return true
            }
        }
        return false
    }

    fun isOffScreen(): Boolean = radius < 0
}
