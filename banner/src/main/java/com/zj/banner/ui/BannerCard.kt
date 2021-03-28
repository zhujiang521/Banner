package com.zj.banner.ui

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zj.banner.model.BaseBannerBean
import dev.chrisbanes.accompanist.coil.CoilImage

private const val TAG = "BannerCard"

/**
 * Banner 图片展示卡片
 *
 * @param bean banner Model
 * @param modifier
 */
@Composable
fun <T : BaseBannerBean> BannerCard(
    bean: T,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    onBannerClick: () -> Unit,
) {
    if (bean.filePath.isEmpty() && bean.url.isEmpty() && bean.imgRes <= 0) {
        throw NullPointerException("Url or imgRes or filePath must have a not for empty.")
    }

    Card(
        shape = shape,
        modifier = modifier
    ) {
        val imgModifier = Modifier.clickable(onClick = onBannerClick)
        if (bean.url.isEmpty()) {
            Log.d(TAG, "PostCardPopular: 加载本地图片")
            val painter = if (bean.imgRes <= 0) {
                val bitmap = BitmapFactory.decodeFile(bean.filePath)
                BitmapPainter(bitmap.asImageBitmap())
            } else {
                painterResource(bean.imgRes)
            }
            Image(
                modifier = imgModifier,
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        } else {
            Log.d(TAG, "PostCardPopular: 加载网络图片")
            CoilImage(
                data = bean.url,
                contentDescription = null,
                modifier = imgModifier
            )
        }

    }
}
