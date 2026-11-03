package com.gamebox.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val games = listOf("Game 1", "Game 2", "Game 3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this).apply {
            text = "GAMEBOX"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 48)
        }

        val list = ListView(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_list_item_1,
                games
            )
            setOnItemClickListener { _, _, position, _ ->
                Toast.makeText(
                    this@MainActivity,
                    "Selected: ${games[position]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            addView(title)
            addView(list)
        }
        setContentView(root)
    }
}
