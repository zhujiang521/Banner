package com.zj.banner.ui.indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.zj.banner.ui.PagerState
import com.zj.banner.ui.indicator.BannerGravity.BottomCenter
import com.zj.banner.ui.indicator.BannerGravity.BottomLeft
import com.zj.banner.ui.indicator.BannerGravity.BottomRight

/**
 * 圆形指示器 eg：。。. 。。
 * @param indicatorColor 指示器默认颜色
 * @param selectIndicatorColor 指示器选中颜色
 * @param indicatorDistance 指示器之间的距离
 */
class CircleIndicator(
    var indicatorColor: Color = Color.Red,
    var selectIndicatorColor: Color = Color.Green,
    var indicatorDistance: Int = 50
) : Indicator() {

    @Composable
    override fun DrawIndicator(pagerState: PagerState) {
        val gravity = getGravity()
        for (pageIndex in 0..pagerState.maxPage) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val color =
                    if (pageIndex == pagerState.currentPage) {
                        indicatorColor
                    } else {
                        selectIndicatorColor
                    }
                val start = when (gravity) {
                    BottomCenter -> {
                        val width = canvasWidth - pagerState.maxPage * indicatorDistance
                        width / 2
                    }
                    BottomLeft -> {
                        100f
                    }
                    BottomRight -> {
                        canvasWidth - pagerState.maxPage * indicatorDistance - 100f

                    }
                    else -> 100f
                }
                drawCircle(
                    color,
                    10f,
                    center = Offset(start + pageIndex * indicatorDistance, canvasHeight)
                )
            }
        }
    }
}
