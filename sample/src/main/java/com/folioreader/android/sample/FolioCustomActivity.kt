package com.folioreader.android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.folioreader.FolioReader
import com.folioreader.ui.activity.FolioActivity
import com.folioreader.ui.fragment.FolioFragment

class FolioCustomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folio_custom)

       val folioReader = FolioReader.get()

        val fragment: FolioFragment =
            folioReader.createBookFragment("file:///android_asset/TheSilverChair.epub", 0)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .addToBackStack(null)
            .commit()

    }
}