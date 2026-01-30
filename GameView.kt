package com.reflexcore.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.view.MotionEvent
import com.reflexcore.game.objects.Player
import com.reflexcore.game.objects.Obstacle
import com.reflexcore.game.core.GameState
import com.reflexcore.game.core.Constants
import kotlin.math.abs
import java.util.concurrent.CopyOnWriteArrayList

class GameView(context: Context) : SurfaceView(context), Runnable {

    private var thread: Thread? = null
    @Volatile private var running = false
    private val surfaceHolder: SurfaceHolder = holder
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var direction = 0
    private lateinit var player: Player
    private var centerX = 0f
    private var centerY = 0f

    private val obstacles = CopyOnWriteArrayList<Obstacle>()
    private var spawnTimer = 0
    private var gameState = GameState.PLAYING

    private var score = 0
    private var frameCounter = 0
    private var difficultyLevel = 1
    private var highScore = 0

    private var shakeTime = 0
    private var flash = 0

    private var deathCount = 0

    init {
        // load highscore
        val prefs = context.getSharedPreferences("reflex_core", Context.MODE_PRIVATE)
        highScore = prefs.getInt("high_score", 0)
    }

    fun resume() {
        running = true
        thread = Thread(this)
        thread!!.start()
    }

    fun pause() {
        running = false
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            // ignore
        }
    }

    override fun run() {
        // initial layout-safe init
        while (width == 0 || height == 0) {
            try { Thread.sleep(10) } catch (e: InterruptedException) {}
        }
        centerX = width / 2f
        centerY = height / 2f
        player = Player(centerX, centerY)

        while (running) {
            if (!surfaceHolder.surface.isValid) continue

            val start = System.currentTimeMillis()

            update()
            val canvas = surfaceHolder.lockCanvas()
            drawGame(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)

            val took = System.currentTimeMillis() - start
            val delay = (1000 / Constants.FPS) - took
            if (delay > 0) {
                try { Thread.sleep(delay) } catch (e: InterruptedException) {}
            }
        }
    }

    private fun update() {
        if (gameState == GameState.PLAYING) {
            // spawn logic
            spawnTimer++
            if (spawnTimer > maxOf(30, 120 - difficultyLevel*8)) {
                obstacles.add(Obstacle(centerX, centerY, difficultyLevel))
                spawnTimer = 0
            }

            // update player and obstacles
            player.update(direction)

            for (ob in obstacles) {
                ob.update()
                if (ob.isColliding(player.position.x, player.position.y)) {
                    gameOver()
                    return
                }
                if (ob.isOffScreen()) {
                    obstacles.remove(ob)
                } else {
                    // near-miss flash feedback
                    val playerDist = kotlin.math.hypot(player.position.x - centerX, player.position.y - centerY)
                    if (kotlin.math.abs(playerDist - ob.radius) < 8f) {
                        flash = 3
                    }
                }
            }

            // score by time
            frameCounter++
            if (frameCounter % Constants.FPS == 0) {
                score++
                // difficulty scale
                if (score > 0 && score % 10 == 0) {
                    difficultyLevel = 1 + (score / 10)
                }
            }
        }
        // reduce shakes/flash
        if (shakeTime > 0) shakeTime--
        if (flash > 0) flash--
    }

    private fun drawGame(canvas: Canvas) {
        // background with slight dynamic
        val bgShade = 10 + (score % 60)
        canvas.drawColor(Color.rgb(0, bgShade, bgShade + 10))

        // apply screen shake
        if (shakeTime > 0) {
            val dx = (Math.random() * 12 - 6).toFloat()
            val dy = (Math.random() * 12 - 6).toFloat()
            canvas.translate(dx, dy)
        }

        // draw obstacles
        for (ob in obstacles) {
            ob.draw(canvas, paint)
        }

        // draw player
        player.draw(canvas, paint)

        // flash overlay
        if (flash > 0) {
            paint.color = Color.argb(30, 255, 255, 255)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }

        // HUD
        paint.color = Color.WHITE
        paint.textSize = 56f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(score.toString(), centerX, 90f, paint)

        if (gameState == GameState.GAME_OVER) {
            paint.textSize = 72f
            canvas.drawText("GAME OVER", centerX, centerY, paint)
            paint.textSize = 36f
            canvas.drawText("BEST $highScore", centerX, centerY + 60f, paint)
        }
    }

    private fun gameOver() {
        gameState = GameState.GAME_OVER
        shakeTime = 12
        deathCount++
        // save highscore
        val prefs = context.getSharedPreferences("reflex_core", Context.MODE_PRIVATE)
        if (score > highScore) {
            highScore = score
            prefs.edit().putInt("high_score", highScore).apply()
        }
    }

    private fun resetGame() {
        obstacles.clear()
        score = 0
        frameCounter = 0
        spawnTimer = 0
        difficultyLevel = 1
        gameState = GameState.PLAYING
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                direction = if (event.x < width / 2f) -1 else 1
                // if game over -> restart on touch
                if (gameState == GameState.GAME_OVER) {
                    resetGame()
                }
            }
            MotionEvent.ACTION_UP -> {
                direction = 0
            }
        }
        return true
    }
}
