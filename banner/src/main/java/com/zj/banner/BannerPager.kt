@file:OptIn(ExperimentalFoundationApi::class)

package com.zj.banner

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.zj.banner.model.BaseBannerBean
import com.zj.banner.ui.BannerCard
import com.zj.banner.ui.config.BannerConfig
import com.zj.banner.utils.HorizontalPagerIndicator
import com.zj.banner.utils.VerticalPagerIndicator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.absoluteValue

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
    val size = items.size

    val loopingCount = Int.MAX_VALUE
    var startIndex = loopingCount / 2
    startIndex -= startIndex % size
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        initialPageOffsetFraction = 0f
    ) {
        loopingCount
    }

    fun pageMapper(index: Int): Int {
        return (index - startIndex).floorMod(size)
    }
    Box(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            key = {
                it
            },
            pageContent = { page ->
                val p = page % size
                val item = items[p]

                BannerCard(
                    bean = item,
                    modifier = Modifier
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val offset =
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            val pageOffset = offset.absoluteValue

                            // We animate the scaleX + scaleY, between 85% and 100%
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .padding(config.bannerImagePadding),
                    shape = config.shape,
                    imageRatio = config.imageRatio,
                    contentScale = config.contentScale
                ) {
                    Log.d(TAG, "item is :${item.javaClass}")
                    onBannerClick(item)
                }
            }
        )

        if (config.repeat) {
            LaunchedEffect(key1 = pagerState) {
                try {
                    while (true) {
                        delay(config.intervalTime)
                        val current = pagerState.currentPage
                        val currentPos = pageMapper(current)
                        val nextPage = current + 1
                        val toPage = nextPage.takeIf { nextPage < pagerState.pageCount }
                            ?: (currentPos + startIndex + 1)
                        if (toPage > current) {
                            pagerState.animateScrollToPage(toPage)
                        } else {
                            pagerState.scrollToPage(toPage)
                        }
                    }
                } catch (e: CancellationException) {
                    Log.i("page", "Launched paging cancelled")
                }
            }
        }

        if (indicatorIsVertical) {
            VerticalPagerIndicator(
                pagerState = pagerState,
                pageCount = size,
                modifier = Modifier
                    .align(indicatorGravity)
                    .padding(16.dp),
            )
        } else {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                pageCount = size,
                modifier = Modifier
                    .align(indicatorGravity)
                    .padding(16.dp),
            )
        }

    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}