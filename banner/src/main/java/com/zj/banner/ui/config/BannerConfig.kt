package com.zj.banner.ui.config

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zj.banner.ui.pagetransformer.BasePageTransformer

data class BannerConfig(
    // banner 高度
    var bannerHeight: Dp = 210.dp,
    // banner 图片距离四周的 padding 值
    var bannerImagePadding: Dp = 8.dp,
    // banner 图片的 shape
    var shape: Shape = RoundedCornerShape(10.dp),
    // banner 切换间隔时间
    var intervalTime: Long = 3000,
    // 是否循环播放
    var repeat: Boolean = true,
    // banner 图片切换动画
    var transformer: BasePageTransformer? = null
)