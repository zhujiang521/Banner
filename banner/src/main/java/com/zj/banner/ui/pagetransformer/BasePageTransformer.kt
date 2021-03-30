package com.zj.banner.ui.pagetransformer

import com.zj.banner.ui.PagerState

/**
 * 图片切换动画基类
 */
abstract class BasePageTransformer {

    /**
     * 切换页面
     *
     * @param pagerState [PagerState]
     */
    abstract fun transformPage(pagerState: PagerState)

}