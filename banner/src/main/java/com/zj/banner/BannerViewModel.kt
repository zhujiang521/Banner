package com.zj.banner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zj.banner.ui.PagerState
import kotlinx.coroutines.launch
import java.util.*

class BannerViewModel : ViewModel() {

    private var timer: Timer? = null
    private var _isActive = false

    fun startBanner(pagerState: PagerState, intervalTime: Long) {
        if (_isActive) {
            return
        }
        _isActive = true
        timer = Timer()
        val mTimerTask: TimerTask = object : TimerTask() {
            override fun run() {
                viewModelScope.launch {
                    if (pagerState.currentPage == pagerState.maxPage) {
                        pagerState.currentPage = 0
                    } else {
                        pagerState.currentPage++
                    }
                    pagerState.fling(-1f)
                }
            }
        }

        timer?.schedule(mTimerTask, 5000, intervalTime)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

}