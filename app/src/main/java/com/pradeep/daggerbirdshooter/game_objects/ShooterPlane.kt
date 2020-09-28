package com.pradeep.daggerbirdshooter.game_objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.pradeep.daggerbirdshooter.R
import com.pradeep.daggerbirdshooter.misc.Coordinates
import com.pradeep.daggerbirdshooter.misc.GameView

class ShooterPlane(private val gameView: GameView, screenHeight: Int, res: Resources) :
    Coordinates() {
    var isSwipeUp: Boolean = false
    var isSwipeDown: Boolean = false
    private var isShowFlight1: Boolean = true
    private var shootBitmapCounter = 1
    var bulletsCounter = 0
    private var flight1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.flight1)
    private var flight2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.flight2)
    var flightBitmapWidth: Int = flight1.width
    var flightBitmapHeight: Int = flight1.height
    private var shoot1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot1)
    private var shoot2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot2)
    private var shoot3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot3)
    private var shoot4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot4)
    private var shoot5: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot5)
    var deadBitmap: Bitmap = BitmapFactory.decodeResource(res, R.drawable.dead)
        private set // making only the setter private

    init {
        flightBitmapWidth /= 4
        flightBitmapHeight /= 4

        flightBitmapWidth = (flightBitmapWidth * GameView.screenWidthPixelMultiplyFactor).toInt()
        flightBitmapHeight = (flightBitmapHeight * GameView.screenHeightPixelMultiplyFactor).toInt()

        flight1 = Bitmap.createScaledBitmap(flight1, flightBitmapWidth, flightBitmapHeight, false)
        flight2 = Bitmap.createScaledBitmap(flight2, flightBitmapWidth, flightBitmapHeight, false)

        // initially shooter plane will be placed vertically center
        yCoordinate = screenHeight / 2

        // setting margin on x-axis for the placing the shooter plane
        xCoordinate = (64 * GameView.screenWidthPixelMultiplyFactor).toInt()

        shoot1 = Bitmap.createScaledBitmap(shoot1, flightBitmapWidth, flightBitmapHeight, false)
        shoot2 = Bitmap.createScaledBitmap(shoot2, flightBitmapWidth, flightBitmapHeight, false)
        shoot3 = Bitmap.createScaledBitmap(shoot3, flightBitmapWidth, flightBitmapHeight, false)
        shoot4 = Bitmap.createScaledBitmap(shoot4, flightBitmapWidth, flightBitmapHeight, false)
        shoot5 = Bitmap.createScaledBitmap(shoot5, flightBitmapWidth, flightBitmapHeight, false)

        deadBitmap =
            Bitmap.createScaledBitmap(deadBitmap, flightBitmapWidth, flightBitmapHeight, false)
    }

    fun getBitmap(): Bitmap {
        if (bulletsCounter != 0) {
            when (shootBitmapCounter) {
                1 -> {
                    shootBitmapCounter++
                    return shoot1
                }
                2 -> {
                    shootBitmapCounter++
                    return shoot2
                }
                3 -> {
                    shootBitmapCounter++
                    return shoot3
                }
                4 -> {
                    shootBitmapCounter++
                    return shoot4
                }
                else -> {
                    shootBitmapCounter = 1
                    bulletsCounter--
                    gameView.newBullet()
                    return shoot5
                }
            }
        }
        return if (isShowFlight1) {
            isShowFlight1 = false
            flight1
        } else {
            isShowFlight1 = true
            flight2
        }
    }

    fun getRectangularWrapper(): Rect {
        // Rect(left, top, right, bottom)
        return Rect(
            xCoordinate,
            yCoordinate,
            xCoordinate + flightBitmapWidth,
            yCoordinate + flightBitmapHeight
        )
    }

}

