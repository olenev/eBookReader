package com.folioreader.android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.ui.fragment.FolioFragment

class FolioActivityForFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folio_for_fragment)

        val folioReader = FolioReader.get()
        val config = Config()
            .setThemeColorRes(R.color.grey_color)
            .setNightMode(false)
            .setShowTts(false)

        folioReader.setConfig(config, true)

        val fragment: FolioFragment =
            folioReader.createFragmentForBook("file:///android_asset/TheSilverChair.epub", 0, true)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .commit()
    }
}