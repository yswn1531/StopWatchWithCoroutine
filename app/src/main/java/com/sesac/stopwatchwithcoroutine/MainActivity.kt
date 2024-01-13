package com.sesac.stopwatchwithcoroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sesac.stopwatchwithcoroutine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        with(binding){
           coroutineButton.setOnClickListener {
               supportFragmentManager.beginTransaction().replace(R.id.frameLayout,StopwatchWithCoroutine()).commit()
           }
            channelButton.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout,StopwatchWithChannel()).commit()
            }
            flowButton.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout,StopwatchWithFlow()).commit()
            }
        }



        /*val list = arrayListOf<String>()
        list.add("StopWatchWithCoroutine")
        list.add("StopWatchWithChannel")
        list.add("StopWatchWithFlow")

        val adapter : ArrayAdapter<*>
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, list)
        with(binding){
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, _, id ->
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
        }*/
    }

}
