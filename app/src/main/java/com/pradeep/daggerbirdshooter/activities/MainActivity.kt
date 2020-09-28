package com.pradeep.daggerbirdshooter.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.pradeep.daggerbirdshooter.BuildConfig
import com.pradeep.daggerbirdshooter.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val HIGH_SCORE = "HIGH_SCORE"
        private const val GAME_SOUNDS_MUTED = "GAME_SOUNDS_MUTED"
        lateinit var sharedPref: SharedPreferences
        var isMuted = false

        fun getHighScore(): Int {
            return sharedPref.getInt(HIGH_SCORE, 0)
        }

        fun setHighScore(highScore: Int) {
            val editor = sharedPref.edit()
            editor.putInt(HIGH_SCORE, highScore)
            editor.apply()
        }

        fun getIsMuted(): Boolean {
            return sharedPref.getBoolean(GAME_SOUNDS_MUTED, false)
        }

        fun setIsMuted(isMute: Boolean) {
            val editor = sharedPref.edit()
            editor.putBoolean(GAME_SOUNDS_MUTED, isMute)
            editor.apply()
        }

        fun getSoundPool(): SoundPool {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
                SoundPool.Builder().setAudioAttributes(audioAttributes).build()
            } else {
                SoundPool(1, AudioManager.STREAM_MUSIC, 0)
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            setContentView(R.layout.activity_main)

            // read the high score from the shared preference and set it to text view
            sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

            tvPlay.setOnClickListener {
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                startActivity(intent)
            }

            // get the state of volume control button from shared pref and update the UI accordingly
            if (getIsMuted()) {
                isMuted = true
                Log.d("log", "volume muted is true")
                ivVolumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24)
            } else {
                isMuted = false
                Log.d("log", "volume muted is false")
                ivVolumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24)
            }

            // toggle volume mute and un-mute on click
            ivVolumeCtrl.setOnClickListener {
                if (isMuted) {
                    setIsMuted(false)
                    isMuted = false
                    ivVolumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24)
                    // play home screen background music
                    playHomeScreenBackgroundMusic()
                } else {
                    setIsMuted(true)
                    isMuted = true
                    ivVolumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24)
                    mediaPlayer?.stop()
                }
            }

            ivAppInfo.setOnClickListener {
                val intent = Intent(this@MainActivity, AppInfoActivity::class.java)
                startActivity(intent)
            }

        } catch (ex: Exception) {
            Log.e("log", "Exception in MainActivity::onCreate()", ex)
        }
    }

    override fun onResume() {
        super.onResume()

        val highScore = getHighScore()
        if (highScore > 0) {
            tvHighScoreText.visibility = View.VISIBLE
            tvHighScore.visibility = View.VISIBLE
            // update the high score in main activity
            tvHighScore.text = getHighScore().toString()
        }

        if (!isMuted) {
            // play home screen background music
            playHomeScreenBackgroundMusic()
        }
    }

    private fun playHomeScreenBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.home_screen_sound)
        mediaPlayer?.setVolume(0.1f, 0.1f)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        // pause the home screen background music
        mediaPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // release media player
        mediaPlayer?.release()
    }
}