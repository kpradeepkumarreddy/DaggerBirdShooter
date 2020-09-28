package com.pradeep.daggerbirdshooter.misc

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceView
import com.pradeep.daggerbirdshooter.R
import com.pradeep.daggerbirdshooter.activities.GameActivity
import com.pradeep.daggerbirdshooter.activities.MainActivity
import com.pradeep.daggerbirdshooter.game_objects.Bird
import com.pradeep.daggerbirdshooter.game_objects.Bullet
import com.pradeep.daggerbirdshooter.game_objects.GameBackground
import com.pradeep.daggerbirdshooter.game_objects.ShooterPlane
import java.util.*
import kotlin.math.abs

class GameView(
    private val gameActivity: GameActivity,
    private val screenWidth: Int,
    private val screenHeight: Int
) : SurfaceView(gameActivity), Runnable {

    private lateinit var thread: Thread
    private var isPlaying = false
    private var isGameOver = false
    private var paint = Paint()
    private val gameBackground1 = GameBackground(screenWidth, screenHeight, resources)
    private val gameBackground2 = GameBackground(screenWidth, screenHeight, resources)
    private val shooterPlane: ShooterPlane
    private val bullets: MutableList<Bullet> = mutableListOf()
    private val birds: Array<Bird>
    private val random = Random()
    private var score = 0
    private val soundPool = MainActivity.getSoundPool()
    private val bulletSound = soundPool.load(gameActivity, R.raw.bullet_sound, 1)
    private val birdsSound = soundPool.load(gameActivity, R.raw.birds_sound, 1)
    private val gameOverSound = soundPool.load(gameActivity, R.raw.game_over, 1)
    private val gestureDetector = GestureDetector(gameActivity, GameGesturesListener())
    private var swipeUpCount = 0
    private var swipeDownCount = 0
    private var birdsSoundStreamId = 0

    companion object {
        // following two variables are to make the game work with different device screen resolutions.
        // 1920 X 1080 is the screen resolution of my device
        var screenWidthPixelMultiplyFactor: Float = 0f
        var screenHeightPixelMultiplyFactor: Float = 0f
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
        private const val GAME_OVER_TEXT = "GAME OVER"
        private const val GAME_OVER_TEXT_HORIZONTAL_MARGIN = 25
        private const val GAME_OVER_TEXT_VERTICAL_MARGIN = 1
        private const val GAME_OVER_RECT_ROUNDED_CORNERS = 35f
    }

    init {
        gameBackground2.xCoordinate = screenWidth
        screenWidthPixelMultiplyFactor = 1920.div(screenWidth.toFloat())
        screenHeightPixelMultiplyFactor = 1080.div(screenHeight.toFloat())
        shooterPlane = ShooterPlane(this, screenHeight, resources)
        birds = Array(4) { Bird(resources) } // there will be 4 birds in the screen at a time
        paint.textSize = 128f
        paint.color = Color.RED
    }

    override fun run() {
        if (isPlaying) {
            // if the game sounds are not muted then play birds sound in loop until game is over
            if (!MainActivity.isMuted) {
                birdsSoundStreamId = soundPool.play(birdsSound, 1f, 1f, 0, 1, 1f)
            }
        }
        // following loop will run until the user is playing
        while (isPlaying) {
            update()
            draw()
            sleep()
        }

    }

    private fun update() {
        // move the background to the left by 10 pixels
        // pixel multiply factor is used to make the background moving with same speed
        // on different device screen resolutions
        gameBackground1.xCoordinate -= (10 * screenWidthPixelMultiplyFactor).toInt()
        gameBackground2.xCoordinate -= (10 * screenWidthPixelMultiplyFactor).toInt()

        if (gameBackground1.xCoordinate + gameBackground1.bitmap.width < 0) {
            gameBackground1.xCoordinate = screenWidth
        }

        if (gameBackground2.xCoordinate + gameBackground2.bitmap.width < 0) {
            gameBackground2.xCoordinate = screenWidth
        }

        // swipe up case
        if (shooterPlane.isSwipeUp) {
            shooterPlane.yCoordinate -= (30 * screenHeightPixelMultiplyFactor).toInt()
            swipeUpCount--
            if (swipeUpCount <= 0) {
                shooterPlane.isSwipeUp = false
            }
        }

        // swipe down case
        if (shooterPlane.isSwipeDown) {
            shooterPlane.yCoordinate += (30 * screenHeightPixelMultiplyFactor).toInt()
            swipeDownCount--
            if (swipeDownCount <= 0) {
                shooterPlane.isSwipeDown = false
            }
        }

        // the following condition is there for not allowing the plane to go beyond the screen
        // from top on swipe up gesture
        if (shooterPlane.yCoordinate < 0) {
            shooterPlane.yCoordinate = 0
            swipeUpCount = 0
        }

        // the following condition is there for not allowing the plane to go below the screen
        // from bottom on swipe down gesture
        if (shooterPlane.yCoordinate > screenHeight - shooterPlane.flightBitmapHeight) {
            shooterPlane.yCoordinate = screenHeight - shooterPlane.flightBitmapHeight
            swipeDownCount = 0
        }

        val iterator = bullets.iterator()
        iterator.forEach {
            if (it.xCoordinate > screenWidth) {
                // remove the bullets from the list which are out of the screen area
                iterator.remove()
            }
            // move the bullet towards the right
            it.xCoordinate += (50 * screenWidthPixelMultiplyFactor).toInt()

            // nested forEach loop
            birds.forEach { bird ->
                if (Rect.intersects(bird.getRectangularWrapper(), it.getRectangularWrapper())) {
                    score += 10
                    bird.xCoordinate = -500
                    it.xCoordinate = screenWidth + 500
                    bird.isShot = true
                }
            }
        }

        birds.forEach {
            it.xCoordinate -= it.speed
            // if the following condition is true, it means that bird is off the screen from the left side
            if (it.xCoordinate + it.birdBitmapWidth < 0) {
                // check if bird passed off screen from left side
                // which means plane missed shooting bird. this is also one of the game over case
                if (!it.isShot) {
                    isGameOver = true
                    return
                }

                // 20 is the speed limit for the bird
                val bound = (20 * screenWidthPixelMultiplyFactor).toInt()
                it.speed = random.nextInt(bound)

                // the following condition is to check and update the speed of the bird
                // if it goes below 10.
                // 10 is the lower speed limit of the bird
                if (it.speed < 10 * screenWidthPixelMultiplyFactor) {
                    it.speed = (10 * screenWidthPixelMultiplyFactor).toInt()
                }

                // after setting bird speed. place the bird back on the screen
                it.xCoordinate = screenWidth
                it.yCoordinate = random.nextInt(screenHeight - it.birdBitmapHeight)

                it.isShot = false
            }

            // checking for game over condition
            // checking if bird crashed into flight
            if (Rect.intersects(it.getRectangularWrapper(), shooterPlane.getRectangularWrapper())) {
                isGameOver = true
                return
            }
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()

            // draw the background bitmaps on the canvas
            canvas.drawBitmap(
                gameBackground1.bitmap,
                gameBackground1.xCoordinate.toFloat(),
                gameBackground1.yCoordinate.toFloat(),
                paint
            )
            canvas.drawBitmap(
                gameBackground2.bitmap,
                gameBackground2.xCoordinate.toFloat(),
                gameBackground2.yCoordinate.toFloat(),
                paint
            )

            // draws birds to the canvas
            birds.forEach {
                canvas.drawBitmap(
                    it.getBird(), it.xCoordinate.toFloat(),
                    it.yCoordinate.toFloat(), paint
                )
            }

            // draw score text on the canvas
            canvas.drawText(score.toString(), screenWidth / 2f, 164f, paint)

            // show dead flight when isGameOver is true
            // i.e, when bird crashes into the plane
            if (isGameOver) {
                isPlaying = false
                // stop birds sound on game over
                soundPool.stop(birdsSoundStreamId)
                canvas.drawBitmap(
                    shooterPlane.deadBitmap, shooterPlane.xCoordinate.toFloat(),
                    shooterPlane.yCoordinate.toFloat(), paint
                )
                // play game over sound
                if (!MainActivity.isMuted) {
                    soundPool.play(gameOverSound, 1f, 1f, 1, 0, 1f)
                }
                // draw Game Over text on the canvas
                val fm = Paint.FontMetrics()
                paint.color = Color.WHITE
                paint.getFontMetrics(fm)
                val gameOverTextXCoord = screenWidth / 3f
                val gameOverTextYCoord = screenWidth / 2f

                canvas.drawRoundRect(
                    gameOverTextXCoord - GAME_OVER_TEXT_HORIZONTAL_MARGIN,
                    gameOverTextYCoord + fm.top - GAME_OVER_TEXT_VERTICAL_MARGIN,
                    gameOverTextXCoord + paint.measureText(GAME_OVER_TEXT) +
                            GAME_OVER_TEXT_HORIZONTAL_MARGIN,
                    gameOverTextYCoord + fm.bottom + GAME_OVER_TEXT_VERTICAL_MARGIN,
                    GAME_OVER_RECT_ROUNDED_CORNERS,
                    GAME_OVER_RECT_ROUNDED_CORNERS,
                    paint
                )
                paint.color = Color.RED
                canvas.drawText(GAME_OVER_TEXT, gameOverTextXCoord, gameOverTextYCoord, paint)

                // draw the canvas on the screen and return
                holder.unlockCanvasAndPost(canvas)
                // update top score to shared preferences
                updateHighScore()
                // wait for few seconds before finishing GameActivity and
                // taking back the user to MainActivity
                waitBeforeExiting()
                return
            }

            // draw shooter plane bitmaps to the canvas
            canvas.drawBitmap(
                shooterPlane.getBitmap(),
                shooterPlane.xCoordinate.toFloat(),
                shooterPlane.yCoordinate.toFloat(),
                paint
            )

            // draw bullet bitmaps to the canvas
            bullets.forEach {
                canvas.drawBitmap(
                    it.bulletBitmap, it.xCoordinate.toFloat(),
                    it.yCoordinate.toFloat(), paint
                )
            }

            // draw the canvas on the screen
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun waitBeforeExiting() {
        try {
            Thread.sleep(3000)
            gameActivity.finish()
        } catch (ex: InterruptedException) {
            Log.e("log", "InterruptedException in GameView::waitBeforeExiting()", ex)
            ex.printStackTrace()
        }
    }

    private fun updateHighScore() {
        if (score > MainActivity.getHighScore()) {
            MainActivity.setHighScore(score)
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(17)
        } catch (ex: InterruptedException) {
            Log.e("log", "InterruptedException in GameView::sleep()", ex)
            ex.printStackTrace()
        }
    }

    // following method is to resume the game
    fun resume() {
        // resume all the streams
        soundPool.autoResume()
        isPlaying = true
        thread = Thread(this)
        thread.start()
    }

    // following method is to pause the game
    fun pause() {
        try {
            // pause all the streams
            soundPool.autoPause()
            isPlaying = false
            thread.join()
        } catch (ex: InterruptedException) {
            Log.e("log", "Exception in GameView::pause()", ex)
            ex.printStackTrace()
        }
    }

    fun newBullet() {
        // if the game sounds are not muted then play bullet sounds
        if (!MainActivity.isMuted) {
            soundPool.play(bulletSound, 1f, 1f, 0, 0, 1f)
        }
        val bullet = Bullet(resources)
        bullet.xCoordinate = shooterPlane.xCoordinate + shooterPlane.flightBitmapWidth
        bullet.yCoordinate = shooterPlane.yCoordinate + (shooterPlane.flightBitmapHeight / 2)
        bullets.add(bullet)
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        val result = gestureDetector.onTouchEvent(motionEvent)
        Log.d("log", "onTouchEvent() = $result")
        return result
    }

    private inner class GameGesturesListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(motionEvent: MotionEvent?): Boolean {
            Log.d("log", "onDown()")
            return true
        }

        override fun onShowPress(motionEvent: MotionEvent?) {
            Log.d("log", "onShowPress()")
        }

        override fun onSingleTapUp(motionEvent: MotionEvent?): Boolean {
            Log.d("log", "onSingleTapUp()")
            shooterPlane.bulletsCounter++
            return true
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            Log.d("log", "onScroll()")
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {
            Log.d("log", "onLongPress()")
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("log", "onFling()")
            var result = false
            try {
                val diffY = e2.y - e1.y
                Log.d("log", "diffY = $diffY")
                //val diffX = e2.x - e1.x
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        shooterPlane.isSwipeDown = true
                        swipeDownCount += 5
                    } else {
                        shooterPlane.isSwipeUp = true
                        swipeUpCount += 5
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }
}