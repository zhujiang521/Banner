package com.zj.test

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zj.banner.model.BaseBannerBean
import com.zj.banner.BannerPager

data class BannerBean(
    override val url: String,
    override val filePath: String,
    override val imgRes: Int
) : BaseBannerBean()



@Composable
fun BannerTest() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {

        val items = arrayListOf(
            BannerBean(
                "https://wanandroid.com/blogimgs/8a0131ac-05b7-4b6c-a8d0-f438678834ba.png",
                "",
                0
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
                "",
                0
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
                "",
                0
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
                "",
                0
            ),
        )

        BannerPager(
            items = items,
        ) { item ->
            Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
        }
    }
}