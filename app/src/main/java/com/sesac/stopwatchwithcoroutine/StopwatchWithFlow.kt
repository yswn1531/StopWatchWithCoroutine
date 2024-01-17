package com.sesac.stopwatchwithcoroutine

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sesac.stopwatchwithcoroutine.common.BaseFragment
import com.sesac.stopwatchwithcoroutine.common.DELAY_TIME
import com.sesac.stopwatchwithcoroutine.common.TEXT_MARGIN
import com.sesac.stopwatchwithcoroutine.common.TIME_FORMAT
import com.sesac.stopwatchwithcoroutine.common.getMilliseconds
import com.sesac.stopwatchwithcoroutine.common.getMinutes
import com.sesac.stopwatchwithcoroutine.common.getSeconds
import com.sesac.stopwatchwithcoroutine.databinding.FragmentStopwatchWithFlowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StopwatchWithFlow : BaseFragment<FragmentStopwatchWithFlowBinding>(FragmentStopwatchWithFlowBinding::inflate) {

    private val defaultCoroutineScope = CoroutineScope(Dispatchers.Default)
    private var repeatedTime = 0
    private var repeatedTimeSub = 0
    private var saveIndex = 1
    private var isRunning = false

    private lateinit var mainTimerJob: Job
    private lateinit var subTimerJob: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchWithFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickStartButton()
        clickResetButton()
    }

    /**
     * Start Button Click Event
     *
     */
    private fun clickStartButton(){
        with(binding){
            startBtn.setOnClickListener {
                isRunning = !isRunning
                if (isRunning) {
                    mainTimerJob = defaultCoroutineScope.launch {
                        mainTimerStart()
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
        }
    }

    /**
     * Reset Button Click Event
     *
     */
    private fun clickResetButton(){
        with(binding) {
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

    private suspend fun mainTimerStart() {
        settingWhenStartButtonUI()
        while (mainTimerJob.isActive) {
            mainTimerFlow().collect{
                val minute = repeatedTime.getMinutes()
                val seconds = repeatedTime.getSeconds()
                val milliseconds = repeatedTime.getMilliseconds()
                CoroutineScope(Dispatchers.Main).launch {
                    with(binding) {
                        minuteText.text = String.format(TIME_FORMAT, minute)
                        secondText.text = String.format(TIME_FORMAT, seconds)
                        milliSecondText.text = String.format(TIME_FORMAT, milliseconds)
                    }
                }
            }
        }
    }

    private fun mainTimerFlow() = flowOf(repeatedTime)
            .map {
                repeatedTime++
                delay(DELAY_TIME)
            }.flowOn(Dispatchers.Default)

    /**
     * 시작버튼 눌렀을 때 Button UI
     *
     */
    private fun settingWhenStartButtonUI() {
        CoroutineScope(Dispatchers.Main).launch {
            with(binding) {
                startBtn.setBackgroundColor(Color.RED)
                startBtn.text = resources.getString(R.string.stop)
                resetBtn.text = resources.getString(R.string.split_timer)
            }
        }
    }

    /**
     * Sub timer start 구간기록 타이머
     *
     */
    private suspend fun subTimerStart() {
        while (subTimerJob.isActive) {
            subTimerFlow().collect {
                val minute = repeatedTimeSub.getMinutes()
                val seconds = repeatedTimeSub.getSeconds()
                val milliseconds = repeatedTimeSub.getMilliseconds()
                CoroutineScope(Dispatchers.Main).launch {
                    with(binding) {
                        subMinuteText.text = String.format(TIME_FORMAT,minute)
                        subSecondText.text = String.format(TIME_FORMAT,seconds)
                        subMilliSecondText.text = String.format(TIME_FORMAT,milliseconds)
                    }
                }
            }
        }
    }

    private fun subTimerFlow() = flowOf(repeatedTimeSub)
        .map {
            repeatedTimeSub++
            delay(DELAY_TIME)
        }.flowOn(Dispatchers.Default)


    /**
     * Pause 정지
     *
     */
    private fun pause() {
        coroutineCancel()
        settingWhenPauseButtonUI()
    }

    /**
     * 정지버튼 눌렀을 떄 Button UI
     *
     */
    private fun settingWhenPauseButtonUI(){
        with(binding){
            startBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.basic))
            startBtn.text = resources.getString(R.string.resume)
            resetBtn.text = resources.getString(R.string.reset)
        }
    }

    /**
     * Reset 초기화
     *
     */
    private fun reset() {
        coroutineCancel()
        initTimer()
    }

    /**
     * Timer 초기화
     *
     */
    private fun initTimer() {
        isRunning = false
        repeatedTime = 0
        repeatedTimeSub = 0
        saveIndex = 1
        resetTimerText()
        resetButtonUI()
        resetTimeLabUI()
    }

    /**
     * 구간 기록 UI 초기화
     *
     */
    private fun resetTimeLabUI() {
        with(binding){
            indexLayout.visibility = View.INVISIBLE
            subTimerLayout.visibility = View.INVISIBLE
            line.visibility = View.INVISIBLE
            timeLabScroll.visibility = View.INVISIBLE
            labLayout.removeAllViews()
            labLayout.invalidate()
        }
    }

    /**
     * 타이머 TEXT 초기화
     *
     */
    private fun resetTimerText(){
        with(binding){
            minuteText.text = resources.getString(R.string.init_zero)
            secondText.text = resources.getString(R.string.init_zero)
            milliSecondText.text = resources.getString(R.string.init_zero)
            subMinuteText.text = resources.getString(R.string.init_zero)
            subSecondText.text = resources.getString(R.string.init_zero)
            subMilliSecondText.text = resources.getString(R.string.init_zero)
        }
    }

    /**
     * Button UI 초기화
     *
     */
    private fun resetButtonUI(){
        with(binding){
            startBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.basic))
            startBtn.text = resources.getString(R.string.start)
            resetBtn.text = resources.getString(R.string.split_timer)
        }
    }

    /**
     * 구간 기록 기능
     *
     */
    private fun currentTapTime() {
        startTimeLabUI()
        addCurrentTapTime()
        if (::subTimerJob.isInitialized && subTimerJob.isActive) subTimerJob.cancel()
        repeatedTimeSub = 0
        saveIndex++
    }

    /**
     * 구간 기록 버튼 눌렀을 때 추가 되는 View
     *
     */
    @SuppressLint("SetTextI18n")
    private fun addCurrentTapTime() {
        val labTimeTV = TextView(requireContext()).apply {
            textSize = 20f
        }

        val minute = String.format(TIME_FORMAT, repeatedTime.getMinutes())
        val seconds = String.format(TIME_FORMAT, repeatedTime.getSeconds())
        val milliseconds = String.format(TIME_FORMAT, repeatedTime.getMilliseconds())
        val subMinute = String.format(TIME_FORMAT, repeatedTimeSub.getMinutes())
        val subSeconds = String.format(TIME_FORMAT, repeatedTimeSub.getSeconds())
        val subMilliseconds = String.format(TIME_FORMAT, repeatedTimeSub.getMilliseconds())

        with(labTimeTV) {
            text = if(saveIndex == 1) {
                """         ${String.format(TIME_FORMAT,saveIndex)}             $minute:$seconds.$milliseconds        $minute:$seconds.$milliseconds     """
            } else {
                """         ${String.format(TIME_FORMAT,saveIndex)}             $subMinute:$subSeconds.$subMilliseconds        $minute:$seconds.$milliseconds     """
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.setMargins(0, TEXT_MARGIN, 0, TEXT_MARGIN)
            }
            binding.labLayout.addView(labTimeTV, 0)
        }
    }

    /**
     * 구간 기록 시작 UI
     *
     */
    private fun startTimeLabUI() {
        with(binding){
            subTimerLayout.visibility = View.VISIBLE
            indexLayout.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
            timeLabScroll.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        coroutineCancel()
    }

    /**
     * Coroutine cancel
     *
     */
    private fun coroutineCancel() {
        if (::mainTimerJob.isInitialized && mainTimerJob.isActive) {
            mainTimerJob.cancel()
        }
        if (::subTimerJob.isInitialized && subTimerJob.isActive) {
            subTimerJob.cancel()
        }
    }

}