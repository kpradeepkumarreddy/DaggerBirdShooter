package com.pradeep.daggerbirdshooter.activities

import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import com.pradeep.daggerbirdshooter.misc.GameView

class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)

        // using Point class to get the height and width of the screen
        val point = Point()
        windowManager.defaultDisplay.getSize(point)

        gameView = GameView(this@GameActivity, point.x, point.y)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()

    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.getSoundPool().release()
    }
}