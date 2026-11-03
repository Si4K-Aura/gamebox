package com.gamebox.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = TextView(this).apply {
            text = "GAMEBOX"
            textSize = 32f
            setTextColor(Color.WHITE)
        }
        val button = Button(this).apply {
            text = "Start"
            setOnClickListener { title.text = "Hello!" }
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#121212"))
            addView(title)
            addView(button)
        }
        setContentView(root)
    }
}
