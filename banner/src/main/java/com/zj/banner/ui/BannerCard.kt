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
    if (bean.data == null) {
        throw NullPointerException("Url or imgRes or filePath must have a not for empty.")
    }

    Card(
        shape = shape,
        modifier = modifier
    ) {
        val imgModifier = Modifier.clickable(onClick = onBannerClick)
        when (bean.data) {
            is String -> {
                val img = bean.data as String
                if (img.contains("https://") || img.contains("http://")) {
                    Log.d(TAG, "PostCardPopular: 加载网络图片")
                    CoilImage(
                        data = img,
                        contentDescription = null,
                        modifier = imgModifier
                    )
                } else {
                    Log.d(TAG, "PostCardPopular: 加载本地图片")
                    val bitmap = BitmapFactory.decodeFile(img)
                    Image(
                        modifier = imgModifier,
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is Int -> {
                Log.d(TAG, "PostCardPopular: 加载本地资源图片")
                Image(
                    modifier = imgModifier,
                    painter = painterResource(bean.data as Int),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                throw IllegalArgumentException("参数类型不符合要求，只能是：url、文件路径或者是 drawable id")
            }
        }
    }
}
