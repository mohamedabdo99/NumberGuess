package com.mohamed.abdo.numberguess

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.mohamed.abdo.numberguess.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    var started = false
    var number = 0
    var tries = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        fetchSavedInstanceData(savedInstanceState)
        binding.btnDoGuess.setEnabled(started)
    }

    //gets called when the activity is suspended temporarily
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        putInstanceData(outState)
    }

    fun start(v: View) {
        log("Game started")
        binding.edtNum.setText("")
        started = true
        binding.btnDoGuess.setEnabled(true)
        binding.txtStatus.text = getString(R.string.guess_hint, 1, 7)
        number = 1 + Math.floor(Math.random()*7).toInt()
        tries = 0
    }

    fun guess(v:View) {
        if(binding.edtNum.text.toString() == "") return
        tries++
        log("Guessed ${binding.edtNum.text} (tries:${tries})")
        val g = binding.edtNum.text.toString().toInt()
        if(g < number) {
            binding.txtStatus.setText(R.string.status_too_low)
            binding.edtNum.setText("")
        } else if(g > number){
            binding.txtStatus.setText(R.string.status_too_high)
            binding.edtNum.setText("")
        } else {
            binding.txtStatus.text = getString(R.string.status_hit,
                tries)
            started = false
            binding.btnDoGuess.setEnabled(false)
        }
    }

    private fun putInstanceData(outState: Bundle?) {
        if (outState != null) with(outState) {
            putBoolean("started", started)
            putInt("number", number)
            putInt("tries", tries)
            putString("statusMsg", binding.txtStatus.text.toString())
            putStringArrayList("logs",
                ArrayList(console.text.split("\n")))
        }
    }

    private fun fetchSavedInstanceData(
        savedInstanceState: Bundle?) {
        if (savedInstanceState != null)
            with(savedInstanceState) {
                started = getBoolean("started")
                number = getInt("number")
                tries = getInt("tries")
                binding.txtStatus.text = getString("statusMsg")
                console.text = getStringArrayList("logs")!!.
                joinToString("\n")
            }
    }

    private fun log(msg:String) {
        Log.d("LOG", msg)
        console.log(msg)
    }
}

class Console(ctx:Context, aset:AttributeSet? = null)
    : ScrollView(ctx, aset) {
    val tv = TextView(ctx)
    var text:String
        get() = tv.text.toString()
        set(value) { tv.setText(value) }
    init {
        setBackgroundColor(0x40FFFF00)
        addView(tv)
    }
    fun log(msg:String) {
        val l = tv.text.let {
            if(it == "") listOf() else it.split("\n")
        }.takeLast(100) + msg
        tv.text = l.joinToString("\n")
        post(object : Runnable {
            override fun run() {
                fullScroll(ScrollView.FOCUS_DOWN)
            }
        })
    }
}



