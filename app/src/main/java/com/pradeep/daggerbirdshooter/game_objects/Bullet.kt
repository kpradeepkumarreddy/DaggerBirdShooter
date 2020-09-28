package com.pradeep.daggerbirdshooter.game_objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.pradeep.daggerbirdshooter.R
import com.pradeep.daggerbirdshooter.misc.Coordinates
import com.pradeep.daggerbirdshooter.misc.GameView

class Bullet(res: Resources) : Coordinates() {
    var bulletBitmap: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bullet)

    init {
        xCoordinate = 0
        yCoordinate = 0

        var width = bulletBitmap.width
        var height = bulletBitmap.height

        width /= 4
        height /= 4

        width = (width * GameView.screenWidthPixelMultiplyFactor).toInt()
        height = (height * GameView.screenHeightPixelMultiplyFactor).toInt()

        bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, width, height, false)
    }

    fun getRectangularWrapper(): Rect {
        // Rect(left, top, right, bottom)
        return Rect(
            xCoordinate,
            yCoordinate,
            xCoordinate + bulletBitmap.width,
            yCoordinate + bulletBitmap.height
        )
    }
}