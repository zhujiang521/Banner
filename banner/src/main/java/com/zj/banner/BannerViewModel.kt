package com.zj.banner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zj.banner.ui.PagerState
import kotlinx.coroutines.launch
import java.util.*

class BannerViewModel : ViewModel() {

    private var timer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private var isActive = false

    fun startBanner(pagerState: PagerState, intervalTime: Long) {
        if (isActive) {
            return
        }
        isActive = true
        timer = Timer()
        mTimerTask = object : TimerTask() {
            override fun run() {
                viewModelScope.launch {
                    pagerState.setNextPage()
                }
            }
        }

        timer?.schedule(mTimerTask, 3000, intervalTime)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        mTimerTask?.cancel()
    }

}