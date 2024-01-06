package com.sesac.stopwatchwithcoroutine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sesac.stopwatchwithcoroutine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        val list = arrayListOf<String>()
        list.add("StopWatchWithCoroutine")
        list.add("StopWatchWithChannel")
        list.add("StopWatchWithFlow")

        val adapter : ArrayAdapter<*>
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, list)
        with(binding){
            listView.adapter = adapter
            listView.setOnItemClickListener { parent, view, position, id ->
                when(id){
                    0L -> {
                        val intent = Intent(this@MainActivity, StopWatchWithCoroutine::class.java)
                        startActivity(intent)
                    }
                    1L -> {
                        val intent = Intent(this@MainActivity, StopWatchWithChannel::class.java)
                        startActivity(intent)
                    }
                    2L -> {
                        val intent = Intent(this@MainActivity, StopWatchWithFlow::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

}
