package com.example.androidstudioscala

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity extends AppCompatActivity {
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    import BuildConfig.{VERSION_NAME, VERSION_CODE}
    findViewById(R.id.textView0).asInstanceOf[android.widget.TextView].setText(s"version = $VERSION_NAME ($VERSION_CODE)")
  }
}
