package com.zj.banner.ui.indicator

import androidx.compose.runtime.Composable
import com.zj.banner.ui.PagerState
import com.zj.banner.ui.indicator.BannerGravity.BottomCenter

/**
 * 指示器基类，如果需要自定义指示器，需要继承此类，并实现 [DrawIndicator] 方法
 * 别忘了在重写方法上添加 @Composable 注解
 */
abstract class Indicator {

    private var gravity: Int = BottomCenter

    fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

    fun getGravity(): Int {
        return gravity
    }

    @Composable
    abstract fun DrawIndicator(pagerState: PagerState)

}