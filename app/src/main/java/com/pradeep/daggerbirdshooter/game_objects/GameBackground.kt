package com.pradeep.daggerbirdshooter.game_objects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.pradeep.daggerbirdshooter.R
import com.pradeep.daggerbirdshooter.misc.Coordinates

class GameBackground(screenWidth: Int, screenHeight: Int, res: Resources) : Coordinates() {
    var bitmap: Bitmap = BitmapFactory.decodeResource(res, R.drawable.game_background)

    init {
        // resizing bitmap to cover entire screen
        bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, false)
    }
}