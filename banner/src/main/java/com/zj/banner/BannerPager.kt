package com.zj.banner

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zj.banner.model.BaseBannerBean
import com.zj.banner.ui.BannerCard
import com.zj.banner.ui.Pager
import com.zj.banner.ui.PagerState
import com.zj.banner.ui.config.BannerConfig
import com.zj.banner.ui.indicator.CircleIndicator
import com.zj.banner.ui.indicator.Indicator
import com.zj.banner.ui.indicator.NumberIndicator

private const val TAG = "BannerPager"

/**
 * 新增一个 Banner，最简单的情况下只需传入数据即可，如果需要更多样式请查看下面参数。
 * 这里需要注意的是，数据 Model 必须要继承自 [BaseBannerBean]，因为在 [BannerCard] 中需要使用到其中的一些参数
 *
 * @param items 数据
 * @param repeat 是否循环播放
 * @param config Banner 的一些配置参数 [BannerConfig]
 * @param indicator Banner 指示器，你可以使用默认的 [CircleIndicator] 或 [NumberIndicator]，也可以自定义，仅需要继承
 * [Indicator] 即可。
 * @param onBannerClick Banner 点击事件的回调
 */
@Composable
fun <T : BaseBannerBean> BannerPager(
    items: List<T> = arrayListOf(),
    repeat: Boolean = true,
    config: BannerConfig = BannerConfig(),
    indicator: Indicator = CircleIndicator(),
    onBannerClick: (T) -> Unit
) {
    if (items.isEmpty()) {
        throw NullPointerException("items is not null")
    }

    val viewModel: BannerViewModel = viewModel()
    val pagerState: PagerState = remember { PagerState() }
    if (repeat) {
        viewModel.startBanner(pagerState, config.intervalTime)
    }

    pagerState.maxPage = (items.size - 1).coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxWidth().height(config.bannerHeight)) {
        Pager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(config.bannerHeight)
        ) {
            val item = items[page]
            BannerCard(
                bean = item,
                modifier = Modifier.fillMaxSize().padding(config.bannerImagePadding),
                shape = config.shape
            ) {
                Log.d(TAG, "item is :${item.javaClass}")
                onBannerClick(item)
            }
        }

        indicator.DrawIndicator(pagerState)
    }
}