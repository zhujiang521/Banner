package com.zj.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun ThreeDImage(x: Float, y: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.icon_three_bg),
            contentDescription = "",
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = x.dp, y = y.dp),
            painter = painterResource(id = R.drawable.icon_three_small),
            contentDescription = "",
        )
    }
}