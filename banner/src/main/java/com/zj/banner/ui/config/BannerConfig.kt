package com.zj.banner.ui.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BannerConfig(
    // banner 高度
    var imageRatio: Float = 2f,
    // banner 图片距离四周的 padding 值
    var bannerImagePadding: Dp = 8.dp,
    // banner 图片的 shape
    var shape: Shape = RoundedCornerShape(10.dp),
    // banner 切换间隔时间
    var intervalTime: Long = 3000,
    // 使用可选的scale参数来确定要使用的纵横比缩放
    var contentScale: ContentScale = ContentScale.Crop,
    // 是否循环播放
    var repeat: Boolean = true,
    // 添加项目之间的水平间距
    var itemSpacing: Dp = 0.dp,
    // 将水平填充添加到“居中”页面
    var contentPadding: PaddingValues = PaddingValues(0.dp),
)