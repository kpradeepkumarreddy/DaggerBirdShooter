package com.pradeep.daggerbirdshooter.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.pradeep.daggerbirdshooter.R
import kotlinx.android.synthetic.main.activity_app_info.*

class AppInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_app_info)
        tvCheckForUpdate.movementMethod = LinkMovementMethod.getInstance();

        ivClose.setOnClickListener {
            finish()
        }
    }
}