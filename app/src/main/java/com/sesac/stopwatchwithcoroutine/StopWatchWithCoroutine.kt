package com.sesac.stopwatchwithcoroutine

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sesac.stopwatchwithcoroutine.common.*
import com.sesac.stopwatchwithcoroutine.databinding.ActivityCoroutineBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopWatchWithCoroutine : AppCompatActivity(){

    private lateinit var binding: ActivityCoroutineBinding
    private val defaultCoroutineScope = CoroutineScope(Dispatchers.Default)
    private var repeatedTime = 0
    private var repeatedTimeSub = 0
    private var saveIndex = 1
    private var isRunning = false

    private lateinit var mainTimerJob: Job
    private lateinit var subTimerJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        with(binding) {
            startBtn.setOnClickListener {
                isRunning = !isRunning
                if (isRunning) {
                    mainTimerJob = defaultCoroutineScope.launch {
                        timerStart()
                    }
                    if (::subTimerJob.isInitialized && subTimerJob.isCancelled && repeatedTime != 0) {
                        subTimerJob = defaultCoroutineScope.launch {
                            subTimerStart()
                        }
                    }
                } else {
                    pause()
                }
            }

            resetBtn.setOnClickListener {
                if (!mainTimerJob.isActive) {
                    reset()
                } else {
                    currentTapTime()
                    subTimerJob = defaultCoroutineScope.launch {
                        subTimerStart()
                    }
                }

            }
        }
    }

    /**
     * Timer start 메인 타이머
     *
     * @param delayTime
     */
    private suspend fun timerStart(delayTime: Long = 10L) {
        CoroutineScope(Dispatchers.Main).launch {
            with(binding){
                startBtn.setBackgroundColor(Color.RED)
                startBtn.text = resources.getString(R.string.stop)
                resetBtn.text = resources.getString(R.string.split_timer)
            }
        }
        while (true) {
            repeatedTime++
            val minute = repeatedTime.getMinutes()
            val seconds = repeatedTime.getSeconds()
            val milliseconds = repeatedTime.getMilliseconds()
            delay(delayTime)
            CoroutineScope(Dispatchers.Main).launch {
                with(binding) {
                    minuteText.text = String.format("%02d",minute)
                    secondText.text = String.format("%02d",seconds)
                    milliSecondText.text = String.format("%02d",milliseconds)
                }
            }
        }
    }


    /**
     * Sub timer start 구간기록 타이머
     *
     * @param delayTime
     */
    @SuppressLint("SetTextI18n")
    private suspend fun subTimerStart(delayTime: Long = 10L) {
        while (true) {
            repeatedTimeSub++
            val minute = repeatedTimeSub.getMinutes()
            val seconds = repeatedTimeSub.getSeconds()
            val milliseconds = repeatedTimeSub.getMilliseconds()
            delay(delayTime)
            CoroutineScope(Dispatchers.Main).launch {
                with(binding) {
                    tempMinuteText.text = String.format("%02d",minute)
                    tempSecondText.text = String.format("%02d",seconds)
                    tempMilliSecondText.text = String.format("%02d",milliseconds)
                }
            }
        }
    }


    /**
     * Pause 정지
     *
     */
    @SuppressLint("ResourceAsColor")
    private fun pause() {
        mainTimerJob.cancel()
        if (::subTimerJob.isInitialized && subTimerJob.isActive) subTimerJob.cancel()
        binding.startBtn.setBackgroundColor(R.color.basic)
        binding.startBtn.text = resources.getString(R.string.resume)
        binding.resetBtn.text = resources.getString(R.string.reset)
    }

    /**
     * Reset 초기화
     *
     */
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun reset() {
        mainTimerJob.cancel()
        if (::subTimerJob.isInitialized && subTimerJob.isActive) subTimerJob.cancel()
        isRunning = false
        repeatedTime = 0
        repeatedTimeSub = 0
        saveIndex = 1
        with(binding) {
            startBtn.setBackgroundColor(R.color.basic)
            minuteText.text = resources.getString(R.string.init_zero)
            secondText.text = resources.getString(R.string.init_zero)
            milliSecondText.text = resources.getString(R.string.init_zero)
            tempMinuteText.text = resources.getString(R.string.init_zero)
            tempSecondText.text = resources.getString(R.string.init_zero)
            tempMilliSecondText.text = resources.getString(R.string.init_zero)
            lapLayout.removeAllViews()
            lapLayout.invalidate()
            startBtn.text = resources.getString(R.string.start)
            resetBtn.text = resources.getString(R.string.split_timer)
            linearLayout.visibility = View.INVISIBLE
            linearLayout2.visibility = View.INVISIBLE
            line.visibility = View.INVISIBLE
            scoll1.visibility = View.INVISIBLE
        }
    }


    /**
     * Current tap time 구간 기록
     *
     */
    @SuppressLint("SetTextI18n")
    private fun currentTapTime() {
        binding.linearLayout.visibility = View.VISIBLE
        binding.linearLayout2.visibility = View.VISIBLE
        binding.line.visibility = View.VISIBLE
        binding.scoll1.visibility = View.VISIBLE

        val labTimeTV = TextView(this).apply {
            textSize = 20f
        }

        val minute = String.format("%02d", repeatedTime.getMinutes())
        val seconds = String.format("%02d", repeatedTime.getSeconds())
        val milliseconds = String.format("%02d", repeatedTime.getMilliseconds())
        val subMinute = String.format("%02d", repeatedTimeSub.getMinutes())
        val subSeconds = String.format("%02d", repeatedTimeSub.getSeconds())
        val subMilliseconds = String.format("%02d", repeatedTimeSub.getMilliseconds())

        with(labTimeTV) {
            if (saveIndex == 1) {
                text =
                    """         ${String.format("%02d",saveIndex)}             $minute:$seconds.$milliseconds        $minute:$seconds.$milliseconds     """
            } else {
                text =
                    """         ${String.format("%02d",saveIndex)}             $subMinute:$subSeconds.$subMilliseconds        $minute:$seconds.$milliseconds     """
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, 15, 0, 15)
            }
            binding.lapLayout.addView(labTimeTV, 0)
        }
        if (::subTimerJob.isInitialized && subTimerJob.isActive) subTimerJob.cancel()
        repeatedTimeSub = 0
        saveIndex++
    }

    override fun onStop() {
        super.onStop()
        coroutineCancel()
    }

    private fun coroutineCancel() {
        if (::mainTimerJob.isInitialized && mainTimerJob.isActive) {
            mainTimerJob.cancel()
        }
        if (::subTimerJob.isInitialized && subTimerJob.isActive) {
            subTimerJob.cancel()
        }
    }



}