package com.folioreader.android.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.folioreader.ui.activity.FolioActivity

class FolioCustomActivity : FolioActivity() {
    private lateinit var text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_folio_custom)
        text = findViewById(R.id.text)
        text.setOnClickListener(View.OnClickListener {
            text.text = "Text"
        })
    }
}