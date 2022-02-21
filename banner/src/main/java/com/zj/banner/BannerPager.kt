package com.zj.banner

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.*
import com.zj.banner.model.BaseBannerBean
import com.zj.banner.ui.BannerCard
import com.zj.banner.ui.config.BannerConfig
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "BannerPager"

/**
 * 新增一个 Banner，最简单的情况下只需传入数据即可，如果需要更多样式请查看下面参数。
 * 这里需要注意的是，数据 Model 必须要继承自 [BaseBannerBean]，因为在 [BannerCard] 中需要使用到其中的一些参数
 *
 * @param items 数据
 * @param config Banner 的一些配置参数 [BannerConfig]
 * @param indicatorIsVertical 指示器是否为竖向
 * @param indicatorGravity Banner 指示器位置，直接使用 Alignment 即可进行设定
 * @param onBannerClick Banner 点击事件的回调
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalCoilApi::class)
@Composable
fun <T : BaseBannerBean> BannerPager(
    modifier: Modifier = Modifier,
    items: List<T> = arrayListOf(),
    config: BannerConfig = BannerConfig(),
    indicatorIsVertical: Boolean = false,
    indicatorGravity: Alignment = Alignment.BottomCenter,
    onBannerClick: (T) -> Unit
) {
    if (items.isEmpty()) {
        throw NullPointerException("items is not null")
    }

    val pagerState = rememberPagerState()

    if (config.repeat) {
        StartBanner(pagerState, config.intervalTime)
    }

    Box(modifier = modifier.height(config.bannerHeight)) {
        HorizontalPager(count = items.size, state = pagerState) { page ->
            val item = items[page]
            BannerCard(
                bean = item,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(config.bannerImagePadding),
                shape = config.shape,
                contentScale = config.contentScale
            ) {
                Log.d(TAG, "item is :${item.javaClass}")
                onBannerClick(item)
            }
        }
        if (indicatorIsVertical) {
            VerticalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(indicatorGravity)
                    .padding(16.dp),
            )
        } else {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(indicatorGravity)
                    .padding(16.dp),
            )
        }

    }
}

var mTimer: Timer? = null
var mTimerTask: TimerTask? = null


@ExperimentalPagerApi
@Composable
fun StartBanner(pagerState: PagerState, intervalTime: Long) {
    val coroutineScope = rememberCoroutineScope()
    mTimer?.cancel()
    mTimerTask?.cancel()
    mTimer = Timer()
    mTimerTask = object : TimerTask() {
        override fun run() {
            coroutineScope.launch {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
            }
        }
    }
    mTimer?.schedule(mTimerTask, intervalTime, intervalTime)
}