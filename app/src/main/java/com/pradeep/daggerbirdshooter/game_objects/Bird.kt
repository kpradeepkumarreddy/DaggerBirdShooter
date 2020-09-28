package com.pradeep.daggerbirdshooter.game_objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.pradeep.daggerbirdshooter.R
import com.pradeep.daggerbirdshooter.misc.Coordinates
import com.pradeep.daggerbirdshooter.misc.GameView

class Bird(res: Resources) : Coordinates() {
    // showing these 4 bitmaps in a chain will make the bird look flying
    var birdBitmap1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird1)
    var birdBitmap2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird2)
    var birdBitmap3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird3)
    var birdBitmap4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird4)
    var birdCounter = 1
    var speed = 20 // default initial speed of the bird
    var birdBitmapWidth = birdBitmap1.width
    var birdBitmapHeight = birdBitmap1.height
    var isShot = true

    init {

        // reduce the size of bird bitmap
        birdBitmapWidth /= 6
        birdBitmapHeight /= 6

        birdBitmapWidth = (birdBitmapWidth * GameView.screenWidthPixelMultiplyFactor).toInt()
        birdBitmapHeight = (birdBitmapHeight * GameView.screenHeightPixelMultiplyFactor).toInt()

        birdBitmap1 =
            Bitmap.createScaledBitmap(birdBitmap1, birdBitmapWidth, birdBitmapHeight, false)
        birdBitmap2 =
            Bitmap.createScaledBitmap(birdBitmap2, birdBitmapWidth, birdBitmapHeight, false)
        birdBitmap3 =
            Bitmap.createScaledBitmap(birdBitmap3, birdBitmapWidth, birdBitmapHeight, false)
        birdBitmap4 =
            Bitmap.createScaledBitmap(birdBitmap4, birdBitmapWidth, birdBitmapHeight, false)

        // initially placing the bird above the screen
        yCoordinate = -birdBitmapHeight
    }

    fun getBird(): Bitmap {
        when (birdCounter) {
            1 -> {
                birdCounter++
                return birdBitmap1
            }
            2 -> {
                birdCounter++
                return birdBitmap2
            }
            3 -> {
                birdCounter++
                return birdBitmap3
            }
            else -> {
                birdCounter = 1
                return birdBitmap4
            }
        }
    }

    fun getRectangularWrapper(): Rect {
        // Rect(left, top, right, bottom)
        return Rect(
            xCoordinate,
            yCoordinate,
            xCoordinate + birdBitmapWidth,
            yCoordinate + birdBitmapHeight
        )
    }
}