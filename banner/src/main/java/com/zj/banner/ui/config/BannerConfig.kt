package com.zj.banner.ui.config

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BannerConfig(
    // banner 高度
    var bannerHeight: Dp = 210.dp,
    // banner 图片距离四周的 padding 值
    var bannerImagePadding: Dp = 8.dp,
    // banner 图片的 shape
    var shape: Shape = RoundedCornerShape(10.dp),
    // banner 切换间隔时间
    var intervalTime: Long = 3000
)